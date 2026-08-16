package com.quizora.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class QuizAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    private int totalQuestions;
    private int correctAnswers;
    private int score;

    @Transient
    private double percentage;

    private LocalDateTime completionTime;

    @ManyToOne
    private Quiz quiz;

    public QuizAttempt(){
        this.completionTime = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public int getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }

    public int getCorrectAnswers() { return correctAnswers; }
    public void setCorrectAnswers(int correctAnswers) { this.correctAnswers = correctAnswers; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public double getPercentage() {
        if (percentage > 0.0) return percentage;
        if (totalQuestions > 0) {
            return Math.round((((double) correctAnswers / totalQuestions) * 100.0) * 10.0) / 10.0;
        }
        return 0.0;
    }
    public void setPercentage(double percentage) { this.percentage = percentage; }

    public LocalDateTime getCompletionTime() { return completionTime; }
    public void setCompletionTime(LocalDateTime completionTime) { this.completionTime = completionTime; }

    public Quiz getQuiz() { return quiz; }
    public void setQuiz(Quiz quiz) { this.quiz = quiz; }
}
