package com.quizora.controller;

import com.quizora.dto.*;
import com.quizora.entity.InterviewSession;
import com.quizora.service.InterviewServiceSimple;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/interview")
@CrossOrigin(origins = "http://localhost:3000")
public class InterviewController {

    private static final Logger logger = LoggerFactory.getLogger(InterviewController.class);

    private final InterviewServiceSimple interviewService;

    public InterviewController(InterviewServiceSimple interviewService) {
        this.interviewService = interviewService;
    }

    // ==================== START INTERVIEW ====================
    @PostMapping("/start")
    public ResponseEntity<?> startInterview(
            @Valid @RequestBody InterviewStartRequest request,
            Authentication authentication) {

        try {
            String userId = authentication.getName();
            logger.info("Starting interview for user: {}", userId);

            InterviewStartResponse response = interviewService.startInterview(userId, request);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "sessionId", response.getSessionId(),
                "nextQuestion", response.getNextQuestion()
            ));

        } catch (Exception e) {
            logger.error("Failed to start interview", e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", "Failed to start interview: " + e.getMessage()
            ));
        }
    }

    // ==================== SUBMIT ANSWER ====================
    @PostMapping("/{sessionId}/answer")
    public ResponseEntity<?> submitAnswer(
            @PathVariable Long sessionId,
            @Valid @RequestBody InterviewAnswerRequest request,
            Authentication authentication) {

        try {
            String userId = authentication.getName();
            logger.info("Submitting answer for session: {}", sessionId);

            InterviewAnswerResponse response = interviewService.submitAnswer(sessionId, request);
            
            Map<String, Object> responseBody = new java.util.HashMap<>();
            responseBody.put("success", true);
            responseBody.put("evaluation", response.getEvaluation());

            if (response.getNextQuestion() != null) {
                responseBody.put("nextQuestion", response.getNextQuestion());
            } else {
                responseBody.put("nextQuestion", null);
                responseBody.put("message", "Interview completed - generating report");
            }

            return ResponseEntity.ok(responseBody);

        } catch (Exception e) {
            logger.error("Failed to submit answer", e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", "Failed to submit answer: " + e.getMessage()
            ));
        }
    }

    // ==================== GET REPORT ====================
    @GetMapping("/{sessionId}/report")
    public ResponseEntity<?> getReport(
            @PathVariable Long sessionId,
            Authentication authentication) {

        try {
            String userId = authentication.getName();
            logger.info("Generating report for session: {}", sessionId);

            InterviewReportDTO report = interviewService.generateReport(sessionId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "report", report
            ));

        } catch (Exception e) {
            logger.error("Failed to generate report", e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", "Failed to generate report: " + e.getMessage()
            ));
        }
    }

    // ==================== LIST INTERVIEWS ====================
    @GetMapping
    public ResponseEntity<?> getInterviews(Authentication authentication) {
        try {
            String userId = authentication.getName();
            logger.info("Getting interviews for user: {}", userId);

            List<InterviewSession> interviews = interviewService.getUserInterviews(userId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "interviews", interviews
            ));

        } catch (Exception e) {
            logger.error("Failed to get interviews", e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", "Failed to get interviews: " + e.getMessage()
            ));
        }
    }
}
