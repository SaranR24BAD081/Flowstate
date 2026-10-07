package com.example.demo.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    @Value("${flowstate.jwt.secret}") // [REQ-JWT-04]
    private String secret;

    @Value("${flowstate.jwt.access-token-expiry-ms:900000}")
    private long accessTokenExpiryMs;

    @Value("${flowstate.jwt.refresh-token-expiry-ms:604800000}")
    private long refreshTokenExpiryMs;

    private SecretKey signingKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)); // HS256
    }

    private String buildToken(String username, String role, Long userId, String type, long expiryMs) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .subject(username)
                .claim("role", role)
                .claim("userId", userId)
                .claim("type", type)
                .issuedAt(new Date(now))
                .expiration(new Date(now + expiryMs))
                .signWith(signingKey(), Jwts.SIG.HS256)
                .compact();
    }

    /** [REQ-JWT-01] */
    public String generateAccessToken(String username, String role, Long userId) {
        return buildToken(username, role, userId, "access", accessTokenExpiryMs);
    }

    public String generateRefreshToken(String username, String role, Long userId) {
        return buildToken(username, role, userId, "refresh", refreshTokenExpiryMs);
    }

    /** [REQ-JWT-02] */
    public boolean isTokenValid(String token, String username) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.getSubject() != null
                    && claims.getSubject().equals(username)
                    && claims.getExpiration().after(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isRefreshToken(String token) {
        try {
            return "refresh".equals(extractAllClaims(token).get("type", String.class));
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isAccessToken(String token) {
        try {
            return "access".equals(extractAllClaims(token).get("type", String.class));
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /** [REQ-JWT-03] */
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    public Long extractUserId(String token) {
        Number n = extractAllClaims(token).get("userId", Number.class);
        return n == null ? null : n.longValue();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith(signingKey()).build().parseSignedClaims(token).getPayload();
    }
}
