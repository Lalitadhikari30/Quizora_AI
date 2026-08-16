package com.quizora.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "interview_sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InterviewSession {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String userId;
    
    @Column(nullable = false)
    private String jobRole;
    
    @Column(nullable = false)
    private String experience;
    
    @Column(nullable = false)
    private String difficulty;
    
    @Column(name = "first_question", columnDefinition = "TEXT")
    private String firstQuestion;
    
    @Column(name = "current_question_index")
    private Integer currentQuestionIndex = 0;
    
    @Column(name = "total_questions")
    private Integer totalQuestions = 10;
    
    @Column(name = "total_score")
    private Integer totalScore = 0;
    
    @Column(name = "session_status")
    @Enumerated(EnumType.STRING)
    private SessionStatus status = SessionStatus.ACTIVE;
    
    @Column(name = "start_time", nullable = false)
    @CreationTimestamp
    private LocalDateTime startedAt;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;
    
    @OneToMany(mappedBy = "interviewSession", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<InterviewResponse> responses;
    
    public enum SessionStatus {
        ACTIVE,
        COMPLETED,
        ABANDONED
    }
    
    @PrePersist
    public void onCreate() {
        if (this.startedAt == null) {
            this.startedAt = LocalDateTime.now();
        }
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isActive = true;
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
