package com.quizora.controller;

import com.quizora.service.SupabaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:3000"})
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    @Autowired
    private SupabaseService supabaseService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> registrationData) {
        try {
            String email = registrationData.get("email");
            String password = registrationData.get("password");
            String firstName = registrationData.get("firstName");
            String lastName = registrationData.get("lastName");
            
            logger.info("Registration attempt for email: {}", email);
            
            // TEMPORARY: Mock successful registration for testing
            Map<String, Object> mockResponse = new HashMap<>();
            mockResponse.put("id", "mock-user-id");
            mockResponse.put("email", email);
            mockResponse.put("first_name", firstName);
            mockResponse.put("last_name", lastName);
            mockResponse.put("created_at", java.time.LocalDateTime.now().toString());
            
            logger.info("Mock registration successful for: {}", email);
            return ResponseEntity.ok(mockResponse);
            
        } catch (Exception e) {
            logger.error("Failed to register user", e);
            return ResponseEntity.badRequest().body(Map.of("error", "Registration failed: " + e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData) {
        try {
            String email = loginData.get("email");
            String password = loginData.get("password");
            
            logger.info("Login attempt for email: {}", email);
            
            // TEMPORARY: Mock successful login for testing
            Map<String, Object> mockResponse = new HashMap<>();
            mockResponse.put("access_token", "mock-jwt-token-for-testing");
            mockResponse.put("token_type", "bearer");
            mockResponse.put("expires_in", 3600);
            mockResponse.put("user", Map.of(
                "id", "mock-user-id",
                "email", email,
                "first_name", "Mock",
                "last_name", "User"
            ));
            
            logger.info("Mock login successful for: {}", email);
            return ResponseEntity.ok(mockResponse);
            
        } catch (Exception e) {
            logger.error("Failed to login user", e);
            return ResponseEntity.badRequest().body(Map.of("error", "Login failed: " + e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        try {
            // Extract token from Bearer token
            String token = authHeader.replace("Bearer ", "");
            
            // Get user info from Supabase
            Map<String, Object> user = supabaseService.getUserInfo(token);
            
            logger.info("Retrieved current user info");
            return ResponseEntity.ok(user);
            
        } catch (Exception e) {
            logger.error("Failed to get current user", e);
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to get user info: " + e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            
            // Logout user from Supabase
            supabaseService.logoutUser(token);
            
            logger.info("User logged out successfully");
            return ResponseEntity.ok(Map.of("message", "Logout successful"));
            
        } catch (Exception e) {
            logger.error("Failed to logout user", e);
            return ResponseEntity.badRequest().body(Map.of("error", "Logout failed: " + e.getMessage()));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> resetData) {
        try {
            String email = resetData.get("email");
            
            // Send password reset email via Supabase
            supabaseService.resetPassword(email);
            
            logger.info("Password reset email sent to: {}", email);
            return ResponseEntity.ok(Map.of("message", "Password reset email sent"));
            
        } catch (Exception e) {
            logger.error("Failed to send password reset email", e);
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to send reset email: " + e.getMessage()));
        }
    }
}
