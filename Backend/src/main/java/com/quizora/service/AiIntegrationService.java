// package com.quizora.service;

// import com.fasterxml.jackson.databind.JsonNode;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.quizora.dto.QuizQuestionDTO;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;
// import org.springframework.web.reactive.function.client.WebClient;

// import com.quizora.config.GeminiProperties;

// import reactor.core.publisher.Mono;
// import java.util.ArrayList;
// import java.util.List;
// import java.util.Map;

// @Service
// public class AiIntegrationService {
    
//     private static final Logger logger = LoggerFactory.getLogger(AiIntegrationService.class);
    
//     private final GeminiProperties geminiProperties;
    
//     private final WebClient webClient;
//     private final ObjectMapper objectMapper = new ObjectMapper();

//     public AiIntegrationService(GeminiProperties geminiProperties, WebClient.Builder webClientBuilder) {
//         this.geminiProperties = geminiProperties;
//         this.webClient = webClientBuilder.build();
//     }

//     public List<String> generateQuizQuestions(String content, int questionCount, String difficulty, String topics) {
//         try {
//             String prompt = buildQuizPrompt(content, questionCount, difficulty, topics);
            
//             Map<String, Object> requestBody = Map.of(
//                 "contents", List.of(
//                     Map.of(
//                         "parts", List.of(
//                             Map.of("text", prompt)
//                         )
//                     )
//                 )
//             );
            
//             String response = webClient.post()
//                     .uri(geminiProperties.getApiUrl())
//                     .header("Content-Type", "application/json")
//                     .header("x-goog-api-key", geminiProperties.getApiKey())
//                     .bodyValue(requestBody)
//                     .retrieve()
//                     .bodyToMono(String.class)
//                     .block();
            
//             return parseQuizQuestions(response);
            
//         } catch (Exception e) {
//             logger.error("Failed to generate quiz questions", e);
//             throw new RuntimeException("Failed to generate quiz questions: " + e.getMessage());
//         }
//     }

//     public List<String> generateInterviewQuestions(String jobRole, String experience, String difficulty) {
//         try {
//             String prompt = buildInterviewPrompt(jobRole, experience, difficulty);
            
//             Map<String, Object> requestBody = Map.of(
//                 "contents", List.of(
//                     Map.of(
//                         "parts", List.of(
//                             Map.of("text", prompt)
//                         )
//                     )
//                 )
//             );
            
//             String response = webClient.post()
//                     .uri(geminiProperties.getApiUrl())
//                     .header("Content-Type", "application/json")
//                     .header("x-goog-api-key", geminiProperties.getApiKey())
//                     .bodyValue(requestBody)
//                     .retrieve()
//                     .bodyToMono(String.class)
//                     .block();
            
//             return parseInterviewQuestions(response);
            
//         } catch (Exception e) {
//             logger.error("Failed to generate interview questions", e);
//             throw new RuntimeException("Failed to generate interview questions: " + e.getMessage());
//         }
//     }

//     public String analyzeInterviewAnswer(String question, String userAnswer, String jobRole) {
//         try {
//             String prompt = buildAnswerAnalysisPrompt(question, userAnswer, jobRole);
            
//             Map<String, Object> requestBody = Map.of(
//                 "contents", List.of(
//                     Map.of(
//                         "parts", List.of(
//                             Map.of("text", prompt)
//                         )
//                     )
//                 )
//             );
            
//             String response = webClient.post()
//                     .uri(geminiProperties.getApiUrl())
//                     .header("Content-Type", "application/json")
//                     .header("x-goog-api-key", geminiProperties.getApiKey())
//                     .bodyValue(requestBody)
//                     .retrieve()
//                     .bodyToMono(String.class)
//                     .block();
            
//             return parseAnswerAnalysis(response);
            
//         } catch (Exception e) {
//             logger.error("Failed to analyze interview answer", e);
//             throw new RuntimeException("Failed to analyze answer: " + e.getMessage());
//         }
//     }

//     public List<QuizQuestionDTO> generateQuizFromContent(String content, String difficulty, String topics, int questionCount) {
//         try {
//             logger.info("=== GEMINI INTEGRATION START ===");
//             logger.info("Content length: {} characters", content.length());
//             logger.info("Difficulty: {}", difficulty);
//             logger.info("Topics: {}", topics);
//             logger.info("Question count: {}", questionCount);
            
//             // Validate input parameters
//             if (content == null || content.trim().isEmpty()) {
//                 throw new RuntimeException("GEMINI_INPUT_EMPTY: Content cannot be null or empty");
//             }
            
//             // Handle large text by chunking if needed
//             String processedContent = chunkLargeText(content);
//             logger.info("Processed content length: {} characters", processedContent.length());
            
//             String prompt = buildStructuredQuizPrompt(processedContent, difficulty, topics, questionCount);
//             logger.info("Generated prompt length: {} characters", prompt.length());

//             Map<String, Object> requestBody = Map.of(
//                 "contents", List.of(
//                     Map.of(
//                         "parts", List.of(
//                             Map.of("text", prompt)
//                         )
//                     )
//                 )
//             );

//             logger.info("Making Gemini API call...");
//             String response = webClient.post()
//                     .uri(geminiProperties.getApiUrl())
//                     .header("Content-Type", "application/json")
//                     .header("x-goog-api-key", geminiProperties.getApiKey())
//                     .bodyValue(requestBody)
//                     .retrieve()
//                     .bodyToMono(String.class)
//                     .block();

//             logger.info("Gemini API response received");
//             logger.info("Response length: {} characters", response != null ? response.length() : 0);

//             if (response == null || response.trim().isEmpty()) {
//                 throw new RuntimeException("GEMINI_RESPONSE_EMPTY: Gemini API returned empty response");
//             }

//             List<QuizQuestionDTO> questions = parseStructuredQuizQuestions(response);
            
//             if (questions == null || questions.isEmpty()) {
//                 throw new RuntimeException("GEMINI_PARSE_EMPTY: No questions parsed from Gemini response");
//             }
            
//             logger.info("GEMINI_CALL_SUCCESS: {} questions generated", questions.size());
//             logger.info("=== GEMINI INTEGRATION END ===");
            
//             return questions;

//         } catch (Exception e) {
//             logger.error("GEMINI_CALL_FAILED: Failed to generate quiz from content", e);
//             throw new RuntimeException("AI quiz generation failed: " + e.getMessage() + 
//                 ". Please check your internet connection, API key, or try uploading a different document.");
//         }
//     }
    
//     /**
//      * Chunk large text to avoid token limits
//      */
//     private String chunkLargeText(String content) {
//         int maxChunkSize = 8000; // Conservative limit for Gemini
//         if (content.length() <= maxChunkSize) {
//             return content;
//         }
        
//         logger.warn("Content too large ({} chars), chunking to {} characters", content.length(), maxChunkSize);
//         return content.substring(0, maxChunkSize) + "... [Content truncated for processing]";
//     }

//     private List<QuizQuestionDTO> generateMockQuestions(String content, String difficulty, String topics, int questionCount) {
//         List<QuizQuestionDTO> mockQuestions = new ArrayList<>();
        
//         mockQuestions.add(new QuizQuestionDTO(
//             "What is the main purpose of this document?",
//             new String[]{"Testing", "Learning", "Development", "Production"},
//             "Testing",
//             "This document is created for testing the quiz generation functionality.",
//             difficulty,
//             new String[]{"testing", "quiz"}
//         ));
        
//         mockQuestions.add(new QuizQuestionDTO(
//             "Which programming concepts are mentioned in this document?",
//             new String[]{"Variables and Loops", "Database Design", "Web Development", "All of the above"},
//             "All of the above",
//             "The document contains information about various programming concepts.",
//             difficulty,
//             new String[]{"programming", "concepts"}
//         ));
        
//         mockQuestions.add(new QuizQuestionDTO(
//             "What type of file processing is demonstrated?",
//             new String[]{"Image Processing", "Text Processing", "Audio Processing", "Video Processing"},
//             "Text Processing",
//             "The document demonstrates text processing for quiz generation.",
//             difficulty,
//             new String[]{"file-processing", "text"}
//         ));
        
//         return mockQuestions;
//     }

//     private String buildQuizPrompt(String content, int questionCount, String difficulty, String topics) {
//         return String.format(
//             "Generate %d quiz questions from the following content:\n\nContent: %s\n\nDifficulty: %s\n\nTopics: %s\n\n" +
//             "Generate questions in the following format:\n" +
//             "1. Multiple choice questions with 4 options each\n" +
//             "2. Include correct answer\n" +
//             "3. Provide explanation\n" +
//             "4. Mark difficulty level\n" +
//             "5. Tag with relevant topics\n\n" +
//             "Return only valid JSON array of questions.",
//             questionCount, content, difficulty, topics
//         );
//     }

//     private String buildInterviewPrompt(String jobRole, String experience, String difficulty) {
//         return String.format(
//             "Generate 10 interview questions for a %s position with %s experience level at %s difficulty.\n\n" +
//             "Requirements:\n" +
//             "1. Questions should be relevant to the job role\n" +
//             "2. Mix of technical and behavioral questions\n" +
//             "3. Questions should test practical knowledge\n" +
//             "4. Include questions about problem-solving and experience\n" +
//             "5. Format as a numbered list\n\n" +
//             "Return only the questions, one per line.",
//             jobRole, experience, difficulty
//         );
//     }

//     private String buildAnswerAnalysisPrompt(String question, String userAnswer, String jobRole) {
//         return String.format(
//             "Analyze the following interview answer for a %s position:\n\n" +
//             "Question: %s\n" +
//             "User Answer: %s\n\n" +
//             "Provide analysis in the following format:\n" +
//             "1. Score (1-10)\n" +
//             "2. Strengths (what was good)\n" +
//             "3. Areas for improvement\n" +
//             "4. Suggested better answer\n\n" +
//             "Be constructive and specific in your feedback.",
//             jobRole, question, userAnswer
//         );
//     }

//     private String buildStructuredQuizPrompt(String content, String difficulty, String topics, int questionCount) {
//         // Truncate content if too long to avoid token limits
//         String truncatedContent = content.length() > 8000 ? content.substring(0, 8000) + "..." : content;
        
//         return String.format(
//             "You are an expert quiz generator. Create %d unique, specific quiz questions from the following content.\n\n" +
//             "CONTENT ANALYSIS REQUIRED:\n" +
//             "First, analyze content thoroughly. Identify:\n" +
//             "- Key concepts and topics\n" +
//             "- Important details and facts\n" +
//             "- Technical terms and definitions\n" +
//             "- Procedures and processes described\n\n" +
//             "CONTENT: %s\n\n" +
//             "REQUIREMENTS:\n" +
//             "1. Generate EXACTLY %d questions\n" +
//             "2. Each question MUST be based on specific content details\n" +
//             "3. Create UNIQUE questions - avoid generic templates\n" +
//             "4. Multiple choice with 4 realistic options each\n" +
//             "5. One clearly correct answer per question\n" +
//             "6. Detailed explanation referencing content\n" +
//             "7. Difficulty: %s\n" +
//             "8. Topics: %s\n\n" +
//             "OUTPUT FORMAT (Strict JSON):\n" +
//             "[\n" +
//             "  {\n" +
//             "    \"question\": \"Specific question about content\",\n" +
//             "    \"options\": [\"Specific Option A\", \"Specific Option B\", \"Specific Option C\", \"Specific Option D\"],\n" +
//             "    \"answer\": \"Correct option\",\n" +
//             "    \"explanation\": \"Detailed explanation with content references\",\n" +
//             "    \"difficulty\": \"%s\",\n" +
//             "    \"topics\": [\"specific_topic1\", \"specific_topic2\"]\n" +
//             "  }\n" +
//             "]\n\n" +
//             "CRITICAL: Do NOT use generic questions like 'What is the main purpose?' unless the content explicitly states a purpose. " +
//             "Create questions about specific facts, concepts, and details from the provided content.",
//             questionCount, truncatedContent, questionCount, difficulty, topics, difficulty
//         );
//     }

//     private List<QuizQuestionDTO> parseStructuredQuizQuestions(String response) {
//         try {
//             logger.info("=== GEMINI RESPONSE PARSING START ===");
//             logger.info("Raw response length: {} characters", response.length());
            
//             if (response == null || response.trim().isEmpty()) {
//                 throw new RuntimeException("GEMINI_PARSE_EMPTY: Response is null or empty");
//             }
            
//             JsonNode root = objectMapper.readTree(response);
//             logger.info("JSON parsing successful");
            
//             JsonNode candidates = root.path("candidates");
//             if (!candidates.isArray() || candidates.size() == 0) {
//                 throw new RuntimeException("GEMINI_PARSE_NO_CANDIDATES: No candidates found in response");
//             }
            
//             JsonNode content = candidates.get(0).path("content");
//             if (content.isMissingNode() || content.isNull()) {
//                 throw new RuntimeException("GEMINI_PARSE_NO_CONTENT: No content found in candidate");
//             }
            
//             String text = content.asText();
//             logger.info("Extracted text content length: {} characters", text.length());
            
//             if (text == null || text.trim().isEmpty()) {
//                 throw new RuntimeException("GEMINI_PARSE_EMPTY_TEXT: Content text is empty");
//             }
            
//             // Try to parse as JSON array directly
//             try {
//                 QuizQuestionDTO[] questions = objectMapper.readValue(text, QuizQuestionDTO[].class);
//                 if (questions == null || questions.length == 0) {
//                     throw new RuntimeException("GEMINI_PARSE_EMPTY_ARRAY: Parsed questions array is empty");
//                 }
//                 logger.info("Successfully parsed {} quiz questions directly", questions.length);
//                 logger.info("=== GEMINI RESPONSE PARSING END ===");
//                 return List.of(questions);
//             } catch (Exception e) {
//                 logger.warn("Direct JSON parsing failed, attempting to extract JSON from text: {}", e.getMessage());
                
//                 // Try to extract JSON from text
//                 int startIndex = text.indexOf('[');
//                 int endIndex = text.lastIndexOf(']');
//                 if (startIndex == -1 || endIndex == -1 || endIndex <= startIndex) {
//                     throw new RuntimeException("GEMINI_PARSE_NO_JSON_ARRAY: No JSON array found in response text");
//                 }
                
//                 String jsonText = text.substring(startIndex, endIndex + 1);
//                 logger.info("Extracted JSON substring length: {} characters", jsonText.length());
                
//                 QuizQuestionDTO[] questions = objectMapper.readValue(jsonText, QuizQuestionDTO[].class);
//                 if (questions == null || questions.length == 0) {
//                     throw new RuntimeException("GEMINI_PARSE_EMPTY_EXTRACTED: Extracted questions array is empty");
//                 }
                
//                 // Validate each question
//                 for (int i = 0; i < questions.length; i++) {
//                     QuizQuestionDTO q = questions[i];
//                     if (q.getQuestion() == null || q.getQuestion().trim().isEmpty()) {
//                         throw new RuntimeException("GEMINI_PARSE_INVALID_QUESTION: Question " + i + " has empty text");
//                     }
//                     if (q.getOptions() == null || q.getOptions().length < 2) {
//                         throw new RuntimeException("GEMINI_PARSE_INVALID_OPTIONS: Question " + i + " has insufficient options");
//                     }
//                     if (q.getAnswer() == null || q.getAnswer().trim().isEmpty()) {
//                         throw new RuntimeException("GEMINI_PARSE_INVALID_ANSWER: Question " + i + " has empty answer");
//                     }
//                 }
                
//                 logger.info("Successfully extracted and validated {} quiz questions", questions.length);
//                 logger.info("=== GEMINI RESPONSE PARSING END ===");
//                 return List.of(questions);
//             }
            
//         } catch (Exception e) {
//             logger.error("GEMINI_PARSE_FAILED: Failed to parse structured quiz questions", e);
//             throw new RuntimeException("Failed to parse quiz questions: " + e.getMessage());
//         }
//     }

//     private List<String> parseInterviewQuestions(String response) {
//         try {
//             JsonNode root = objectMapper.readTree(response);
//             JsonNode candidates = root.path("candidates");
            
//             if (candidates.isArray() && candidates.size() > 0) {
//                 JsonNode content = candidates.get(0).path("content");
//                 if (content != null) {
//                     String text = content.asText();
//                     List<String> questions = new ArrayList<>();
//                     String[] lines = text.split("\n");
                    
//                     for (String line : lines) {
//                         line = line.trim();
//                         if (!line.isEmpty() && Character.isDigit(line.charAt(0))) {
//                             questions.add(line.replaceFirst("^\\d+\\.\\s*", ""));
//                         }
//                     }
//                     return questions;
//                 }
//             }
//         } catch (Exception e) {
//             logger.error("Failed to parse interview questions", e);
//         }
        
//         return new ArrayList<>();
//     }

//     private String parseAnswerAnalysis(String response) {
//         try {
//             JsonNode root = objectMapper.readTree(response);
//             JsonNode candidates = root.path("candidates");
            
//             if (candidates.isArray() && candidates.size() > 0) {
//                 JsonNode content = candidates.get(0).path("content");
//                 if (content != null) {
//                     return content.asText();
//                 }
//             }
//         } catch (Exception e) {
//             logger.error("Failed to parse answer analysis", e);
//         }
        
//         return "Unable to analyze answer at this time.";
//     }

//     private List<String> parseQuizQuestions(String response) {
//         try {
//             JsonNode root = objectMapper.readTree(response);
//             JsonNode candidates = root.path("candidates");
            
//             if (candidates.isArray() && candidates.size() > 0) {
//                 JsonNode content = candidates.get(0).path("content");
//                 if (content != null) {
//                     String text = content.asText();
//                     // Simple JSON parsing - split by question number
//                     List<String> questions = new ArrayList<>();
//                     String[] lines = text.split("\n");
                    
//                     for (String line : lines) {
//                         line = line.trim();
//                         if (!line.isEmpty() && (line.startsWith("{") || line.contains("question") || line.contains("options"))) {
//                             questions.add(line);
//                         }
//                     }
//                     return questions;
//                 }
//             }
//         } catch (Exception e) {
//             logger.error("Failed to parse AI response", e);
//         }
        
//         return new ArrayList<>();
//     }
// }



package com.quizora.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quizora.dto.QuizQuestionDTO;
import com.quizora.config.GeminiProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

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
        this.webClient = webClientBuilder.build();
    }

    // ==============================
    // MAIN QUIZ GENERATION METHOD
    // ==============================
    public List<QuizQuestionDTO> generateQuizFromContent(String content,
                                                         String difficulty,
                                                         String topics,
                                                         int questionCount) {

        try {
            logger.info("=== GEMINI INTEGRATION START ===");

            if (content == null || content.trim().isEmpty()) {
                throw new RuntimeException("Content cannot be empty");
            }

            // 🔥 DEBUG LOGGING (VERY IMPORTANT)
            logger.info("API URL LOADED: [{}]", geminiProperties.getApiUrl());
            logger.info("API KEY LOADED (first 10 chars): [{}]",
                    geminiProperties.getApiKey() != null
                            ? geminiProperties.getApiKey().substring(0, 10) + "..."
                            : "NULL");

            // Safety check (this will immediately tell you if URL is empty)
            if (geminiProperties.getApiUrl() == null || geminiProperties.getApiUrl().isBlank()) {
                throw new RuntimeException("Gemini API URL is EMPTY or NULL");
            }

            if (geminiProperties.getApiKey() == null || geminiProperties.getApiKey().isBlank()) {
                throw new RuntimeException("Gemini API KEY is EMPTY or NULL");
            }

            // Escape % to avoid String.format crash
            content = content.replace("%", "%%");

            String truncatedContent = content.length() > 8000
                    ? content.substring(0, 8000) + "..."
                    : content;

            String prompt = buildStructuredQuizPrompt(
                    truncatedContent,
                    difficulty,
                    topics,
                    questionCount
            );

            logger.info("Prompt generated successfully.");
            logger.info("=== GEMINI API CALL START ===");

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
                    .uri(geminiProperties.getApiUrl())
                    .header("Content-Type", "application/json")
                    .header("x-goog-api-key", geminiProperties.getApiKey())
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            logger.info("=== GEMINI API RESPONSE RECEIVED ===");

            if (response == null || response.isEmpty()) {
                throw new RuntimeException("Empty response from Gemini API");
            }

            logger.info("Response preview: {}",
                    response.substring(0, Math.min(500, response.length())));

            List<QuizQuestionDTO> questions = parseStructuredQuizQuestions(response);

            logger.info("GEMINI_CALL_SUCCESS: {} questions generated", questions.size());
            logger.info("=== GEMINI INTEGRATION END ===");

            return questions;

        } catch (Exception e) {
            logger.error("GEMINI_CALL_FAILED", e);
            throw new RuntimeException("AI quiz generation failed: " + e.getMessage());
        }
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
            JsonNode root = objectMapper.readTree(response);
            JsonNode candidates = root.path("candidates");

            if (!candidates.isArray() || candidates.size() == 0) {
                throw new RuntimeException("No candidates found in Gemini response");
            }

            JsonNode contentNode = candidates.get(0).path("content");

            String text = contentNode.path("parts").get(0).path("text").asText();

            int start = text.indexOf('[');
            int end = text.lastIndexOf(']');

            if (start == -1 || end == -1) {
                throw new RuntimeException("No JSON array found in response");
            }

            String jsonArray = text.substring(start, end + 1);

            QuizQuestionDTO[] questions =
                    objectMapper.readValue(jsonArray, QuizQuestionDTO[].class);

            return List.of(questions);

        } catch (Exception e) {
            logger.error("Parsing failed", e);
            throw new RuntimeException("Failed to parse Gemini response: " + e.getMessage());
        }
    }
}
