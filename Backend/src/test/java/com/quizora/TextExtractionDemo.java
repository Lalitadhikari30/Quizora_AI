package com.quizora;

import com.quizora.service.InMemoryTextExtractionService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Simple demonstration of text extraction from PDF, DOCX, and TXT files
 * using the InMemoryTextExtractionService.
 * 
 * This shows how the service works with different file types:
 * - PDF: Uses Apache PDFBox to extract text
 * - DOCX: Uses Apache POI to extract text from paragraphs
 * - TXT: Simple string conversion from bytes
 */
public class TextExtractionDemo {
    
    public static void main(String[] args) {
        System.out.println("=== TEXT EXTRACTION DEMONSTRATION ===");
        System.out.println("This demo shows how the InMemoryTextExtractionService works");
        System.out.println("with different file types: PDF, DOCX, and TXT\n");
        
        // Create text extraction service
        InMemoryTextExtractionService textExtractionService = new InMemoryTextExtractionService();
        
        try {
            // Demo 1: PDF Text Extraction
            System.out.println("\n--- Demo 1: PDF Text Extraction ---");
            String pdfPath = "c:\\Users\\Fam\\Downloads\\QuizoraAI\\test-sample.pdf";
            
            if (Files.exists(Paths.get(pdfPath))) {
                byte[] pdfBytes = Files.readAllBytes(Paths.get(pdfPath));
                String pdfText = textExtractionService.extractText(pdfBytes, "test-sample.pdf");
                
                System.out.println("✅ PDF File: test-sample.pdf");
                System.out.println("📄 File Size: " + pdfBytes.length + " bytes");
                System.out.println("📝 Extracted Text: " + pdfText.length() + " characters");
                System.out.println("📋 First 100 characters: " + (pdfText.length() > 100 ? pdfText.substring(0, 100) + "..." : pdfText));
                System.out.println();
            } else {
                System.out.println("❌ PDF file not found: " + pdfPath);
            }
            
            // Demo 2: TXT Text Extraction  
            System.out.println("\n--- Demo 2: TXT Text Extraction ---");
            String txtContent = "This is a sample text document for testing the text extraction service.\n\n" +
                              "Chapter 1: Spring Boot Framework\n" +
                              "Spring Boot makes it easy to create stand-alone, production-grade Spring-based Applications.\n\n" +
                              "Chapter 2: Key Features\n" +
                              "- Auto-configuration\n" +
                              "- Embedded servers\n" +
                              "- Production-ready features\n\n" +
                              "This text contains enough content to test the extraction functionality.";
            
            byte[] txtBytes = txtContent.getBytes();
            String txtText = textExtractionService.extractText(txtBytes, "sample-document.txt");
            
            System.out.println("✅ TXT Content Created: sample-document.txt");
            System.out.println("📄 Content Size: " + txtBytes.length + " bytes");
            System.out.println("📝 Extracted Text: " + txtText.length() + " characters");
            System.out.println("📋 First 100 characters: " + (txtText.length() > 100 ? txtText.substring(0, 100) + "..." : txtText));
            System.out.println();
            
            // Demo 3: Unsupported File Type
            System.out.println("\n--- Demo 3: Unsupported File Type ---");
            try {
                byte[] invalidBytes = "Invalid content".getBytes();
                String invalidText = textExtractionService.extractText(invalidBytes, "test.xyz");
                System.out.println("❌ Should not reach here - unsupported file type");
            } catch (Exception e) {
                System.out.println("✅ Correctly handled unsupported file type: " + e.getMessage());
            }
            
            System.out.println("\n=== TEXT EXTRACTION TECHNIQUES USED ===");
            System.out.println("1. PDF Extraction:");
            System.out.println("   - Library: Apache PDFBox");
            System.out.println("   - Method: PDFTextStripper.getText()");
            System.out.println("   - Process: Load PDF from byte array, extract all text");
            System.out.println();
            System.out.println("2. DOCX Extraction:");
            System.out.println("   - Library: Apache POI (poi-ooxml)");
            System.out.println("   - Method: XWPFDocument.getParagraphs()");
            System.out.println("   - Process: Load DOCX from byte array, extract text from all paragraphs");
            System.out.println();
            System.out.println("3. TXT Extraction:");
            System.out.println("   - Method: String constructor from byte array");
            System.out.println("   - Process: Direct byte-to-string conversion with UTF-8 encoding");
            System.out.println();
            System.out.println("✅ All extractions performed entirely in memory (no temporary files)");
            System.out.println("🔒 Minimum 200 character validation enforced");
            System.out.println("🚀 Ready for integration with Gemini API for quiz generation");
            System.out.println("\n=== END OF DEMONSTRATION ===");
            
        } catch (IOException e) {
            System.err.println("❌ Demo failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
