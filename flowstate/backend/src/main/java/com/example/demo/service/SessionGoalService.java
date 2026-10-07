package com.example.demo.service;

import com.example.demo.dto.GoalCompletionRateResponseDto;
import com.example.demo.dto.SessionGoalRequestDto;
import com.example.demo.dto.SessionGoalResponseDto;
import com.example.demo.entity.FocusSession;
import com.example.demo.entity.SessionGoal;
import com.example.demo.exception.BusinessValidationException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.FocusSessionRepository;
import com.example.demo.repository.SessionGoalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SessionGoalService {

    private final SessionGoalRepository goalRepository;
    private final FocusSessionRepository sessionRepository;

    @Transactional
    public SessionGoalResponseDto attachGoal(SessionGoalRequestDto dto, Long practitionerId) {
        FocusSession session = sessionRepository.findByIdAndPractitionerId(dto.getSessionId(), practitionerId)
                .orElseThrow(() -> new ResourceNotFoundException("FocusSession not found"));
        if (session.getStatus() != FocusSession.SessionStatus.SCHEDULED
                && session.getStatus() != FocusSession.SessionStatus.IN_PROGRESS) {
            throw new BusinessValidationException("Goals can only be attached to SCHEDULED or IN_PROGRESS sessions");
        }
        SessionGoal goal = new SessionGoal();
        goal.setSession(session);
        goal.setDescription(dto.getDescription());
        goal.setTargetMinutes(dto.getTargetMinutes());
        goal.setAchievedMinutes(0);
        goal.setStatus(SessionGoal.GoalStatus.PENDING);
        return SessionGoalResponseDto.from(goalRepository.save(goal));
    }

    @Transactional
    public SessionGoalResponseDto markGoalOutcome(Long goalId, Integer achievedMinutes, Long practitionerId) {
        SessionGoal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new ResourceNotFoundException("SessionGoal not found"));
        if (!goal.getSession().getPractitioner().getId().equals(practitionerId)) {
            throw new ResourceNotFoundException("SessionGoal not found");
        }
        if (achievedMinutes == null || achievedMinutes < 0) {
            throw new BusinessValidationException("Achieved minutes must be zero or greater");
        }
        goal.setAchievedMinutes(achievedMinutes);
        if (achievedMinutes >= goal.getTargetMinutes()) {
            goal.setStatus(SessionGoal.GoalStatus.ACHIEVED);
        } else if (achievedMinutes > 0) {
            goal.setStatus(SessionGoal.GoalStatus.PARTIAL);
        } else {
            goal.setStatus(SessionGoal.GoalStatus.MISSED);
        }
        return SessionGoalResponseDto.from(goalRepository.save(goal));
    }

    @Transactional(readOnly = true)
    public List<SessionGoalResponseDto> getGoalsForSession(Long sessionId) {
        return goalRepository.findBySessionId(sessionId).stream()
                .map(SessionGoalResponseDto::from).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public GoalCompletionRateResponseDto getGoalCompletionRate(Long practitionerId) {
        long achieved = goalRepository.countAchievedGoalsByPractitioner(practitionerId);
        long resolved = goalRepository.countResolvedGoalsByPractitioner(practitionerId);
        double rate = resolved == 0 ? 0 : Math.round(((double) achieved / resolved) * 1000.0) / 10.0;

        Map<String, Long> byStatus = new LinkedHashMap<>();
        for (SessionGoal.GoalStatus s : SessionGoal.GoalStatus.values()) byStatus.put(s.name(), 0L);
        for (Object[] row : goalRepository.countGoalsByStatusForPractitioner(practitionerId)) {
            byStatus.put(((SessionGoal.GoalStatus) row[0]).name(), ((Number) row[1]).longValue());
        }
        return new GoalCompletionRateResponseDto(achieved, resolved, rate, byStatus);
    }
}
