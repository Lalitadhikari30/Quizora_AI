package com.quizora.dto;

public class InterviewEvaluationDTO {
    
    private Integer score;
    private FeedbackDTO feedback;
    
    public InterviewEvaluationDTO() {}
    
    public InterviewEvaluationDTO(Integer score, FeedbackDTO feedback) {
        this.score = score;
        this.feedback = feedback;
    }
    
    public Integer getScore() {
        return score;
    }
    
    public void setScore(Integer score) {
        this.score = score;
    }
    
    public FeedbackDTO getFeedback() {
        return feedback;
    }
    
    public void setFeedback(FeedbackDTO feedback) {
        this.feedback = feedback;
    }
    
    public static class FeedbackDTO {
        
        private String strengths;
        private String improvements;
        
        public FeedbackDTO() {}
        
        public FeedbackDTO(String strengths, String improvements) {
            this.strengths = strengths;
            this.improvements = improvements;
        }
        
        public String getStrengths() {
            return strengths;
        }
        
        public void setStrengths(String strengths) {
            this.strengths = strengths;
        }
        
        public String getImprovements() {
            return improvements;
        }
        
        public void setImprovements(String improvements) {
            this.improvements = improvements;
        }
    }
}
