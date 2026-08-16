package com.quizora.dto;

import jakarta.validation.constraints.NotBlank;

public class InterviewAnswerRequest {
    
    @NotBlank
    private String questionId;
    
    @NotBlank
    private String answerText;
    
    public InterviewAnswerRequest() {}
    
    public InterviewAnswerRequest(String questionId, String answerText) {
        this.questionId = questionId;
        this.answerText = answerText;
    }
    
    public String getQuestionId() {
        return questionId;
    }
    
    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }
    
    public String getAnswerText() {
        return answerText;
    }
    
    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }
}
