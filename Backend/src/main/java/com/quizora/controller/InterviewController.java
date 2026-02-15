package com.quizora.controller;

import com.quizora.dto.*;
import com.quizora.service.InterviewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/interview")
@CrossOrigin(origins = {"http://localhost:3000"})
public class InterviewController {
    
    private static final Logger logger = LoggerFactory.getLogger(InterviewController.class);
    
    @Autowired
    private InterviewService interviewService;

    @PostMapping("/start")
    public ResponseEntity<InterviewSessionResponse> startInterview(
            @RequestBody InterviewStartRequest request,
            Authentication authentication) {
        try {
            String userId = authentication.getName();
            InterviewSessionResponse response = interviewService.startInterview(request, userId);
            
            logger.info("Started interview for user: {}", userId);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Failed to start interview", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{sessionId}/answer")
    public ResponseEntity<String> submitAnswer(
            @PathVariable Long sessionId,
            @RequestParam String question,
            @RequestParam String userAnswer,
            Authentication authentication) {
        try {
            String userId = authentication.getName();
            String result = interviewService.submitAnswer(sessionId, question, userAnswer, userId);
            
            logger.info("Answer submitted for session {}: {}", sessionId, result);
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            logger.error("Failed to submit answer", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{sessionId}/report")
    public ResponseEntity<InterviewReportResponse> getInterviewReport(@PathVariable Long sessionId, Authentication authentication) {
        try {
            String userId = authentication.getName();
            InterviewReportResponse report = interviewService.completeInterview(sessionId, userId);
            
            logger.info("Generated interview report for session {}: {}", sessionId);
            return ResponseEntity.ok(report);
            
        } catch (Exception e) {
            logger.error("Failed to generate interview report", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<?> getUserInterviews(Authentication authentication) {
        try {
            String userId = authentication.getName();
            var interviews = interviewService.getUserInterviews(userId);
            
            logger.info("Retrieved {} interviews for user: {}", interviews.size(), userId);
            return ResponseEntity.ok(interviews);
            
        } catch (Exception e) {
            logger.error("Failed to get user interviews", e);
            return ResponseEntity.badRequest().build();
        }
    }
}
