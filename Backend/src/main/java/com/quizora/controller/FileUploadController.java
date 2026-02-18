package com.quizora.controller;

import com.quizora.service.FileUploadService;
import com.quizora.service.SupabaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin(origins = {"http://localhost:3000"})
public class FileUploadController {
    
    private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);
    
    @Autowired
    private FileUploadService fileUploadService;
    
    @Autowired
    private SupabaseService supabaseService;

    @PostMapping("/pdf")
    public ResponseEntity<?> uploadPdf(@RequestParam("file") MultipartFile file, @RequestHeader("Authorization") String authHeader) {
        try {
            // Extract user from JWT token
            String token = authHeader.replace("Bearer ", "");
            Map<String, Object> user = supabaseService.validateTokenAndGetUser(token);
            String userId = (String) user.get("id");
            
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
    public ResponseEntity<?> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            HttpServletRequest request) {
        
        logger.info("Received file upload request: {}", file.getOriginalFilename());
        logger.info("Content-Type: {}", file.getContentType());
        logger.info("File size: {} bytes", file.getSize());
        
        try {
            // Validate file
            if (file.isEmpty()) {
                logger.warn("Empty file received");
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", "File is empty",
                    "message", "Please select a file to upload"
                ));
            }
            
            // Validate file size (50MB limit)
            if (file.getSize() > 50 * 1024 * 1024) {
                logger.warn("File too large: {} bytes", file.getSize());
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", "File too large",
                    "message", "File size exceeds 50MB limit"
                ));
            }
            
            // Extract and validate user from JWT token or development token
            String userId = null;
            String devToken = (String) request.getAttribute("dev-token");
            
            if (devToken != null) {
                // Handle development token
                try {
                    Map<String, Object> user = supabaseService.validateTokenAndGetUser(devToken);
                    userId = (String) user.get("id");
                    logger.info("Development token authenticated user: {}", userId);
                } catch (Exception e) {
                    logger.error("Development token validation failed: {}", e.getMessage());
                    return ResponseEntity.status(401).body(Map.of(
                        "success", false,
                        "error", "Authentication failed",
                        "message", "Invalid development token"
                    ));
                }
            } else if (authHeader != null && authHeader.startsWith("Bearer ")) {
                // Handle JWT token
                try {
                    String token = authHeader.replace("Bearer ", "");
                    Map<String, Object> user = supabaseService.validateTokenAndGetUser(token);
                    userId = (String) user.get("id");
                    logger.info("JWT authenticated user: {}", userId);
                } catch (Exception e) {
                    logger.error("JWT validation failed: {}", e.getMessage());
                    return ResponseEntity.status(401).body(Map.of(
                        "success", false,
                        "error", "Authentication failed",
                        "message", "Invalid or expired token"
                    ));
                }
            } else {
                logger.warn("No authorization header provided");
                return ResponseEntity.status(401).body(Map.of(
                    "success", false,
                    "error", "Authentication required",
                    "message", "Please login to upload files"
                ));
            }
            
            // Upload file
            String publicUrl = fileUploadService.uploadFile(file, userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "File uploaded successfully");
            response.put("publicUrl", publicUrl);
            response.put("fileName", file.getOriginalFilename());
            response.put("fileSize", file.getSize());
            response.put("contentType", file.getContentType());
            response.put("userId", userId);
            
            logger.info("File uploaded successfully: {} for user: {}", file.getOriginalFilename(), userId);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Failed to upload file: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "error", "Upload failed",
                "message", "Failed to upload file: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/quiz")
    public ResponseEntity<?> uploadFileForQuiz(@RequestParam("file") MultipartFile file, @RequestHeader("Authorization") String authHeader) {
        try {
            // Extract user from JWT token
            String token = authHeader.replace("Bearer ", "");
            Map<String, Object> user = supabaseService.validateTokenAndGetUser(token);
            String userId = (String) user.get("id");
            
            String publicUrl = fileUploadService.uploadFile(file, userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "File uploaded successfully for quiz generation");
            response.put("publicUrl", publicUrl);
            response.put("fileName", file.getOriginalFilename());
            response.put("fileSize", file.getSize());
            response.put("contentType", file.getContentType());
            
            logger.info("Quiz file uploaded: {} for user: {}", file.getOriginalFilename(), userId);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Failed to upload quiz file", e);
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to upload quiz file: " + e.getMessage()));
        }
    }
}
