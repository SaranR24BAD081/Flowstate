package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "distraction_log")
@Getter
@Setter
@NoArgsConstructor
public class DistractionLog {

    public enum DistractionCategory { NOTIFICATION, SOCIAL, ENVIRONMENT, MENTAL, OTHER }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private FocusSession session;

    @Column(name = "logged_at", nullable = false)
    private LocalDateTime loggedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 30)
    private DistractionCategory category;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "duration_seconds", nullable = false)
    private Integer durationSeconds;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (loggedAt == null) loggedAt = now;
        if (createdAt == null) createdAt = now;
    }
}
