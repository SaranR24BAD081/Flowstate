package com.example.demo.service;

import com.example.demo.dto.FocusSessionRequestDto;
import com.example.demo.dto.FocusSessionResponseDto;
import com.example.demo.dto.SessionStatsResponseDto;
import com.example.demo.entity.FlowUser;
import com.example.demo.entity.FocusSession;
import com.example.demo.entity.SessionGoal;
import com.example.demo.exception.BusinessValidationException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.DistractionLogRepository;
import com.example.demo.repository.FlowUserRepository;
import com.example.demo.repository.FocusSessionRepository;
import com.example.demo.repository.SessionGoalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FocusSessionService {

    private static final long MIN_SESSION_MINUTES = 15;

    private final FocusSessionRepository sessionRepository;
    private final FlowUserRepository userRepository;
    private final SessionGoalRepository goalRepository;
    private final DistractionLogRepository distractionRepository;

    // ------------------------------------------------------------------ schedule
    /** [REQ-SVC-09] [REQ-SVC-05] */
    @Transactional
    public FocusSessionResponseDto scheduleSession(FocusSessionRequestDto dto, Long practitionerId) {
        FlowUser practitioner = userRepository.findById(practitionerId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        validateDuration(dto.getPlannedStart(), dto.getPlannedEnd());
        if (!sessionRepository.findConflictingSessions(practitionerId, dto.getPlannedStart(), dto.getPlannedEnd()).isEmpty()) {
            throw new BusinessValidationException("Session overlaps with an existing scheduled or in-progress session");
        }

        FocusSession session = new FocusSession();
        session.setPractitioner(practitioner);
        applyRequest(session, dto);
        session.setStatus(FocusSession.SessionStatus.SCHEDULED);
        return FocusSessionResponseDto.from(sessionRepository.save(session));
    }

    @Transactional
    public FocusSessionResponseDto updateSession(Long sessionId, FocusSessionRequestDto dto, Long practitionerId) {
        FocusSession session = findOwned(sessionId, practitionerId);
        if (session.getStatus() != FocusSession.SessionStatus.SCHEDULED) {
            throw new BusinessValidationException("Only SCHEDULED sessions can be edited");
        }
        validateDuration(dto.getPlannedStart(), dto.getPlannedEnd());
        boolean conflict = sessionRepository
                .findConflictingSessions(practitionerId, dto.getPlannedStart(), dto.getPlannedEnd())
                .stream().anyMatch(s -> !s.getId().equals(sessionId));
        if (conflict) {
            throw new BusinessValidationException("Session overlaps with an existing scheduled or in-progress session");
        }
        applyRequest(session, dto);
        return FocusSessionResponseDto.from(sessionRepository.save(session));
    }

    // ------------------------------------------------------------------ lifecycle
    /** [REQ-SVC-07] */
    @Transactional
    public FocusSessionResponseDto startSession(Long sessionId, Long practitionerId) {
        FocusSession session = findOwned(sessionId, practitionerId);
        if (session.getStatus() != FocusSession.SessionStatus.SCHEDULED) {
            throw new BusinessValidationException("Only SCHEDULED sessions can be started");
        }
        session.setActualStart(LocalDateTime.now());
        session.setStatus(FocusSession.SessionStatus.IN_PROGRESS);
        return FocusSessionResponseDto.from(sessionRepository.save(session));
    }

    /** [REQ-SVC-07] [REQ-SVC-03] */
    @Transactional
    public FocusSessionResponseDto completeSession(Long sessionId, Long practitionerId) {
        FocusSession session = findOwned(sessionId, practitionerId);
        if (session.getStatus() != FocusSession.SessionStatus.IN_PROGRESS) {
            throw new BusinessValidationException("Only IN_PROGRESS sessions can be completed");
        }
        session.setActualEnd(LocalDateTime.now());
        session.setStatus(FocusSession.SessionStatus.COMPLETED);

        markPendingGoalsMissed(sessionId);
        session.setFocusScore(calculateFocusScore(session));
        return FocusSessionResponseDto.from(sessionRepository.save(session));
    }

    /** [REQ-SVC-04] */
    @Transactional
    public FocusSessionResponseDto abandonSession(Long sessionId, String reason, Long practitionerId) {
        FocusSession session = findOwned(sessionId, practitionerId);
        if (session.getStatus() != FocusSession.SessionStatus.IN_PROGRESS) {
            throw new BusinessValidationException("Only IN_PROGRESS sessions can be abandoned");
        }
        session.setAbandonReason(reason);
        session.setActualEnd(LocalDateTime.now());
        session.setStatus(FocusSession.SessionStatus.ABANDONED);
        markPendingGoalsMissed(sessionId);
        return FocusSessionResponseDto.from(sessionRepository.save(session));
    }

    /** [REQ-SVC-08] */
    @Transactional
    public void deleteSession(Long sessionId, Long practitionerId) {
        FocusSession session = findOwned(sessionId, practitionerId);
        if (session.getStatus() != FocusSession.SessionStatus.SCHEDULED) {
            throw new BusinessValidationException("Only SCHEDULED sessions can be deleted");
        }
        sessionRepository.delete(session);
    }

    // ------------------------------------------------------------------ queries
    @Transactional(readOnly = true)
    public FocusSessionResponseDto getSessionById(Long sessionId, Long practitionerId) {
        return FocusSessionResponseDto.from(findOwned(sessionId, practitionerId));
    }

    @Transactional(readOnly = true)
    public Page<FocusSessionResponseDto> getMySessionsPaginated(Long practitionerId, String status, String title,
                                                                int page, int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return sessionRepository
                .findByPractitionerIdAndStatusAndTitle(practitionerId, parseStatus(status), normalizeTitle(title), pageable)
                .map(FocusSessionResponseDto::from);
    }

    @Transactional(readOnly = true)
    public Page<FocusSessionResponseDto> getAllSessionsPaginated(String status, String title, int page, int size) {
        return sessionRepository
                .findAllWithStatusAndTitleFilter(parseStatus(status), normalizeTitle(title), PageRequest.of(page, size))
                .map(FocusSessionResponseDto::from);
    }

    /** [REQ-SVC-STATS] */
    @Transactional(readOnly = true)
    public SessionStatsResponseDto getSummaryStats(Long userId) {
        Map<String, Long> byType = new LinkedHashMap<>();
        for (FocusSession.SessionType t : FocusSession.SessionType.values()) {
            byType.put(t.name(), 0L);
        }
        long total = 0;
        for (Object[] row : sessionRepository.countBySessionType(userId)) {
            long count = ((Number) row[1]).longValue();
            byType.put(((FocusSession.SessionType) row[0]).name(), count);
            total += count;
        }

        long completed = sessionRepository
                .findByPractitionerIdAndStatus(userId, FocusSession.SessionStatus.COMPLETED).size();
        long abandoned = sessionRepository
                .findByPractitionerIdAndStatus(userId, FocusSession.SessionStatus.ABANDONED).size();

        Double avg = sessionRepository.findAverageFocusScore(userId);
        Long minutes = sessionRepository.findTotalFocusMinutes(userId);
        long totalMinutes = minutes == null ? 0 : minutes;

        double avgRounded = avg == null ? 0 : Math.round(avg * 10.0) / 10.0;
        double hours = Math.round((totalMinutes / 60.0) * 10.0) / 10.0;
        return new SessionStatsResponseDto(total, completed, abandoned, avgRounded, totalMinutes, hours, byType);
    }

    // ------------------------------------------------------------------ helpers
    private FocusSession findOwned(Long sessionId, Long practitionerId) {
        return sessionRepository.findByIdAndPractitionerId(sessionId, practitionerId)
                .orElseThrow(() -> new ResourceNotFoundException("FocusSession not found"));
    }

    private void validateDuration(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null || !end.isAfter(start)) {
            throw new BusinessValidationException("Planned end time must be after planned start time");
        }
        if (Duration.between(start, end).toMinutes() < MIN_SESSION_MINUTES) {
            throw new BusinessValidationException("Session must be at least 15 minutes long");
        }
    }

    private void applyRequest(FocusSession session, FocusSessionRequestDto dto) {
        session.setTitle(dto.getTitle());
        session.setPlannedStart(dto.getPlannedStart());
        session.setPlannedEnd(dto.getPlannedEnd());
        session.setSessionType(FocusSession.SessionType.valueOf(dto.getSessionType()));
        session.setNotes(dto.getNotes());
    }

    private void markPendingGoalsMissed(Long sessionId) {
        List<SessionGoal> pending = goalRepository.findBySessionIdAndStatus(sessionId, SessionGoal.GoalStatus.PENDING);
        pending.forEach(g -> g.setStatus(SessionGoal.GoalStatus.MISSED));
        if (!pending.isEmpty()) {
            goalRepository.saveAll(pending);
        }
    }

    /**
     * Base 100 - (distractionSeconds / sessionSeconds) * 50 + (achievedGoals / totalGoals) * 20 (bonus capped at 20),
     * floored at 0 and capped at 100.
     */
    private int calculateFocusScore(FocusSession session) {
        long sessionSeconds = Duration.between(session.getActualStart(), session.getActualEnd()).getSeconds();
        Long distractionSeconds = distractionRepository.sumDurationSecondsBySessionId(session.getId());
        double penalty = 0;
        if (sessionSeconds > 0 && distractionSeconds != null) {
            penalty = ((double) distractionSeconds / sessionSeconds) * 50;
        }

        List<SessionGoal> goals = goalRepository.findBySessionId(session.getId());
        double bonus = 0;
        if (!goals.isEmpty()) {
            long achieved = goals.stream().filter(g -> g.getStatus() == SessionGoal.GoalStatus.ACHIEVED).count();
            bonus = Math.min(20, ((double) achieved / goals.size()) * 20);
        }

        double score = Math.max(0, 100 - penalty + bonus);
        return (int) Math.round(Math.min(100, score));
    }

    private FocusSession.SessionStatus parseStatus(String status) {
        if (status == null || status.isBlank()) return null;
        try {
            return FocusSession.SessionStatus.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessValidationException("Invalid status filter: " + status);
        }
    }

    private String normalizeTitle(String title) {
        return (title == null || title.isBlank()) ? null : title.trim();
    }
}
