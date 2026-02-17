package com.quizora.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_performance")
public class UserPerformance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String userId;
    
    @Column(nullable = false)
    private Integer totalQuizzesTaken;
    
    @Column(nullable = false)
    private Double averageQuizScore;
    
    @Column(nullable = false)
    private Integer totalInterviewsTaken;
    
    @Column(nullable = false)
    private Double averageInterviewScore;
    
    @Column(name = "strongest_topics", columnDefinition = "TEXT")
    private String strongestTopics;
    
    @Column(name = "improvement_areas", columnDefinition = "TEXT")
    private String improvementAreas;
    
    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    public UserPerformance() {
        this.createdAt = LocalDateTime.now();
        this.lastUpdated = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public Integer getTotalQuizzesTaken() { return totalQuizzesTaken; }
    public void setTotalQuizzesTaken(Integer totalQuizzesTaken) { this.totalQuizzesTaken = totalQuizzesTaken; }
    
    public Double getAverageQuizScore() { return averageQuizScore; }
    public void setAverageQuizScore(Double averageQuizScore) { this.averageQuizScore = averageQuizScore; }
    
    public Integer getTotalInterviewsTaken() { return totalInterviewsTaken; }
    public void setTotalInterviewsTaken(Integer totalInterviewsTaken) { this.totalInterviewsTaken = totalInterviewsTaken; }
    
    public Double getAverageInterviewScore() { return averageInterviewScore; }
    public void setAverageInterviewScore(Double averageInterviewScore) { this.averageInterviewScore = averageInterviewScore; }
    
    public String getStrongestTopics() { return strongestTopics; }
    public void setStrongestTopics(String strongestTopics) { this.strongestTopics = strongestTopics; }
    
    public String getImprovementAreas() { return improvementAreas; }
    public void setImprovementAreas(String improvementAreas) { this.improvementAreas = improvementAreas; }
    
    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    @PreUpdate
    public void preUpdate() {
        this.lastUpdated = LocalDateTime.now();
    }
}
