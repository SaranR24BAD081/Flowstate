package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoalCompletionRateResponseDto {
    private long achievedGoals;
    private long resolvedGoals;
    private double completionRate; // percentage 0-100
    private Map<String, Long> goalsByStatus;
}
