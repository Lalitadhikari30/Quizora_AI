

package com.quizora.controller;

import com.quizora.dto.QuizResponse;
import com.quizora.service.FileUploadService;
import com.quizora.service.SupabaseService;
import com.quizora.service.ContentExtractionService;
import com.quizora.service.QuizService;
import com.quizora.service.ExtractedContentService;
import com.quizora.entity.ExtractedContent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin(origins = {"http://localhost:3000"})
public class FileUploadController {

    private static final Logger logger =
            LoggerFactory.getLogger(FileUploadController.class);

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private SupabaseService supabaseService;

    @Autowired
    private ContentExtractionService contentExtractionService;

    @Autowired
    private QuizService quizService;
    
    @Autowired
    private ExtractedContentService extractedContentService;


    /* =====================================================
       NORMAL FILE UPLOAD
       ===================================================== */
    @PostMapping("/file")
    public ResponseEntity<?> uploadFile(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {

        try {
            String userId = (authentication != null && authentication.getName() != null)
                    ? authentication.getName()
                    : "dev-user";

            String publicUrl =
                    fileUploadService.uploadFile(file, userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("publicUrl", publicUrl);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Upload failed", e);
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }


    /* =====================================================
       NEW ARCHITECTURE: Upload → Extract → Store → Return ID
       ===================================================== */
    @PostMapping("/extract")
    public ResponseEntity<?> uploadAndExtract(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {

        try {
            String userId = authentication.getName();
            
            logger.info("=== FILE UPLOAD AND EXTRACTION START ===");
            logger.info("User ID: {}", userId);
            logger.info("File: {}", file.getOriginalFilename());

            // Upload, extract, and store - NO Gemini call yet
            ExtractedContent extractedContent = extractedContentService.uploadAndExtract(file, userId);
            
            logger.info("=== FILE UPLOAD AND EXTRACTION SUCCESS ===");
            logger.info("ExtractedContent ID: {}", extractedContent.getId());
            logger.info("Extracted text length: {} characters", extractedContent.getExtractedText().length());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("extractedContentId", extractedContent.getId());
            response.put("fileName", extractedContent.getFileName());
            response.put("fileUrl", extractedContent.getFileUrl());
            response.put("extractedTextLength", extractedContent.getExtractedText().length());
            response.put("status", extractedContent.getStatus().toString());
            response.put("message", "File uploaded and text extracted successfully. Ready for quiz generation.");
            
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("FILE_UPLOAD_EXTRACTION_FAILED: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            errorResponse.put("message", "Failed to upload and extract file");
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /* =====================================================
       QUIZ GENERATION PIPELINE
       ===================================================== */
    @PostMapping("/quiz")
    public ResponseEntity<?> uploadFileForQuiz(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "questionCount", required = false, defaultValue = "10") Integer questionCount,
            @RequestParam(value = "difficulty", required = false, defaultValue = "INTERMEDIATE") String difficulty,
            @RequestParam(value = "timePerQuestion", required = false, defaultValue = "60") Integer timePerQuestion,
            @RequestParam(value = "timeLimit", required = false) Integer timeLimit,
            Authentication authentication) {

        try {
            String userId = (authentication != null && authentication.getName() != null)
                    ? authentication.getName()
                    : "dev-user";
            
            logger.info("=== QUIZ GENERATION START ===");
            logger.info("User ID: {}, QuestionCount: {}, Difficulty: {}, TimePerQ: {}s, TimeLimit: {}s",
                    userId, questionCount, difficulty, timePerQuestion, timeLimit);

            /* 1️⃣ Upload file */
            String publicUrl =
                    fileUploadService.uploadFile(file, userId);

            /* 2️⃣ Extract text */
            String extractedText =
                    contentExtractionService.extractContent(file);

            logger.info("Text extracted length: {}", extractedText.length());

            int calcTotalTime = (timeLimit != null && timeLimit > 0) ? timeLimit : (questionCount * timePerQuestion);

            /* 3️⃣ Generate quiz using Gemini with custom settings */
            QuizResponse quiz =
                    quizService.generateQuizFromText(
                            userId,
                            extractedText,
                            file.getOriginalFilename(),
                            questionCount != null ? questionCount : 10,
                            difficulty != null ? difficulty : "INTERMEDIATE",
                            calcTotalTime,
                            timePerQuestion != null ? timePerQuestion : 60
                    );

            logger.info("Quiz generated successfully with {} questions", quiz.getQuestions() != null ? quiz.getQuestions().size() : 0);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Quiz generated successfully");
            response.put("quiz", quiz);
            response.put("fileUrl", publicUrl);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Quiz generation failed", e);
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Quiz generation failed: " + e.getMessage()));
        }
    }
}
