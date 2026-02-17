package com.quizora.dto;

import java.time.LocalDateTime;

public class InterviewSessionResponse {

    private Long sessionId;
    private String jobRole;
    private String experience;
    private String difficulty;
    private String firstQuestion;
    private Integer totalQuestions;
    private LocalDateTime startedAt;
    private Boolean active;
    private Integer currentQuestionIndex;

    public InterviewSessionResponse(){}

    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }

    public String getJobRole() { return jobRole; }
    public void setJobRole(String jobRole) { this.jobRole = jobRole; }

    public String getExperience() { return experience; }
    public void setExperience(String experience) { this.experience = experience; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public String getFirstQuestion() { return firstQuestion; }
    public void setFirstQuestion(String firstQuestion) { this.firstQuestion = firstQuestion; }

    public Integer getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(Integer totalQuestions) { this.totalQuestions = totalQuestions; }

    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public Integer getCurrentQuestionIndex() { return currentQuestionIndex; }
    public void setCurrentQuestionIndex(Integer currentQuestionIndex) { this.currentQuestionIndex = currentQuestionIndex; }
}
