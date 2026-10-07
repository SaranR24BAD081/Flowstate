package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "focus_block")
@Getter
@Setter
@NoArgsConstructor
public class FocusBlock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "practitioner_id", nullable = false)
    private FlowUser practitioner;

    @Column(name = "day_of_week", nullable = false, length = 10)
    private String dayOfWeek;

    @Column(name = "block_start_time", nullable = false)
    private LocalTime blockStartTime;

    @Column(name = "block_end_time", nullable = false)
    private LocalTime blockEndTime;

    @Column(name = "is_protected", nullable = false)
    private Boolean isProtected = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_session_type", length = 20)
    private FocusSession.SessionType preferredSessionType;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (isProtected == null) isProtected = false;
    }
}
