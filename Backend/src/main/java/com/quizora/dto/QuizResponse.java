package com.quizora.dto;

import java.util.List;

public class QuizResponse {
    private Long id;
    private String title;
    private String description;
    private String type;
    private String sourceType;
    private String sourceContent;
    private List<QuestionResponse> questions;
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }
    
    public String getSourceContent() { return sourceContent; }
    public void setSourceContent(String sourceContent) { this.sourceContent = sourceContent; }
    
    public List<QuestionResponse> getQuestions() { return questions; }
    public void setQuestions(List<QuestionResponse> questions) { this.questions = questions; }
}
