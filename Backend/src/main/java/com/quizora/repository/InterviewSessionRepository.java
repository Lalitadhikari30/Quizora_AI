package com.quizora.repository;

import com.quizora.entity.InterviewSession;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InterviewSessionRepository extends JpaRepository<InterviewSession, Long> {

    List<InterviewSession> findByUserId(String userId);

    List<InterviewSession> findByUserIdOrderByStartedAtDesc(String userId);

    Optional<InterviewSession> findByIdAndUserId(Long id, String userId);

    @Query("SELECT i FROM InterviewSession i WHERE i.userId = :userId AND i.status = 'ACTIVE'")
    List<InterviewSession> findActiveByUserId(@Param("userId") String userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM InterviewSession i WHERE i.userId = :userId AND i.status = 'ACTIVE'")
    Optional<InterviewSession> findActiveByUserIdForUpdate(@Param("userId") String userId);
}
