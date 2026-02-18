package com.quizora.service;

import com.quizora.entity.Quiz;
import com.quizora.entity.Question;
import com.quizora.entity.SourceType;
import com.quizora.entity.QuizType;
import com.quizora.repository.QuizRepository;
import com.quizora.repository.QuestionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

@Service
public class PipelineValidationService {
    
    private static final Logger logger = LoggerFactory.getLogger(PipelineValidationService.class);
    
    @Autowired
    private FileUploadService fileUploadService;
    
    @Autowired
    private ContentExtractionService contentExtractionService;
    
    @Autowired
    private AiIntegrationService aiIntegrationService;
    
    @Autowired
    private QuizService quizService;
    
    @Autowired
    private QuizRepository quizRepository;
    
    @Autowired
    private QuestionRepository questionRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    public Map<String, Object> validateFullPipeline(MultipartFile file, String userId) {
        Map<String, Object> validationResults = new HashMap<>();
        boolean pipelineSuccess = true;
        
        try {
            logger.info("=== PIPELINE VALIDATION START ===");
            
            // Step 1: Validate Supabase Storage upload
            try {
                String uploadUrl = fileUploadService.uploadFile(file, userId);
                validationResults.put("UPLOAD_SUCCESS", true);
                validationResults.put("UPLOAD_URL", uploadUrl);
                logger.info("UPLOAD_SUCCESS: {}", uploadUrl);
            } catch (Exception e) {
                validationResults.put("UPLOAD_SUCCESS", false);
                validationResults.put("UPLOAD_ERROR", e.getMessage());
                logger.error("UPLOAD_FAILED: {}", e.getMessage());
                pipelineSuccess = false;
            }
            
            // Step 2: Validate text extraction
            try {
                String extractedText = contentExtractionService.extractContent(file);
                if (extractedText == null || extractedText.trim().isEmpty()) {
                    throw new RuntimeException("Extracted text is empty");
                }
                validationResults.put("TEXT_EXTRACTION_SUCCESS", true);
                validationResults.put("TEXT_LENGTH", extractedText.length());
                validationResults.put("TEXT_PREVIEW", extractedText.length() > 100 ? extractedText.substring(0, 100) + "..." : extractedText);
                logger.info("TEXT_EXTRACTION_SUCCESS: {} characters", extractedText.length());
            } catch (Exception e) {
                validationResults.put("TEXT_EXTRACTION_SUCCESS", false);
                validationResults.put("TEXT_EXTRACTION_ERROR", e.getMessage());
                logger.error("TEXT_EXTRACTION_FAILED: {}", e.getMessage());
                pipelineSuccess = false;
            }
            
            // Step 3: Validate Gemini API call
            try {
                String extractedText = contentExtractionService.extractContent(file);
                var quizQuestions = aiIntegrationService.generateQuizFromContent(extractedText, "medium", "general", 5);
                if (quizQuestions == null || quizQuestions.isEmpty()) {
                    throw new RuntimeException("Gemini returned empty questions");
                }
                validationResults.put("GEMINI_CALL_SUCCESS", true);
                validationResults.put("GEMINI_QUESTIONS_COUNT", quizQuestions.size());
                logger.info("GEMINI_CALL_SUCCESS: {} questions generated", quizQuestions.size());
            } catch (Exception e) {
                validationResults.put("GEMINI_CALL_SUCCESS", false);
                validationResults.put("GEMINI_CALL_ERROR", e.getMessage());
                logger.error("GEMINI_CALL_FAILED: {}", e.getMessage());
                pipelineSuccess = false;
            }
            
            // Step 4: Validate Quiz save
            try {
                String extractedText = contentExtractionService.extractContent(file);
                var quizQuestions = aiIntegrationService.generateQuizFromContent(extractedText, "medium", "general", 5);
                
                Quiz quiz = new Quiz();
                quiz.setTitle("Validation Test Quiz");
                quiz.setDescription("Automated validation test");
                quiz.setUserId(userId);
                quiz.setType(QuizType.MULTIPLE_CHOICE);
                quiz.setSourceType(SourceType.TEXT);
                quiz.setSourceContent(extractedText);
                
                Quiz savedQuiz = quizRepository.save(quiz);
                validationResults.put("QUIZ_SAVE_SUCCESS", true);
                validationResults.put("QUIZ_ID", savedQuiz.getId());
                logger.info("QUIZ_SAVE_SUCCESS: Quiz ID {}", savedQuiz.getId());
            } catch (Exception e) {
                validationResults.put("QUIZ_SAVE_SUCCESS", false);
                validationResults.put("QUIZ_SAVE_ERROR", e.getMessage());
                logger.error("QUIZ_SAVE_FAILED: {}", e.getMessage());
                pipelineSuccess = false;
            }
            
            // Step 5: Validate Question save
            try {
                String extractedText = contentExtractionService.extractContent(file);
                var quizQuestions = aiIntegrationService.generateQuizFromContent(extractedText, "medium", "general", 5);
                
                Quiz quiz = new Quiz();
                quiz.setTitle("Validation Test Quiz");
                quiz.setDescription("Automated validation test");
                quiz.setUserId(userId);
                quiz.setType(QuizType.MULTIPLE_CHOICE);
                quiz.setSourceType(SourceType.TEXT);
                quiz.setSourceContent(extractedText);
                
                Quiz savedQuiz = quizRepository.save(quiz);
                
                // Save questions
                for (var questionDto : quizQuestions) {
                    Question question = new Question();
                    question.setQuiz(savedQuiz);
                    question.setQuestionText(questionDto.getQuestion());
                    question.setOptions(String.join("|", questionDto.getOptions()));
                    question.setCorrectAnswer(questionDto.getAnswer());
                    question.setExplanation(questionDto.getExplanation());
                    question.setType(com.quizora.entity.QuestionType.MULTIPLE_CHOICE);
                    questionRepository.save(question);
                }
                
                validationResults.put("QUESTION_SAVE_SUCCESS", true);
                validationResults.put("QUESTIONS_SAVED_COUNT", quizQuestions.size());
                logger.info("QUESTION_SAVE_SUCCESS: {} questions saved", quizQuestions.size());
            } catch (Exception e) {
                validationResults.put("QUESTION_SAVE_SUCCESS", false);
                validationResults.put("QUESTION_SAVE_ERROR", e.getMessage());
                logger.error("QUESTION_SAVE_FAILED: {}", e.getMessage());
                pipelineSuccess = false;
            }
            
            // Step 6: Validate Quiz fetch
            try {
                String extractedText = contentExtractionService.extractContent(file);
                var quizQuestions = aiIntegrationService.generateQuizFromContent(extractedText, "medium", "general", 5);
                
                Quiz quiz = new Quiz();
                quiz.setTitle("Validation Test Quiz");
                quiz.setDescription("Automated validation test");
                quiz.setUserId(userId);
                quiz.setType(QuizType.MULTIPLE_CHOICE);
                quiz.setSourceType(SourceType.TEXT);
                quiz.setSourceContent(extractedText);
                
                Quiz savedQuiz = quizRepository.save(quiz);
                
                var fetchedQuiz = quizService.getQuiz(savedQuiz.getId(), userId);
                if (fetchedQuiz == null) {
                    throw new RuntimeException("Quiz fetch returned null");
                }
                validationResults.put("QUIZ_FETCH_SUCCESS", true);
                validationResults.put("FETCHED_QUIZ_ID", fetchedQuiz.getId());
                logger.info("QUIZ_FETCH_SUCCESS: Quiz ID {}", fetchedQuiz.getId());
            } catch (Exception e) {
                validationResults.put("QUIZ_FETCH_SUCCESS", false);
                validationResults.put("QUIZ_FETCH_ERROR", e.getMessage());
                logger.error("QUIZ_FETCH_FAILED: {}", e.getMessage());
                pipelineSuccess = false;
            }
            
            // Step 7: Validate JWT authentication
            try {
                if (userId == null || userId.trim().isEmpty()) {
                    throw new RuntimeException("JWT subject is null or empty");
                }
                validationResults.put("JWT_AUTH_SUCCESS", true);
                validationResults.put("JWT_SUBJECT", userId);
                logger.info("JWT_AUTH_SUCCESS: Subject {}", userId);
            } catch (Exception e) {
                validationResults.put("JWT_AUTH_SUCCESS", false);
                validationResults.put("JWT_AUTH_ERROR", e.getMessage());
                logger.error("JWT_AUTH_FAILED: {}", e.getMessage());
                pipelineSuccess = false;
            }
            
            // Step 8: Validate JSON response
            try {
                String jsonResponse = objectMapper.writeValueAsString(validationResults);
                if (jsonResponse == null || jsonResponse.trim().isEmpty()) {
                    throw new RuntimeException("JSON serialization failed");
                }
                validationResults.put("JSON_RESPONSE_SUCCESS", true);
                validationResults.put("JSON_RESPONSE_LENGTH", jsonResponse.length());
                logger.info("JSON_RESPONSE_SUCCESS: {} characters", jsonResponse.length());
            } catch (Exception e) {
                validationResults.put("JSON_RESPONSE_SUCCESS", false);
                validationResults.put("JSON_RESPONSE_ERROR", e.getMessage());
                logger.error("JSON_RESPONSE_FAILED: {}", e.getMessage());
                pipelineSuccess = false;
            }
            
            validationResults.put("PIPELINE_COMPLETE", pipelineSuccess);
            validationResults.put("PIPELINE_SUCCESS", pipelineSuccess);
            
            if (pipelineSuccess) {
                logger.info("=== PIPELINE VALIDATION SUCCESS ===");
            } else {
                logger.error("=== PIPELINE VALIDATION FAILED ===");
            }
            
        } catch (Exception e) {
            validationResults.put("PIPELINE_COMPLETE", false);
            validationResults.put("PIPELINE_ERROR", e.getMessage());
            logger.error("PIPELINE_VALIDATION_ERROR: {}", e.getMessage());
        }
        
        return validationResults;
    }
}
