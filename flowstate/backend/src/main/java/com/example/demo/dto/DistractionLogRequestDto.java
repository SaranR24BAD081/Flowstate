package com.example.demo.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DistractionLogRequestDto {

    @NotNull(message = "Session ID is required")
    private Long sessionId;

    @NotBlank(message = "Category is required")
    @Pattern(regexp = "NOTIFICATION|SOCIAL|ENVIRONMENT|MENTAL|OTHER",
            message = "Category must be NOTIFICATION, SOCIAL, ENVIRONMENT, MENTAL or OTHER")
    private String category;

    @Size(max = 500, message = "Description must be at most 500 characters")
    private String description;

    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 second")
    private Integer durationSeconds;
}
