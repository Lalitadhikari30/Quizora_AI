package com.quizora.repository;

import com.quizora.entity.ExtractedContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExtractedContentRepository extends JpaRepository<ExtractedContent, Long> {
    
    /**
     * Find all extracted contents for a user
     */
    List<ExtractedContent> findByUserId(String userId);
    
    /**
     * Find extracted contents by user and status
     */
    List<ExtractedContent> findByUserIdAndStatus(String userId, ExtractedContent.Status status);
    
    /**
     * Find extracted content by ID and user (security check)
     */
    Optional<ExtractedContent> findByIdAndUserId(Long id, String userId);
    
    /**
     * Find extracted contents by status
     */
    List<ExtractedContent> findByStatus(ExtractedContent.Status status);
    
    /**
     * Find extracted contents created after a specific date
     */
    @Query("SELECT ec FROM ExtractedContent ec WHERE ec.userId = :userId AND ec.createdAt >= :date")
    List<ExtractedContent> findByUserIdAndCreatedAtAfter(@Param("userId") String userId, @Param("date") LocalDateTime date);
    
    /**
     * Find extracted contents by file name (for duplicate checking)
     */
    @Query("SELECT ec FROM ExtractedContent ec WHERE ec.userId = :userId AND ec.fileName = :fileName")
    Optional<ExtractedContent> findByUserIdAndFileName(@Param("userId") String userId, @Param("fileName") String fileName);
    
    /**
     * Count extracted contents by user and status
     */
    @Query("SELECT COUNT(ec) FROM ExtractedContent ec WHERE ec.userId = :userId AND ec.status = :status")
    long countByUserIdAndStatus(@Param("userId") String userId, @Param("status") ExtractedContent.Status status);
    
    /**
     * Find extracted contents that need retry (failed status)
     */
    @Query("SELECT ec FROM ExtractedContent ec WHERE ec.userId = :userId AND ec.status = 'FAILED' ORDER BY ec.updatedAt DESC")
    List<ExtractedContent> findFailedExtractionsByUserId(@Param("userId") String userId);
    
    /**
     * Find extracted contents with extracted text length
     */
    @Query("SELECT ec.id, ec.fileName, LENGTH(ec.extractedText) as textLength FROM ExtractedContent ec WHERE ec.userId = :userId")
    List<Object[]> findExtractedContentInfoByUserId(@Param("userId") String userId);
}
