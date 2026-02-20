package com.quizora;

import com.quizora.service.InMemoryTextExtractionService;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class SimpleTextExtractionTest {

    @Test
    public void testPdfTextExtraction() {
        try {
            // Load test PDF file
            byte[] pdfBytes = Files.readAllBytes(Paths.get("c:\\Users\\Fam\\Downloads\\QuizoraAI\\test-sample.pdf"));
            
            // Create text extraction service
            InMemoryTextExtractionService textExtractionService = new InMemoryTextExtractionService();
            
            // Extract text from PDF
            String extractedText = textExtractionService.extractText(pdfBytes, "test-sample.pdf");
            
            // Print results to console
            System.out.println("=== PDF TEXT EXTRACTION TEST ===");
            System.out.println("File: test-sample.pdf");
            System.out.println("Size: " + pdfBytes.length + " bytes");
            System.out.println("Extracted text length: " + extractedText.length() + " characters");
            System.out.println("\n--- EXTRACTED TEXT ---");
            System.out.println(extractedText);
            System.out.println("--- END OF TEXT ---\n");
            
            // Verify extraction worked
            assertNotNull(extractedText);
            assertTrue(extractedText.length() > 200, "Extracted text should be at least 200 characters");
            assertTrue(extractedText.contains("Java"), "Extracted text should contain 'Java'");
            assertTrue(extractedText.contains("Chapter 1"), "Extracted text should contain chapter headings");
            
            System.out.println("✅ PDF text extraction test PASSED!");
        } catch (IOException e) {
            System.err.println("❌ Test failed: " + e.getMessage());
            fail("PDF text extraction test failed: " + e.getMessage());
        }
    }
    
    @Test
    public void testTxtTextExtraction() {
        try {
            // Create a simple TXT content for testing
            String txtContent = "This is a test TXT document.\n\n" +
                              "Chapter 1: Microservices Architecture\n" +
                              "Microservices is an architectural style that structures an application as a collection of loosely coupled, independently deployable services.\n\n" +
                              "Chapter 2: Characteristics\n" +
                              "- Single Responsibility\n" +
                              "- Autonomous\n" +
                              "- Decentralized\n" +
                              "- Fault Tolerant\n" +
                              "- Highly Available\n\n" +
                              "This content demonstrates text extraction from plain text files.";
            
            byte[] txtBytes = txtContent.getBytes();
            
            // Create text extraction service
            InMemoryTextExtractionService textExtractionService = new InMemoryTextExtractionService();
            
            // Extract text from TXT
            String extractedText = textExtractionService.extractText(txtBytes, "test-document.txt");
            
            // Print the extracted text to console
            System.out.println("=== TXT TEXT EXTRACTION TEST ===");
            System.out.println("File: test-document.txt");
            System.out.println("Size: " + txtBytes.length + " bytes");
            System.out.println("Extracted text length: " + extractedText.length() + " characters");
            System.out.println("\n--- EXTRACTED TEXT ---");
            System.out.println(extractedText);
            System.out.println("--- END OF TEXT ---\n");
            
            // Verify extraction worked
            assertNotNull(extractedText);
            assertTrue(extractedText.length() > 200, "Extracted text should be at least 200 characters");
            assertTrue(extractedText.contains("Microservices"), "Extracted text should contain 'Microservices'");
            assertTrue(extractedText.contains("Chapter 1"), "Extracted text should contain chapter headings");
            
            System.out.println("✅ TXT text extraction test PASSED!");
        } catch (Exception e) {
            System.err.println("❌ TXT test failed: " + e.getMessage());
            fail("TXT text extraction test failed: " + e.getMessage());
        }
    }
}
