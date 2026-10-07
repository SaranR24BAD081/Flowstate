package com.example.demo.controller;

import com.example.demo.config.JwtAuthenticationFilter;
import com.example.demo.dto.FocusBlockRequestDto;
import com.example.demo.dto.FocusBlockResponseDto;
import com.example.demo.service.FocusBlockService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blocks")
@RequiredArgsConstructor
public class FocusBlockController {

    private final FocusBlockService blockService;

    private Long userId(HttpServletRequest request) {
        return (Long) request.getAttribute(JwtAuthenticationFilter.USER_ID_ATTRIBUTE);
    }

    @PostMapping
    @PreAuthorize("hasRole('PRACTITIONER')")
    public ResponseEntity<FocusBlockResponseDto> register(@Valid @RequestBody FocusBlockRequestDto dto,
                                                          HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(blockService.registerBlock(dto, userId(request)));
    }

    @GetMapping("/mine")
    @PreAuthorize("hasRole('PRACTITIONER')")
    public ResponseEntity<List<FocusBlockResponseDto>> mine(HttpServletRequest request) {
        return ResponseEntity.ok(blockService.getMyBlocks(userId(request)));
    }

    @GetMapping("/capacity-plan")
    @PreAuthorize("hasAnyRole('PRACTITIONER','FLOW_COACH','PLATFORM_ADMIN')")
    public ResponseEntity<List<FocusBlockResponseDto>> capacityPlan(Authentication authentication,
                                                                    HttpServletRequest request) {
        boolean practitioner = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PRACTITIONER"));
        return ResponseEntity.ok(practitioner
                ? blockService.getMyBlocks(userId(request))
                : blockService.getCapacityPlan());
    }

    @PutMapping("/{id}/protect")
    @PreAuthorize("hasRole('PRACTITIONER')")
    public ResponseEntity<FocusBlockResponseDto> protect(@PathVariable Long id, HttpServletRequest request) {
        return ResponseEntity.ok(blockService.protectBlock(id, userId(request)));
    }

    @PutMapping("/{id}/unprotect")
    @PreAuthorize("hasRole('PRACTITIONER')")
    public ResponseEntity<FocusBlockResponseDto> unprotect(@PathVariable Long id, HttpServletRequest request) {
        return ResponseEntity.ok(blockService.unprotectBlock(id, userId(request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('PRACTITIONER','PLATFORM_ADMIN')") // [REQ-CTRL-SEC-08]
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication authentication,
                                       HttpServletRequest request) {
        boolean admin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PLATFORM_ADMIN"));
        blockService.deleteBlock(id, userId(request), admin);
        return ResponseEntity.noContent().build();
    }
}
