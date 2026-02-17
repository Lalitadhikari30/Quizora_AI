package com.quizora.repository;

import com.quizora.entity.UserPerformance;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserPerformanceRepository extends JpaRepository<UserPerformance, Long> {

    Optional<UserPerformance> findByUserId(String userId);

    UserPerformance findByUserIdOrderByLastUpdatedDesc(String userId);

    @Query("SELECT up FROM UserPerformance up WHERE up.userId = :userId")
    UserPerformance findByUserIdWithLock(@Param("userId") String userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT up FROM UserPerformance up WHERE up.userId = :userId")
    UserPerformance findByUserIdForUpdate(@Param("userId") String userId);
}
