package com.quizora.service;

import com.quizora.entity.SourceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContentExtractionService {

    @Autowired
    private PdfExtractionService pdfExtractionService;
    
    @Autowired
    private YoutubeTranscriptService youtubeTranscriptService;

    public String extractContent(String content, SourceType sourceType) {
        switch (sourceType) {
            case TEXT:
                return content;
            case PDF:
                return pdfExtractionService.extractTextFromPdf(content);
            case YOUTUBE:
                return youtubeTranscriptService.getTranscript(content);
            default:
                throw new IllegalArgumentException("Unsupported source type: " + sourceType);
        }
    }
}
