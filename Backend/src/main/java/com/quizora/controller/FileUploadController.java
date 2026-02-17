package com.quizora.controller;

import com.quizora.service.FileUploadService;
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
    
    private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);
    
    @Autowired
    private FileUploadService fileUploadService;

    @PostMapping("/pdf")
    public ResponseEntity<?> uploadPdf(@RequestParam("file") MultipartFile file, Authentication authentication) {
        try {
            String userId = authentication.getName();
            String publicUrl = fileUploadService.uploadPdfToSupabase(file, userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "PDF uploaded successfully");
            response.put("publicUrl", publicUrl);
            response.put("fileName", file.getOriginalFilename());
            
            logger.info("PDF uploaded: {} for user: {}", file.getOriginalFilename(), userId);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Failed to upload PDF", e);
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to upload PDF: " + e.getMessage()));
        }
    }

    @PostMapping("/file")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file, Authentication authentication) {
        try {
            String userId = authentication.getName();
            String publicUrl = fileUploadService.uploadFile(file, userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "File uploaded successfully");
            response.put("publicUrl", publicUrl);
            response.put("fileName", file.getOriginalFilename());
            
            logger.info("File uploaded: {} for user: {}", file.getOriginalFilename(), userId);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Failed to upload file", e);
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to upload file: " + e.getMessage()));
        }
    }
}
