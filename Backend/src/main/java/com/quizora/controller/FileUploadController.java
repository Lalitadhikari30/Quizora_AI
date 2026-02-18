// package com.quizora.controller;

// import com.quizora.dto.QuizResponse;
// import com.quizora.service.FileUploadService;
// import com.quizora.service.QuizService;
// import com.quizora.service.SupabaseService;
// import com.quizora.service.TextExtractionService;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;
// import org.springframework.web.multipart.MultipartFile;

// import jakarta.servlet.http.HttpServletRequest;
// import java.io.ByteArrayInputStream;
// import java.io.File;
// import java.io.IOException;
// import java.io.InputStream;
// import java.nio.file.Files;
// import java.nio.file.Path;
// import java.nio.file.Paths;
// import java.util.HashMap;
// import java.util.Map;

// @RestController
// @RequestMapping("/api/upload")
// @CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
// public class FileUploadController {

//     private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);

//     @Autowired
//     private FileUploadService fileUploadService;

//     @Autowired
//     private SupabaseService supabaseService;

//     @Autowired
//     private QuizService quizService;

//     @Autowired
//     private TextExtractionService textExtractionService;

//     @PostMapping("/pdf")
//     public ResponseEntity<?> uploadPdf(
//             @RequestParam("file") MultipartFile file,
//             @RequestHeader(value = "Authorization", required = false) String authHeader) {

//         try {
//             if (file.isEmpty()) {
//                 return ResponseEntity.badRequest().body(Map.of("error", "File is empty"));
//             }

//             String userId = "dev-user";

//             if (authHeader != null && authHeader.startsWith("Bearer ")) {
//                 String token = authHeader.replace("Bearer ", "");
//                 Map<String, Object> user = supabaseService.validateTokenAndGetUser(token);
//                 userId = (String) user.get("id");
//             }

//             String publicUrl = fileUploadService.uploadFile(file, userId);

//             Map<String, Object> response = new HashMap<>();
//             response.put("message", "PDF uploaded successfully");
//             response.put("publicUrl", publicUrl);
//             response.put("fileName", file.getOriginalFilename());

//             logger.info("PDF uploaded: {} for user: {}", file.getOriginalFilename(), userId);

//             return ResponseEntity.ok(response);

//         } catch (Exception e) {
//             logger.error("Failed to upload PDF", e);
//             return ResponseEntity.badRequest().body(Map.of("error", "Failed to upload PDF: " + e.getMessage()));
//         }
//     }

//     @PostMapping("/file")
//     public ResponseEntity<?> uploadFile(
//             @RequestParam("file") MultipartFile file,
//             @RequestHeader(value = "Authorization", required = false) String authHeader,
//             HttpServletRequest request) {

//         logger.info("Received file upload request: {}", file.getOriginalFilename());
//         logger.info("Authorization header: {}", authHeader);

//         try {
//             if (file.isEmpty()) {
//                 return ResponseEntity.badRequest().body(Map.of(
//                         "success", false,
//                         "message", "File is empty"
//                 ));
//             }

//             String userId = "dev-user";

//             // Temporarily skip authentication for testing
//             logger.info("Using dev-user for testing (auth temporarily disabled)");

//             String publicUrl = fileUploadService.uploadFile(file, userId);

//             Map<String, Object> response = new HashMap<>();
//             response.put("success", true);
//             response.put("message", "File uploaded successfully");
//             response.put("publicUrl", publicUrl);
//             response.put("fileName", file.getOriginalFilename());
//             response.put("userId", userId);

//             logger.info("File uploaded successfully: {} for user: {}", file.getOriginalFilename(), userId);

//             return ResponseEntity.ok(response);

//         } catch (Exception e) {
//             logger.error("Failed to upload file", e);
//             return ResponseEntity.badRequest().body(Map.of("error", "Failed to upload file: " + e.getMessage()));
//         }
//     }

//     @GetMapping("/download/{fileName}")
//     public ResponseEntity<?> downloadFile(@PathVariable String fileName) {
//         try {
//             Path filePath = Paths.get("uploads").resolve(fileName);
//             if (!Files.exists(filePath)) {
//                 return ResponseEntity.notFound().build();
//             }

//             byte[] fileContent = Files.readAllBytes(filePath);
//             return ResponseEntity.ok()
//                     .header("Content-Disposition", "attachment; filename=\"" + fileName + "\"")
//                     .body(fileContent);

//         } catch (Exception e) {
//             logger.error("Failed to download file: {}", fileName, e);
//             return ResponseEntity.notFound().build();
//         }
//     }

//     @PostMapping("/simple")
//     public ResponseEntity<?> simpleUpload(
//             @RequestBody byte[] fileBytes,
//             @RequestParam(value = "filename", defaultValue = "test.txt") String filename) {

//         try {
//             logger.info("=== SIMPLE UPLOAD START ===");
//             logger.info("Filename: {}", filename);
//             logger.info("File size: {} bytes", fileBytes.length);
            
//             if (fileBytes.length == 0) {
//                 return ResponseEntity.badRequest().body(Map.of("error", "File is empty"));
//             }

//             logger.info("=== SIMPLE UPLOAD SUCCESS ===");
//             return ResponseEntity.ok(Map.of(
//                 "message", "Simple upload successful",
//                 "fileName", filename,
//                 "fileSize", fileBytes.length,
//                 "preview", new String(fileBytes).substring(0, Math.min(100, fileBytes.length)) + "..."
//             ));

//         } catch (Exception e) {
//             logger.error("=== SIMPLE UPLOAD FAILED ===", e);
//             return ResponseEntity.badRequest().body(Map.of("error", "Simple test failed: " + e.getMessage()));
//         }
//     }

//     @PostMapping("/test")
//     public ResponseEntity<?> testUpload(
//             @RequestParam("file") MultipartFile file) {

//         try {
//             logger.info("=== TEST UPLOAD START ===");
//             logger.info("File received: {}", file.getOriginalFilename());
//             logger.info("File size: {} bytes", file.getSize());
//             logger.info("Content type: {}", file.getContentType());
            
//             if (file.isEmpty()) {
//                 return ResponseEntity.badRequest().body(Map.of("error", "File is empty"));
//             }

//             logger.info("=== TEST UPLOAD SUCCESS ===");
//             return ResponseEntity.ok(Map.of(
//                 "message", "Test upload successful",
//                 "fileName", file.getOriginalFilename(),
//                 "fileSize", file.getSize(),
//                 "contentType", file.getContentType()
//             ));

//         } catch (Exception e) {
//             logger.error("=== TEST UPLOAD FAILED ===", e);
//             return ResponseEntity.badRequest().body(Map.of("error", "Test failed: " + e.getMessage()));
//         }
//     }

//     @PostMapping("/test-ai")
//     public ResponseEntity<?> testAIGeneration(@RequestBody String text) {
//         try {
//             logger.info("=== AI GENERATION TEST START ===");
//             logger.info("Text length: {} characters", text.length());
            
//             String userId = "dev-user";
//             String filename = "test-content.txt";

//             // Generate quiz directly from text
//             logger.info("Step 1: Generating quiz from text...");
//             QuizResponse quizResponse = quizService.generateQuizFromText(userId, text, filename);
//             logger.info("Step 1 COMPLETED: Quiz generated successfully: {} questions", quizResponse.getQuestions().size());

//             // Return complete response
//             Map<String, Object> response = new HashMap<>();
//             response.put("message", "AI quiz generation test successful");
//             response.put("fileName", filename);
//             response.put("quiz", quizResponse);
//             response.put("textLength", text.length());

//             logger.info("=== AI GENERATION TEST COMPLETE ===");
//             return ResponseEntity.ok(response);

//         } catch (Exception e) {
//             logger.error("=== AI GENERATION TEST FAILED ===", e);
//             return ResponseEntity.badRequest().body(Map.of("error", "AI test failed: " + e.getMessage()));
//         }
//     }

//     @PostMapping("/quiz")
//     public ResponseEntity<?> uploadFileForQuiz(
//             @RequestParam(value = "file", required = false) MultipartFile file,
//             @RequestBody(required = false) byte[] fileBytes,
//             @RequestParam(value = "filename", defaultValue = "uploaded-file") String filename,
//             @RequestHeader(value = "Authorization", required = false) String authHeader) {

//         try {
//             logger.info("=== QUIZ GENERATION START ===");
            
//             // Handle both multipart and byte array inputs
//             if (file != null && !file.isEmpty()) {
//                 logger.info("File received via multipart: {}", file.getOriginalFilename());
//                 logger.info("File size: {} bytes", file.getSize());
//                 logger.info("Content type: {}", file.getContentType());
                
//                 if (file.isEmpty()) {
//                     return ResponseEntity.badRequest().body(Map.of("error", "File is empty"));
//                 }
                
//                 // Process multipart file
//                 return processQuizGeneration(file, file.getOriginalFilename(), authHeader);
                
//             } else if (fileBytes != null && fileBytes.length > 0) {
//                 logger.info("File received via byte array: {}", filename);
//                 logger.info("File size: {} bytes", fileBytes.length);
                
//                 // Create a custom MultipartFile from byte array
//                 CustomMultipartFile customFile = new CustomMultipartFile(
//                     filename, 
//                     filename, 
//                     "text/plain", 
//                     fileBytes
//                 );
                
//                 return processQuizGeneration(customFile, filename, authHeader);
                
//             } else {
//                 return ResponseEntity.badRequest().body(Map.of("error", "No file provided"));
//             }

//         } catch (Exception e) {
//             logger.error("=== QUIZ GENERATION FAILED ===", e);
//             logger.error("Error details: {}", e.getMessage(), e);
//             return ResponseEntity.badRequest().body(Map.of("error", "Failed to generate quiz: " + e.getMessage()));
//         }
//     }

//     private ResponseEntity<?> processQuizGeneration(MultipartFile file, String filename, String authHeader) {
//         try {
//             String userId = "dev-user";

//             if (authHeader != null && authHeader.startsWith("Bearer ")) {
//                 String token = authHeader.replace("Bearer ", "");
//                 Map<String, Object> user = supabaseService.validateTokenAndGetUser(token);
//                 userId = (String) user.get("id");
//             }

//             logger.info("User ID: {}", userId);
//             logger.info("Starting quiz generation pipeline for file: {} for user: {}", filename, userId);

//             // Step 1: Upload file to Supabase
//             logger.info("Step 1: Uploading to Supabase...");
//             String publicUrl = fileUploadService.uploadFile(file, userId);
//             logger.info("Step 1 COMPLETED: File uploaded to Supabase: {}", publicUrl);

//             // Step 2: Extract text from file
//             logger.info("Step 2: Extracting text...");
//             String extractedText = textExtractionService.extractText(file);
//             logger.info("Step 2 COMPLETED: Text extracted from file: {} characters", extractedText.length());

//             // Step 3: Generate quiz from extracted text
//             logger.info("Step 3: Generating quiz...");
//             QuizResponse quizResponse = quizService.generateQuizFromText(userId, extractedText, filename);
//             logger.info("Step 3 COMPLETED: Quiz generated successfully: {} questions", quizResponse.getQuestions().size());

//             // Step 4: Return complete response
//             Map<String, Object> response = new HashMap<>();
//             response.put("message", "Quiz generated successfully from uploaded file");
//             response.put("fileUrl", publicUrl);
//             response.put("fileName", filename);
//             response.put("quiz", quizResponse);
//             response.put("extractedTextLength", extractedText.length());

//             logger.info("=== QUIZ GENERATION COMPLETE ===");
//             logger.info("Complete quiz generation pipeline finished for file: {}", filename);

//             return ResponseEntity.ok(response);

//         } catch (Exception e) {
//             logger.error("Quiz generation processing failed", e);
//             throw e;
//         }
//     }

//     // Custom MultipartFile implementation for testing
//     private static class CustomMultipartFile implements MultipartFile {
//         private final String name;
//         private final String originalFilename;
//         private final String contentType;
//         private final byte[] content;

//         public CustomMultipartFile(String name, String originalFilename, String contentType, byte[] content) {
//             this.name = name;
//             this.originalFilename = originalFilename;
//             this.contentType = contentType;
//             this.content = content;
//         }

//         @Override
//         public String getName() {
//             return name;
//         }

//         @Override
//         public String getOriginalFilename() {
//             return originalFilename;
//         }

//         @Override
//         public String getContentType() {
//             return contentType;
//         }

//         @Override
//         public boolean isEmpty() {
//             return content == null || content.length == 0;
//         }

//         @Override
//         public long getSize() {
//             return content != null ? content.length : 0;
//         }

//         @Override
//         public byte[] getBytes() throws IOException {
//             return content;
//         }

//         @Override
//         public InputStream getInputStream() throws IOException {
//             return new ByteArrayInputStream(content);
//         }

//         @Override
//         public void transferTo(File dest) throws IOException, IllegalStateException {
//             Files.write(dest.toPath(), content);
//         }
//     }
// }


package com.quizora.controller;

import com.quizora.dto.QuizResponse;
import com.quizora.service.FileUploadService;
import com.quizora.service.SupabaseService;
import com.quizora.service.ContentExtractionService;
import com.quizora.service.QuizService;
import com.quizora.service.ExtractedContentService;
import com.quizora.entity.ExtractedContent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin(origins = {"http://localhost:3000"})
public class FileUploadController {

    private static final Logger logger =
            LoggerFactory.getLogger(FileUploadController.class);

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private SupabaseService supabaseService;

    @Autowired
    private ContentExtractionService contentExtractionService;

    @Autowired
    private QuizService quizService;
    
    @Autowired
    private ExtractedContentService extractedContentService;


    /* =====================================================
       NORMAL FILE UPLOAD
       ===================================================== */
    @PostMapping("/file")
    public ResponseEntity<?> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("Authorization") String authHeader) {

        try {

            String token = authHeader.replace("Bearer ", "");
            Map<String, Object> user =
                    supabaseService.validateTokenAndGetUser(token);

            String userId = (String) user.get("id");

            String publicUrl =
                    fileUploadService.uploadFile(file, userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("publicUrl", publicUrl);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Upload failed", e);
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }


    /* =====================================================
       NEW ARCHITECTURE: Upload → Extract → Store → Return ID
       ===================================================== */
    @PostMapping("/extract")
    public ResponseEntity<?> uploadAndExtract(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {

        try {
            String userId = authentication.getName();
            
            logger.info("=== FILE UPLOAD AND EXTRACTION START ===");
            logger.info("User ID: {}", userId);
            logger.info("File: {}", file.getOriginalFilename());

            // Upload, extract, and store - NO Gemini call yet
            ExtractedContent extractedContent = extractedContentService.uploadAndExtract(file, userId);
            
            logger.info("=== FILE UPLOAD AND EXTRACTION SUCCESS ===");
            logger.info("ExtractedContent ID: {}", extractedContent.getId());
            logger.info("Extracted text length: {} characters", extractedContent.getExtractedText().length());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("extractedContentId", extractedContent.getId());
            response.put("fileName", extractedContent.getFileName());
            response.put("fileUrl", extractedContent.getFileUrl());
            response.put("extractedTextLength", extractedContent.getExtractedText().length());
            response.put("status", extractedContent.getStatus().toString());
            response.put("message", "File uploaded and text extracted successfully. Ready for quiz generation.");
            
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("FILE_UPLOAD_EXTRACTION_FAILED: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            errorResponse.put("message", "Failed to upload and extract file");
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /* =====================================================
       QUIZ GENERATION PIPELINE
       ===================================================== */
    @PostMapping("/quiz")
    public ResponseEntity<?> uploadFileForQuiz(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {

        try {
            String userId = authentication.getName();
            
            logger.info("=== QUIZ GENERATION START ===");
            logger.info("User ID: {}", userId);

            /* 1️⃣ Upload file */
            String publicUrl =
                    fileUploadService.uploadFile(file, userId);

            /* 2️⃣ Extract text */
            String extractedText =
                    contentExtractionService.extractContent(file);

            logger.info("Text extracted length: {}", extractedText.length());

            /* 3️⃣ Generate quiz using Gemini */
            QuizResponse quiz =
                    quizService.generateQuizFromText(
                            userId,
                            extractedText,
                            file.getOriginalFilename()
                    );

            logger.info("Quiz generated successfully");

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Quiz generated successfully");
            response.put("quiz", quiz);
            response.put("fileUrl", publicUrl);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Quiz generation failed", e);
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Quiz generation failed: " + e.getMessage()));
        }
    }
}
