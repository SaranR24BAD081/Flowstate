package com.example.demo.controller;

import com.example.demo.dto.UserSummaryDto;
import com.example.demo.entity.FlowUser;
import com.example.demo.repository.FlowUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/** Small helper endpoint so coaches can pick a practitioner when issuing a recommendation. */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final FlowUserRepository userRepository;

    @GetMapping("/practitioners")
    @PreAuthorize("hasAnyRole('FLOW_COACH','PLATFORM_ADMIN')")
    public ResponseEntity<List<UserSummaryDto>> practitioners() {
        return ResponseEntity.ok(userRepository.findActiveByRole(FlowUser.UserRole.PRACTITIONER).stream()
                .map(u -> new UserSummaryDto(u.getId(), u.getUsername(), u.getFullName()))
                .collect(Collectors.toList()));
    }
}
