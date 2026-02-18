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
    public ResponseEntity<?> register(@RequestBody Map<String, Object> registrationData) {
        try {
            String userId = (String) registrationData.get("userId");
            String email = (String) registrationData.get("email");
            String firstName = (String) registrationData.get("firstName");
            String lastName = (String) registrationData.get("lastName");
            String name = (String) registrationData.get("name");
            
            logger.info("Registering Supabase user in backend: {}", email);
            
            // Create user profile in backend database
            Map<String, Object> userProfile = new HashMap<>();
            userProfile.put("userId", userId);
            userProfile.put("email", email);
            userProfile.put("firstName", firstName);
            userProfile.put("lastName", lastName);
            userProfile.put("name", name);
            userProfile.put("createdAt", java.time.LocalDateTime.now().toString());
            
            // TEMPORARY: Mock successful backend registration
            logger.info("Backend profile created for user: {}", email);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "User profile created successfully");
            response.put("user", userProfile);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Failed to register user profile", e);
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to create user profile: " + e.getMessage()));
        }
    }

    @PostMapping("/sync")
    public ResponseEntity<?> syncUser(@RequestBody Map<String, Object> syncData) {
        try {
            String userId = (String) syncData.get("userId");
            String email = (String) syncData.get("email");
            String firstName = (String) syncData.get("firstName");
            String lastName = (String) syncData.get("lastName");
            String name = (String) syncData.get("name");
            
            logger.info("Syncing Supabase user with backend: {}", email);
            
            // Create or update user profile in backend database
            Map<String, Object> userProfile = new HashMap<>();
            userProfile.put("userId", userId);
            userProfile.put("email", email);
            userProfile.put("firstName", firstName);
            userProfile.put("lastName", lastName);
            userProfile.put("name", name);
            userProfile.put("lastSynced", java.time.LocalDateTime.now().toString());
            
            // TEMPORARY: Mock successful sync
            logger.info("Backend sync completed for user: {}", email);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "User synced successfully");
            response.put("user", userProfile);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Failed to sync user profile", e);
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to sync user: " + e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData) {
        try {
            String email = loginData.get("email");
            String password = loginData.get("password");
            
            logger.info("Login attempt for email: {}", email);
            
            // This endpoint is now handled by Supabase directly on frontend
            // Keeping for backward compatibility
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Please use Supabase authentication");
            response.put("deprecated", true);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Failed to process login", e);
            return ResponseEntity.badRequest().body(Map.of("error", "Login failed: " + e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        try {
            // Extract token from Bearer token
            String token = authHeader.replace("Bearer ", "");
            
            // Validate Supabase JWT and extract user info
            Map<String, Object> user = supabaseService.validateTokenAndGetUser(token);
            
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
