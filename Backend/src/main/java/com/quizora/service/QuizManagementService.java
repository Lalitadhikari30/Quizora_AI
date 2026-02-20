package com.quizora.service;

import com.quizora.entity.Quiz;
import com.quizora.entity.Question;
import com.quizora.repository.QuizRepository;
import com.quizora.repository.QuestionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@Service
public class QuizManagementService {
    
    private static final Logger logger = LoggerFactory.getLogger(QuizManagementService.class);
    
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    
    public QuizManagementService(QuizRepository quizRepository, QuestionRepository questionRepository) {
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
    }
    
    @Transactional
    public Quiz saveQuiz(Long extractedContentId, List<Map<String, Object>> questionsData) {
        try {
            logger.info("Saving quiz for extracted content ID: {}", extractedContentId);
            logger.info("Number of questions: {}", questionsData.size());
            
            // Create quiz entity
            Quiz quiz = new Quiz();
            quiz.setSourceContent(extractedContentId.toString());
            quiz = quizRepository.save(quiz);
            
            logger.info("Quiz created with ID: {}", quiz.getId());
            
            // Save questions
            for (int i = 0; i < questionsData.size(); i++) {
                Map<String, Object> questionData = questionsData.get(i);
                
                Question question = new Question();
                question.setQuiz(quiz);
                question.setQuestionText((String) questionData.get("question"));
                
                @SuppressWarnings("unchecked")
                List<String> options = (List<String>) questionData.get("options");
                question.setOptions(String.join(",", options));
                
                question.setCorrectAnswer(((Number) questionData.get("correctAnswer")).toString());
                
                questionRepository.save(question);
                logger.info("Saved question {}: {}", i + 1, question.getQuestionText());
            }
            
            logger.info("Successfully saved quiz with {} questions", questionsData.size());
            return quiz;
            
        } catch (Exception e) {
            logger.error("Failed to save quiz: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save quiz: " + e.getMessage(), e);
        }
    }
}
