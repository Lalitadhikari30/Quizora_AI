package com.quizora.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileUploadService {
    
    private static final Logger logger = LoggerFactory.getLogger(FileUploadService.class);
    
    @Value("${upload.directory}")
    private String uploadDirectory;
    
    @Autowired
    private SupabaseService supabaseService;

    public String uploadFile(MultipartFile file, String userId) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Please select a file to upload");
            }
            
            // Upload to Supabase Storage
            String publicUrl = supabaseService.uploadFile(
                    file.getBytes(), 
                    file.getOriginalFilename(), 
                    userId, 
                    "quizora-files"
            );
            
            logger.info("File uploaded successfully to Supabase: {}", publicUrl);
            return publicUrl;
            
        } catch (Exception e) {
            logger.error("Failed to upload file", e);
            throw new RuntimeException("Failed to upload file: " + e.getMessage());
        }
    }

    public String uploadPdfToSupabase(MultipartFile file, String userId) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Please select a PDF file to upload");
            }
            
            if (!file.getOriginalFilename().toLowerCase().endsWith(".pdf")) {
                throw new RuntimeException("Only PDF files are allowed");
            }
            
            // Upload to Supabase Storage
            String publicUrl = supabaseService.uploadFile(
                    file.getBytes(), 
                    file.getOriginalFilename(), 
                    userId, 
                    "quizora-files"
            );
            
            logger.info("PDF uploaded successfully to Supabase: {}", publicUrl);
            return publicUrl;
            
        } catch (Exception e) {
            logger.error("Failed to upload PDF", e);
            throw new RuntimeException("Failed to upload PDF: " + e.getMessage());
        }
    }

    public String saveToLocal(MultipartFile file) {
        try {
            Path uploadPath = Paths.get(uploadDirectory);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            Path filePath = uploadPath.resolve(file.getOriginalFilename());
            Files.copy(file.getInputStream(), filePath);
            
            logger.info("File saved locally: {}", filePath.toString());
            return filePath.toString();
            
        } catch (IOException e) {
            logger.error("Failed to save file locally", e);
            throw new RuntimeException("Failed to save file: " + e.getMessage());
        }
    }
}
