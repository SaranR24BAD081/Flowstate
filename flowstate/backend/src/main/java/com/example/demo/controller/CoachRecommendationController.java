package com.example.demo.controller;

import com.example.demo.config.JwtAuthenticationFilter;
import com.example.demo.dto.CoachRecommendationRequestDto;
import com.example.demo.dto.CoachRecommendationResponseDto;
import com.example.demo.service.CoachRecommendationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class CoachRecommendationController {

    private final CoachRecommendationService recommendationService;

    private Long userId(HttpServletRequest request) {
        return (Long) request.getAttribute(JwtAuthenticationFilter.USER_ID_ATTRIBUTE);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('FLOW_COACH','PLATFORM_ADMIN')")
    public ResponseEntity<CoachRecommendationResponseDto> issue(@Valid @RequestBody CoachRecommendationRequestDto dto,
                                                                HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(recommendationService.issueRecommendation(dto, userId(request)));
    }

    @GetMapping("/practitioner/{practitionerId}")
    @PreAuthorize("hasAnyRole('FLOW_COACH','PLATFORM_ADMIN','PRACTITIONER')")
    public ResponseEntity<Page<CoachRecommendationResponseDto>> forPractitioner(
            @PathVariable Long practitionerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication,
            HttpServletRequest request) {
        boolean practitioner = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PRACTITIONER"));
        if (practitioner && !practitionerId.equals(userId(request))) {
            throw new AccessDeniedException("Practitioners can only view their own recommendations");
        }
        return ResponseEntity.ok(recommendationService.getRecommendationsForPractitioner(practitionerId, page, size));
    }

    @GetMapping("/mine/pending")
    @PreAuthorize("hasRole('PRACTITIONER')")
    public ResponseEntity<List<CoachRecommendationResponseDto>> minePending(HttpServletRequest request) {
        return ResponseEntity.ok(recommendationService.getPendingRecommendationsForPractitioner(userId(request)));
    }

    @GetMapping("/mine/issued")
    @PreAuthorize("hasRole('FLOW_COACH')")
    public ResponseEntity<Page<CoachRecommendationResponseDto>> mineIssued(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        return ResponseEntity.ok(recommendationService.getRecommendationsByCoach(userId(request), page, size));
    }

    @PutMapping("/{id}/acknowledge")
    @PreAuthorize("hasRole('PRACTITIONER')")
    public ResponseEntity<CoachRecommendationResponseDto> acknowledge(@PathVariable Long id,
                                                                      HttpServletRequest request) {
        return ResponseEntity.ok(recommendationService.acknowledgeRecommendation(id, userId(request)));
    }

    @PutMapping("/{id}/dismiss")
    @PreAuthorize("hasRole('PRACTITIONER')")
    public ResponseEntity<CoachRecommendationResponseDto> dismiss(@PathVariable Long id,
                                                                  HttpServletRequest request) {
        return ResponseEntity.ok(recommendationService.dismissRecommendation(id, userId(request)));
    }
}
