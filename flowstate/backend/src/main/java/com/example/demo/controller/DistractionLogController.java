package com.example.demo.controller;

import com.example.demo.config.JwtAuthenticationFilter;
import com.example.demo.dto.DistractionBreakdownResponseDto;
import com.example.demo.dto.DistractionLogRequestDto;
import com.example.demo.dto.DistractionLogResponseDto;
import com.example.demo.service.DistractionLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/distractions")
@RequiredArgsConstructor
public class DistractionLogController {

    private final DistractionLogService distractionService;

    private Long userId(HttpServletRequest request) {
        return (Long) request.getAttribute(JwtAuthenticationFilter.USER_ID_ATTRIBUTE);
    }

    @PostMapping
    @PreAuthorize("hasRole('PRACTITIONER')")
    public ResponseEntity<DistractionLogResponseDto> log(@Valid @RequestBody DistractionLogRequestDto dto,
                                                         HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(distractionService.logDistraction(dto, userId(request)));
    }

    @GetMapping("/session/{sessionId}")
    @PreAuthorize("hasAnyRole('PRACTITIONER','FLOW_COACH','PLATFORM_ADMIN')")
    public ResponseEntity<List<DistractionLogResponseDto>> forSession(@PathVariable Long sessionId) {
        return ResponseEntity.ok(distractionService.getLogsForSession(sessionId));
    }

    @GetMapping("/breakdown")
    @PreAuthorize("hasAnyRole('PRACTITIONER','FLOW_COACH','PLATFORM_ADMIN')")
    public ResponseEntity<DistractionBreakdownResponseDto> breakdown(HttpServletRequest request) {
        return ResponseEntity.ok(distractionService.getDistractionBreakdown(userId(request)));
    }

    @GetMapping("/trend")
    @PreAuthorize("hasAnyRole('PRACTITIONER','FLOW_COACH','PLATFORM_ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> trend(HttpServletRequest request) {
        return ResponseEntity.ok(distractionService.getDistractionTrendByWeek(userId(request)));
    }

    @GetMapping
    @PreAuthorize("hasRole('PLATFORM_ADMIN')")
    public ResponseEntity<Page<DistractionLogResponseDto>> all(@RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(distractionService.getAllLogs(page, size));
    }
}
