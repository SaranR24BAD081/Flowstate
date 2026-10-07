package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FocusBlockRequestDto {

    @NotBlank(message = "Day of week is required")
    @Pattern(regexp = "MONDAY|TUESDAY|WEDNESDAY|THURSDAY|FRIDAY|SATURDAY|SUNDAY",
            message = "Day must be a valid day of week in uppercase")
    private String dayOfWeek;

    @NotNull(message = "Block start time is required")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime blockStartTime;

    @NotNull(message = "Block end time is required")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime blockEndTime;

    @Pattern(regexp = "DEEP_WORK|CREATIVE|REVIEW",
            message = "Preferred session type must be DEEP_WORK, CREATIVE or REVIEW")
    private String preferredSessionType;
}
