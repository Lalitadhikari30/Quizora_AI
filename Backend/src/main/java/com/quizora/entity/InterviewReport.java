package com.quizora.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "interview_reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InterviewReport {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private InterviewSession interviewSession;
    
    @Column(name = "overall_score", nullable = false)
    private Integer overallScore;
    
    @Column(name = "technical_score")
    private Integer technicalScore;
    
    @Column(name = "communication_score")
    private Integer communicationScore;
    
    @Column(name = "strengths_summary", nullable = false, columnDefinition = "TEXT")
    private String strengthsOverview;
    
    @Column(name = "weaknesses_summary", nullable = false, columnDefinition = "TEXT")
    private String weaknessesOverview;
    
    @Column(name = "improvement_plan", nullable = false, columnDefinition = "TEXT")
    private String improvementPlan;
    
    @Column(name = "hire_recommendation", nullable = false, columnDefinition = "TEXT")
    private String hireRecommendation;
    
    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public InterviewSession getInterviewSession() { return interviewSession; }
    public void setInterviewSession(InterviewSession interviewSession) { this.interviewSession = interviewSession; }
    
    public Integer getOverallScore() { return overallScore; }
    public void setOverallScore(Integer overallScore) { this.overallScore = overallScore; }
    
    public Integer getTechnicalScore() { return technicalScore; }
    public void setTechnicalScore(Integer technicalScore) { this.technicalScore = technicalScore; }
    
    public Integer getCommunicationScore() { return communicationScore; }
    public void setCommunicationScore(Integer communicationScore) { this.communicationScore = communicationScore; }
    
    public String getStrengthsOverview() { return strengthsOverview; }
    public void setStrengthsOverview(String strengthsOverview) { this.strengthsOverview = strengthsOverview; }
    
    public String getWeaknessesOverview() { return weaknessesOverview; }
    public void setWeaknessesOverview(String weaknessesOverview) { this.weaknessesOverview = weaknessesOverview; }
    
    public String getImprovementPlan() { return improvementPlan; }
    public void setImprovementPlan(String improvementPlan) { this.improvementPlan = improvementPlan; }
    
    public String getHireRecommendation() { return hireRecommendation; }
    public void setHireRecommendation(String hireRecommendation) { this.hireRecommendation = hireRecommendation; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
