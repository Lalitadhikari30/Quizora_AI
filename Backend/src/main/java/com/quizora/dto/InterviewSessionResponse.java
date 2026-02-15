package com.quizora.dto;

import java.time.LocalDateTime;

public class InterviewSessionResponse {
    private Long sessionId;
    private String role;
    private String topics;
    private String difficulty;
    private LocalDateTime startTime;
    private Boolean isActive;
    private Integer currentQuestionIndex;
    private Integer totalScore;
    
    // Getters and Setters
    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public String getTopics() { return topics; }
    public void setTopics(String topics) { this.topics = topics; }
    
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public Integer getCurrentQuestionIndex() { return currentQuestionIndex; }
    public void setCurrentQuestionIndex(Integer currentQuestionIndex) { this.currentQuestionIndex = currentQuestionIndex; }
    
    public Integer getTotalScore() { return totalScore; }
    public void setTotalScore(Integer totalScore) { this.totalScore = totalScore; }
}
