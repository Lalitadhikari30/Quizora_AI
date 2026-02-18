package com.quizora.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Service
public class FileUploadService {
    
    private static final Logger logger = LoggerFactory.getLogger(FileUploadService.class);
    
    @Autowired
    private SupabaseService supabaseService;
    
    @Value("${upload.directory}")
    private String uploadDirectory;
    
    public String uploadFile(MultipartFile file, String userId) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Please select a file to upload");
            }
            
            logger.info("Starting file upload: {} for user: {}", file.getOriginalFilename(), userId);
            logger.info("File size: {} bytes", file.getSize());
            
            try {
                // Try to upload to Supabase Storage first with existing bucket
                logger.info("Attempting Supabase upload with existing bucket...");
                String publicUrl = supabaseService.uploadFile(
                        file.getBytes(), 
                        file.getOriginalFilename(), 
                        userId, 
                        "quizora-files"  // Use existing Supabase bucket
                );
                
                logger.info("File uploaded successfully to Supabase: {}", publicUrl);
                return publicUrl;
            } catch (Exception supabaseError) {
                logger.warn("Supabase upload failed, falling back to local storage: {}", supabaseError.getMessage());
                logger.warn("Supabase error details: {}", supabaseError.toString());
                
                // Fallback to local storage
                logger.info("Attempting local storage fallback...");
                String localPath = saveToLocal(file);
                logger.info("File saved locally as fallback: {}", localPath);
                
                // Return a local URL that can be used for testing
                String localUrl = "http://localhost:8081/api/download/" + file.getOriginalFilename();
                logger.info("Returning local URL: {}", localUrl);
                return localUrl;
            }
            
        } catch (Exception e) {
            logger.error("Failed to upload file completely", e);
            throw new RuntimeException("Failed to upload file: " + e.getMessage());
        }
    }
    
    public String saveToLocal(MultipartFile file) {
        try {
            logger.info("Saving file locally to directory: {}", uploadDirectory);
            Path uploadPath = Paths.get(uploadDirectory);
            if (!Files.exists(uploadPath)) {
                logger.info("Creating upload directory: {}", uploadPath);
                Files.createDirectories(uploadPath);
            }
            
            Path filePath = uploadPath.resolve(file.getOriginalFilename());
            logger.info("Target file path: {}", filePath);
            
            Files.copy(file.getInputStream(), filePath);
            logger.info("File copied successfully to: {}", filePath);
            
            String absolutePath = filePath.toAbsolutePath().toString();
            logger.info("File saved locally with absolute path: {}", absolutePath);
            return absolutePath;
            
        } catch (IOException e) {
            logger.error("Failed to save file locally", e);
            throw new RuntimeException("Failed to save file: " + e.getMessage());
        }
    }
}
