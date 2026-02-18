package com.quizora.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quizora.dto.*;
import com.quizora.entity.*;
import com.quizora.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InterviewService {
    
    private static final Logger logger = LoggerFactory.getLogger(InterviewService.class);
    
    @Autowired
    private InterviewSessionRepository interviewSessionRepository;
    
    @Autowired
    private InterviewResponseRepository interviewResponseRepository;
    
    @Autowired
    private UserPerformanceRepository userPerformanceRepository;
    
    @Autowired
    // private AiIntegrationService aiIntegrationService;
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public InterviewSessionResponse startInterview(InterviewStartRequest request, String userId) {
        try {
            InterviewSession session = new InterviewSession();
            session.setUserId(userId);
            session.setJobRole(request.getRole());
            session.setExperience(request.getExperience());
            session.setDifficulty(request.getDifficulty());
            session.setTotalQuestions(10);
            session.setCurrentQuestionIndex(0);
            session.setStatus(InterviewSession.SessionStatus.ACTIVE);
            
            // Generate first question using AI
            List<String> questions = generateMockQuestions(request.getRole(), request.getExperience(), request.getDifficulty());
            session.setFirstQuestion(questions.get(0));
            
            session = interviewSessionRepository.save(session);
            
            InterviewSessionResponse response = new InterviewSessionResponse();
            response.setSessionId(session.getId());
            response.setJobRole(session.getJobRole());
            response.setExperience(session.getExperience());
            response.setDifficulty(session.getDifficulty());
            response.setFirstQuestion(session.getFirstQuestion());
            response.setTotalQuestions(session.getTotalQuestions());
            response.setStartedAt(session.getStartedAt());
            response.setActive(session.getStatus() == InterviewSession.SessionStatus.ACTIVE);
            
            logger.info("Started interview session {} for user: {}", session.getId(), userId);
            return response;
            
        } catch (Exception e) {
            logger.error("Failed to start interview", e);
            throw new RuntimeException("Failed to start interview: " + e.getMessage());
        }
    }

    @Transactional
    public String submitAnswer(Long sessionId, String question, String userAnswer, String userId) {
        try {
            InterviewSession session = interviewSessionRepository.findActiveByUserIdForUpdate(userId)
                    .orElseThrow(() -> new RuntimeException("No active interview session found"));
            
            // Evaluate answer using AI (mock implementation for testing)
            // String evaluation = evaluateAnswer(question, userAnswer, session.getTopics());
            
            // Mock evaluation for testing
            String evaluation = "Correct answer. Well explained.";
            
            boolean isCorrect = evaluation.toLowerCase().contains("correct");
            
            InterviewResponse response = new InterviewResponse();
            response.setInterviewSession(session);
            response.setQuestion(question);
            response.setUserAnswer(userAnswer);
            response.setCorrectAnswer(evaluation);
            response.setIsCorrect(isCorrect);
            
            interviewResponseRepository.save(response);
            
            // Update session progress
            session.setCurrentQuestionIndex(session.getCurrentQuestionIndex() + 1);
            
            interviewSessionRepository.save(session);
            
            logger.info("Answer submitted for session {}: {} - {}", sessionId, isCorrect ? "Correct" : "Incorrect");
            
            return isCorrect ? "Correct!" : "Incorrect.";
            
        } catch (Exception e) {
            logger.error("Failed to submit answer", e);
            throw new RuntimeException("Failed to submit answer: " + e.getMessage());
        }
    }

    private String evaluateAnswer(String question, String userAnswer, String topics) {
        try {
            String prompt = String.format(
                "Evaluate this answer for the following question in a %s interview context:\n\n" +
                "Question: %s\n\n" +
                "User Answer: %s\n\n" +
                "Topics: %s\n\n" +
                "Provide a brief evaluation (correct/incorrect) and explanation.\n" +
                "Return only 'CORRECT' or 'INCORRECT'.",
                question, userAnswer, topics
            );
            
            // Simple evaluation logic - in real implementation, this would call AI service
            if (userAnswer != null && !userAnswer.trim().isEmpty()) {
                // Basic keyword matching for demo
                String lowerAnswer = userAnswer.toLowerCase();
                String lowerQuestion = question.toLowerCase();
                
                if (lowerQuestion.contains("what") && lowerAnswer.contains("java")) {
                    return "CORRECT";
                } else if (lowerQuestion.contains("spring") && lowerAnswer.contains("boot")) {
                    return "CORRECT";
                } else if (lowerQuestion.contains("database") && lowerAnswer.contains("sql")) {
                    return "CORRECT";
                }
            }
            
            return "INCORRECT";
            
        } catch (Exception e) {
            logger.error("Failed to evaluate answer", e);
            return "INCORRECT";
        }
    }

    @Transactional
    public InterviewReportResponse completeInterview(Long sessionId, String userId) {
        try {
            InterviewSession session = interviewSessionRepository.findById(sessionId)
                    .orElseThrow(() -> new RuntimeException("Interview session not found"));
            
            if (!session.getUserId().equals(userId)) {
                throw new RuntimeException("Unauthorized access to interview session");
            }
            
            List<InterviewResponse> responses = interviewResponseRepository.findByInterviewSessionIdOrderById(sessionId);
            
            session.setCompletedAt(LocalDateTime.now());
            session.setStatus(InterviewSession.SessionStatus.COMPLETED);
            interviewSessionRepository.save(session);
            
            // Generate final feedback using AI
            String feedback = generateFinalFeedback(session, responses);
            
            // Update user performance
            updateUserPerformance(userId, session, responses);
            
            InterviewReportResponse report = new InterviewReportResponse();
            report.setUserId(userId);
            report.setJobRole(session.getJobRole());
            report.setExperience(session.getExperience());
            report.setDifficulty(session.getDifficulty());
            report.setStartedAt(session.getStartedAt());
            report.setCompletedAt(session.getCompletedAt());
            report.setFinalFeedback(feedback);
            report.setTotalQuestions(responses.size());
            
            int correctCount = (int) responses.stream()
                    .mapToInt(r -> r.getIsCorrect() ? 1 : 0)
                    .sum();
            report.setCorrectAnswers(correctCount);
            report.setAccuracy((double) correctCount / responses.size() * 100);
            
            logger.info("Completed interview {} for user: {} with score: {}/{}", sessionId, userId, correctCount);
            
            return report;
            
        } catch (Exception e) {
            logger.error("Failed to complete interview", e);
            throw new RuntimeException("Failed to complete interview: " + e.getMessage());
        }
    }

    private String generateFinalFeedback(InterviewSession session, List<InterviewResponse> responses) {
        try {
            StringBuilder feedback = new StringBuilder();
            feedback.append("Interview completed. ");
            feedback.append(String.format("Total questions: %d. ", responses.size()));
            
            long correctCount = responses.stream()
                    .mapToInt(r -> r.getIsCorrect() ? 1 : 0)
                    .sum();
            
            feedback.append(String.format("Correct answers: %d. ", correctCount));
            feedback.append(String.format("Score: %d/%d (%.1f%%).", 
                    correctCount, responses.size(), 
                    (double) correctCount / responses.size() * 100));
            
            feedback.append("Areas for improvement: ");
            if (correctCount < responses.size() * 0.7) {
                feedback.append("Review fundamental concepts and practice more problems.");
            } else if (correctCount < responses.size() * 0.9) {
                feedback.append("Good performance with minor areas to strengthen.");
            } else {
                feedback.append("Excellent performance! Keep up the great work.");
            }
            
            return feedback.toString();
            
        } catch (Exception e) {
            logger.error("Failed to generate feedback", e);
            return "Interview completed. Review your performance.";
        }
    }

    private void updateUserPerformance(String userId, InterviewSession session, List<InterviewResponse> responses) {
        try {
            UserPerformance performance = userPerformanceRepository.findByUserId(userId)
                    .orElse(new UserPerformance());
            
            performance.setUserId(userId);
            
            // Update interview statistics
            int totalInterviews = performance.getTotalInterviewsTaken() + 1;
            performance.setTotalInterviewsTaken(totalInterviews);
            
            // Update average score
            int currentScore = session.getTotalScore();
            double previousTotal = performance.getAverageInterviewScore() != null ? 
                    performance.getAverageInterviewScore() * performance.getTotalInterviewsTaken() : 0.0;
            performance.setAverageInterviewScore((double) (previousTotal + currentScore) / totalInterviews);
            
            performance.setLastUpdated(LocalDateTime.now());
            userPerformanceRepository.save(performance);
            
        } catch (Exception e) {
            logger.error("Failed to update user performance", e);
        }
    }

    public List<InterviewSessionResponse> getUserInterviews(String userId) {
        List<InterviewSession> sessions = interviewSessionRepository.findByUserIdOrderByStartedAtDesc(userId);

        return sessions.stream()
                .map(session -> {
                    InterviewSessionResponse response = new InterviewSessionResponse();
                    response.setSessionId(session.getId());
                    response.setJobRole(session.getJobRole());
                    response.setExperience(session.getExperience());
                    response.setDifficulty(session.getDifficulty());
                    response.setStartedAt(session.getStartedAt());
                    response.setActive(session.getStatus() == InterviewSession.SessionStatus.ACTIVE);
                    response.setCurrentQuestionIndex(session.getCurrentQuestionIndex());
                    return response;
                })
                .collect(Collectors.toList());
    }

    private List<String> generateMockQuestions(String jobRole, String experience, String difficulty) {
        // Mock questions - in production, this would call the AI service
        return Arrays.asList(
            "Tell me about your experience with " + jobRole + " technologies.",
            "What challenges have you faced in your " + experience + " role?",
            "How do you stay updated with the latest " + jobRole + " trends?",
            "Describe a complex problem you solved recently.",
            "What's your approach to debugging " + jobRole + " issues?",
            "How do you handle tight deadlines?",
            "What's your experience with team collaboration?",
            "How do you ensure code quality?",
            "What motivates you in your work?",
            "Where do you see yourself in 5 years?"
        );
    }
}
