package com.example.demo.dto;

import com.example.demo.entity.SessionGoal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionGoalResponseDto {
    private Long id;
    private Long sessionId;
    private String description;
    private Integer targetMinutes;
    private Integer achievedMinutes;
    private String status;
    private LocalDateTime createdAt;

    public static SessionGoalResponseDto from(SessionGoal g) {
        SessionGoalResponseDto d = new SessionGoalResponseDto();
        d.id = g.getId();
        d.sessionId = g.getSession() != null ? g.getSession().getId() : null;
        d.description = g.getDescription();
        d.targetMinutes = g.getTargetMinutes();
        d.achievedMinutes = g.getAchievedMinutes();
        d.status = g.getStatus() != null ? g.getStatus().name() : null;
        d.createdAt = g.getCreatedAt();
        return d;
    }
}
