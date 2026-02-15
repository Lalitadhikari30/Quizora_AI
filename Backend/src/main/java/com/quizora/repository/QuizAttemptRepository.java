package com.quizora.repository;

import com.quizora.entity.QuizAttempt;
import com.quizora.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {
    
    List<QuizAttempt> findByUserId(String userId);
    
    List<QuizAttempt> findByUserIdOrderByCreatedAtDesc(String userId);
    
    List<QuizAttempt> findByQuizId(Long quizId);
    
    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.userId = :userId AND qa.quiz.id = :quizId")
    QuizAttempt findByUserIdAndQuizId(@Param("userId") String userId, @Param("quizId") Long quizId);
    
    @Query("SELECT COUNT(qa) FROM QuizAttempt qa WHERE qa.userId = :userId")
    long countByUserId(String userId);
}
