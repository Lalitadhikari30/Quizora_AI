package com.quizora.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
public class PdfExtractionService {
    
    private static final Logger logger = LoggerFactory.getLogger(PdfExtractionService.class);

    public String extractTextFromPdf(String filePath) {
        try (PDDocument document = PDDocument.load(new java.io.File(filePath))) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            logger.info("Successfully extracted text from PDF: {}", filePath);
            return text.trim();
        } catch (IOException e) {
            logger.error("Failed to extract text from PDF: {}", filePath, e);
            throw new RuntimeException("Failed to extract text from PDF: " + e.getMessage());
        }
    }

    public String extractTextFromPdfStream(InputStream pdfStream) {
        try (PDDocument document = PDDocument.load(pdfStream)) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            logger.info("Successfully extracted text from PDF stream");
            return text.trim();
        } catch (IOException e) {
            logger.error("Failed to extract text from PDF stream", e);
            throw new RuntimeException("Failed to extract text from PDF: " + e.getMessage());
        }
    }
}
