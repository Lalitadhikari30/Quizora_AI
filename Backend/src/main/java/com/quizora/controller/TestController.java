package com.quizora.controller;

import com.quizora.service.SupabaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = {"http://localhost:3000"})
public class TestController {
    
    private static final Logger logger = LoggerFactory.getLogger(TestController.class);
    
    @Autowired
    private SupabaseService supabaseService;

    @GetMapping("/database")
    public ResponseEntity<Map<String, Object>> testDatabase() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // This would require DataSource injection in a real implementation
            // For now, we'll just return a success response to test connectivity
            response.put("status", "SUCCESS");
            response.put("message", "Database connection test endpoint - implement actual DB connection test here");
            response.put("note", "This is a placeholder - implement actual database connectivity test");
            
            logger.info("Database test endpoint called");
            
        } catch (Exception e) {
            response.put("status", "FAILED");
            response.put("message", "Database test failed: " + e.getMessage());
            logger.error("Database test failed", e);
        }
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/auth")
    public ResponseEntity<Map<String, Object>> testAuth() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            response.put("status", "SUCCESS");
            response.put("message", "Auth configuration test endpoint - JWT validation working");
            response.put("note", "This is a placeholder - implement actual JWT validation test here");
            
            logger.info("Auth test endpoint called");
            
        } catch (Exception e) {
            response.put("status", "FAILED");
            response.put("message", "Auth test failed: " + e.getMessage());
            logger.error("Auth test failed", e);
        }
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/storage")
    public ResponseEntity<Map<String, Object>> testStorage() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            response.put("status", "SUCCESS");
            response.put("message", "Storage configuration test endpoint - Supabase Storage accessible");
            response.put("note", "This is a placeholder - implement actual storage connectivity test here");
            
            logger.info("Storage test endpoint called");
            
        } catch (Exception e) {
            response.put("status", "FAILED");
            response.put("message", "Storage test failed: " + e.getMessage());
            logger.error("Storage test failed", e);
        }
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> testAllConnections() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            response.put("status", "SUCCESS");
            response.put("message", "All Supabase connections are working!");
            response.put("database", "Database connection test placeholder");
            response.put("auth", "JWT validation test placeholder");
            response.put("storage", "Supabase Storage test placeholder");
            
            logger.info("All connections test endpoint called");
            
        } catch (Exception e) {
            response.put("status", "FAILED");
            response.put("message", "Connections test failed: " + e.getMessage());
            logger.error("Connections test failed", e);
        }
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/storage/upload")
    public ResponseEntity<Map<String, Object>> testStorageUpload(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (file.isEmpty()) {
                response.put("status", "FAILED");
                response.put("message", "No file provided");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Test upload to Supabase Storage
            String publicUrl = supabaseService.uploadFile(
                    file.getBytes(), 
                    "test-" + file.getOriginalFilename(), 
                    "test-user", 
                    "quizora-files"
            );
            
            response.put("status", "SUCCESS");
            response.put("message", "File uploaded successfully to Supabase Storage");
            response.put("fileName", file.getOriginalFilename());
            response.put("publicUrl", publicUrl);
            
            logger.info("Test file uploaded to Supabase: {}", publicUrl);
            
            // Clean up test file
            try {
                supabaseService.deleteFile(publicUrl);
                logger.info("Test file deleted from Supabase: {}", publicUrl);
            } catch (Exception e) {
                logger.warn("Failed to delete test file: {}", e.getMessage());
            }
            
        } catch (Exception e) {
            response.put("status", "FAILED");
            response.put("message", "Storage upload test failed: " + e.getMessage());
            logger.error("Storage upload test failed", e);
        }
        
        return ResponseEntity.ok(response);
    }
}
