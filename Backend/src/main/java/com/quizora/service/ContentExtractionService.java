package com.quizora.service;

import com.quizora.entity.SourceType;
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
public class ContentExtractionService {
    
    private static final Logger logger = LoggerFactory.getLogger(ContentExtractionService.class);

    /**
     * Extract text from uploaded file (PDF/DOCX/TXT)
     * Pipeline: File → Apache PDFBox/POI extraction → Extracted TEXT
     */
    public String extractContent(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        String fileExtension = getFileExtension(fileName);
        
        logger.info("=== CONTENT EXTRACTION START ===");
        logger.info("File name: {}", fileName);
        logger.info("File extension: {}", fileExtension);
        logger.info("File size: {} bytes", file.getSize());
        logger.info("Content type: {}", file.getContentType());
        
        try {
            String extractedText;
            
            switch (fileExtension.toLowerCase()) {
                case "pdf":
                    logger.info("Extracting text from PDF using Apache PDFBox");
                    extractedText = extractFromPdf(file);
                    break;
                case "docx":
                    logger.info("Extracting text from DOCX using Apache POI");
                    extractedText = extractFromDocx(file);
                    break;
                case "txt":
                    logger.info("Extracting text from TXT file");
                    extractedText = extractFromTxt(file);
                    break;
                default:
                    logger.error("Unsupported file type: {}", fileExtension);
                    throw new IllegalArgumentException("Unsupported file type: " + fileExtension + ". Supported types: PDF, DOCX, TXT");
            }
            
            // Normalize extracted text
            extractedText = normalizeExtractedText(extractedText);
            
            // Explicit check for empty text
            if (extractedText == null || extractedText.trim().isEmpty()) {
                String errorMsg = String.format("Extracted text is empty for file: %s (size: %d bytes)", fileName, file.getSize());
                logger.error("TEXT_EXTRACTION_EMPTY: {}", errorMsg);
                throw new RuntimeException("TEXT_EXTRACTION_EMPTY: " + errorMsg);
            }
            
            logger.info("Extraction completed successfully");
            logger.info("Extracted text length: {} characters", extractedText.length());
            logger.info("Extracted text preview: {}", extractedText.length() > 200 ? extractedText.substring(0, 200) + "..." : extractedText);
            logger.info("=== CONTENT EXTRACTION END ===");
            
            return extractedText;
            
        } catch (Exception e) {
            logger.error("Failed to extract content from file: {}", fileName, e);
            throw new RuntimeException("Content extraction failed: " + e.getMessage());
        }
    }
    
    /**
     * Normalize extracted text by removing excessive whitespace and normalizing line breaks
     */
    private String normalizeExtractedText(String text) {
        if (text == null) {
            return "";
        }
        
        // Remove excessive whitespace
        text = text.replaceAll("\\s+", " ").trim();
        
        // Normalize line breaks
        text = text.replaceAll("\\r\\n|\\r|\\n", " ");
        
        // Remove control characters
        text = text.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "");
        
        return text;
    }

    /**
     * Extract content from string based on source type (legacy support)
     */
    public String extractContent(String content, SourceType sourceType) {
        logger.info("Extracting content from string with source type: {}", sourceType);
        
        switch (sourceType) {
            case TEXT:
                return content;
            case PDF:
                // For PDF string content, return as-is (assuming it's already extracted)
                return content;
            case YOUTUBE:
                // This would need YouTube transcript service integration
                throw new UnsupportedOperationException("YouTube transcript extraction not implemented in this method");
            default:
                throw new IllegalArgumentException("Unsupported source type: " + sourceType);
        }
    }
    
    /**
     * Extract text from PDF using Apache PDFBox
     */
    private String extractFromPdf(MultipartFile file) throws IOException {
        logger.info("=== PDF EXTRACTION START ===");
        
        try (InputStream inputStream = file.getInputStream()) {
            // Validate PDF file format
            byte[] headerBytes = new byte[4];
            int bytesRead = inputStream.read(headerBytes);
            if (bytesRead < 4) {
                throw new RuntimeException("Invalid PDF file: file too small");
            }
            
            String header = new String(headerBytes);
            if (!header.startsWith("%PDF")) {
                throw new RuntimeException("Invalid PDF file format");
            }
            
            // Reset stream to beginning for PDF parsing
            inputStream.reset();
            
            // Load PDF with proper error handling
            try (PDDocument document = PDDocument.load(inputStream)) {
                
                logger.info("PDF loaded successfully, pages: {}", document.getNumberOfPages());
                
                PDFTextStripper stripper = new PDFTextStripper();
                String text = stripper.getText(document);
                
                logger.info("PDF text extraction completed");
                logger.info("Extracted PDF text length: {} characters", text.length());
                logger.info("PDF text preview: {}", text.length() > 200 ? text.substring(0, 200) + "..." : text);
                logger.info("=== PDF EXTRACTION END ===");
                
                return text.trim();
            }
            
        } catch (IOException e) {
            logger.error("PDF parsing failed: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to extract text from PDF: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error during PDF extraction: {}", e.getMessage(), e);
            throw new RuntimeException("PDF extraction failed: " + e.getMessage());
        }
    }
    
    /**
     * Extract text from DOCX using Apache POI
     */
    private String extractFromDocx(MultipartFile file) throws IOException {
        logger.info("=== DOCX EXTRACTION START ===");
        
        try (InputStream inputStream = file.getInputStream();
             XWPFDocument document = new XWPFDocument(inputStream)) {
            
            logger.info("DOCX loaded successfully");
            
            XWPFWordExtractor extractor = new XWPFWordExtractor(document);
            String text = extractor.getText();
            
            logger.info("DOCX text extraction completed");
            logger.info("Extracted DOCX text length: {} characters", text.length());
            logger.info("DOCX text preview: {}", text.length() > 200 ? text.substring(0, 200) + "..." : text);
            logger.info("=== DOCX EXTRACTION END ===");
            
            return text.trim();
        }
    }
    
    /**
     * Extract text from TXT file
     */
    private String extractFromTxt(MultipartFile file) throws IOException {
        logger.info("=== TXT EXTRACTION START ===");
        
        try (InputStream inputStream = file.getInputStream()) {
            String text = new String(inputStream.readAllBytes());
            
            logger.info("TXT text extraction completed");
            logger.info("Extracted TXT text length: {} characters", text.length());
            logger.info("TXT text preview: {}", text.length() > 200 ? text.substring(0, 200) + "..." : text);
            logger.info("=== TXT EXTRACTION END ===");
            
            return text.trim();
        }
    }
    
    /**
     * Get file extension from filename
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf('.') == -1) {
            throw new IllegalArgumentException("Invalid filename: " + fileName);
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }
}
