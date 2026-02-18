package com.quizora.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quizora.dto.QuizQuestionDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AiIntegrationService {
    
    private static final Logger logger = LoggerFactory.getLogger(AiIntegrationService.class);
    
    @Value("${AI_API_URL}")
    private String aiApiUrl;
    
    @Value("${AI_API_KEY}")
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

    public List<String> generateInterviewQuestions(String jobRole, String experience, String difficulty) {
        try {
            String prompt = buildInterviewPrompt(jobRole, experience, difficulty);
            
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
            
            return parseInterviewQuestions(response);
            
        } catch (Exception e) {
            logger.error("Failed to generate interview questions", e);
            throw new RuntimeException("Failed to generate interview questions: " + e.getMessage());
        }
    }

    public String analyzeInterviewAnswer(String question, String userAnswer, String jobRole) {
        try {
            String prompt = buildAnswerAnalysisPrompt(question, userAnswer, jobRole);
            
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
            
            return parseAnswerAnalysis(response);
            
        } catch (Exception e) {
            logger.error("Failed to analyze interview answer", e);
            throw new RuntimeException("Failed to analyze answer: " + e.getMessage());
        }
    }

    public List<QuizQuestionDTO> generateQuizFromContent(String content, String difficulty, String topics, int questionCount) {
        try {
            logger.info("Generating quiz from content using Gemini AI");
            String prompt = buildStructuredQuizPrompt(content, difficulty, topics, questionCount);

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

            return parseStructuredQuizQuestions(response);

        } catch (Exception e) {
            logger.error("Failed to generate quiz from content", e);
            // Fallback to mock questions if AI fails
            logger.warn("AI generation failed, falling back to mock questions");
            return generateMockQuestions(content, difficulty, topics, questionCount);
        }
    }

    private List<QuizQuestionDTO> generateMockQuestions(String content, String difficulty, String topics, int questionCount) {
        List<QuizQuestionDTO> mockQuestions = new ArrayList<>();
        
        mockQuestions.add(new QuizQuestionDTO(
            "What is the main purpose of this document?",
            new String[]{"Testing", "Learning", "Development", "Production"},
            "Testing",
            "This document is created for testing the quiz generation functionality.",
            difficulty,
            new String[]{"testing", "quiz"}
        ));
        
        mockQuestions.add(new QuizQuestionDTO(
            "Which programming concepts are mentioned in this document?",
            new String[]{"Variables and Loops", "Database Design", "Web Development", "All of the above"},
            "All of the above",
            "The document contains information about various programming concepts.",
            difficulty,
            new String[]{"programming", "concepts"}
        ));
        
        mockQuestions.add(new QuizQuestionDTO(
            "What type of file processing is demonstrated?",
            new String[]{"Image Processing", "Text Processing", "Audio Processing", "Video Processing"},
            "Text Processing",
            "The document demonstrates text processing for quiz generation.",
            difficulty,
            new String[]{"file-processing", "text"}
        ));
        
        return mockQuestions;
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

    private String buildInterviewPrompt(String jobRole, String experience, String difficulty) {
        return String.format(
            "Generate 10 interview questions for a %s position with %s experience level at %s difficulty.\n\n" +
            "Requirements:\n" +
            "1. Questions should be relevant to the job role\n" +
            "2. Mix of technical and behavioral questions\n" +
            "3. Questions should test practical knowledge\n" +
            "4. Include questions about problem-solving and experience\n" +
            "5. Format as a numbered list\n\n" +
            "Return only the questions, one per line.",
            jobRole, experience, difficulty
        );
    }

    private String buildAnswerAnalysisPrompt(String question, String userAnswer, String jobRole) {
        return String.format(
            "Analyze the following interview answer for a %s position:\n\n" +
            "Question: %s\n" +
            "User Answer: %s\n\n" +
            "Provide analysis in the following format:\n" +
            "1. Score (1-10)\n" +
            "2. Strengths (what was good)\n" +
            "3. Areas for improvement\n" +
            "4. Suggested better answer\n\n" +
            "Be constructive and specific in your feedback.",
            jobRole, question, userAnswer
        );
    }

    private String buildStructuredQuizPrompt(String content, String difficulty, String topics, int questionCount) {
        return String.format(
            "Generate %d quiz questions from the following content:\n\nContent: %s\n\nDifficulty: %s\n\nTopics: %s\n\n" +
            "Generate questions in the following JSON format:\n" +
            "[\n" +
            "  {\n" +
            "    \"question\": \"Question text here\",\n" +
            "    \"options\": [\"Option A\", \"Option B\", \"Option C\", \"Option D\"],\n" +
            "    \"answer\": \"Correct option\",\n" +
            "    \"explanation\": \"Explanation for why this answer is correct\",\n" +
            "    \"difficulty\": \"%s\",\n" +
            "    \"topics\": [\"topic1\", \"topic2\"]\n" +
            "  }\n" +
            "]\n\n" +
            "Requirements:\n" +
            "1. Multiple choice questions with 4 options each\n" +
            "2. Include correct answer\n" +
            "3. Provide explanation\n" +
            "4. Mark difficulty level\n" +
            "5. Tag with relevant topics\n" +
            "6. Return only valid JSON array\n" +
            "7. Ensure all questions are based on the provided content",
            questionCount, content, difficulty, topics, difficulty
        );
    }

    private List<QuizQuestionDTO> parseStructuredQuizQuestions(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode candidates = root.path("candidates");
            
            if (candidates.isArray() && candidates.size() > 0) {
                JsonNode content = candidates.get(0).path("content");
                if (content != null) {
                    String text = content.asText();
                    
                    // Try to parse as JSON array directly
                    try {
                        QuizQuestionDTO[] questions = objectMapper.readValue(text, QuizQuestionDTO[].class);
                        logger.info("Successfully parsed {} quiz questions from AI response", questions.length);
                        return List.of(questions);
                    } catch (Exception e) {
                        logger.warn("Failed to parse as JSON array, attempting to extract JSON from text: {}", e.getMessage());
                        
                        // Try to extract JSON from the text
                        int startIndex = text.indexOf('[');
                        int endIndex = text.lastIndexOf(']');
                        if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
                            String jsonText = text.substring(startIndex, endIndex + 1);
                            QuizQuestionDTO[] questions = objectMapper.readValue(jsonText, QuizQuestionDTO[].class);
                            logger.info("Successfully extracted and parsed {} quiz questions", questions.length);
                            return List.of(questions);
                        }
                    }
                }
            }
            
            logger.warn("No valid quiz questions found in AI response");
            return List.of();
            
        } catch (Exception e) {
            logger.error("Failed to parse structured quiz questions", e);
            throw new RuntimeException("Failed to parse quiz questions: " + e.getMessage());
        }
    }

    private List<String> parseInterviewQuestions(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode candidates = root.path("candidates");
            
            if (candidates.isArray() && candidates.size() > 0) {
                JsonNode content = candidates.get(0).path("content");
                if (content != null) {
                    String text = content.asText();
                    List<String> questions = new ArrayList<>();
                    String[] lines = text.split("\n");
                    
                    for (String line : lines) {
                        line = line.trim();
                        if (!line.isEmpty() && Character.isDigit(line.charAt(0))) {
                            questions.add(line.replaceFirst("^\\d+\\.\\s*", ""));
                        }
                    }
                    return questions;
                }
            }
        } catch (Exception e) {
            logger.error("Failed to parse interview questions", e);
        }
        
        return new ArrayList<>();
    }

    private String parseAnswerAnalysis(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode candidates = root.path("candidates");
            
            if (candidates.isArray() && candidates.size() > 0) {
                JsonNode content = candidates.get(0).path("content");
                if (content != null) {
                    return content.asText();
                }
            }
        } catch (Exception e) {
            logger.error("Failed to parse answer analysis", e);
        }
        
        return "Unable to analyze answer at this time.";
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
