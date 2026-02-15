package com.quizora.repository;

import com.quizora.entity.InterviewSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InterviewSessionRepository extends JpaRepository<InterviewSession, Long> {
    
    List<InterviewSession> findByUserId(String userId);
    
    List<InterviewSession> findByUserIdOrderByCreatedAtDesc(String userId);
    
    Optional<InterviewSession> findByIdAndUserId(Long id, String userId);
    
    @Query("SELECT is FROM InterviewSession is WHERE is.userId = :userId AND is.isActive = true")
    List<InterviewSession> findActiveByUserId(@Param("userId") String userId);
    
    @Query("SELECT is FROM InterviewSession is WHERE is.userId = :userId AND is.isActive = true FOR UPDATE")
    Optional<InterviewSession> findActiveByUserIdForUpdate(@Param("userId") String userId);
}
