package com.example.demo.service;

import com.example.demo.dto.CoachRecommendationRequestDto;
import com.example.demo.dto.CoachRecommendationResponseDto;
import com.example.demo.entity.CoachRecommendation;
import com.example.demo.entity.FlowUser;
import com.example.demo.exception.BusinessValidationException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.CoachRecommendationRepository;
import com.example.demo.repository.FlowUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CoachRecommendationService {

    private final CoachRecommendationRepository recommendationRepository;
    private final FlowUserRepository userRepository;

    /** [REQ-SVC-REC-01] */
    @Transactional
    public CoachRecommendationResponseDto issueRecommendation(CoachRecommendationRequestDto dto, Long coachId) {
        FlowUser coach = userRepository.findById(coachId)
                .orElseThrow(() -> new ResourceNotFoundException("Coach not found"));
        if (coach.getRole() != FlowUser.UserRole.FLOW_COACH) {
            throw new BusinessValidationException("Only users with the FLOW_COACH role can issue recommendations");
        }
        FlowUser practitioner = userRepository.findById(dto.getPractitionerId())
                .orElseThrow(() -> new ResourceNotFoundException("Practitioner not found"));
        if (practitioner.getRole() != FlowUser.UserRole.PRACTITIONER) {
            throw new BusinessValidationException("Recommendations can only be issued to PRACTITIONER users");
        }
        if (practitioner.getId().equals(coachId)) {
            throw new BusinessValidationException("A coach cannot issue a recommendation to themselves");
        }

        CoachRecommendation rec = new CoachRecommendation();
        rec.setCoach(coach);
        rec.setPractitioner(practitioner);
        rec.setRecommendationText(dto.getRecommendationText());
        rec.setPriority(CoachRecommendation.Priority.valueOf(dto.getPriority()));
        rec.setStatus(CoachRecommendation.RecommendationStatus.PENDING);
        return CoachRecommendationResponseDto.from(recommendationRepository.save(rec));
    }

    /** [REQ-SVC-REC-02] */
    @Transactional
    public CoachRecommendationResponseDto acknowledgeRecommendation(Long id, Long practitionerId) {
        CoachRecommendation rec = findPendingOwned(id, practitionerId);
        rec.setStatus(CoachRecommendation.RecommendationStatus.ACKNOWLEDGED);
        rec.setAcknowledgedAt(LocalDateTime.now());
        return CoachRecommendationResponseDto.from(recommendationRepository.save(rec));
    }

    /** [REQ-SVC-REC-02] */
    @Transactional
    public CoachRecommendationResponseDto dismissRecommendation(Long id, Long practitionerId) {
        CoachRecommendation rec = findPendingOwned(id, practitionerId);
        rec.setStatus(CoachRecommendation.RecommendationStatus.DISMISSED);
        return CoachRecommendationResponseDto.from(recommendationRepository.save(rec));
    }

    @Transactional(readOnly = true)
    public Page<CoachRecommendationResponseDto> getRecommendationsForPractitioner(Long practitionerId, int page, int size) {
        return recommendationRepository
                .findByPractitionerIdOrderByCreatedAtDesc(practitionerId, PageRequest.of(page, size))
                .map(CoachRecommendationResponseDto::from);
    }

    @Transactional(readOnly = true)
    public List<CoachRecommendationResponseDto> getPendingRecommendationsForPractitioner(Long practitionerId) {
        return recommendationRepository
                .findByPractitionerIdAndStatus(practitionerId, CoachRecommendation.RecommendationStatus.PENDING)
                .stream().map(CoachRecommendationResponseDto::from).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<CoachRecommendationResponseDto> getRecommendationsByCoach(Long coachId, int page, int size) {
        return recommendationRepository
                .findByCoachIdOrderByCreatedAtDesc(coachId, PageRequest.of(page, size))
                .map(CoachRecommendationResponseDto::from);
    }

    private CoachRecommendation findPendingOwned(Long id, Long practitionerId) {
        if (!recommendationRepository.existsByIdAndPractitionerId(id, practitionerId)) {
            throw new ResourceNotFoundException("CoachRecommendation not found");
        }
        CoachRecommendation rec = recommendationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CoachRecommendation not found"));
        if (rec.getStatus() != CoachRecommendation.RecommendationStatus.PENDING) {
            throw new BusinessValidationException("Only PENDING recommendations can be updated");
        }
        return rec;
    }
}
