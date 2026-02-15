package com.quizora.service;

import com.fasterxml.jackson.databind.JsonNode;
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
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuizService {
    
    private static final Logger logger = LoggerFactory.getLogger(QuizService.class);
    
    @Autowired
    private QuizRepository quizRepository;
    
    @Autowired
    private QuestionRepository questionRepository;
    
    @Autowired
    private QuizAttemptRepository quizAttemptRepository;
    
    @Autowired
    private ContentExtractionService contentExtractionService;
    
    @Autowired
    // private AiIntegrationService aiIntegrationService;
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public QuizResponse generateQuiz(QuizGenerationRequest request, String userId) {
        try {
            // Extract content based on source type
            String extractedContent = contentExtractionService.extractContent(
                request.getSourceContent(), 
                SourceType.valueOf(request.getSourceType())
            );
            
            // Generate questions using AI (mock implementation for testing)
            // List<String> generatedQuestions = aiIntegrationService.generateQuizQuestions(
            //     extractedContent,
            //     request.getQuestionCount() != null ? request.getQuestionCount() : 10,
            //     request.getDifficulty() != null ? request.getDifficulty() : "medium",
            //     request.getTopics() != null ? request.getTopics() : "general"
            // );
            
            // Mock questions for testing
            List<String> generatedQuestions = List.of(
                "What is JavaScript? A programming language for web development.",
                "What is a variable? A container for storing data values.",
                "What is a function? A block of code designed to perform a particular task.",
                "What is an array? A data structure that stores multiple values.",
                "What is DOM? Document Object Model, programming interface for web documents."
            );
            
            // Create quiz entity
            Quiz quiz = new Quiz();
            quiz.setTitle(request.getTitle());
            quiz.setDescription(request.getDescription());
            quiz.setUserId(userId);
            quiz.setType(QuizType.valueOf(request.getType()));
            quiz.setSourceContent(request.getSourceContent());
            quiz.setSourceType(SourceType.valueOf(request.getSourceType()));
            
            quiz = quizRepository.save(quiz);
            
            // Parse and create questions
            List<Question> questions = parseAndCreateQuestions(generatedQuestions, quiz);
            
            logger.info("Generated quiz with {} questions for user: {}", questions.size(), userId);
            
            return mapToQuizResponse(quiz, questions);
            
        } catch (Exception e) {
            logger.error("Failed to generate quiz", e);
            throw new RuntimeException("Failed to generate Quiz: " + e.getMessage());
        }
    }

    private List<Question> parseAndCreateQuestions(List<String> questionTexts, Quiz quiz) {
        List<Question> questions = new ArrayList<>();
        
        for (int i = 0; i < questionTexts.size(); i++) {
            try {
                String questionJson = questionTexts.get(i);
                JsonNode questionNode = objectMapper.readTree(questionJson);
                
                Question question = new Question();
                question.setQuiz(quiz);
                question.setQuestionText(questionNode.path("question").asText());
                question.setType(QuestionType.valueOf(questionNode.path("type").asText()));
                question.setCorrectAnswer(questionNode.path("correct_answer").asText());
                
                if (questionNode.has("options")) {
                    question.setOptions(questionNode.path("options").asText());
                }
                
                if (questionNode.has("explanation")) {
                    question.setExplanation(questionNode.path("explanation").asText());
                }
                
                if (questionNode.has("difficulty")) {
                    question.setDifficulty(questionNode.path("difficulty").asInt());
                }
                
                if (questionNode.has("topic_tags")) {
                    question.setTopicTags(questionNode.path("topic_tags").asText());
                }
                
                questions.add(questionRepository.save(question));
                
            } catch (Exception e) {
                logger.error("Failed to parse question: {}", questionTexts.get(i), e);
                // Create a fallback question
                Question fallbackQuestion = new Question();
                fallbackQuestion.setQuiz(quiz);
                fallbackQuestion.setQuestionText(questionTexts.get(i));
                fallbackQuestion.setType(QuestionType.MULTIPLE_CHOICE);
                fallbackQuestion.setCorrectAnswer("A");
                fallbackQuestion.setOptions("A. Option A\nB. Option B\nC. Option C\nD. Option D");
                fallbackQuestion.setExplanation("Generated question - please review");
                fallbackQuestion.setDifficulty(2);
                fallbackQuestion.setTopicTags("general");
                
                questions.add(questionRepository.save(fallbackQuestion));
            }
        }
        
        return questions;
    }

    private QuizResponse mapToQuizResponse(Quiz quiz, List<Question> questions) {
        QuizResponse response = new QuizResponse();
        response.setId(quiz.getId());
        response.setTitle(quiz.getTitle());
        response.setDescription(quiz.getDescription());
        response.setType(quiz.getType().toString());
        response.setSourceType(quiz.getSourceType().toString());
        response.setSourceContent(quiz.getSourceContent());
        response.setQuestions(questions.stream()
                .map(q -> {
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
                })
                .collect(Collectors.toList()));
        
        return response;
    }

    public QuizResponse getQuiz(Long quizId, String userId) {
        Quiz quiz = quizRepository.findByIdAndUserId(quizId, userId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
        
        List<Question> questions = questionRepository.findByQuizIdOrderById(quizId);
        
        return mapToQuizResponse(quiz, questions);
    }

    @Transactional
    public QuizResultResponse submitQuiz(Long quizId, List<AnswerReviewResponse> answers, String userId) {
        try {
            Quiz quiz = quizRepository.findByIdAndUserId(quizId, userId)
                    .orElseThrow(() -> new RuntimeException("Quiz not found"));
            
            QuizAttempt attempt = new QuizAttempt();
            attempt.setQuiz(quiz);
            attempt.setUserId(userId);
            attempt.setTotalQuestions(answers.size());
            
            int correctCount = 0;
            List<AnswerReviewResponse> answerReviews = new ArrayList<>();
            
            for (int i = 0; i < answers.size(); i++) {
                AnswerReviewResponse review = new AnswerReviewResponse();
                review.setQuestion(answers.get(i).getQuestion());
                review.setUserAnswer(answers.get(i).getUserAnswer());
                review.setCorrectAnswer(answers.get(i).getCorrectAnswer());
                review.setIsCorrect(answers.get(i).getIsCorrect());
                
                if (answers.get(i).getIsCorrect()) {
                    correctCount++;
                }
                
                answerReviews.add(review);
            }
            
            attempt.setCorrectAnswers(correctCount);
            attempt.setScore(correctCount);
            attempt.setCompletionTime(LocalDateTime.now());
            
            quizAttemptRepository.save(attempt);
            
            QuizResultResponse result = new QuizResultResponse();
            result.setQuizId(quizId);
            result.setTitle(quiz.getTitle());
            result.setTotalQuestions(answers.size());
            result.setCorrectAnswers(correctCount);
            result.setScore(correctCount);
            result.setPercentage((double) correctCount / answers.size() * 100);
            result.setAnswerReviews(answerReviews);
            
            logger.info("Quiz submitted: {} correct out of {} for user: {}", correctCount, answers.size(), userId);
            
            return result;
            
        } catch (Exception e) {
            logger.error("Failed to submit quiz", e);
            throw new RuntimeException("Failed to submit Quiz: " + e.getMessage());
        }
    }

    public List<QuizResponse> getUserQuizzes(String userId) {
        List<Quiz> quizzes = quizRepository.findByUserIdOrderByCreatedAtDesc(userId);
        
        return quizzes.stream()
                .map(quiz -> {
                    List<Question> questions = questionRepository.findByQuizIdOrderById(quiz.getId());
                    return mapToQuizResponse(quiz, questions);
                })
                .collect(Collectors.toList());
    }
}
