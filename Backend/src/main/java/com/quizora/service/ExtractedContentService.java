package com.quizora.service;

import com.quizora.entity.ExtractedContent;
import com.quizora.repository.ExtractedContentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
public class ExtractedContentService {
    
    private static final Logger logger = LoggerFactory.getLogger(ExtractedContentService.class);
    
    @Autowired
    private ExtractedContentRepository extractedContentRepository;
    
    @Autowired
    private FileUploadService fileUploadService;
    
    @Autowired
    private ContentExtractionService contentExtractionService;
    
    /**
     * Upload file, extract text, and store in database
     * Returns the extractedContentId for later quiz generation
     */
    @Transactional
    public ExtractedContent uploadAndExtract(MultipartFile file, String userId) {
        try {
            logger.info("=== UPLOAD AND EXTRACTION START ===");
            logger.info("User ID: {}", userId);
            logger.info("File: {}", file.getOriginalFilename());
            logger.info("File size: {} bytes", file.getSize());
            
            // Step 1: Upload file to Supabase Storage
            logger.info("Step 1: Uploading file to Supabase Storage...");
            String fileUrl = fileUploadService.uploadFile(file, userId);
            logger.info("File uploaded successfully: {}", fileUrl);
            
            // Step 2: Extract text from file
            logger.info("Step 2: Extracting text from file...");
            String extractedText = contentExtractionService.extractContent(file);
            
            // Validate extracted text
            if (extractedText == null || extractedText.trim().isEmpty()) {
                throw new RuntimeException("EXTRACTION_EMPTY: No text could be extracted from file");
            }
            
            logger.info("Text extracted successfully. Length: {} characters", extractedText.length());
            logger.info("Text preview: {}", extractedText.length() > 200 ? extractedText.substring(0, 200) + "..." : extractedText);
            
            // Step 3: Store extracted content in database
            logger.info("Step 3: Storing extracted content in database...");
            ExtractedContent extractedContent = new ExtractedContent(
                userId,
                file.getOriginalFilename(),
                fileUrl,
                extractedText
            );
            
            ExtractedContent savedContent = extractedContentRepository.save(extractedContent);
            logger.info("ExtractedContent saved with id: {}", savedContent.getId());
            
            logger.info("=== UPLOAD AND EXTRACTION SUCCESS ===");
            
            return savedContent;
            
        } catch (Exception e) {
            logger.error("UPLOAD_AND_EXTRACTION_FAILED: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to upload and extract file: " + e.getMessage());
        }
    }
    
    /**
     * Get extracted content by ID and user (security check)
     */
    public Optional<ExtractedContent> getExtractedContent(Long id, String userId) {
        logger.info("Getting extracted content: id={}, userId={}", id, userId);
        return extractedContentRepository.findByIdAndUserId(id, userId);
    }
    
    /**
     * Update extracted content status and quiz ID after successful AI generation
     */
    @Transactional
    public void markAsAiGenerated(Long extractedContentId, Long quizId) {
        logger.info("Marking extracted content as AI generated: extractedContentId={}, quizId={}", extractedContentId, quizId);
        
        Optional<ExtractedContent> contentOpt = extractedContentRepository.findById(extractedContentId);
        if (contentOpt.isPresent()) {
            ExtractedContent content = contentOpt.get();
            content.markAsAiGenerated(quizId);
            extractedContentRepository.save(content);
            logger.info("ExtractedContent marked as AI generated successfully");
        } else {
            throw new RuntimeException("ExtractedContent not found: " + extractedContentId);
        }
    }
    
    /**
     * Mark extracted content as failed with error message
     */
    @Transactional
    public void markAsFailed(Long extractedContentId, String errorMessage) {
        logger.info("Marking extracted content as failed: extractedContentId={}, error={}", extractedContentId, errorMessage);
        
        Optional<ExtractedContent> contentOpt = extractedContentRepository.findById(extractedContentId);
        if (contentOpt.isPresent()) {
            ExtractedContent content = contentOpt.get();
            content.markAsFailed(errorMessage);
            extractedContentRepository.save(content);
            logger.info("ExtractedContent marked as failed successfully");
        } else {
            throw new RuntimeException("ExtractedContent not found: " + extractedContentId);
        }
    }
    
    /**
     * Get user's extracted contents
     */
    public java.util.List<ExtractedContent> getUserExtractedContents(String userId) {
        logger.info("Getting extracted contents for user: {}", userId);
        return extractedContentRepository.findByUserId(userId);
    }
    
    /**
     * Get user's failed extractions for retry
     */
    public java.util.List<ExtractedContent> getUserFailedExtractions(String userId) {
        logger.info("Getting failed extractions for user: {}", userId);
        return extractedContentRepository.findFailedExtractionsByUserId(userId);
    }
    
    /**
     * Validate extracted text is safe for Gemini API
     */
    public String validateAndPrepareTextForGemini(String extractedText) {
        if (extractedText == null || extractedText.trim().isEmpty()) {
            throw new RuntimeException("GEMINI_INPUT_EMPTY: Extracted text cannot be null or empty");
        }
        
        // Truncate if too long for Gemini (conservative limit)
        int maxGeminiLength = 12000;
        if (extractedText.length() > maxGeminiLength) {
            logger.warn("Text too long for Gemini ({} chars), truncating to {}", extractedText.length(), maxGeminiLength);
            extractedText = extractedText.substring(0, maxGeminiLength) + "... [Content truncated for Gemini processing]";
        }
        
        logger.info("Text prepared for Gemini: {} characters", extractedText.length());
        return extractedText;
    }
}
