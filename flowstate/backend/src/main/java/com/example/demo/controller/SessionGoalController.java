package com.example.demo.controller;

import com.example.demo.config.JwtAuthenticationFilter;
import com.example.demo.dto.GoalCompletionRateResponseDto;
import com.example.demo.dto.SessionGoalRequestDto;
import com.example.demo.dto.SessionGoalResponseDto;
import com.example.demo.service.SessionGoalService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
public class SessionGoalController {

    private final SessionGoalService goalService;

    private Long userId(HttpServletRequest request) {
        return (Long) request.getAttribute(JwtAuthenticationFilter.USER_ID_ATTRIBUTE);
    }

    @PostMapping
    @PreAuthorize("hasRole('PRACTITIONER')")
    public ResponseEntity<SessionGoalResponseDto> attach(@Valid @RequestBody SessionGoalRequestDto dto,
                                                         HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(goalService.attachGoal(dto, userId(request)));
    }

    @GetMapping("/session/{sessionId}")
    @PreAuthorize("hasAnyRole('PRACTITIONER','FLOW_COACH','PLATFORM_ADMIN')")
    public ResponseEntity<List<SessionGoalResponseDto>> forSession(@PathVariable Long sessionId) {
        return ResponseEntity.ok(goalService.getGoalsForSession(sessionId));
    }

    @PutMapping("/{id}/outcome")
    @PreAuthorize("hasRole('PRACTITIONER')")
    public ResponseEntity<SessionGoalResponseDto> outcome(@PathVariable Long id,
                                                          @RequestBody Map<String, Integer> body,
                                                          HttpServletRequest request) {
        return ResponseEntity.ok(goalService.markGoalOutcome(id, body.get("achievedMinutes"), userId(request)));
    }

    @GetMapping("/completion-rate")
    @PreAuthorize("hasAnyRole('PRACTITIONER','FLOW_COACH','PLATFORM_ADMIN')")
    public ResponseEntity<GoalCompletionRateResponseDto> completionRate(HttpServletRequest request) {
        return ResponseEntity.ok(goalService.getGoalCompletionRate(userId(request)));
    }
}
