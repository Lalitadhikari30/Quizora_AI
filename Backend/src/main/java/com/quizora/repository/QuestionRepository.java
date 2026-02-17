package com.quizora.repository;

import com.quizora.entity.Question;
import com.quizora.entity.QuestionType;
import com.quizora.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    
    List<Question> findByQuizId(Long quizId);
    
    List<Question> findByQuizIdOrderById(Long quizId);
    
    @Query("SELECT q FROM Question q WHERE q.quiz.id = :quizId AND q.type = :type")
    List<Question> findByQuizIdAndType(@Param("quizId") Long quizId, @Param("type") QuestionType type);
    
    @Query("SELECT q FROM Question q WHERE q.quiz.id = :quizId ORDER BY q.id")
    List<Question> findByQuizIdOrderByOrder(@Param("quizId") Long quizId);
}
