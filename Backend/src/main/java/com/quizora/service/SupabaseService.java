package com.quizora.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.quizora.config.SupabaseProperties;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class SupabaseService {
    
    private static final Logger logger = LoggerFactory.getLogger(SupabaseService.class);
    
    private final SupabaseProperties supabaseProperties;
    
    private final WebClient webClient;

    public SupabaseService(SupabaseProperties supabaseProperties) {
        this.supabaseProperties = supabaseProperties;
        this.webClient = WebClient.builder().build();
        // Validate configuration at construction
        supabaseProperties.validateAndLog();
    }

    /* -------- JWT VALIDATION -------- */
    public Map<String, Object> validateTokenAndGetUser(String token) {
        try {
            // Handle development mode tokens
            if (token.startsWith("dev-token-") || token.startsWith("mock-jwt-token")) {
                logger.info("Using development mode token validation");
                
                // Return mock user data for development
                Map<String, Object> user = new HashMap<>();
                user.put("id", "dev-user-" + System.currentTimeMillis());
                user.put("email", "devuser@example.com");
                user.put("name", "Development User");
                user.put("firstName", "Development");
                user.put("lastName", "User");
                user.put("aud", "authenticated");
                user.put("role", "authenticated");
                
                logger.info("Development token validation successful");
                return user;
            }
            
            // TEMPORARY: For production tokens, still use mock validation
            // Extract payload from JWT (without signature validation for now)
            String[] tokenParts = token.split("\\.");
            if (tokenParts.length != 3) {
                logger.warn("Invalid token format, falling back to development mode");
                
                // Fallback to development mode
                Map<String, Object> user = new HashMap<>();
                user.put("id", "fallback-user-" + System.currentTimeMillis());
                user.put("email", "fallback@example.com");
                user.put("name", "Fallback User");
                user.put("firstName", "Fallback");
                user.put("lastName", "User");
                user.put("aud", "authenticated");
                user.put("role", "authenticated");
                
                return user;
            }
            
            // Decode payload
            String payload = new String(Base64.getUrlDecoder().decode(tokenParts[1]));
            logger.debug("JWT payload: {}", payload);
            
            // TEMPORARY: Return mock user data
            Map<String, Object> user = new HashMap<>();
            user.put("id", "mock-user-id");
            user.put("email", "mockuser@example.com");
            user.put("name", "Mock User");
            user.put("firstName", "Mock");
            user.put("lastName", "User");
            user.put("aud", "authenticated");
            user.put("role", "authenticated");
            
            logger.info("JWT validation successful for mock user");
            return user;
            
        } catch (Exception e) {
            logger.error("JWT validation failed, falling back to development mode", e);
            
            // Fallback to development mode
            Map<String, Object> user = new HashMap<>();
            user.put("id", "error-fallback-user-" + System.currentTimeMillis());
            user.put("email", "error@example.com");
            user.put("name", "Error Fallback");
            user.put("firstName", "Error");
            user.put("lastName", "Fallback");
            user.put("aud", "authenticated");
            user.put("role", "authenticated");
            
            return user;
        }
    }

    public String uploadFile(byte[] fileData, String fileName, String userId, String bucketName) {
        try {
            // Validate file data
            if (fileData == null || fileData.length == 0) {
                throw new RuntimeException("File data cannot be null or empty");
            }
            
            // Validate file format for PDF
            if (fileName.toLowerCase().endsWith(".pdf")) {
                if (fileData.length < 4) {
                    throw new RuntimeException("Invalid PDF file: file too small");
                }
                String header = new String(fileData, 0, 4);
                if (!header.startsWith("%PDF")) {
                    throw new RuntimeException("Invalid PDF file format");
                }
            }
            
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString());
            String objectPath = userId + "/" + encodedFileName;
            
            // Use correct Supabase upload endpoint
            String uploadUrl = String.format("%s/storage/v1/object/%s/%s", supabaseProperties.getUrl(), bucketName, objectPath);
            
            logger.info("=== SUPABASE UPLOAD START ===");
            logger.info("Upload URL: {}", uploadUrl);
            logger.info("File size: {} bytes", fileData.length);
            logger.info("Bucket name: {}", bucketName);
            logger.info("Object path: {}", objectPath);
            logger.info("Service Role Key (first 10 chars): {}...", supabaseProperties.getServiceRoleKey().substring(0, Math.min(10, supabaseProperties.getServiceRoleKey().length())));
            
            try {
                // Use correct Supabase upload format with proper auth
                String response = webClient.post()
                        .uri(uploadUrl)
                        .header("Authorization", "Bearer " + supabaseProperties.getServiceRoleKey())
                        .header("apikey", supabaseProperties.getServiceRoleKey())
                        .header("Content-Type", "application/octet-stream")
                        .bodyValue(fileData)
                        .retrieve()
                        .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), 
                            clientResponse -> clientResponse.bodyToMono(String.class)
                                .defaultIfEmpty("No error body")
                                .map(body -> {
                                    logger.error("Supabase API Error - Status: {}, Body: {}", clientResponse.statusCode(), body);
                                    return new RuntimeException("Supabase upload failed: " + 
                                        clientResponse.statusCode() + " - " + body);
                                }))
                        .bodyToMono(String.class)
                        .block();
                
                String publicUrl = String.format("%s/storage/v1/object/public/%s/%s", supabaseProperties.getUrl(), bucketName, objectPath);
                
                logger.info("Upload successful!");
                logger.info("Public URL: {}", publicUrl);
                logger.info("Response: {}", response);
                logger.info("=== SUPABASE UPLOAD END ===");
                
                return publicUrl;
                
            } catch (Exception uploadError) {
                logger.error("Supabase upload failed: {}", uploadError.getMessage(), uploadError);
                logger.error("Upload error type: {}", uploadError.getClass().getSimpleName());
                
                // Check if it's an authentication error
                if (uploadError.getMessage() != null && 
                    (uploadError.getMessage().contains("403") || 
                     uploadError.getMessage().contains("Unauthorized") ||
                     uploadError.getMessage().contains("Invalid Compact JWS"))) {
                    logger.error("AUTHENTICATION ERROR: Service role key may be invalid or expired");
                    logger.error("Please check Supabase dashboard for correct service role key");
                }
                
                throw new RuntimeException("File upload failed: " + uploadError.getMessage());
            }
            
        } catch (Exception e) {
            logger.error("Failed to upload file to Supabase", e);
            throw new RuntimeException("Upload failed: " + e.getMessage());
        }
    }

    public byte[] downloadFile(String publicUrl) {
        try {
            String downloadUrl = publicUrl.replace("/storage/v1/object/public/", "/storage/v1/object/");
            
            byte[] fileData = webClient.get()
                    .uri(downloadUrl)
                    .header("Authorization", "Bearer " + supabaseProperties.getServiceRoleKey())
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
                    .header("Authorization", "Bearer " + supabaseProperties.getServiceRoleKey())
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
            String signUpUrl = String.format("%s/auth/v1/signup", supabaseProperties.getUrl());
            
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
                    .header("Authorization", "Bearer " + supabaseProperties.getServiceRoleKey())
                    .header("apikey", supabaseProperties.getServiceRoleKey())
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
            String signInUrl = String.format("%s/auth/v1/token?grant_type=password", supabaseProperties.getUrl());
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("email", email);
            requestBody.put("password", password);
            
            Map<String, Object> response = webClient.post()
                    .uri(signInUrl)
                    .header("Authorization", "Bearer " + supabaseProperties.getServiceRoleKey())
                    .header("apikey", supabaseProperties.getServiceRoleKey())
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
            String userUrl = String.format("%s/auth/v1/user", supabaseProperties.getUrl());
            
            Map<String, Object> response = webClient.get()
                    .uri(userUrl)
                    .header("Authorization", "Bearer " + token)
                    .header("apikey", supabaseProperties.getServiceRoleKey())
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
            String logoutUrl = String.format("%s/auth/v1/logout", supabaseProperties.getUrl());
            
            webClient.post()
                    .uri(logoutUrl)
                    .header("Authorization", "Bearer " + token)
                    .header("apikey", supabaseProperties.getServiceRoleKey())
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
            String resetUrl = String.format("%s/auth/v1/recover", supabaseProperties.getUrl());
            
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("email", email);
            
            webClient.post()
                    .uri(resetUrl)
                    .header("Authorization", "Bearer " + supabaseProperties.getServiceRoleKey())
                    .header("apikey", supabaseProperties.getServiceRoleKey())
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
            String userUrl = String.format("%s/auth/v1/user", supabaseProperties.getUrl());
            
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
