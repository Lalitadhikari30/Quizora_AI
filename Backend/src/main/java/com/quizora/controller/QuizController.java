package com.quizora.controller;

import com.quizora.dto.*;
import com.quizora.service.QuizService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/quizzes")
@CrossOrigin(origins = {"http://localhost:3000"})
public class QuizController {
    
    private static final Logger logger = LoggerFactory.getLogger(QuizController.class);
    
    @Autowired
    private QuizService quizService;

    @PostMapping("/generate")
    public ResponseEntity<QuizResponse> generateQuiz(
            @RequestBody QuizGenerationRequest request,
            Authentication authentication) {
        try {
            String userId = authentication.getName();
            QuizResponse response = quizService.generateQuiz(request, userId);
            
            logger.info("Generated quiz for user: {}", userId);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Failed to generate quiz", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuizResponse> getQuiz(@PathVariable Long id) {
        try {
            String userId = SecurityContextHolder.getContext().getAuthentication().getName();
            QuizResponse response = quizService.getQuiz(id, userId);
            
            logger.info("Retrieved quiz {} for user: {}", id, userId);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Failed to get Quiz", e);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<QuizResultResponse> submitQuiz(
            @PathVariable Long id,
            @RequestBody List<AnswerSubmitRequest> answers,
            Authentication authentication) {
        try {
            String userId = authentication.getName();
            QuizResultResponse result = quizService.submitQuiz(id, answers, userId);
            
            logger.info("Submitted quiz {} for user: {}", id, userId);
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            logger.error("Failed to submit Quiz", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<?> getUserQuizzes(Authentication authentication) {
        try {
            String userId = authentication.getName();
            List<QuizResponse> quizzes = quizService.getUserQuizzes(userId);
            
            logger.info("Retrieved {} quizzes for user: {}", quizzes.size(), userId);
            return ResponseEntity.ok(quizzes);
            
        } catch (Exception e) {
            logger.error("Failed to get user quizzes", e);
            return ResponseEntity.badRequest().build();
        }
    }
}
