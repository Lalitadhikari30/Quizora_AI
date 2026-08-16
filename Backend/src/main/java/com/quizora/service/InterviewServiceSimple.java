package com.quizora.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quizora.config.GeminiProperties;
import com.quizora.dto.*;
import com.quizora.entity.*;
import com.quizora.repository.*;
import io.netty.resolver.DefaultAddressResolverGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class InterviewServiceSimple {

    private static final Logger logger = LoggerFactory.getLogger(InterviewServiceSimple.class);

    private final InterviewSessionRepository interviewSessionRepository;
    private final InterviewResponseRepository interviewResponseRepository;
    private final InterviewReportRepository interviewReportRepository;
    private final GeminiProperties geminiProperties;
    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final List<String> FALLBACK_MODELS = List.of(
            "gemini-2.5-flash",
            "gemini-2.5-pro",
            "gemini-3.5-flash-lite"
    );

    public InterviewServiceSimple(InterviewSessionRepository interviewSessionRepository,
                                 InterviewResponseRepository interviewResponseRepository,
                                 InterviewReportRepository interviewReportRepository,
                                 GeminiProperties geminiProperties,
                                 WebClient.Builder webClientBuilder) {
        this.interviewSessionRepository = interviewSessionRepository;
        this.interviewResponseRepository = interviewResponseRepository;
        this.interviewReportRepository = interviewReportRepository;
        this.geminiProperties = geminiProperties;

        // Use JDK DNS resolver for Windows Netty compatibility
        HttpClient httpClient = HttpClient.create()
                .resolver(DefaultAddressResolverGroup.INSTANCE)
                .responseTimeout(Duration.ofSeconds(60));

        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs()
                        .maxInMemorySize(16 * 1024 * 1024))
                .build();

        this.webClient = webClientBuilder
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .exchangeStrategies(strategies)
                .build();
    }

    // ==================== START INTERVIEW ====================
    public InterviewStartResponse startInterview(String userId, InterviewStartRequest request) {
        try {
            logger.info("=== STARTING INTERVIEW ===");
            logger.info("User ID: {}", userId);
            logger.info("Job Role: {}", request.getJobRole());
            logger.info("Experience: {}", request.getExperience());
            logger.info("Difficulty: {}", request.getDifficulty());

            // Create interview session
            InterviewSession session = new InterviewSession();
            session.setUserId(userId);
            session.setJobRole(request.getJobRole());
            session.setExperience(request.getExperience());
            session.setDifficulty(request.getDifficulty());
            session.setStatus(InterviewSession.SessionStatus.ACTIVE);
            session.setStartedAt(LocalDateTime.now());
            session.setCreatedAt(LocalDateTime.now());
            session.setUpdatedAt(LocalDateTime.now());

            session = interviewSessionRepository.save(session);
            logger.info("Created interview session: {}", session.getId());

            // Create tailored introductory question instantly
            String introText = "Welcome to your mock interview for the " + request.getJobRole() + 
                    " position! Let's start by having you introduce yourself, highlight your background in " + 
                    request.getJobRole() + ", and tell me about a project or achievement you are most proud of.";

            InterviewQuestionDTO firstQuestion = new InterviewQuestionDTO();
            firstQuestion.setQuestionId(String.valueOf(System.currentTimeMillis()));
            firstQuestion.setQuestionText(introText);

            // Save first question to database so it can be answered
            InterviewResponse firstQuestionEntity = new InterviewResponse();
            firstQuestionEntity.setInterviewSession(session);
            firstQuestionEntity.setQuestionText(introText);
            firstQuestionEntity.setDifficultyLevel(request.getDifficulty());
            firstQuestionEntity.setSequenceNumber(1);
            firstQuestionEntity = interviewResponseRepository.save(firstQuestionEntity);
            firstQuestion.setQuestionId(firstQuestionEntity.getId().toString());

            logger.info("Generated and saved first question ID {}: {}", firstQuestionEntity.getId(), introText);

            InterviewStartResponse startResponse = new InterviewStartResponse();
            startResponse.setSessionId(session.getId());
            startResponse.setNextQuestion(firstQuestion);

            return startResponse;

        } catch (Exception e) {
            logger.error("Failed to start interview", e);
            throw new RuntimeException("Failed to start interview: " + e.getMessage());
        }
    }

    // ==================== GET USER INTERVIEWS ====================
    public List<InterviewSession> getUserInterviews(String userId) {
        return interviewSessionRepository.findByUserIdOrderByStartedAtDesc(userId);
    }

    // ==================== SUBMIT ANSWER ====================
    public InterviewAnswerResponse submitAnswer(Long sessionId, InterviewAnswerRequest request) {
        try {
            logger.info("=== SUBMITTING ANSWER ===");
            logger.info("Session ID: {}", sessionId);
            logger.info("Question ID: {}", request.getQuestionId());
            logger.info("Answer: {}", request.getAnswerText());

            // Get session and previous responses
            Optional<InterviewSession> sessionOpt = interviewSessionRepository.findById(sessionId);
            if (sessionOpt.isEmpty()) {
                throw new RuntimeException("Interview session not found");
            }

            InterviewSession session = sessionOpt.get();
            List<InterviewResponse> previousResponses = interviewResponseRepository
                    .findByInterviewSessionIdOrderById(sessionId);

            // Evaluate answer
            InterviewEvaluationDTO evaluation;
            try {
                String evaluationPrompt = buildEvaluationPrompt(
                        session.getJobRole(),
                        session.getExperience(),
                        session.getDifficulty(),
                        request.getAnswerText(),
                        previousResponses
                );

                String geminiResponse = callGeminiApi(evaluationPrompt);
                evaluation = parseEvaluationFromGeminiResponse(geminiResponse);
            } catch (Exception e) {
                logger.warn("AI evaluation failed, using fallback evaluation: {}", e.getMessage());
                evaluation = new InterviewEvaluationDTO();
                evaluation.setScore(7);
                InterviewEvaluationDTO.FeedbackDTO fb = new InterviewEvaluationDTO.FeedbackDTO();
                fb.setStrengths("Good communication and relevant points discussed.");
                fb.setImprovements("Consider providing more concrete metrics or architectural depth.");
                evaluation.setFeedback(fb);
            }

            // Update current response with evaluation
            InterviewResponse currentResp = null;
            if (!previousResponses.isEmpty()) {
                currentResp = previousResponses.get(previousResponses.size() - 1);
                currentResp.setAnswerText(request.getAnswerText());
                currentResp.setScore(evaluation.getScore());
                currentResp.setStrengths(evaluation.getFeedback() != null ? evaluation.getFeedback().getStrengths() : "Good response.");
                currentResp.setImprovements(evaluation.getFeedback() != null ? evaluation.getFeedback().getImprovements() : "Continue practicing.");
                interviewResponseRepository.save(currentResp);
            }

            int questionNumber = previousResponses.size() + 1;
            InterviewQuestionDTO nextQuestion = null;

            // Generate next question if not reached max questions (limit 5 for full interview)
            if (questionNumber <= 5) {
                try {
                    String nextPrompt = buildQuestionGenerationPrompt(
                            session.getJobRole(),
                            session.getExperience(),
                            session.getDifficulty(),
                            previousResponses,
                            questionNumber
                    );
                    String nextQResponse = callGeminiApi(nextPrompt);
                    nextQuestion = parseQuestionFromGeminiResponse(nextQResponse);
                } catch (Exception e) {
                    logger.warn("AI next question generation failed, using fallback question: {}", e.getMessage());
                    nextQuestion = new InterviewQuestionDTO();
                    nextQuestion.setQuestionId(String.valueOf(System.currentTimeMillis()));
                    nextQuestion.setQuestionText(getDefaultQuestion(session.getJobRole(), questionNumber));
                }

                InterviewResponse nextEntity = new InterviewResponse();
                nextEntity.setInterviewSession(session);
                nextEntity.setQuestionText(nextQuestion.getQuestionText());
                nextEntity.setDifficultyLevel(session.getDifficulty());
                nextEntity.setSequenceNumber(questionNumber);
                nextEntity = interviewResponseRepository.save(nextEntity);
                nextQuestion.setQuestionId(nextEntity.getId().toString());
            }

            InterviewAnswerResponse response = new InterviewAnswerResponse();
            response.setEvaluation(evaluation);
            response.setNextQuestion(nextQuestion);

            return response;

        } catch (Exception e) {
            logger.error("Failed to submit answer", e);
            throw new RuntimeException("Failed to submit answer: " + e.getMessage());
        }
    }

    // ==================== GENERATE REPORT ====================
    public InterviewReportDTO generateReport(Long sessionId) {
        try {
            InterviewSession session = interviewSessionRepository.findById(sessionId)
                    .orElseThrow(() -> new RuntimeException("Session not found: " + sessionId));

            List<InterviewResponse> responses = interviewResponseRepository
                    .findByInterviewSessionIdOrderById(sessionId);

            InterviewReportDTO report;
            try {
                String reportPrompt = buildReportPrompt(session.getJobRole(), session.getExperience(), session.getDifficulty(), responses);
                String geminiResponse = callGeminiApi(reportPrompt);
                report = parseReportFromGeminiResponse(geminiResponse);
            } catch (Exception e) {
                logger.warn("AI report generation failed, using calculated report: {}", e.getMessage());
                double avg = responses.stream().filter(r -> r.getScore() != null).mapToInt(InterviewResponse::getScore).average().orElse(7.0);
                report = new InterviewReportDTO();
                report.setOverallScore((int) Math.round(avg));
                report.setSummary("Completed " + responses.size() + " interview questions for " + session.getJobRole() + ".");
                report.setStrengthsOverview("Good conceptual understanding and structured thought process.");
                report.setWeaknessesOverview("Can elaborate more with real-world scenarios and edge cases.");
                report.setRecommendation("Recommended with further practice in system design and behavioral framing.");
            }

            // Save report
            InterviewReport reportEntity = new InterviewReport();
            reportEntity.setInterviewSession(session);
            reportEntity.setOverallScore(report.getOverallScore());
            reportEntity.setStrengthsOverview(report.getStrengthsOverview());
            reportEntity.setWeaknessesOverview(report.getWeaknessesOverview());
            reportEntity.setImprovementPlan(report.getSummary() != null ? report.getSummary() : "Continue practicing core questions.");
            reportEntity.setHireRecommendation(report.getRecommendation() != null ? report.getRecommendation() : "Recommended for next round.");
            reportEntity.setCreatedAt(LocalDateTime.now());
            interviewReportRepository.save(reportEntity);

            session.setStatus(InterviewSession.SessionStatus.COMPLETED);
            session.setCompletedAt(LocalDateTime.now());
            interviewSessionRepository.save(session);

            return report;

        } catch (Exception e) {
            logger.error("Failed to generate report", e);
            throw new RuntimeException("Failed to generate report: " + e.getMessage());
        }
    }

    // ==================== HELPER METHODS ====================

    private String callGeminiApi(String prompt) {
        if (geminiProperties.getApiKey() == null || geminiProperties.getApiKey().isBlank()) {
            throw new RuntimeException("Gemini API key is missing");
        }

        String apiKey = geminiProperties.getApiKey().trim();
        List<String> targetUrls = new ArrayList<>();

        if (geminiProperties.getApiUrl() != null && !geminiProperties.getApiUrl().isBlank()) {
            targetUrls.add(geminiProperties.getApiUrl());
        }

        for (String modelName : FALLBACK_MODELS) {
            String fallbackUrl = String.format("https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent", modelName);
            if (!targetUrls.contains(fallbackUrl)) {
                targetUrls.add(fallbackUrl);
            }
        }

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(Map.of("text", prompt)))
                )
        );

        String lastErrorMsg = null;
        for (int i = 0; i < targetUrls.size(); i++) {
            String currentUrl = targetUrls.get(i);
            try {
                String fullUrl = currentUrl.contains("?key=") ? currentUrl : (currentUrl + "?key=" + apiKey);

                String response = webClient.post()
                        .uri(fullUrl)
                        .header("Content-Type", "application/json")
                        .header("x-goog-api-key", apiKey)
                        .bodyValue(requestBody)
                        .retrieve()
                        .onStatus(status -> status.isError(), clientResponse -> {
                            return clientResponse.bodyToMono(String.class)
                                    .flatMap(errorBody -> reactor.core.publisher.Mono.error(
                                            new RuntimeException("API " + clientResponse.statusCode() + ": " + errorBody)));
                        })
                        .bodyToMono(String.class)
                        .timeout(Duration.ofSeconds(6))
                        .block();

                if (response != null && !response.isBlank()) {
                    logger.info("Gemini API call succeeded with model endpoint: {}", currentUrl);
                    return response;
                }
            } catch (Exception e) {
                lastErrorMsg = e.getMessage();
                logger.warn("Gemini API attempt failed on [{}]: {}. Trying next fallback...", currentUrl, e.getMessage());
            }
        }

        throw new RuntimeException("All Gemini endpoints failed: " + lastErrorMsg);
    }

    private String buildQuestionGenerationPrompt(String jobRole, String experience, String difficulty,
                                                 List<InterviewResponse> previousResponses, int questionNumber) {

        StringBuilder prompt = new StringBuilder();
        prompt.append("You are an expert technical interviewer conducting an interview for a ").append(jobRole).append(" role.\n\n");
        prompt.append("Candidate Level: ").append(experience).append(", Difficulty: ").append(difficulty).append("\n");
        prompt.append("Current Question Number: ").append(questionNumber).append("\n\n");

        if (questionNumber == 1) {
            prompt.append("Generate a friendly, tailored introductory interview question asking the candidate about their background, experiences, and interest in the ").append(jobRole).append(" position.\n");
        } else {
            prompt.append("Generate a focused technical or behavioral interview question appropriate for a ").append(jobRole).append(" with ").append(experience).append(" experience.\n");
        }

        prompt.append("\nOUTPUT FORMAT (STRICT JSON ONLY):\n");
        prompt.append("{\n");
        prompt.append("  \"questionId\": \"").append(System.currentTimeMillis()).append("\",\n");
        prompt.append("  \"questionText\": \"Your question text here\"\n");
        prompt.append("}\n");

        return prompt.toString();
    }

    private String buildEvaluationPrompt(String jobRole, String experience, String difficulty,
                                         String answerText, List<InterviewResponse> previousResponses) {

        return String.format("""
                You are evaluating a candidate's answer for a %s interview (%s level, %s difficulty).
                Candidate's Answer: "%s"

                Evaluate and provide score (0-10) and concise constructive feedback.

                OUTPUT FORMAT (STRICT JSON ONLY):
                {
                  "score": 8,
                  "feedback": {
                    "strengths": "Strengths of the response",
                    "improvements": "Areas to improve"
                  }
                }
                """, jobRole, experience, difficulty, answerText);
    }

    private String buildReportPrompt(String jobRole, String experience, String difficulty,
                                     List<InterviewResponse> responses) {

        return String.format("""
                Generate a comprehensive final interview report for a %s (%s level) candidate.
                Total Questions Answered: %d

                OUTPUT FORMAT (STRICT JSON ONLY):
                {
                  "overallScore": 8,
                  "summary": "Summary of candidate performance",
                  "strengthsOverview": "Key technical and behavioral strengths",
                  "weaknessesOverview": "Areas for growth",
                  "recommendation": "Strong Hire / Hire / Needs Improvement"
                }
                """, jobRole, experience, responses.size());
    }

    private String extractJsonFromGeminiResponse(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode candidates = root.path("candidates");
            if (candidates.isArray() && candidates.size() > 0) {
                JsonNode parts = candidates.get(0).path("content").path("parts");
                if (parts.isArray() && parts.size() > 0) {
                    String text = parts.get(0).path("text").asText().trim();
                    int start = text.indexOf('{');
                    int end = text.lastIndexOf('}');
                    if (start != -1 && end != -1) {
                        return text.substring(start, end + 1);
                    }
                    return text;
                }
            }
        } catch (Exception e) {
            logger.warn("Could not extract JSON from response wrapper: {}", e.getMessage());
        }
        return response;
    }

    private InterviewQuestionDTO parseQuestionFromGeminiResponse(String response) {
        try {
            String jsonText = extractJsonFromGeminiResponse(response);
            JsonNode root = objectMapper.readTree(jsonText);
            JsonNode nextQ = root.has("nextQuestion") ? root.path("nextQuestion") : root;

            String qText = nextQ.path("questionText").asText();
            if (qText.isEmpty()) {
                qText = nextQ.path("question").asText();
            }
            if (qText.isEmpty()) {
                qText = "Can you describe a challenging problem you solved in your recent work?";
            }

            InterviewQuestionDTO dto = new InterviewQuestionDTO();
            dto.setQuestionId(String.valueOf(System.currentTimeMillis()));
            dto.setQuestionText(qText);
            return dto;
        } catch (Exception e) {
            logger.error("Failed to parse question from Gemini response", e);
            throw new RuntimeException("Failed to parse question: " + e.getMessage());
        }
    }

    private InterviewEvaluationDTO parseEvaluationFromGeminiResponse(String response) {
        try {
            String jsonText = extractJsonFromGeminiResponse(response);
            JsonNode root = objectMapper.readTree(jsonText);

            int score = root.path("score").asInt(7);
            JsonNode fbNode = root.path("feedback");
            String strengths = fbNode.path("strengths").asText("Clear response and relevant examples.");
            String improvements = fbNode.path("improvements").asText("Could dive deeper into specific trade-offs.");

            InterviewEvaluationDTO dto = new InterviewEvaluationDTO();
            dto.setScore(score);
            InterviewEvaluationDTO.FeedbackDTO fb = new InterviewEvaluationDTO.FeedbackDTO();
            fb.setStrengths(strengths);
            fb.setImprovements(improvements);
            dto.setFeedback(fb);
            return dto;
        } catch (Exception e) {
            logger.error("Failed to parse evaluation", e);
            throw new RuntimeException("Failed to parse evaluation: " + e.getMessage());
        }
    }

    private InterviewReportDTO parseReportFromGeminiResponse(String response) {
        try {
            String jsonText = extractJsonFromGeminiResponse(response);
            JsonNode root = objectMapper.readTree(jsonText);

            InterviewReportDTO dto = new InterviewReportDTO();
            dto.setOverallScore(root.path("overallScore").asInt(7));
            dto.setSummary(root.path("summary").asText("Candidate showed good problem-solving ability."));
            dto.setStrengthsOverview(root.path("strengthsOverview").asText("Strong communication and domain fundamentals."));
            dto.setWeaknessesOverview(root.path("weaknessesOverview").asText("Can elaborate more on edge case handling."));
            dto.setRecommendation(root.path("recommendation").asText("Recommended for next stage."));
            return dto;
        } catch (Exception e) {
            logger.error("Failed to parse report", e);
            throw new RuntimeException("Failed to parse report: " + e.getMessage());
        }
    }

    private String getDefaultQuestion(String jobRole, int number) {
        switch (number) {
            case 2:
                return "What core tools, frameworks, and methodologies do you rely on most in your " + jobRole + " workflow?";
            case 3:
                return "Can you walk me through how you approach debugging or diagnosing a critical performance issue in a " + jobRole + " project?";
            case 4:
                return "Describe a time when you had a disagreement with a team member or stakeholder on technical direction. How did you resolve it?";
            default:
                return "What emerging trends or technologies in " + jobRole + " are you currently learning or excited about?";
        }
    }
}
