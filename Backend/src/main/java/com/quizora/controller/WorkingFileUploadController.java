package com.quizora.controller;

import com.quizora.service.SupabaseService;
import com.quizora.service.ContentExtractionService;
import com.quizora.entity.ExtractedContent;
import com.quizora.repository.ExtractedContentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class WorkingFileUploadController {
    
    private static final Logger logger = LoggerFactory.getLogger(WorkingFileUploadController.class);
    
    private final SupabaseService supabaseService;
    private final ContentExtractionService textExtractionService;
    private final ExtractedContentRepository extractedContentRepository;
    
    public WorkingFileUploadController(
            SupabaseService supabaseService,
            ContentExtractionService textExtractionService,
            ExtractedContentRepository extractedContentRepository) {
        this.supabaseService = supabaseService;
        this.textExtractionService = textExtractionService;
        this.extractedContentRepository = extractedContentRepository;
    }
    
    @PostMapping
    public ResponseEntity<?> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        try {
            logger.info("=== FILE UPLOAD START ===");
            logger.info("File: {}", file.getOriginalFilename());
            logger.info("Size: {} bytes", file.getSize());
            logger.info("Type: {}", file.getContentType());
            
            // Extract user ID
            String userId = extractUserId(authHeader);
            logger.info("User ID: {}", userId);
            
            // Step 1: Upload to Supabase
            logger.info("Step 1: Uploading to Supabase Storage");
            String storageUrl = supabaseService.uploadFile(
                    file.getBytes(), 
                    file.getOriginalFilename(), 
                    userId, 
                    "quizora-files"
            );
            logger.info("✅ File uploaded to: {}", storageUrl);
            
            // Step 2: Extract text
            logger.info("Step 2: Extracting text");
            String extractedText = textExtractionService.extractContent(file);
            logger.info("✅ Extracted {} characters", extractedText.length());
            
            // Step 3: Save to database
            logger.info("Step 3: Saving to database");
            ExtractedContent extractedContent = new ExtractedContent(
                    userId, // userId
                    file.getOriginalFilename(), // fileName
                    storageUrl, // fileUrl
                    extractedText // extractedText
            );
            extractedContent = extractedContentRepository.save(extractedContent);
            logger.info("✅ Saved with ID: {}", extractedContent.getId());
            
            logger.info("=== FILE UPLOAD COMPLETE ===");
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "File uploaded and text extracted successfully");
            response.put("fileUrl", storageUrl);
            response.put("fileName", file.getOriginalFilename());
            response.put("extractedTextLength", extractedText.length());
            response.put("extractedContentId", extractedContent.getId());
            response.put("status", "EXTRACTED");
            
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            logger.error("❌ File upload failed: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "File upload failed");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            logger.error("❌ Unexpected error: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Internal server error");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    private String extractUserId(String authHeader) {
        if (authHeader == null || authHeader.isEmpty()) {
            return "anonymous-user";
        }
        
        if (authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (token.startsWith("dev-token-")) {
                return "dev-user";
            }
        }
        
        return "authenticated-user";
    }
}
