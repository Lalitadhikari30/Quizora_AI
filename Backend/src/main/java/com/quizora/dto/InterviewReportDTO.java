package com.quizora.dto;

public class InterviewReportDTO {
    
    private Integer overallScore;
    private String summary;
    private String strengthsOverview;
    private String weaknessesOverview;
    private String recommendation;
    
    public InterviewReportDTO() {}
    
    public InterviewReportDTO(Integer overallScore, String summary, String strengthsOverview, String weaknessesOverview, String recommendation) {
        this.overallScore = overallScore;
        this.summary = summary;
        this.strengthsOverview = strengthsOverview;
        this.weaknessesOverview = weaknessesOverview;
        this.recommendation = recommendation;
    }
    
    public Integer getOverallScore() {
        return overallScore;
    }
    
    public void setOverallScore(Integer overallScore) {
        this.overallScore = overallScore;
    }
    
    public String getSummary() {
        return summary;
    }
    
    public void setSummary(String summary) {
        this.summary = summary;
    }
    
    public String getStrengthsOverview() {
        return strengthsOverview;
    }
    
    public void setStrengthsOverview(String strengthsOverview) {
        this.strengthsOverview = strengthsOverview;
    }
    
    public String getWeaknessesOverview() {
        return weaknessesOverview;
    }
    
    public void setWeaknessesOverview(String weaknessesOverview) {
        this.weaknessesOverview = weaknessesOverview;
    }
    
    public String getRecommendation() {
        return recommendation;
    }
    
    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }
}
