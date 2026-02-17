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

    private LocalDateTime completionTime;

    @ManyToOne
    private Quiz quiz;

    public QuizAttempt(){}

    public void setQuiz(Quiz quiz){ this.quiz = quiz; }
    public void setUserId(String userId){ this.userId = userId; }
    public void setTotalQuestions(int totalQuestions){ this.totalQuestions = totalQuestions; }
    public void setCorrectAnswers(int correctAnswers){ this.correctAnswers = correctAnswers; }
    public void setScore(int score){ this.score = score; }
    public void setCompletionTime(LocalDateTime completionTime){ this.completionTime = completionTime; }
}
