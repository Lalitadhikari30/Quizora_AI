package com.quizora.dto;

public class InterviewQuestionDTO {
    
    private String questionId;
    private String questionText;
    
    public InterviewQuestionDTO() {}
    
    public InterviewQuestionDTO(String questionId, String questionText) {
        this.questionId = questionId;
        this.questionText = questionText;
    }
    
    public String getQuestionId() {
        return questionId;
    }
    
    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }
    
    public String getQuestionText() {
        return questionText;
    }
    
    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }
}
