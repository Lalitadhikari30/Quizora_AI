package com.quizora.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.quizora.config.SupabaseProperties;

@Service
public class SupabaseStorageService {
    
    private static final Logger logger = LoggerFactory.getLogger(SupabaseStorageService.class);
    
    private final SupabaseProperties supabaseProperties;
    
    private final WebClient webClient;

    public SupabaseStorageService(SupabaseProperties supabaseProperties) {
        this.supabaseProperties = supabaseProperties;
        this.webClient = WebClient.builder().build();
    }
    
    public byte[] fetchFile(String storagePath) {
        try {
            logger.info("Fetching file from Supabase: {}", storagePath);
            
            String downloadUrl = String.format("%s/storage/v1/object/%s/%s", supabaseProperties.getUrl(), supabaseProperties.getBucketName(), storagePath);
            
            logger.info("Download URL: {}", downloadUrl);
            
            byte[] fileBytes = webClient.get()
                    .uri(downloadUrl)
                    .header("Authorization", "Bearer " + supabaseProperties.getServiceRoleKey())
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .block();
            
            logger.info("Successfully fetched file: {} bytes", fileBytes.length);
            return fileBytes;
            
        } catch (Exception e) {
            logger.error("Failed to fetch file from Supabase: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch file from Supabase: " + e.getMessage(), e);
        }
    }
}
