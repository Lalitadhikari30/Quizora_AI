package com.quizora.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
public class TextExtractionService {
    
    private static final Logger logger = LoggerFactory.getLogger(TextExtractionService.class);
    private static final int MIN_TEXT_LENGTH = 200;

    public String extractText(MultipartFile file) {
        try {
            String fileName = file.getOriginalFilename();
            String contentType = file.getContentType();
            
            logger.info("Extracting text from file: {} (type: {})", fileName, contentType);
            
            String extractedText;
            
            if (fileName != null) {
                if (fileName.toLowerCase().endsWith(".pdf") || "application/pdf".equals(contentType)) {
                    extractedText = extractFromPdf(file.getInputStream());
                } else if (fileName.toLowerCase().endsWith(".docx") || 
                          contentType != null && contentType.contains("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
                    extractedText = extractFromDocx(file.getInputStream());
                } else if (fileName.toLowerCase().endsWith(".txt") || 
                          "text/plain".equals(contentType)) {
                    extractedText = extractFromTxt(file.getInputStream());
                } else {
                    throw new IllegalArgumentException("Unsupported file type: " + fileName + " (Content-Type: " + contentType + ")");
                }
            } else {
                throw new IllegalArgumentException("File name is null");
            }
            
            // Validate extracted text
            if (extractedText == null || extractedText.trim().isEmpty()) {
                throw new RuntimeException("No text could be extracted from the file");
            }
            
            if (extractedText.trim().length() < MIN_TEXT_LENGTH) {
                throw new RuntimeException("Extracted text is too short. Minimum " + MIN_TEXT_LENGTH + " characters required. Got: " + extractedText.trim().length());
            }
            
            logger.info("Successfully extracted {} characters from file: {}", extractedText.length(), fileName);
            return extractedText.trim();
            
        } catch (IOException e) {
            logger.error("Failed to extract text from file: {}", file.getOriginalFilename(), e);
            throw new RuntimeException("Failed to extract text from file: " + e.getMessage());
        }
    }
    
    private String extractFromPdf(InputStream pdfStream) throws IOException {
        try (PDDocument document = PDDocument.load(pdfStream)) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            logger.debug("Extracted text from PDF stream: {} characters", text.length());
            return text.trim();
        }
    }
    
    private String extractFromDocx(InputStream docxStream) throws IOException {
        try (XWPFDocument document = new XWPFDocument(docxStream);
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
            String text = extractor.getText();
            logger.debug("Extracted text from DOCX stream: {} characters", text.length());
            return text.trim();
        }
    }
    
    private String extractFromTxt(InputStream txtStream) throws IOException {
            StringBuilder textBuilder = new StringBuilder();
            byte[] buffer = new byte[1024];
            int bytesRead;
            
            while ((bytesRead = txtStream.read(buffer)) != -1) {
                textBuilder.append(new String(buffer, 0, bytesRead));
            }
            
            String text = textBuilder.toString();
            logger.debug("Extracted text from TXT stream: {} characters", text.length());
            return text.trim();
    }
}
