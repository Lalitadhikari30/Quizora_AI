package com.quizora.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "questions")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String questionText;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionType type;
    
    @Column(nullable = false)
    private String correctAnswer;
    
    @Column(columnDefinition = "TEXT")
    private String options;
    
    @Column(columnDefinition = "TEXT")
    private String explanation;
    
    @Column
    private Integer difficulty;
    
    @Column(columnDefinition = "TEXT")
    private String topicTags;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;
    
    public Question() {
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }
    
    public QuestionType getType() { return type; }
    public void setType(QuestionType type) { this.type = type; }
    
    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }
    
    public String getOptions() { return options; }
    public void setOptions(String options) { this.options = options; }
    
    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
    
    public Integer getDifficulty() { return difficulty; }
    public void setDifficulty(Integer difficulty) { this.difficulty = difficulty; }
    
    public String getTopicTags() { return topicTags; }
    public void setTopicTags(String topicTags) { this.topicTags = topicTags; }
    
    public Quiz getQuiz() { return quiz; }
    public void setQuiz(Quiz quiz) { this.quiz = quiz; }
}
