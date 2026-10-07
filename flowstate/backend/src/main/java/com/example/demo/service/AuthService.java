package com.example.demo.service;

import com.example.demo.dto.AuthRequestDto;
import com.example.demo.dto.AuthResponseDto;
import com.example.demo.dto.RegisterDto;
import com.example.demo.entity.FlowUser;
import com.example.demo.exception.BusinessValidationException;
import com.example.demo.repository.FlowUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final FlowUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public AuthResponseDto register(RegisterDto dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new BusinessValidationException("Username is already taken");
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessValidationException("Email is already registered");
        }

        FlowUser user = new FlowUser();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setFullName(dto.getFullName());
        user.setRole(FlowUser.UserRole.valueOf(dto.getRole()));
        user.setIsActive(true);
        user = userRepository.save(user);
        return buildResponse(user);
    }

    @Transactional
    public AuthResponseDto login(AuthRequestDto dto) {
        FlowUser user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));
        if (!Boolean.TRUE.equals(user.getIsActive())
                || !passwordEncoder.matches(dto.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid username or password");
        }
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);
        return buildResponse(user);
    }

    @Transactional(readOnly = true)
    public AuthResponseDto refreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new BadCredentialsException("Invalid refresh token");
        }
        String username;
        try {
            username = jwtService.extractUsername(refreshToken);
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid refresh token");
        }
        if (!jwtService.isRefreshToken(refreshToken) || !jwtService.isTokenValid(refreshToken, username)) {
            throw new BadCredentialsException("Invalid refresh token");
        }
        FlowUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadCredentialsException("Invalid refresh token"));
        if (!Boolean.TRUE.equals(user.getIsActive())) {
            throw new BadCredentialsException("Invalid refresh token");
        }
        return buildResponse(user);
    }

    private AuthResponseDto buildResponse(FlowUser user) {
        String role = user.getRole().name();
        String access = jwtService.generateAccessToken(user.getUsername(), role, user.getId());
        String refresh = jwtService.generateRefreshToken(user.getUsername(), role, user.getId());
        return new AuthResponseDto(access, refresh, user.getId(), user.getUsername(), user.getFullName(), role);
    }
}
