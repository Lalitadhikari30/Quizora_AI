package com.quizora.dto;

import java.time.LocalDateTime;

public class InterviewReportResponse {
    private String userId;
    private String role;
    private String topics;
    private String difficulty;
    private LocalDateTime startTime;
    private LocalDateTime completionTime;
    private Integer totalScore;
    private String finalFeedback;
    private Integer totalQuestions;
    private Integer correctAnswers;
    private Double accuracy;
    
    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public String getTopics() { return topics; }
    public void setTopics(String topics) { this.topics = topics; }
    
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    
    public LocalDateTime getCompletionTime() { return completionTime; }
    public void setCompletionTime(LocalDateTime completionTime) { this.completionTime = completionTime; }
    
    public Integer getTotalScore() { return totalScore; }
    public void setTotalScore(Integer totalScore) { this.totalScore = totalScore; }
    
    public String getFinalFeedback() { return finalFeedback; }
    public void setFinalFeedback(String finalFeedback) { this.finalFeedback = finalFeedback; }
    
    public Integer getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(Integer totalQuestions) { this.totalQuestions = totalQuestions; }
    
    public Integer getCorrectAnswers() { return correctAnswers; }
    public void setCorrectAnswers(Integer correctAnswers) { this.correctAnswers = correctAnswers; }
    
    public Double getAccuracy() { return accuracy; }
    public void setAccuracy(Double accuracy) { this.accuracy = accuracy; }
    
    // Additional setters for PerformanceController
    public void setTotalInterviews(Integer totalInterviews) {
        this.totalQuestions = totalInterviews; // Reuse field
    }
    
    public void setAverageInterviewScore(Double averageScore) {
        this.accuracy = averageScore; // Reuse field
    }
    
    public void setStrongestTopics(String topics) {
        this.topics = topics;
    }
    
    public void setImprovementAreas(String areas) {
        this.finalFeedback = areas; // Reuse field
    }
}
