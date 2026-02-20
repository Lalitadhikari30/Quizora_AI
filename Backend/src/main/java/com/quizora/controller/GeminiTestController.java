package com.quizora.controller;

import com.quizora.dto.QuizQuestionDTO;
import com.quizora.service.AiIntegrationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GeminiTestController {

    private final AiIntegrationService aiService;

    public GeminiTestController(AiIntegrationService aiService) {
        this.aiService = aiService;
    }

    @GetMapping("/api/test-gemini")
    public String testGemini() {
        try {
            String sampleText = "Artificial Intelligence is the simulation of human intelligence by machines.";

            List<QuizQuestionDTO> result = aiService.generateQuizFromContent(
                    sampleText,
                    "easy",
                    "AI",
                    2
            );

            return "✅ GEMINI WORKING\n\nQuestions Generated: " + result.size();

        } catch (Exception e) {
            return "❌ GEMINI FAILED\n\nError:\n" + e.getMessage();
        }
    }
}
