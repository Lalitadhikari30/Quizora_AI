package com.quizora.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Service
public class InMemoryTextExtractionService {
    
    private static final Logger logger = LoggerFactory.getLogger(InMemoryTextExtractionService.class);
    
    public String extractText(byte[] fileBytes, String fileName) throws IOException {
        String extension = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
        
        logger.info("Extracting text from file: {} (type: {})", fileName, extension);
        logger.info("File size: {} bytes", fileBytes.length);
        
        String extractedText;
        
        switch (extension) {
            case ".pdf":
                extractedText = extractFromPdf(fileBytes);
                break;
            case ".docx":
                extractedText = extractFromDocx(fileBytes);
                break;
            case ".txt":
                extractedText = extractFromTxt(fileBytes);
                break;
            default:
                throw new IllegalArgumentException("Unsupported file type: " + extension + ". Supported types: PDF, DOCX, TXT");
        }
        
        if (extractedText.trim().isEmpty()) {
            throw new RuntimeException("No text could be extracted from the file");
        }
        
        logger.info("Successfully extracted {} characters", extractedText.length());
        return extractedText.trim();
    }
    
    private String extractFromPdf(byte[] fileBytes) throws IOException {
        try (PDDocument document = PDDocument.load(new ByteArrayInputStream(fileBytes))) {
            PDFTextStripper textStripper = new PDFTextStripper();
            String text = textStripper.getText(document);
            logger.info("PDF extraction completed: {} characters", text.length());
            return text.trim();
        }
    }
    
    private String extractFromDocx(byte[] fileBytes) throws IOException {
        try (XWPFDocument document = new XWPFDocument(new ByteArrayInputStream(fileBytes));
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
            
            String text = extractor.getText();
            logger.info("DOCX extraction completed: {} characters", text.length());
            return text.trim();
        }
    }
    
    private String extractFromTxt(byte[] fileBytes) throws IOException {
        String text = new String(fileBytes, "UTF-8");
        logger.info("TXT extraction completed: {} characters", text.length());
        return text.trim();
    }
}
