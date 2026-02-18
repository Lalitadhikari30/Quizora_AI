package com.quizora.controller;

import com.quizora.service.FileUploadService;
import com.quizora.service.SupabaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class FileUploadController {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private SupabaseService supabaseService;

    @PostMapping("/pdf")
    public ResponseEntity<?> uploadPdf(
            @RequestParam("file") MultipartFile file,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "File is empty"));
            }

            String userId = "dev-user";

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.replace("Bearer ", "");
                Map<String, Object> user = supabaseService.validateTokenAndGetUser(token);
                userId = (String) user.get("id");
            }

            String publicUrl = fileUploadService.uploadFile(file, userId);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "PDF uploaded successfully");
            response.put("publicUrl", publicUrl);
            response.put("fileName", file.getOriginalFilename());

            logger.info("PDF uploaded: {} for user: {}", file.getOriginalFilename(), userId);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Failed to upload PDF", e);
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to upload PDF: " + e.getMessage()));
        }
    }

    @PostMapping("/file")
    public ResponseEntity<?> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            HttpServletRequest request) {

        logger.info("Received file upload request: {}", file.getOriginalFilename());
        logger.info("Authorization header: {}", authHeader);

        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "File is empty"
                ));
            }

            String userId = "dev-user";

            // Temporarily skip authentication for testing
            logger.info("Using dev-user for testing (auth temporarily disabled)");

            String publicUrl = fileUploadService.uploadFile(file, userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "File uploaded successfully");
            response.put("publicUrl", publicUrl);
            response.put("fileName", file.getOriginalFilename());
            response.put("userId", userId);

            logger.info("File uploaded successfully: {} for user: {}", file.getOriginalFilename(), userId);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Failed to upload file", e);
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to upload file: " + e.getMessage()));
        }
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<?> downloadFile(@PathVariable String fileName) {
        try {
            Path filePath = Paths.get("uploads").resolve(fileName);
            if (!Files.exists(filePath)) {
                return ResponseEntity.notFound().build();
            }

            byte[] fileContent = Files.readAllBytes(filePath);
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"" + fileName + "\"")
                    .body(fileContent);

        } catch (Exception e) {
            logger.error("Failed to download file: {}", fileName, e);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/quiz")
    public ResponseEntity<?> uploadFileForQuiz(
            @RequestParam("file") MultipartFile file,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        try {
            String userId = "dev-user";

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.replace("Bearer ", "");
                Map<String, Object> user = supabaseService.validateTokenAndGetUser(token);
                userId = (String) user.get("id");
            }

            String publicUrl = fileUploadService.uploadFile(file, userId);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "File uploaded successfully for quiz generation");
            response.put("publicUrl", publicUrl);
            response.put("fileName", file.getOriginalFilename());

            logger.info("Quiz file uploaded: {} for user: {}", file.getOriginalFilename(), userId);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Failed to upload quiz file", e);
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to upload quiz file: " + e.getMessage()));
        }
    }
}
