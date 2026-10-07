package com.example.demo.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionGoalRequestDto {

    @NotNull(message = "Session ID is required")
    private Long sessionId;

    @NotBlank(message = "Goal description is required")
    @Size(max = 500, message = "Description must be at most 500 characters")
    private String description;

    @NotNull(message = "Target minutes is required")
    @Min(value = 1, message = "Target minutes must be at least 1")
    private Integer targetMinutes;
}
