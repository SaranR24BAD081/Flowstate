package com.example.demo.dto;

import com.example.demo.entity.CoachRecommendation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoachRecommendationResponseDto {
    private Long id;
    private Long coachId;
    private String coachName;
    private Long practitionerId;
    private String practitionerName;
    private String recommendationText;
    private String priority;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime acknowledgedAt;

    public static CoachRecommendationResponseDto from(CoachRecommendation r) {
        CoachRecommendationResponseDto d = new CoachRecommendationResponseDto();
        d.id = r.getId();
        if (r.getCoach() != null) {
            d.coachId = r.getCoach().getId();
            d.coachName = r.getCoach().getFullName();
        }
        if (r.getPractitioner() != null) {
            d.practitionerId = r.getPractitioner().getId();
            d.practitionerName = r.getPractitioner().getFullName();
        }
        d.recommendationText = r.getRecommendationText();
        d.priority = r.getPriority() != null ? r.getPriority().name() : null;
        d.status = r.getStatus() != null ? r.getStatus().name() : null;
        d.createdAt = r.getCreatedAt();
        d.acknowledgedAt = r.getAcknowledgedAt();
        return d;
    }
}
