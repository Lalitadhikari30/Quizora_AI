package com.quizora.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "extracted_contents", indexes = {
    @Index(name = "idx_extracted_content_user_id", columnList = "user_id"),
    @Index(name = "idx_extracted_content_created_at", columnList = "created_at"),
    @Index(name = "idx_extracted_content_status", columnList = "status")
})
public class ExtractedContent {
    
    public enum Status {
        EXTRACTED,
        AI_GENERATED,
        FAILED
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private String userId;
    
    @Column(name = "file_name", nullable = false)
    private String fileName;
    
    @Column(name = "file_url", columnDefinition = "TEXT", nullable = false)
    private String fileUrl;
    
    @Column(name = "extracted_text", columnDefinition = "TEXT", nullable = false)
    private String extractedText;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;
    
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @Column(name = "quiz_id")
    private Long quizId;
    
    public ExtractedContent() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = Status.EXTRACTED;
    }
    
    // Constructors
    public ExtractedContent(String userId, String fileName, String fileUrl, String extractedText) {
        this();
        this.userId = userId;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.extractedText = extractedText;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    
    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
    
    public String getExtractedText() { return extractedText; }
    public void setExtractedText(String extractedText) { this.extractedText = extractedText; }
    
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public Long getQuizId() { return quizId; }
    public void setQuizId(Long quizId) { this.quizId = quizId; }
    
    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) createdAt = now;
        if (updatedAt == null) updatedAt = now;
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Utility methods
    public boolean isExtracted() {
        return Status.EXTRACTED.equals(status);
    }
    
    public boolean isAiGenerated() {
        return Status.AI_GENERATED.equals(status);
    }
    
    public boolean isFailed() {
        return Status.FAILED.equals(status);
    }
    
    public void markAsFailed(String errorMessage) {
        this.status = Status.FAILED;
        this.errorMessage = errorMessage;
    }
    
    public void markAsAiGenerated(Long quizId) {
        this.status = Status.AI_GENERATED;
        this.quizId = quizId;
        this.errorMessage = null;
    }
}
