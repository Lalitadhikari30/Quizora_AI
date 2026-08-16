package com.quizora.dto;

public class InterviewAnswerResponse {
    
    private InterviewEvaluationDTO evaluation;
    private InterviewQuestionDTO nextQuestion;
    
    public InterviewAnswerResponse() {}
    
    public InterviewAnswerResponse(InterviewEvaluationDTO evaluation, InterviewQuestionDTO nextQuestion) {
        this.evaluation = evaluation;
        this.nextQuestion = nextQuestion;
    }
    
    public InterviewEvaluationDTO getEvaluation() {
        return evaluation;
    }
    
    public void setEvaluation(InterviewEvaluationDTO evaluation) {
        this.evaluation = evaluation;
    }
    
    public InterviewQuestionDTO getNextQuestion() {
        return nextQuestion;
    }
    
    public void setNextQuestion(InterviewQuestionDTO nextQuestion) {
        this.nextQuestion = nextQuestion;
    }
}
