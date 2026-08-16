package com.quizora.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quizora.dto.QuizQuestionDTO;
import com.quizora.config.GeminiProperties;

import io.netty.resolver.DefaultAddressResolverGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AiIntegrationService {

    private static final Logger logger = LoggerFactory.getLogger(AiIntegrationService.class);

    private final GeminiProperties geminiProperties;
    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AiIntegrationService(GeminiProperties geminiProperties,
                                WebClient.Builder webClientBuilder) {
        this.geminiProperties = geminiProperties;

        // Use JDK's DNS resolver instead of Netty's async resolver
        // (fixes "Failed to resolve" errors on Windows)
        HttpClient httpClient = HttpClient.create()
                .resolver(DefaultAddressResolverGroup.INSTANCE)
                .responseTimeout(Duration.ofSeconds(60));

        // Allow larger response payloads (Gemini can return large JSON)
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs()
                        .maxInMemorySize(16 * 1024 * 1024)) // 16 MB
                .build();

        this.webClient = webClientBuilder
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .exchangeStrategies(strategies)
                .build();
    }

    // Current stable Gemini models (August 2026)
    private static final List<String> FALLBACK_MODELS = List.of(
            "gemini-2.5-flash",
            "gemini-2.5-pro",
            "gemini-3.5-flash-lite"
    );

    // ==============================
    // MAIN QUIZ GENERATION METHOD
    // ==============================
    public List<QuizQuestionDTO> generateQuizFromContent(String content,
                                                         String difficulty,
                                                         String topics,
                                                         int questionCount) {

        if (content == null || content.trim().isEmpty()) {
            throw new RuntimeException("Content cannot be empty");
        }

        if (geminiProperties.getApiKey() == null || geminiProperties.getApiKey().isBlank()) {
            throw new RuntimeException("Gemini API KEY is EMPTY or NULL in application.properties");
        }

        logger.info("=== GEMINI INTEGRATION START ===");
        logger.info("Content length: {} chars, difficulty: {}, topics: {}, count: {}", 
                content.length(), difficulty, topics, questionCount);

        String cleanContent = content.replace("%", "%%");
        String truncatedContent = cleanContent.length() > 8000
                ? cleanContent.substring(0, 8000) + "..."
                : cleanContent;

        String prompt = buildStructuredQuizPrompt(
                truncatedContent,
                difficulty,
                topics,
                questionCount
        );

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(Map.of("text", prompt)))
                )
        );

        // Build list of target URLs to try (primary configured URL first, then fallback model URLs)
        List<String> targetUrls = new ArrayList<>();
        if (geminiProperties.getApiUrl() != null && !geminiProperties.getApiUrl().isBlank()) {
            targetUrls.add(geminiProperties.getApiUrl());
        }

        String apiKey = geminiProperties.getApiKey().trim();
        for (String modelName : FALLBACK_MODELS) {
            String fallbackUrl = String.format("https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent", modelName);
            if (!targetUrls.contains(fallbackUrl)) {
                targetUrls.add(fallbackUrl);
            }
        }

        String lastErrorMsg = null;
        for (int i = 0; i < targetUrls.size(); i++) {
            String currentUrl = targetUrls.get(i);
            logger.info("Attempting Gemini API call [{}/{}] with endpoint: {}", i + 1, targetUrls.size(), currentUrl);

            try {
                String fullUrl = currentUrl.contains("?key=") ? currentUrl : (currentUrl + "?key=" + apiKey);

                String response = webClient.post()
                        .uri(fullUrl)
                        .header("Content-Type", "application/json")
                        .header("x-goog-api-key", apiKey)
                        .bodyValue(requestBody)
                        .retrieve()
                        .onStatus(status -> status.isError(), clientResponse -> {
                            return clientResponse.bodyToMono(String.class)
                                    .flatMap(errorBody -> {
                                        logger.warn("Gemini API endpoint [{}] returned error ({}): {}", 
                                                currentUrl, clientResponse.statusCode(), errorBody);
                                        return reactor.core.publisher.Mono.error(
                                                new RuntimeException("API " + clientResponse.statusCode() + ": " + errorBody));
                                    });
                        })
                        .bodyToMono(String.class)
                        .block();

                if (response != null && !response.isBlank()) {
                    logger.info("Gemini API call SUCCESSFUL with endpoint: {}", currentUrl);
                    List<QuizQuestionDTO> questions = parseStructuredQuizQuestions(response);
                    logger.info("GEMINI_CALL_SUCCESS: {} questions generated", questions.size());
                    logger.info("=== GEMINI INTEGRATION END ===");
                    return questions;
                }
            } catch (Exception e) {
                lastErrorMsg = e.getMessage();
                logger.warn("Gemini API call failed for endpoint [{}]: {}. Trying next fallback...", currentUrl, e.getMessage());
            }
        }

        logger.error("ALL Gemini API endpoints failed. Last error: {}", lastErrorMsg);
        throw new RuntimeException("AI quiz generation failed across all available Gemini models: " + lastErrorMsg);
    }

    // ==============================
    // PROMPT BUILDER
    // ==============================
    private String buildStructuredQuizPrompt(String content,
                                             String difficulty,
                                             String topics,
                                             int questionCount) {

        return String.format("""
                You are an expert quiz generator.

                Create %d unique, content-specific quiz questions.

                CONTENT:
                %s

                REQUIREMENTS:
                1. Generate EXACTLY %d questions
                2. Multiple choice (4 options)
                3. One correct answer
                4. Detailed explanation
                5. Difficulty: %s
                6. Topics: %s

                OUTPUT FORMAT (STRICT JSON ARRAY ONLY):

                [
                  {
                    "question": "Question text",
                    "options": ["A", "B", "C", "D"],
                    "answer": "Correct option",
                    "explanation": "Explanation text",
                    "difficulty": "%s",
                    "topics": ["topic1", "topic2"]
                  }
                ]

                IMPORTANT:
                - Return ONLY JSON
                - No markdown
                - No extra text
                """,
                questionCount,
                content,
                questionCount,
                difficulty,
                topics,
                difficulty
        );
    }

    // ==============================
    // RESPONSE PARSER
    // ==============================
    private List<QuizQuestionDTO> parseStructuredQuizQuestions(String response) {

        try {
            logger.info("=== PARSING GEMINI RESPONSE START ===");
            
            JsonNode root = objectMapper.readTree(response);
            logger.info("Parsed JSON root successfully");
            
            JsonNode candidates = root.path("candidates");
            logger.info("Found candidates node: {}", candidates);

            if (!candidates.isArray() || candidates.size() == 0) {
                throw new RuntimeException("No candidates found in Gemini response");
            }

            JsonNode contentNode = candidates.get(0).path("content");
            logger.info("Found content node: {}", contentNode);

            // Try different ways to extract the text
            String text = "";
            
            // Method 1: Standard Gemini format
            JsonNode parts = contentNode.path("parts");
            if (parts.isArray() && parts.size() > 0) {
                text = parts.get(0).path("text").asText();
                logger.info("Extracted text via parts: {}", text.substring(0, Math.min(100, text.length())));
            }
            
            // Method 2: Direct text content
            if (text.isEmpty()) {
                text = contentNode.asText();
                logger.info("Extracted text directly: {}", text.substring(0, Math.min(100, text.length())));
            }

            // Look for JSON array in the text
            int start = text.indexOf('[');
            int end = text.lastIndexOf(']');

            if (start == -1 || end == -1) {
                logger.error("No JSON array found in response text");
                throw new RuntimeException("No JSON array found in response");
            }

            String jsonArray = text.substring(start, end + 1);
            logger.info("Extracted JSON array: {}", jsonArray.substring(0, Math.min(200, jsonArray.length())));

            QuizQuestionDTO[] questions =
                    objectMapper.readValue(jsonArray, QuizQuestionDTO[].class);

            logger.info("=== PARSING GEMINI RESPONSE SUCCESS ===");
            return List.of(questions);

        } catch (Exception e) {
            logger.error("Parsing failed", e);
            throw new RuntimeException("Failed to parse Gemini response: " + e.getMessage());
        }
    }
}
