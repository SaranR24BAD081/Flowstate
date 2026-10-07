package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "session_goal")
@Getter
@Setter
@NoArgsConstructor
public class SessionGoal {

    public enum GoalStatus { PENDING, ACHIEVED, PARTIAL, MISSED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private FocusSession session;

    @Column(name = "description", nullable = false, length = 500)
    private String description;

    @Column(name = "target_minutes", nullable = false)
    private Integer targetMinutes;

    @Column(name = "achieved_minutes", nullable = false)
    private Integer achievedMinutes;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private GoalStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (achievedMinutes == null) achievedMinutes = 0;
        if (status == null) status = GoalStatus.PENDING;
    }
}
