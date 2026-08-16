package com.quizora.dto;

public class InterviewStartResponse {
    
    private Long sessionId;
    private InterviewQuestionDTO nextQuestion;
    
    public InterviewStartResponse() {}
    
    public InterviewStartResponse(Long sessionId, InterviewQuestionDTO nextQuestion) {
        this.sessionId = sessionId;
        this.nextQuestion = nextQuestion;
    }
    
    public Long getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }
    
    public InterviewQuestionDTO getNextQuestion() {
        return nextQuestion;
    }
    
    public void setNextQuestion(InterviewQuestionDTO nextQuestion) {
        this.nextQuestion = nextQuestion;
    }
}
