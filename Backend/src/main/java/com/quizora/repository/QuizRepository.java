package com.quizora.repository;

import com.quizora.entity.Quiz;
import com.quizora.entity.QuizType;
import com.quizora.entity.QuestionType;
import com.quizora.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    
    List<Quiz> findByUserId(String userId);
    
    List<Quiz> findByUserIdOrderByCreatedAtDesc(String userId);
    
    Optional<Quiz> findByIdAndUserId(Long id, String userId);
    
    @Query("SELECT q FROM Quiz q WHERE q.userId = :userId AND q.type = :type")
    List<Quiz> findByUserIdAndType(String userId, QuizType type);
    
    @Query("SELECT COUNT(q) FROM Quiz q WHERE q.userId = :userId")
    long countByUserId(String userId);
}
