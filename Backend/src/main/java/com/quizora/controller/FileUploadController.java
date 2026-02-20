

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
            @RequestHeader("Authorization") String authHeader) {

        try {

            String token = authHeader.replace("Bearer ", "");
            Map<String, Object> user =
                    supabaseService.validateTokenAndGetUser(token);

            String userId = (String) user.get("id");

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
            Authentication authentication) {

        try {
            String userId = authentication.getName();
            
            logger.info("=== QUIZ GENERATION START ===");
            logger.info("User ID: {}", userId);

            /* 1️⃣ Upload file */
            String publicUrl =
                    fileUploadService.uploadFile(file, userId);

            /* 2️⃣ Extract text */
            String extractedText =
                    contentExtractionService.extractContent(file);

            logger.info("Text extracted length: {}", extractedText.length());

            /* 3️⃣ Generate quiz using Gemini */
            QuizResponse quiz =
                    quizService.generateQuizFromText(
                            userId,
                            extractedText,
                            file.getOriginalFilename()
                    );

            logger.info("Quiz generated successfully");

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
