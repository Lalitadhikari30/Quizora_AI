package com.quizora.controller;

import com.quizora.entity.ExtractedContent;
import com.quizora.entity.Quiz;
import com.quizora.entity.Question;
import com.quizora.service.*;
import com.quizora.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class QuizProcessingController {
    
    private static final Logger logger = LoggerFactory.getLogger(QuizProcessingController.class);
    
    private final SupabaseStorageService supabaseStorageService;
    private final InMemoryTextExtractionService textExtractionService;
    private final ExtractedContentUpdateService extractedContentService;
    private final GeminiService geminiService;
    private final QuizManagementService quizService;
    private final com.quizora.repository.ExtractedContentRepository extractedContentRepository;
    private final com.quizora.repository.QuizRepository quizRepository;
    private final com.quizora.repository.QuestionRepository questionRepository;
    
    public QuizProcessingController(
            SupabaseStorageService supabaseStorageService,
            InMemoryTextExtractionService textExtractionService,
            ExtractedContentUpdateService extractedContentService,
            GeminiService geminiService,
            QuizManagementService quizService,
            com.quizora.repository.ExtractedContentRepository extractedContentRepository,
            com.quizora.repository.QuizRepository quizRepository,
            com.quizora.repository.QuestionRepository questionRepository) {
        this.supabaseStorageService = supabaseStorageService;
        this.textExtractionService = textExtractionService;
        this.extractedContentService = extractedContentService;
        this.geminiService = geminiService;
        this.quizService = quizService;
        this.extractedContentRepository = extractedContentRepository;
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
    }
    
    @PostMapping("/process-file/{extractedContentId}")
    public ResponseEntity<?> processFile(@PathVariable Long extractedContentId) {
        try {
            logger.info("=== PROCESSING FILE FOR QUIZ GENERATION ===");
            logger.info("Extracted Content ID: {}", extractedContentId);
            
            // Step 1: Fetch extracted content
            ExtractedContent extractedContent = extractedContentRepository.findById(extractedContentId)
                    .orElseThrow(() -> new RuntimeException("Extracted content not found: " + extractedContentId));
            
            logger.info("Found extracted content: {}", extractedContent.getFileName());
            logger.info("File URL: {}", extractedContent.getFileUrl());
            
            // Step 2: Fetch file from Supabase
            byte[] fileBytes = supabaseStorageService.fetchFile(extractedContent.getFileUrl());
            logger.info("Fetched file from Supabase: {} bytes", fileBytes.length);
            
            // Step 3: Extract text in-memory
            String extractedText = textExtractionService.extractText(fileBytes, extractedContent.getFileName());
            logger.info("Extracted text: {} characters", extractedText.length());
            
            // Step 4: Update extracted text in database
            extractedContentService.updateExtractedText(extractedContentId, extractedText);
            logger.info("Updated extracted text in database");
            
            // Step 5: Generate quiz with Gemini
            var geminiResponse = geminiService.generateQuiz(extractedText);
            logger.info("Generated {} questions from Gemini", geminiResponse.questions().size());
            
            // Step 6: Save quiz to database
            Quiz quiz = quizService.saveQuiz(extractedContentId, geminiResponse.questions());
            logger.info("Saved quiz with ID: {}", quiz.getId());
            
            // Step 7: Return response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Quiz generated successfully");
            response.put("quizId", quiz.getId());
            response.put("questionCount", geminiResponse.questions().size());
            response.put("status", "QUIZ_GENERATED");
            
            logger.info("=== FILE PROCESSING COMPLETE ===");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Failed to process file: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to process file");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    @GetMapping("/quiz/{quizId}")
    public ResponseEntity<?> getQuiz(@PathVariable Long quizId) {
        try {
            logger.info("Fetching quiz with ID: {}", quizId);
            
            Quiz quiz = quizRepository.findById(quizId)
                    .orElseThrow(() -> new RuntimeException("Quiz not found: " + quizId));
            
            List<Question> questions = questionRepository.findByQuizId(quizId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("quizId", quiz.getId());
            response.put("sourceContent", quiz.getSourceContent());
            response.put("questions", questions.stream().map(this::mapQuestionToResponse).toList());
            response.put("questionCount", questions.size());
            response.put("status", "READY");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Failed to fetch quiz: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch quiz");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    private Map<String, Object> mapQuestionToResponse(Question question) {
        Map<String, Object> questionResponse = new HashMap<>();
        questionResponse.put("id", question.getId());
        questionResponse.put("question", question.getQuestionText());
        questionResponse.put("options", question.getOptions());
        questionResponse.put("correctAnswer", question.getCorrectAnswer());
        return questionResponse;
    }
}
