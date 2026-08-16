package com.quizora.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class InterviewStartRequest {

    @NotBlank
    private String jobRole;

    @NotBlank
    private String experience;

    @NotBlank
    private String difficulty;
}
