package com.quizora.controller;

import com.quizora.dto.InterviewReportResponse;
import com.quizora.entity.QuizAttempt;
import com.quizora.entity.UserPerformance;
import com.quizora.repository.InterviewSessionRepository;
import com.quizora.repository.QuizAttemptRepository;
import com.quizora.repository.QuizRepository;
import com.quizora.repository.UserPerformanceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/performance")
@CrossOrigin(origins = {"http://localhost:3000"})
public class PerformanceController {
    
    private static final Logger logger = LoggerFactory.getLogger(PerformanceController.class);
    
    @Autowired
    private UserPerformanceRepository userPerformanceRepository;

    @Autowired
    private QuizAttemptRepository quizAttemptRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private InterviewSessionRepository interviewSessionRepository;

    private static final String[] CATEGORY_COLORS = new String[]{
            "#f97316", "#fb923c", "#fdba74", "#fed7aa", "#38bdf8", "#34d399", "#a78bfa"
    };

    @GetMapping
    public ResponseEntity<?> getUserPerformance(Authentication authentication) {
        try {
            String userId = authentication.getName();
            List<QuizAttempt> attempts = quizAttemptRepository.findByUserIdOrderByCompletionTimeDesc(userId);

            int totalQuizzes = attempts.size();
            double avgScore = 0.0;

            if (!attempts.isEmpty()) {
                double sum = 0.0;
                for (QuizAttempt a : attempts) {
                    double p = a.getPercentage();
                    if (p == 0.0 && a.getTotalQuestions() > 0) {
                        p = ((double) a.getCorrectAnswers() / a.getTotalQuestions()) * 100.0;
                    }
                    sum += p;
                }
                avgScore = sum / attempts.size();
            }

            // Calculate study streak (distinct consecutive days with quiz/interview activity)
            Set<LocalDate> activeDays = attempts.stream()
                    .filter(a -> a.getCompletionTime() != null)
                    .map(a -> a.getCompletionTime().toLocalDate())
                    .collect(Collectors.toSet());
            int studyStreak = calculateStreak(activeDays);

            // Estimate study time (approx 15 mins = 0.25h per quiz attempt)
            double studyTimeHours = Math.round((totalQuizzes * 0.25) * 10.0) / 10.0;

            // Generate category breakdown based on actual quizzes
            List<Map<String, Object>> categories = buildCategoryData(attempts);

            Map<String, Object> result = new HashMap<>();
            result.put("userId", userId);
            result.put("totalQuizzesTaken", totalQuizzes);
            result.put("totalQuizzes", totalQuizzes);
            result.put("averageQuizScore", Math.round(avgScore * 10.0) / 10.0);
            result.put("averageScore", Math.round(avgScore * 10.0) / 10.0);
            result.put("studyStreak", studyStreak);
            result.put("totalStudyTime", studyTimeHours);
            result.put("categories", categories);
            result.put("lastUpdated", LocalDateTime.now().toString());

            logger.info("Retrieved real performance data for user {}: {} quizzes, avg score {}%", userId, totalQuizzes, avgScore);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            logger.error("Failed to get user performance", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/history")
    public ResponseEntity<?> getPerformanceHistory(Authentication authentication) {
        try {
            String userId = authentication.getName();
            List<QuizAttempt> attempts = quizAttemptRepository.findByUserIdOrderByCompletionTimeDesc(userId);

            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM d");
            List<Map<String, Object>> history = new ArrayList<>();

            // Oldest first for the line chart progression
            List<QuizAttempt> chronological = new ArrayList<>(attempts);
            Collections.reverse(chronological);

            for (QuizAttempt a : chronological) {
                double p = a.getPercentage();
                if (p == 0.0 && a.getTotalQuestions() > 0) {
                    p = ((double) a.getCorrectAnswers() / a.getTotalQuestions()) * 100.0;
                }

                Map<String, Object> item = new HashMap<>();
                item.put("id", a.getId());
                item.put("quizTitle", a.getQuiz() != null ? a.getQuiz().getTitle() : "Quiz #" + a.getId());
                item.put("score", Math.round(p * 10.0) / 10.0);
                item.put("percentage", Math.round(p * 10.0) / 10.0);
                item.put("correctAnswers", a.getCorrectAnswers());
                item.put("totalQuestions", a.getTotalQuestions());
                item.put("date", a.getCompletionTime() != null ? a.getCompletionTime().format(fmt) : "Recent");
                item.put("timestamp", a.getCompletionTime() != null ? a.getCompletionTime().toString() : null);

                history.add(item);
            }

            logger.info("Retrieved {} performance history records for user: {}", history.size(), userId);
            return ResponseEntity.ok(history);

        } catch (Exception e) {
            logger.error("Failed to get performance history", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/interviews")
    public ResponseEntity<?> getInterviewPerformance(Authentication authentication) {
        try {
            String userId = authentication.getName();
            UserPerformance performance = userPerformanceRepository.findByUserId(userId)
                    .orElse(new UserPerformance());

            InterviewReportResponse summary = new InterviewReportResponse();
            summary.setUserId(userId);
            summary.setTotalInterviews(performance.getTotalInterviewsTaken());
            summary.setAverageInterviewScore(performance.getAverageInterviewScore());
            summary.setStrongestTopics(performance.getStrongestTopics());
            summary.setImprovementAreas(performance.getImprovementAreas());

            logger.info("Retrieved interview performance for user: {}", userId);
            return ResponseEntity.ok(summary);

        } catch (Exception e) {
            logger.error("Failed to get interview performance", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    private int calculateStreak(Set<LocalDate> activeDays) {
        if (activeDays.isEmpty()) return 0;

        LocalDate today = LocalDate.now();
        int streak = 0;
        LocalDate current = today;

        if (!activeDays.contains(current)) {
            current = today.minusDays(1);
        }

        while (activeDays.contains(current)) {
            streak++;
            current = current.minusDays(1);
        }

        return Math.max(streak, activeDays.isEmpty() ? 0 : 1);
    }

    private List<Map<String, Object>> buildCategoryData(List<QuizAttempt> attempts) {
        if (attempts.isEmpty()) {
            return List.of(
                    Map.of("name", "Document Quizzes", "value", 100, "score", 0, "color", "#f97316")
            );
        }

        Map<String, List<Double>> categoryScores = new HashMap<>();

        for (QuizAttempt a : attempts) {
            String title = (a.getQuiz() != null && a.getQuiz().getTitle() != null)
                    ? a.getQuiz().getTitle()
                    : "General";

            String category = extractCategoryName(title);
            double score = a.getPercentage();
            if (score == 0.0 && a.getTotalQuestions() > 0) {
                score = ((double) a.getCorrectAnswers() / a.getTotalQuestions()) * 100.0;
            }

            categoryScores.computeIfAbsent(category, k -> new ArrayList<>()).add(score);
        }

        int totalItems = attempts.size();
        List<Map<String, Object>> result = new ArrayList<>();
        int colorIdx = 0;

        for (Map.Entry<String, List<Double>> entry : categoryScores.entrySet()) {
            double avg = entry.getValue().stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            int percentShare = (int) Math.round(((double) entry.getValue().size() / totalItems) * 100.0);

            Map<String, Object> cat = new HashMap<>();
            cat.put("name", entry.getKey());
            cat.put("value", percentShare);
            cat.put("score", Math.round(avg * 10.0) / 10.0);
            cat.put("color", CATEGORY_COLORS[colorIdx % CATEGORY_COLORS.length]);
            result.add(cat);
            colorIdx++;
        }

        return result;
    }

    private String extractCategoryName(String title) {
        String lower = title.toLowerCase();
        if (lower.contains("java") || lower.contains("spring")) return "Java / Spring";
        if (lower.contains("python") || lower.contains("django") || lower.contains("flask")) return "Python";
        if (lower.contains("react") || lower.contains("front") || lower.contains("html") || lower.contains("css")) return "Frontend";
        if (lower.contains("db") || lower.contains("sql") || lower.contains("database") || lower.contains("postgres")) return "Databases";
        if (lower.contains("ai") || lower.contains("ml") || lower.contains("machine learning")) return "AI & ML";
        if (lower.contains("farm") || lower.contains("report") || lower.contains("project") || lower.contains("internship")) return "Project Reports";
        
        // Clean up "Quiz from filename.docx" -> "filename"
        if (title.startsWith("Quiz from ")) {
            String sub = title.substring(10);
            int dot = sub.lastIndexOf('.');
            return dot > 0 ? sub.substring(0, dot) : sub;
        }

        return title.length() > 20 ? title.substring(0, 18) + "..." : title;
    }
}
