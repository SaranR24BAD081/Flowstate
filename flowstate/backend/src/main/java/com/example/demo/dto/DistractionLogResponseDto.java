package com.example.demo.dto;

import com.example.demo.entity.DistractionLog;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DistractionLogResponseDto {
    private Long id;
    private Long sessionId;
    private LocalDateTime loggedAt;
    private String category;
    private String description;
    private Integer durationSeconds;
    private LocalDateTime createdAt;

    public static DistractionLogResponseDto from(DistractionLog l) {
        DistractionLogResponseDto d = new DistractionLogResponseDto();
        d.id = l.getId();
        d.sessionId = l.getSession() != null ? l.getSession().getId() : null;
        d.loggedAt = l.getLoggedAt();
        d.category = l.getCategory() != null ? l.getCategory().name() : null;
        d.description = l.getDescription();
        d.durationSeconds = l.getDurationSeconds();
        d.createdAt = l.getCreatedAt();
        return d;
    }
}
