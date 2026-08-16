package com.quizora.service;

import com.quizora.dto.*;
import com.quizora.entity.*;
import com.quizora.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuizService {

    private static final Logger logger =
            LoggerFactory.getLogger(QuizService.class);

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuizAttemptRepository quizAttemptRepository;

    @Autowired
    private UserPerformanceRepository userPerformanceRepository;

    @Autowired
    private ContentExtractionService contentExtractionService;

    @Autowired
    private AiIntegrationService aiIntegrationService;

    /* =====================================================
       NORMAL QUIZ GENERATION (Manual Input)
       ===================================================== */
    @Transactional
    public QuizResponse generateQuiz(
            QuizGenerationRequest request,
            String userId) {

        try {
            String sourceTypeStr = (request.getSourceType() != null && !request.getSourceType().trim().isEmpty())
                    ? request.getSourceType().trim().toUpperCase()
                    : "TEXT";

            String extractedContent =
                    contentExtractionService.extractContent(
                            request.getSourceContent(),
                            SourceType.valueOf(sourceTypeStr)
                    );

            int qCount = (request.getQuestionCount() != null && request.getQuestionCount() > 0)
                    ? request.getQuestionCount()
                    : 10;
            String difficulty = (request.getDifficulty() != null && !request.getDifficulty().isBlank())
                    ? request.getDifficulty()
                    : "INTERMEDIATE";
            int timePerQ = (request.getTimePerQuestion() != null && request.getTimePerQuestion() > 0)
                    ? request.getTimePerQuestion()
                    : 60;
            int totalTime = (request.getTimeLimit() != null && request.getTimeLimit() > 0)
                    ? request.getTimeLimit()
                    : (qCount * timePerQ);

            return generateQuizFromText(
                    userId,
                    extractedContent,
                    request.getTitle(),
                    qCount,
                    difficulty,
                    totalTime,
                    timePerQ
            );

        } catch (Exception e) {
            logger.error("Failed to generate quiz", e);
            throw new RuntimeException("Failed to generate quiz: " + e.getMessage());
        }
    }

    /* =====================================================
       FILE BASED QUIZ GENERATION
       ===================================================== */
    @Transactional
    public QuizResponse generateQuizFromText(
            String userId,
            String extractedText,
            String fileName) {
        return generateQuizFromText(userId, extractedText, fileName, 10, "INTERMEDIATE", 600, 60);
    }

    @Transactional
    public QuizResponse generateQuizFromText(
            String userId,
            String extractedText,
            String fileName,
            int questionCount,
            String difficulty,
            int totalTimeSeconds,
            int timePerQuestionSeconds) {

        try {
            int effectiveCount = Math.max(1, Math.min(30, questionCount));
            String effectiveDiff = (difficulty != null && !difficulty.isBlank()) ? difficulty : "INTERMEDIATE";

            List<QuizQuestionDTO> generatedQuestions =
                    aiIntegrationService.generateQuizFromContent(
                            extractedText,
                            effectiveDiff.toLowerCase(),
                            "general",
                            effectiveCount
                    );

            if (generatedQuestions.isEmpty()) {
                throw new RuntimeException("No questions generated");
            }

            int calcTotalTime = (totalTimeSeconds > 0) ? totalTimeSeconds : (effectiveCount * timePerQuestionSeconds);

            Quiz quiz = new Quiz();
            quiz.setTitle("Quiz from " + fileName);
            quiz.setDescription("Auto-generated quiz [timeLimit:" + calcTotalTime + ",timePerQuestion:" + timePerQuestionSeconds + "]");
            quiz.setUserId(userId);
            quiz.setType(QuizType.MULTIPLE_CHOICE);
            quiz.setSourceContent(extractedText);
            quiz.setSourceType(SourceType.PDF);
            quiz.setCreatedAt(LocalDateTime.now());
            quiz.setUpdatedAt(LocalDateTime.now());

            quiz = quizRepository.save(quiz);

            List<Question> questions = new ArrayList<>();

            com.fasterxml.jackson.databind.ObjectMapper jsonMapper = new com.fasterxml.jackson.databind.ObjectMapper();

            for (QuizQuestionDTO dto : generatedQuestions) {

                Question question = new Question();
                question.setQuiz(quiz);
                question.setQuestionText(dto.getQuestion());
                question.setCorrectAnswer(dto.getAnswer());
                question.setExplanation(dto.getExplanation());
                question.setDifficulty(getDifficultyValue(dto.getDifficulty()));
                question.setType(QuestionType.MULTIPLE_CHOICE);

                // Store options safely as JSON array
                try {
                    question.setOptions(jsonMapper.writeValueAsString(dto.getOptions()));
                } catch (Exception ex) {
                    question.setOptions(String.join(",", dto.getOptions()));
                }
                question.setTopicTags(String.join(",", dto.getTopics()));

                questions.add(question);
            }

            questionRepository.saveAll(questions);

            QuizResponse response = mapToQuizResponse(quiz, questions);
            response.setQuestionCount(questions.size());
            response.setTimeLimit(calcTotalTime);
            response.setTimePerQuestion(timePerQuestionSeconds);

            return response;

        } catch (Exception e) {
            logger.error("Failed to generate quiz from text", e);
            throw new RuntimeException("Quiz generation failed: " + e.getMessage());
        }
    }

    /* =====================================================
       GET QUIZ
       ===================================================== */
    public QuizResponse getQuiz(Long quizId, String userId) {

        Quiz quiz = quizRepository.findById(quizId)
                .orElseGet(() -> quizRepository.findByIdAndUserId(quizId, userId)
                        .orElseThrow(() -> new RuntimeException("Quiz not found with ID: " + quizId)));

        List<Question> questions =
                questionRepository.findByQuizIdOrderById(quizId);

        return mapToQuizResponse(quiz, questions);
    }

    /* =====================================================
       SUBMIT QUIZ (FINAL CORRECT VERSION)
       ===================================================== */
    @Transactional
    public QuizResultResponse submitQuiz(
            Long quizId,
            List<AnswerSubmitRequest> answers,
            String userId) {

        Quiz quiz = quizRepository.findById(quizId)
                .orElseGet(() -> quizRepository.findByIdAndUserId(quizId, userId)
                        .orElseThrow(() -> new RuntimeException("Quiz not found with ID: " + quizId)));

        List<Question> questions =
                questionRepository.findByQuizIdOrderById(quizId);

        int correctCount = 0;
        List<AnswerReviewResponse> reviewList = new ArrayList<>();

        for (AnswerSubmitRequest submitted : answers) {

            Question question = questions.stream()
                    .filter(q -> q.getId().equals(submitted.getQuestionId()))
                    .findFirst()
                    .orElseThrow(() ->
                            new RuntimeException("Question not found"));

            List<String> options = parseQuestionOptions(question.getOptions());

            Integer selectedIndex = submitted.getSelectedAnswer();
            String userAnswer = null;

            if (selectedIndex != null &&
                    selectedIndex >= 0 &&
                    selectedIndex < options.size()) {
                userAnswer = options.get(selectedIndex);
            }

            String correctAnswer = question.getCorrectAnswer();
            boolean isCorrect = isAnswerCorrect(userAnswer, selectedIndex, correctAnswer, options);

            if (isCorrect) correctCount++;

            AnswerReviewResponse review = new AnswerReviewResponse();
            review.setQuestionId(question.getId());
            review.setQuestion(question.getQuestionText());
            review.setUserAnswer(userAnswer);
            review.setCorrectAnswer(correctAnswer);
            review.setIsCorrect(isCorrect);
            review.setExplanation(question.getExplanation());

            reviewList.add(review);
        }

        double percentage = answers.isEmpty()
                ? 0
                : ((double) correctCount / answers.size()) * 100;

        QuizAttempt attempt = new QuizAttempt();
        attempt.setQuiz(quiz);
        attempt.setUserId(userId);
        attempt.setTotalQuestions(answers.size());
        attempt.setCorrectAnswers(correctCount);
        attempt.setScore(correctCount);
        attempt.setPercentage(percentage);
        attempt.setCompletionTime(LocalDateTime.now());

        quizAttemptRepository.save(attempt);

        // Update aggregated UserPerformance record
        try {
            updateUserPerformance(userId);
        } catch (Exception pe) {
            logger.warn("Failed to update user performance: {}", pe.getMessage());
        }

        QuizResultResponse result = new QuizResultResponse();
        result.setQuizId(quizId);
        result.setTitle(quiz.getTitle());
        result.setTotalQuestions(answers.size());
        result.setCorrectAnswers(correctCount);
        result.setScore(correctCount);
        result.setPercentage(percentage);
        result.setAnswerReviews(reviewList);

        return result;
    }

    private void updateUserPerformance(String userId) {
        List<QuizAttempt> attempts = quizAttemptRepository.findByUserId(userId);
        if (attempts.isEmpty()) return;

        double totalScorePercent = 0.0;
        for (QuizAttempt a : attempts) {
            double p = a.getPercentage();
            if (p == 0.0 && a.getTotalQuestions() > 0) {
                p = ((double) a.getCorrectAnswers() / a.getTotalQuestions()) * 100.0;
            }
            totalScorePercent += p;
        }
        double avgScore = totalScorePercent / attempts.size();

        UserPerformance perf = userPerformanceRepository.findByUserId(userId)
                .orElse(new UserPerformance());

        perf.setUserId(userId);
        perf.setTotalQuizzesTaken(attempts.size());
        perf.setAverageQuizScore(Math.round(avgScore * 100.0) / 100.0);
        perf.setLastUpdated(LocalDateTime.now());

        userPerformanceRepository.save(perf);
        logger.info("Updated UserPerformance for {}: {} quizzes, avg score {}%", userId, attempts.size(), perf.getAverageQuizScore());
    }

    /* =====================================================
       GET USER QUIZZES
       ===================================================== */
    public List<QuizResponse> getUserQuizzes(String userId) {

        List<Quiz> quizzes =
                quizRepository.findByUserIdOrderByCreatedAtDesc(userId);

        return quizzes.stream()
                .map(q -> {
                    List<Question> questions =
                            questionRepository.findByQuizIdOrderById(q.getId());
                    return mapToQuizResponse(q, questions);
                })
                .collect(Collectors.toList());
    }

    /* =====================================================
       HELPER METHODS
       ===================================================== */
    private QuizResponse mapToQuizResponse(
            Quiz quiz,
            List<Question> questions) {

        QuizResponse response = new QuizResponse();
        response.setId(quiz.getId());
        response.setTitle(quiz.getTitle());
        response.setDescription(quiz.getDescription());
        response.setType(quiz.getType().toString());
        response.setSourceType(quiz.getSourceType().toString());
        response.setSourceContent(quiz.getSourceContent());

        response.setQuestions(
                questions.stream().map(q -> {
                    QuestionResponse qr = new QuestionResponse();
                    qr.setId(q.getId());
                    qr.setQuestionText(q.getQuestionText());
                    qr.setType(q.getType().toString());
                    qr.setCorrectAnswer(q.getCorrectAnswer());
                    qr.setOptions(q.getOptions());
                    qr.setExplanation(q.getExplanation());
                    qr.setDifficulty(q.getDifficulty());
                    qr.setTopicTags(q.getTopicTags());
                    return qr;
                }).collect(Collectors.toList())
        );

        response.setQuestionCount(questions.size());

        int defaultTimePerQ = 60;
        int defaultTotalTime = questions.size() * defaultTimePerQ;

        if (quiz.getDescription() != null) {
            String desc = quiz.getDescription();
            if (desc.contains("[timeLimit:")) {
                try {
                    int start = desc.indexOf("[timeLimit:") + 11;
                    int end = desc.indexOf(",", start);
                    if (end == -1) end = desc.indexOf("]", start);
                    if (end != -1) {
                        defaultTotalTime = Integer.parseInt(desc.substring(start, end).trim());
                    }
                } catch (Exception ignored) {}
            }
            if (desc.contains("timePerQuestion:")) {
                try {
                    int start = desc.indexOf("timePerQuestion:") + 16;
                    int end = desc.indexOf("]", start);
                    if (end != -1) {
                        defaultTimePerQ = Integer.parseInt(desc.substring(start, end).trim());
                    }
                } catch (Exception ignored) {}
            }
        }

        response.setTimeLimit(defaultTotalTime);
        response.setTimePerQuestion(defaultTimePerQ);

        return response;
    }

    private Integer getDifficultyValue(String difficulty) {
        if (difficulty == null) return 2;

        switch (difficulty.toLowerCase()) {
            case "beginner": return 1;
            case "advanced": return 3;
            default: return 2;
        }
    }

    private List<String> parseQuestionOptions(String rawOptions) {
        List<String> options = new ArrayList<>();
        if (rawOptions == null || rawOptions.trim().isEmpty()) {
            return options;
        }

        String raw = rawOptions.trim();
        if (raw.startsWith("[") && raw.endsWith("]")) {
            try {
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                List<String> parsed = mapper.readValue(raw, new com.fasterxml.jackson.core.type.TypeReference<List<String>>() {});
                if (parsed != null && !parsed.isEmpty()) {
                    for (String opt : parsed) {
                        if (opt != null) {
                            options.add(opt.trim());
                        }
                    }
                    return options;
                }
            } catch (Exception ignored) {}
        }

        // Delimiter splitting fallback (| or ,)
        String delimiter = raw.contains("|") ? "\\|" : ",";
        String[] split = raw.split(delimiter);
        for (String opt : split) {
            String clean = opt.trim()
                    .replaceAll("^\\[?\"?", "")
                    .replaceAll("\"?\\]?$", "")
                    .trim();
            if (!clean.isEmpty()) {
                options.add(clean);
            }
        }
        return options;
    }

    private boolean isAnswerCorrect(String userAnswer, Integer selectedIndex, String correctAnswer, List<String> options) {
        if (correctAnswer == null || correctAnswer.trim().isEmpty()) {
            return false;
        }

        String normCorrect = normalizeAnswer(correctAnswer);

        // 1. If correct answer is an index ("0", "1", "2", "3")
        if (selectedIndex != null) {
            if (correctAnswer.trim().equals(selectedIndex.toString())) {
                return true;
            }
        }

        // 2. Direct normalized match with user answer
        if (userAnswer != null) {
            String normUser = normalizeAnswer(userAnswer);
            if (normUser.equals(normCorrect)) {
                return true;
            }

            // Substring or prefix matching if long enough
            if (normUser.length() > 5 && (normCorrect.startsWith(normUser) || normUser.startsWith(normCorrect))) {
                return true;
            }
            if (normUser.length() > 5 && (normCorrect.contains(normUser) || normUser.contains(normCorrect))) {
                return true;
            }
        }

        // 3. Match against options list
        if (selectedIndex != null && selectedIndex >= 0 && selectedIndex < options.size()) {
            String optText = options.get(selectedIndex);
            String normOpt = normalizeAnswer(optText);
            if (normOpt.equals(normCorrect)) {
                return true;
            }
            if (normOpt.length() > 5 && (normCorrect.startsWith(normOpt) || normOpt.startsWith(normCorrect))) {
                return true;
            }
            if (normOpt.length() > 5 && (normCorrect.contains(normOpt) || normOpt.contains(normCorrect))) {
                return true;
            }
        }

        return false;
    }

    private String normalizeAnswer(String text) {
        if (text == null) return "";
        return text.trim()
                .toLowerCase()
                .replaceAll("^(option\\s+[a-d][:.]?|[a-d][.:)]\\s*)", "")
                .replaceAll("[^a-z0-9]", "");
    }
}
