package com.quizora.dto;

public class QuestionResponse {
    private Long id;
    private String questionText;
    private String type;
    private String options;
    private String correctAnswer;
    private String explanation;
    private Integer difficulty;
    private String topicTags;
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getOptions() { return options; }
    public void setOptions(String options) { this.options = options; }
    
    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }
    
    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
    
    public Integer getDifficulty() { return difficulty; }
    public void setDifficulty(Integer difficulty) { this.difficulty = difficulty; }
    
    public String getTopicTags() { return topicTags; }
    public void setTopicTags(String topicTags) { this.topicTags = topicTags; }
}
