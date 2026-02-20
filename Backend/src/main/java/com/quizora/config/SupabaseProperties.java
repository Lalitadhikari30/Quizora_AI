package com.quizora.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@ConfigurationProperties(prefix = "supabase")
@Validated
public class SupabaseProperties {
    
    private String url;
    private String serviceRoleKey;
    private String jwkUrl;
    private String bucketName;
    
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public String getServiceRoleKey() {
        return serviceRoleKey;
    }
    
    public void setServiceRoleKey(String serviceRoleKey) {
        this.serviceRoleKey = serviceRoleKey;
    }
    
    public String getJwkUrl() {
        return jwkUrl;
    }
    
    public void setJwkUrl(String jwkUrl) {
        this.jwkUrl = jwkUrl;
    }
    
    public String getBucketName() {
        return bucketName;
    }
    
    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }
    
    public void validateAndLog() {
        if (url == null || url.isBlank()) {
            throw new IllegalStateException("Supabase URL is not configured. Please check application.properties");
        }
        if (serviceRoleKey == null || serviceRoleKey.isBlank()) {
            throw new IllegalStateException("Supabase service role key is not configured. Please check application.properties");
        }
        if (jwkUrl == null || jwkUrl.isBlank()) {
            throw new IllegalStateException("Supabase JWK URL is not configured. Please check application.properties");
        }
        if (bucketName == null || bucketName.isBlank()) {
            throw new IllegalStateException("Supabase bucket name is not configured. Please check application.properties");
        }
        
        // Log configuration (mask sensitive data)
        System.out.println("=== SUPABASE CONFIGURATION VALIDATED ===");
        System.out.println("URL: " + url);
        System.out.println("Service Role Key: " + (serviceRoleKey.length() > 5 ? serviceRoleKey.substring(0, 5) + "..." : serviceRoleKey));
        System.out.println("JWK URL: " + jwkUrl);
        System.out.println("Bucket Name: " + bucketName);
        System.out.println("==========================================");
    }
}
