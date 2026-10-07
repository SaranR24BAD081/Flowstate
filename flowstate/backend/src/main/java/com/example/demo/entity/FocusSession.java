package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "focus_session")
@Getter
@Setter
@NoArgsConstructor
public class FocusSession {

    public enum SessionStatus { SCHEDULED, IN_PROGRESS, COMPLETED, ABANDONED }

    public enum SessionType { DEEP_WORK, CREATIVE, REVIEW }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "practitioner_id", nullable = false)
    private FlowUser practitioner;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "planned_start", nullable = false)
    private LocalDateTime plannedStart;

    @Column(name = "planned_end", nullable = false)
    private LocalDateTime plannedEnd;

    @Column(name = "actual_start")
    private LocalDateTime actualStart;

    @Column(name = "actual_end")
    private LocalDateTime actualEnd;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private SessionStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "session_type", nullable = false, length = 20)
    private SessionType sessionType;

    @Column(name = "focus_score")
    private Integer focusScore;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "abandon_reason", columnDefinition = "TEXT")
    private String abandonReason;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SessionGoal> goals = new ArrayList<>();

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DistractionLog> distractions = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (status == null) status = SessionStatus.SCHEDULED;
    }
}
