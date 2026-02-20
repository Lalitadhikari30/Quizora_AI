package com.quizora.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.quizora.config.GeminiProperties;

import java.util.List;
import java.util.Map;

@Service
public class GeminiService {
    
    private static final Logger logger = LoggerFactory.getLogger(GeminiService.class);
    
    private final GeminiProperties geminiProperties;
    
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    
    public GeminiService(GeminiProperties geminiProperties) {
        this.geminiProperties = geminiProperties;
        this.webClient = WebClient.builder().build();
        this.objectMapper = new ObjectMapper();
    }
    
    public GeminiQuizResponse generateQuiz(String extractedText) {
        try {
            String prompt = buildQuizPrompt(extractedText);
            
            logger.info("Sending request to Gemini API");
            logger.info("Text length: {} characters", extractedText.length());
            
            // Trim text if too long
            String processedText = extractedText.length() > 8000 ? extractedText.substring(0, 8000) : extractedText;
            
            Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                    Map.of(
                        "parts", List.of(
                            Map.of("text", prompt + "\n\nContent:\n\n" + processedText)
                        )
                    )
                )
            );
            
            String requestUrl = geminiProperties.getApiUrl() + "?key=" + geminiProperties.getApiKey();
            
            String response = webClient.post()
                    .uri(requestUrl)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            
            logger.info("Gemini response received");
            logger.debug("Raw response: {}", response);
            
            return parseGeminiResponse(response);
            
        } catch (Exception e) {
            logger.error("Failed to generate quiz with Gemini: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate quiz: " + e.getMessage(), e);
        }
    }
    
    private String buildQuizPrompt(String text) {
        return String.format("""
            You are an expert quiz generator.
            
            Generate exactly 10 multiple choice questions from the content below.
            
            Requirements:
            - 4 options per question (A, B, C, D)
            - Only one correct answer (index 0-3)
            - Medium difficulty
            - Focus on important concepts
            - Avoid trivial or vague questions
            
            Return STRICT JSON format:
            {
              "questions": [
                {
                  "question": "Question text here",
                  "options": ["Option A", "Option B", "Option C", "Option D"],
                  "correctAnswer": 1
                }
              ]
            }
            
            Content:
            %s
            """, text);
    }
    
    private GeminiQuizResponse parseGeminiResponse(String response) {
        try {
            // Extract JSON from Gemini response
            String jsonContent = extractJsonFromResponse(response);
            
            // Parse the JSON
            Map<String, Object> parsedResponse = objectMapper.readValue(jsonContent, Map.class);
            
            // Extract questions array
            List<Map<String, Object>> questions = (List<Map<String, Object>>) parsedResponse.get("questions");
            
            logger.info("Successfully parsed {} questions from Gemini response", questions.size());
            
            return new GeminiQuizResponse(questions);
            
        } catch (Exception e) {
            logger.error("Failed to parse Gemini response: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to parse Gemini response: " + e.getMessage(), e);
        }
    }
    
    private String extractJsonFromResponse(String response) {
        // Gemini response format: {"candidates": [{"content": {"parts": [{"text": "..."}]}]}
        // We need to extract the JSON from the content
        
        int jsonStart = response.indexOf("{");
        if (jsonStart == -1) {
            throw new RuntimeException("No valid JSON found in Gemini response");
        }
        
        String jsonContent = response.substring(jsonStart);
        int jsonEnd = jsonContent.lastIndexOf("}");
        if (jsonEnd == -1) {
            throw new RuntimeException("Invalid JSON format in Gemini response");
        }
        
        return jsonContent.substring(0, jsonEnd + 1);
    }
    
    public record GeminiQuizResponse(List<Map<String, Object>> questions) {
        public List<Map<String, Object>> questions() { return questions; }
    }
}
