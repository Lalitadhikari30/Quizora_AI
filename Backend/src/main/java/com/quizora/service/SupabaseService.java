package com.quizora.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class SupabaseService {
    
    private static final Logger logger = LoggerFactory.getLogger(SupabaseService.class);
    
    @Value("${supabase.url}")
    private String supabaseUrl;
    
    @Value("${supabase.service.key}")
    private String supabaseServiceKey;
    
    @Value("${supabase.storage.bucket}")
    private String storageBucket;
    
    private final WebClient webClient;

    public SupabaseService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public String uploadFile(byte[] fileData, String fileName, String userId, String bucketName) {
        try {
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString());
            String objectPath = userId + "/" + encodedFileName;
            
            String uploadUrl = String.format("%s/storage/v1/object/%s/%s", supabaseUrl, bucketName, objectPath);
            
            Map<String, Object> body = new HashMap<>();
            body.put("data", fileData);
            
            String response = webClient.post()
                    .uri(uploadUrl)
                    .header("Authorization", "Bearer " + supabaseServiceKey)
                    .header("Content-Type", "application/octet-stream")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            
            String publicUrl = String.format("%s/storage/v1/object/public/%s/%s", supabaseUrl, bucketName, objectPath);
            
            logger.info("Uploaded file to Supabase storage: {}", publicUrl);
            return publicUrl;
            
        } catch (Exception e) {
            logger.error("Failed to upload file to Supabase", e);
            throw new RuntimeException("Failed to upload file: " + e.getMessage());
        }
    }

    public byte[] downloadFile(String publicUrl) {
        try {
            String downloadUrl = publicUrl.replace("/storage/v1/object/public/", "/storage/v1/object/");
            
            byte[] fileData = webClient.get()
                    .uri(downloadUrl)
                    .header("Authorization", "Bearer " + supabaseServiceKey)
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .block();
            
            logger.info("Downloaded file from Supabase: {}", publicUrl);
            return fileData;
            
        } catch (Exception e) {
            logger.error("Failed to download file from Supabase", e);
            throw new RuntimeException("Failed to download file: " + e.getMessage());
        }
    }

    public void deleteFile(String publicUrl) {
        try {
            String deleteUrl = publicUrl.replace("/storage/v1/object/public/", "/storage/v1/object/");
            
            webClient.delete()
                    .uri(deleteUrl)
                    .header("Authorization", "Bearer " + supabaseServiceKey)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
            
            logger.info("Deleted file from Supabase: {}", publicUrl);
            
        } catch (Exception e) {
            logger.error("Failed to delete file from Supabase", e);
            throw new RuntimeException("Failed to delete file: " + e.getMessage());
        }
    }

    public boolean validateUser(String userId, String token) {
        try {
            String userUrl = String.format("%s/auth/v1/user", supabaseUrl);
            
            Map<String, Object> response = webClient.get()
                    .uri(userUrl)
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            
            if (response != null && response.containsKey("id")) {
                String responseUserId = response.get("id").toString();
                boolean isValid = userId.equals(responseUserId);
                
                logger.info("User validation result for {}: {}", userId, isValid);
                return isValid;
            }
            
            return false;
            
        } catch (Exception e) {
            logger.error("Failed to validate user", e);
            return false;
        }
    }
}
