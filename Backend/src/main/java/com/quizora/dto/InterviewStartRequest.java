package com.quizora.dto;

import jakarta.validation.constraints.NotBlank;

public class InterviewStartRequest {
    @NotBlank
    private String role;
    
    private String topics;
    
    @NotBlank
    private String difficulty; // BEGINNER, INTERMEDIATE, ADVANCED
    
    // Getters and Setters
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public String getTopics() { return topics; }
    public void setTopics(String topics) { this.topics = topics; }
    
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
}
