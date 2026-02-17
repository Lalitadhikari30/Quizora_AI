package com.quizora.dto;

import java.time.LocalDateTime;

public class InterviewReportResponse {
    private String userId;
    private String jobRole;
    private String experience;
    private String difficulty;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private String finalFeedback;
    private Integer totalQuestions;
    private Integer correctAnswers;
    private Double accuracy;
    
    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getJobRole() { return jobRole; }
    public void setJobRole(String jobRole) { this.jobRole = jobRole; }
    
    public String getExperience() { return experience; }
    public void setExperience(String experience) { this.experience = experience; }
    
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    
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
        this.experience = topics; // Reuse field
    }
    
    public void setImprovementAreas(String areas) {
        this.finalFeedback = areas; // Reuse field
    }
}
