package com.quizora.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class QuizQuestionDTO {
    
    @JsonProperty("question")
    private String question;
    
    @JsonProperty("options")
    private String[] options;
    
    @JsonProperty("answer")
    private String answer;
    
    @JsonProperty("explanation")
    private String explanation;
    
    @JsonProperty("difficulty")
    private String difficulty;
    
    @JsonProperty("topics")
    private String[] topics;
    
    // Default constructor
    public QuizQuestionDTO() {}
    
    // Constructor with all fields
    public QuizQuestionDTO(String question, String[] options, String answer, String explanation, String difficulty, String[] topics) {
        this.question = question;
        this.options = options;
        this.answer = answer;
        this.explanation = explanation;
        this.difficulty = difficulty;
        this.topics = topics;
    }
    
    // Getters and Setters
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
    
    public String[] getOptions() { return options; }
    public void setOptions(String[] options) { this.options = options; }
    
    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }
    
    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
    
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    
    public String[] getTopics() { return topics; }
    public void setTopics(String[] topics) { this.topics = topics; }
}
