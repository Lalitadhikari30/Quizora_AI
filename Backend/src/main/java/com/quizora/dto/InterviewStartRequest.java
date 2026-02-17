package com.quizora.dto;

import jakarta.validation.constraints.NotBlank;

public class InterviewStartRequest {
    @NotBlank
    private String role;
    
    @NotBlank
    private String experience;
    
    @NotBlank
    private String difficulty; // BEGINNER, INTERMEDIATE, ADVANCED
    
    // Getters and Setters
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public String getExperience() { return experience; }
    public void setExperience(String experience) { this.experience = experience; }
    
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
}
