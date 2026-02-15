package com.quizora.controller;

import com.quizora.dto.InterviewReportResponse;
import com.quizora.entity.UserPerformance;
import com.quizora.repository.UserPerformanceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/performance")
@CrossOrigin(origins = {"http://localhost:3000"})
public class PerformanceController {
    
    private static final Logger logger = LoggerFactory.getLogger(PerformanceController.class);
    
    @Autowired
    private UserPerformanceRepository userPerformanceRepository;

    @GetMapping
    public ResponseEntity<?> getUserPerformance(Authentication authentication) {
        try {
            String userId = authentication.getName();
            UserPerformance performance = userPerformanceRepository.findByUserId(userId)
                    .orElse(new UserPerformance());
            
            logger.info("Retrieved performance data for user: {}", userId);
            return ResponseEntity.ok(performance);
            
        } catch (Exception e) {
            logger.error("Failed to get user performance", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/history")
    public ResponseEntity<?> getPerformanceHistory(Authentication authentication) {
        try {
            String userId = authentication.getName();
            UserPerformance performance = userPerformanceRepository.findByUserIdOrderByLastUpdatedDesc(userId);
            
            logger.info("Retrieved performance data for user: {}", userId);
            return ResponseEntity.ok(performance);
            
        } catch (Exception e) {
            logger.error("Failed to get performance history", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/interviews")
    public ResponseEntity<?> getInterviewPerformance(Authentication authentication) {
        try {
            String userId = authentication.getName();
            UserPerformance performance = userPerformanceRepository.findByUserId(userId)
                    .orElse(new UserPerformance());
            
            // Create interview performance summary
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
            return ResponseEntity.badRequest().build();
        }
    }
}
