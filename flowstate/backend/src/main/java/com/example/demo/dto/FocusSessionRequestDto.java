package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FocusSessionRequestDto {

    @NotBlank(message = "Session title is required")
    @Size(max = 200, message = "Title must be at most 200 characters")
    private String title;

    @NotNull(message = "Planned start time is required")
    private LocalDateTime plannedStart;

    @NotNull(message = "Planned end time is required")
    private LocalDateTime plannedEnd;

    @NotBlank(message = "Session type is required")
    @Pattern(regexp = "DEEP_WORK|CREATIVE|REVIEW", message = "Session type must be DEEP_WORK, CREATIVE or REVIEW")
    private String sessionType;

    private String notes;
}
