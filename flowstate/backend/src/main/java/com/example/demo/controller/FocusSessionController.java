package com.example.demo.controller;

import com.example.demo.config.JwtAuthenticationFilter;
import com.example.demo.dto.FocusSessionRequestDto;
import com.example.demo.dto.FocusSessionResponseDto;
import com.example.demo.dto.SessionStatsResponseDto;
import com.example.demo.service.FocusSessionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class FocusSessionController {

    private final FocusSessionService sessionService;

    private Long userId(HttpServletRequest request) {
        return (Long) request.getAttribute(JwtAuthenticationFilter.USER_ID_ATTRIBUTE);
    }

    @PostMapping
    @PreAuthorize("hasRole('PRACTITIONER')")
    public ResponseEntity<FocusSessionResponseDto> schedule(@Valid @RequestBody FocusSessionRequestDto dto,
                                                            HttpServletRequest request) { // [REQ-CTRL-04/09]
        return ResponseEntity.status(HttpStatus.CREATED).body(sessionService.scheduleSession(dto, userId(request)));
    }

    @GetMapping("/mine")
    @PreAuthorize("hasRole('PRACTITIONER')")
    public ResponseEntity<Page<FocusSessionResponseDto>> mine(@RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "10") int size,
                                                              @RequestParam(required = false) String status,
                                                              @RequestParam(required = false) String title,
                                                              HttpServletRequest request) { // [REQ-CTRL-06]
        return ResponseEntity.ok(sessionService.getMySessionsPaginated(userId(request), status, title, page, size));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('PLATFORM_ADMIN','FLOW_COACH')")
    public ResponseEntity<Page<FocusSessionResponseDto>> all(@RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "10") int size,
                                                             @RequestParam(required = false) String status,
                                                             @RequestParam(required = false) String title) {
        return ResponseEntity.ok(sessionService.getAllSessionsPaginated(status, title, page, size));
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('PRACTITIONER','PLATFORM_ADMIN')")
    public ResponseEntity<SessionStatsResponseDto> stats(HttpServletRequest request) {
        return ResponseEntity.ok(sessionService.getSummaryStats(userId(request)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('PRACTITIONER')")
    public ResponseEntity<FocusSessionResponseDto> getById(@PathVariable Long id, HttpServletRequest request) { // [REQ-CTRL-10]
        return ResponseEntity.ok(sessionService.getSessionById(id, userId(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PRACTITIONER')")
    public ResponseEntity<FocusSessionResponseDto> update(@PathVariable Long id,
                                                          @Valid @RequestBody FocusSessionRequestDto dto,
                                                          HttpServletRequest request) {
        return ResponseEntity.ok(sessionService.updateSession(id, dto, userId(request)));
    }

    @PutMapping("/{id}/start")
    @PreAuthorize("hasRole('PRACTITIONER')")
    public ResponseEntity<FocusSessionResponseDto> start(@PathVariable Long id, HttpServletRequest request) { // [REQ-CTRL-07]
        return ResponseEntity.ok(sessionService.startSession(id, userId(request)));
    }

    @PutMapping("/{id}/complete")
    @PreAuthorize("hasRole('PRACTITIONER')")
    public ResponseEntity<FocusSessionResponseDto> complete(@PathVariable Long id, HttpServletRequest request) {
        return ResponseEntity.ok(sessionService.completeSession(id, userId(request)));
    }

    @PutMapping("/{id}/abandon")
    @PreAuthorize("hasRole('PRACTITIONER')")
    public ResponseEntity<FocusSessionResponseDto> abandon(@PathVariable Long id,
                                                           @RequestBody(required = false) Map<String, String> body,
                                                           HttpServletRequest request) {
        String reason = body == null ? null : body.get("abandonReason");
        return ResponseEntity.ok(sessionService.abandonSession(id, reason, userId(request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PRACTITIONER')")
    public ResponseEntity<String> delete(@PathVariable Long id, HttpServletRequest request) { // [REQ-CTRL-08]
        sessionService.deleteSession(id, userId(request));
        return ResponseEntity.ok("FocusSession deleted successfully.");
    }
}
