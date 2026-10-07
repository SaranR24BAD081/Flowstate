package com.example.demo.dto;

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
public class CoachRecommendationRequestDto {

    @NotNull(message = "Practitioner ID is required")
    private Long practitionerId;

    @NotBlank(message = "Recommendation text is required")
    @Size(min = 10, message = "Recommendation must be at least 10 characters")
    private String recommendationText;

    @NotBlank(message = "Priority is required")
    @Pattern(regexp = "LOW|MEDIUM|HIGH", message = "Priority must be LOW, MEDIUM or HIGH")
    private String priority;
}
