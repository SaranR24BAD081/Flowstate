package com.example.demo.dto;

import com.example.demo.entity.FocusSession;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FocusSessionResponseDto {
    private Long id;
    private Long practitionerId;
    private String practitionerName;
    private String title;
    private LocalDateTime plannedStart;
    private LocalDateTime plannedEnd;
    private LocalDateTime actualStart;
    private LocalDateTime actualEnd;
    private String status;
    private String sessionType;
    private Integer focusScore;
    private String notes;
    private String abandonReason;
    private LocalDateTime createdAt;

    public static FocusSessionResponseDto from(FocusSession s) {
        FocusSessionResponseDto d = new FocusSessionResponseDto();
        d.id = s.getId();
        if (s.getPractitioner() != null) {
            d.practitionerId = s.getPractitioner().getId();
            d.practitionerName = s.getPractitioner().getFullName();
        }
        d.title = s.getTitle();
        d.plannedStart = s.getPlannedStart();
        d.plannedEnd = s.getPlannedEnd();
        d.actualStart = s.getActualStart();
        d.actualEnd = s.getActualEnd();
        d.status = s.getStatus() != null ? s.getStatus().name() : null;
        d.sessionType = s.getSessionType() != null ? s.getSessionType().name() : null;
        d.focusScore = s.getFocusScore();
        d.notes = s.getNotes();
        d.abandonReason = s.getAbandonReason();
        d.createdAt = s.getCreatedAt();
        return d;
    }
}
