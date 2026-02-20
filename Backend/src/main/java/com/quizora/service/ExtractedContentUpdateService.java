// package com.quizora.service;

// import com.quizora.entity.ExtractedContent;
// import com.quizora.repository.ExtractedContentRepositoryNew;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

// @Service
// public class ExtractedContentUpdateService {
    
//     private static final Logger logger = LoggerFactory.getLogger(ExtractedContentUpdateService.class);
    
//     private final ExtractedContentRepositoryNew extractedContentRepository;
    
//     public ExtractedContentUpdateService(ExtractedContentRepositoryNew extractedContentRepository) {
//         this.extractedContentRepository = extractedContentRepository;
//     }
    
//     @Transactional
//     public ExtractedContent updateExtractedText(Long id, String extractedText) {
//         logger.info("Updating extracted text for content ID: {}", id);
//         logger.info("Extracted text length: {} characters", extractedText.length());
        
//         java.util.Optional<ExtractedContent> contentOpt = extractedContentRepository.findById(id);
//         if (contentOpt.isPresent()) {
//             ExtractedContent content = contentOpt.get();
//             content.setExtractedText(extractedText);
//             ExtractedContent saved = extractedContentRepository.save(content);
//             logger.info("Successfully updated extracted content with ID: {}", saved.getId());
//             return saved;
//         } else {
//             throw new RuntimeException("Extracted content not found with ID: " + id);
//         }
//     }
// }


package com.quizora.service;

import com.quizora.entity.ExtractedContent;
import com.quizora.repository.ExtractedContentRepository; // ✅ FIXED
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ExtractedContentUpdateService {

    private static final Logger logger = LoggerFactory.getLogger(ExtractedContentUpdateService.class);

    private final ExtractedContentRepository extractedContentRepository; // ✅ FIXED

    public ExtractedContentUpdateService(ExtractedContentRepository extractedContentRepository) { // ✅ FIXED
        this.extractedContentRepository = extractedContentRepository;
    }

    @Transactional
    public ExtractedContent updateExtractedText(Long id, String extractedText) {
        logger.info("Updating extracted text for content ID: {}", id);
        logger.info("Extracted text length: {} characters", extractedText.length());

        java.util.Optional<ExtractedContent> contentOpt = extractedContentRepository.findById(id);

        if (contentOpt.isPresent()) {
            ExtractedContent content = contentOpt.get();
            content.setExtractedText(extractedText);
            ExtractedContent saved = extractedContentRepository.save(content);
            logger.info("Successfully updated extracted content with ID: {}", saved.getId());
            return saved;
        } else {
            throw new RuntimeException("Extracted content not found with ID: " + id);
        }
    }
}