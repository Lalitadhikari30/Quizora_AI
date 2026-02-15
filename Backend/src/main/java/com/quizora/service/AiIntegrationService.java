package com.quizora.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AiIntegrationService {
    
    private static final Logger logger = LoggerFactory.getLogger(AiIntegrationService.class);
    
    @Value("${ai.api.url}")
    private String aiApiUrl;
    
    @Value("${ai.api.key}")
    private String aiApiKey;
    
    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AiIntegrationService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public List<String> generateQuizQuestions(String content, int questionCount, String difficulty, String topics) {
        try {
            String prompt = buildQuizPrompt(content, questionCount, difficulty, topics);
            
            Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                    Map.of(
                        "parts", List.of(
                            Map.of("text", prompt)
                        )
                    )
                )
            );
            
            String response = webClient.post()
                    .uri(aiApiUrl)
                    .header("Content-Type", "application/json")
                    .header("x-goog-api-key", aiApiKey)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            
            return parseQuizQuestions(response);
            
        } catch (Exception e) {
            logger.error("Failed to generate quiz questions", e);
            throw new RuntimeException("Failed to generate quiz questions: " + e.getMessage());
        }
    }

    private String buildQuizPrompt(String content, int questionCount, String difficulty, String topics) {
        return String.format(
            "Generate %d quiz questions from the following content:\n\nContent: %s\n\nDifficulty: %s\n\nTopics: %s\n\n" +
            "Generate questions in the following format:\n" +
            "1. Multiple choice questions with 4 options each\n" +
            "2. Include correct answer\n" +
            "3. Provide explanation\n" +
            "4. Mark difficulty level\n" +
            "5. Tag with relevant topics\n\n" +
            "Return only valid JSON array of questions.",
            questionCount, content, difficulty, topics
        );
    }

    private List<String> parseQuizQuestions(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode candidates = root.path("candidates");
            
            if (candidates.isArray() && candidates.size() > 0) {
                JsonNode content = candidates.get(0).path("content");
                if (content != null) {
                    String text = content.asText();
                    // Simple JSON parsing - split by question number
                    List<String> questions = new ArrayList<>();
                    String[] lines = text.split("\n");
                    
                    for (String line : lines) {
                        line = line.trim();
                        if (!line.isEmpty() && (line.startsWith("{") || line.contains("question") || line.contains("options"))) {
                            questions.add(line);
                        }
                    }
                    return questions;
                }
            }
        } catch (Exception e) {
            logger.error("Failed to parse AI response", e);
        }
        
        return new ArrayList<>();
    }
}
