package com.quizora.controller;

import com.quizora.dto.QuizResponse;
import com.quizora.entity.ExtractedContent;
import com.quizora.service.ExtractedContentService;
import com.quizora.service.QuizService;
import com.quizora.service.AiIntegrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/quizzes")
@CrossOrigin(origins = {"http://localhost:3000"})
public class QuizGenerationController {
    
    private static final Logger logger = LoggerFactory.getLogger(QuizGenerationController.class);
    
    @Autowired
    private ExtractedContentService extractedContentService;
    
    @Autowired
    private QuizService quizService;
    
    @Autowired
    private AiIntegrationService aiIntegrationService;
    
    /**
     * Generate quiz from stored extracted content
     * POST /api/quizzes/generate-from-extraction/{extractedContentId}
     */
    @PostMapping("/generate-from-extraction/{extractedContentId}")
    public ResponseEntity<?> generateQuizFromExtraction(
            @PathVariable Long extractedContentId,
            Authentication authentication) {
        
        try {
            String userId = authentication.getName();
            
            logger.info("=== QUIZ GENERATION FROM EXTRACTION START ===");
            logger.info("User ID: {}", userId);
            logger.info("ExtractedContent ID: {}", extractedContentId);
            
            // Step 1: Load ExtractedContent by ID and user (security check)
            Optional<ExtractedContent> contentOpt = extractedContentService.getExtractedContent(extractedContentId, userId);
            if (contentOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            ExtractedContent extractedContent = contentOpt.get();
            logger.info("Loaded extracted content: fileName={}, status={}", extractedContent.getFileName(), extractedContent.getStatus());
            
            // Step 2: Validate extracted text is not empty
            String extractedText = extractedContent.getExtractedText();
            if (extractedText == null || extractedText.trim().isEmpty()) {
                String errorMsg = "EXTRACTION_EMPTY: Extracted text is empty for content ID: " + extractedContentId;
                logger.error(errorMsg);
                extractedContentService.markAsFailed(extractedContentId, errorMsg);
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", errorMsg,
                    "message", "Cannot generate quiz from empty extracted text"
                ));
            }
            
            logger.info("Validated extracted text: {} characters", extractedText.length());
            
            // Step 3: Prepare text for Gemini (validate and truncate if needed)
            String geminiText = extractedContentService.validateAndPrepareTextForGemini(extractedText);
            logger.info("Text prepared for Gemini: {} characters", geminiText.length());
            
            // Step 4: Call Gemini AI to generate quiz
            logger.info("Step 4: Sending text to Gemini AI...");
            var quizQuestions = aiIntegrationService.generateQuizFromContent(geminiText, "medium", "general", 5);
            logger.info("Gemini response received: {} questions generated", quizQuestions.size());
            
            // Step 5: Save quiz to database (generateQuizFromText already saves to DB)
            logger.info("Step 5: Saving quiz to database...");
            QuizResponse savedQuiz = quizService.generateQuizFromText(userId, geminiText, extractedContent.getFileName());
            logger.info("Quiz saved successfully: ID {}", savedQuiz.getId());
            
            // Step 6: Update ExtractedContent status to AI_GENERATED
            extractedContentService.markAsAiGenerated(extractedContentId, savedQuiz.getId());
            logger.info("ExtractedContent marked as AI generated");
            
            logger.info("=== QUIZ GENERATION FROM EXTRACTION SUCCESS ===");
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Quiz generated successfully from extracted content");
            response.put("quiz", savedQuiz);
            response.put("extractedContentId", extractedContentId);
            response.put("fileName", extractedContent.getFileName());
            response.put("extractedTextLength", extractedText.length());
            response.put("questionsGenerated", quizQuestions.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("QUIZ_GENERATION_FROM_EXTRACTION_FAILED: {}", e.getMessage(), e);
            
            // Mark as failed
            try {
                extractedContentService.markAsFailed(extractedContentId, e.getMessage());
            } catch (Exception markFailedError) {
                logger.error("FAILED_TO_MARK_AS_FAILED: {}", markFailedError.getMessage());
            }
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            errorResponse.put("message", "Failed to generate quiz from extracted content");
            errorResponse.put("extractedContentId", extractedContentId);
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * Retry quiz generation for failed extraction
     * POST /api/quizzes/retry-generation/{extractedContentId}
     */
    @PostMapping("/retry-generation/{extractedContentId}")
    public ResponseEntity<?> retryQuizGeneration(
            @PathVariable Long extractedContentId,
            Authentication authentication) {
        
        try {
            String userId = authentication.getName();
            
            logger.info("=== RETRY QUIZ GENERATION START ===");
            logger.info("User ID: {}", userId);
            logger.info("ExtractedContent ID: {}", extractedContentId);
            
            // Check if extraction exists and belongs to user
            Optional<ExtractedContent> contentOpt = extractedContentService.getExtractedContent(extractedContentId, userId);
            if (contentOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            ExtractedContent extractedContent = contentOpt.get();
            
            // Only allow retry for failed extractions
            if (!extractedContent.isFailed()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", "RETRY_NOT_ALLOWED",
                    "message", "Can only retry failed extractions. Current status: " + extractedContent.getStatus()
                ));
            }
            
            logger.info("Retrying generation for failed extraction: {}", extractedContent.getFileName());
            
            // Reset status to EXTRACTED before retrying
            extractedContent.setStatus(ExtractedContent.Status.EXTRACTED);
            // Note: We would need to save this, but let's proceed with generation
            
            // Call the same generation logic
            return generateQuizFromExtraction(extractedContentId, authentication);
            
        } catch (Exception e) {
            logger.error("RETRY_QUIZ_GENERATION_FAILED: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            errorResponse.put("message", "Failed to retry quiz generation");
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * Get user's extracted contents
     * GET /api/quizzes/extracted-contents
     */
    @GetMapping("/extracted-contents")
    public ResponseEntity<?> getUserExtractedContents(Authentication authentication) {
        try {
            String userId = authentication.getName();
            
            logger.info("Getting extracted contents for user: {}", userId);
            
            var extractedContents = extractedContentService.getUserExtractedContents(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("extractedContents", extractedContents);
            response.put("count", extractedContents.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("GET_EXTRACTED_CONTENTS_FAILED: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * Get user's failed extractions for retry
     * GET /api/quizzes/failed-extractions
     */
    @GetMapping("/failed-extractions")
    public ResponseEntity<?> getUserFailedExtractions(Authentication authentication) {
        try {
            String userId = authentication.getName();
            
            logger.info("Getting failed extractions for user: {}", userId);
            
            var failedExtractions = extractedContentService.getUserFailedExtractions(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("failedExtractions", failedExtractions);
            response.put("count", failedExtractions.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("GET_FAILED_EXTRACTIONS_FAILED: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
