package com.quizora.repository;

import com.quizora.entity.InterviewResponse;
import com.quizora.entity.InterviewSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterviewResponseRepository extends JpaRepository<InterviewResponse, Long> {
    
    List<InterviewResponse> findByInterviewSessionId(Long sessionId);
    
    @Query("SELECT ir FROM InterviewResponse ir WHERE ir.interviewSession.id = :sessionId ORDER BY ir.createdAt ASC")
    List<InterviewResponse> findByInterviewSessionIdOrderById(@Param("sessionId") Long sessionId);
    
    @Query("SELECT COUNT(ir) FROM InterviewResponse ir WHERE ir.interviewSession.id = :sessionId")
    long countBySessionId(@Param("sessionId") Long sessionId);
}
