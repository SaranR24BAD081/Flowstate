package com.example.demo.dto;

import com.example.demo.entity.FocusBlock;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FocusBlockResponseDto {
    private Long id;
    private Long practitionerId;
    private String practitionerName;
    private String dayOfWeek;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime blockStartTime;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime blockEndTime;
    private Boolean isProtected;
    private String preferredSessionType;
    private LocalDateTime createdAt;

    public static FocusBlockResponseDto from(FocusBlock b) {
        FocusBlockResponseDto d = new FocusBlockResponseDto();
        d.id = b.getId();
        if (b.getPractitioner() != null) {
            d.practitionerId = b.getPractitioner().getId();
            d.practitionerName = b.getPractitioner().getFullName();
        }
        d.dayOfWeek = b.getDayOfWeek();
        d.blockStartTime = b.getBlockStartTime();
        d.blockEndTime = b.getBlockEndTime();
        d.isProtected = b.getIsProtected();
        d.preferredSessionType = b.getPreferredSessionType() != null ? b.getPreferredSessionType().name() : null;
        d.createdAt = b.getCreatedAt();
        return d;
    }
}
