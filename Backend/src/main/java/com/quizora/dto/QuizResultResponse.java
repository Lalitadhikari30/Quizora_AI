package com.quizora.dto;

import java.util.List;

public class QuizResultResponse {
    private Long quizId;
    private String title;
    private Integer totalQuestions;
    private Integer correctAnswers;
    private Integer score;
    private Double percentage;
    private List<AnswerReviewResponse> answerReviews;
    
    // Getters and Setters
    public Long getQuizId() { return quizId; }
    public void setQuizId(Long quizId) { this.quizId = quizId; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public Integer getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(Integer totalQuestions) { this.totalQuestions = totalQuestions; }
    
    public Integer getCorrectAnswers() { return correctAnswers; }
    public void setCorrectAnswers(Integer correctAnswers) { this.correctAnswers = correctAnswers; }
    
    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
    
    public Double getPercentage() { return percentage; }
    public void setPercentage(Double percentage) { this.percentage = percentage; }
    
    public List<AnswerReviewResponse> getAnswerReviews() { return answerReviews; }
    public void setAnswerReviews(List<AnswerReviewResponse> answerReviews) { this.answerReviews = answerReviews; }
}
