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
    
    @Value("${SUPABASE_URL}")
    private String supabaseUrl;
    
    @Value("${SUPABASE_SERVICE_KEY}")
    private String supabaseServiceKey;
    
    @Value("${SUPABASE_STORAGE_BUCKET}")
    private String storageBucket;
    
    private final WebClient webClient;

    public SupabaseService() {
        this.webClient = WebClient.builder().build();
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

    public Map<String, Object> registerUser(String email, String password, String firstName, String lastName) {
        try {
            String signUpUrl = String.format("%s/auth/v1/signup", supabaseUrl);
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("email", email);
            requestBody.put("password", password);
            requestBody.put("data", Map.of(
                "first_name", firstName,
                "last_name", lastName,
                "name", firstName + " " + lastName
            ));
            
            Map<String, Object> response = webClient.post()
                    .uri(signUpUrl)
                    .header("Authorization", "Bearer " + supabaseServiceKey)
                    .header("apikey", supabaseServiceKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            
            logger.info("User registered successfully with Supabase: {}", email);
            return response;
            
        } catch (Exception e) {
            logger.error("Failed to register user with Supabase", e);
            throw new RuntimeException("Registration failed: " + e.getMessage());
        }
    }

    public Map<String, Object> authenticateUser(String email, String password) {
        try {
            String signInUrl = String.format("%s/auth/v1/token?grant_type=password", supabaseUrl);
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("email", email);
            requestBody.put("password", password);
            
            Map<String, Object> response = webClient.post()
                    .uri(signInUrl)
                    .header("Authorization", "Bearer " + supabaseServiceKey)
                    .header("apikey", supabaseServiceKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            
            // Get user info
            String token = (String) response.get("access_token");
            Map<String, Object> userInfo = getUserInfo(token);
            
            // Combine token and user info
            Map<String, Object> authResult = new HashMap<>(response);
            authResult.put("user", userInfo);
            authResult.put("token", token);
            
            logger.info("User authenticated successfully with Supabase: {}", email);
            return authResult;
            
        } catch (Exception e) {
            logger.error("Failed to authenticate user with Supabase", e);
            throw new RuntimeException("Authentication failed: " + e.getMessage());
        }
    }

    public Map<String, Object> getUserInfo(String token) {
        try {
            String userUrl = String.format("%s/auth/v1/user", supabaseUrl);
            
            Map<String, Object> response = webClient.get()
                    .uri(userUrl)
                    .header("Authorization", "Bearer " + token)
                    .header("apikey", supabaseServiceKey)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            
            logger.info("Retrieved user info from Supabase");
            return response;
            
        } catch (Exception e) {
            logger.error("Failed to get user info from Supabase", e);
            throw new RuntimeException("Failed to get user info: " + e.getMessage());
        }
    }

    public void logoutUser(String token) {
        try {
            String logoutUrl = String.format("%s/auth/v1/logout", supabaseUrl);
            
            webClient.post()
                    .uri(logoutUrl)
                    .header("Authorization", "Bearer " + token)
                    .header("apikey", supabaseServiceKey)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
            
            logger.info("User logged out successfully from Supabase");
            
        } catch (Exception e) {
            logger.error("Failed to logout user from Supabase", e);
            throw new RuntimeException("Logout failed: " + e.getMessage());
        }
    }

    public void resetPassword(String email) {
        try {
            String resetUrl = String.format("%s/auth/v1/recover", supabaseUrl);
            
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("email", email);
            
            webClient.post()
                    .uri(resetUrl)
                    .header("Authorization", "Bearer " + supabaseServiceKey)
                    .header("apikey", supabaseServiceKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
            
            logger.info("Password reset email sent for: {}", email);
            
        } catch (Exception e) {
            logger.error("Failed to send password reset email", e);
            throw new RuntimeException("Failed to send reset email: " + e.getMessage());
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
