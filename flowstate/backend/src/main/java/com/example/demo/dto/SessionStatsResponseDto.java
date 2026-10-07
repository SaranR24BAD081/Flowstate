package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionStatsResponseDto {
    private long totalSessions;
    private long completedSessions;
    private long abandonedSessions;
    private double averageFocusScore;
    private long totalFocusMinutes;
    private double totalFocusHours;
    private Map<String, Long> sessionsByType;
}
