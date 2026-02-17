package com.quizora.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class QuizGenerationRequest {
    @NotBlank
    private String title;
    
    private String description;
    
    @NotBlank
    private String sourceContent;
    
    @NotBlank
    private String sourceType; // TEXT, PDF, YOUTUBE
    
    @NotBlank
    private String type; // MULTIPLE_CHOICE, TRUE_FALSE, SHORT_ANSWER, MIXED
    
    private Integer questionCount;
    
    private String difficulty; // BEGINNER, INTERMEDIATE, ADVANCED
    
    private String topics;
    
    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getSourceContent() { return sourceContent; }
    public void setSourceContent(String sourceContent) { this.sourceContent = sourceContent; }
    
    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public Integer getQuestionCount() { return questionCount; }
    public void setQuestionCount(Integer questionCount) { this.questionCount = questionCount; }
    
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    
    public String getTopics() { return topics; }
    public void setTopics(String topics) { this.topics = topics; }
}
