package com.example.demo.service;

import com.example.demo.dto.DistractionBreakdownResponseDto;
import com.example.demo.dto.DistractionLogRequestDto;
import com.example.demo.dto.DistractionLogResponseDto;
import com.example.demo.entity.DistractionLog;
import com.example.demo.entity.FocusSession;
import com.example.demo.exception.BusinessValidationException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.DistractionLogRepository;
import com.example.demo.repository.FocusSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DistractionLogService {

    private final DistractionLogRepository logRepository;
    private final FocusSessionRepository sessionRepository;

    @Transactional
    public DistractionLogResponseDto logDistraction(DistractionLogRequestDto dto, Long practitionerId) {
        FocusSession session = sessionRepository.findByIdAndPractitionerId(dto.getSessionId(), practitionerId)
                .orElseThrow(() -> new ResourceNotFoundException("FocusSession not found"));
        if (session.getStatus() != FocusSession.SessionStatus.IN_PROGRESS) {
            throw new BusinessValidationException("Distractions can only be logged for IN_PROGRESS sessions");
        }
        DistractionLog log = new DistractionLog();
        log.setSession(session);
        log.setCategory(DistractionLog.DistractionCategory.valueOf(dto.getCategory()));
        log.setDescription(dto.getDescription());
        log.setDurationSeconds(dto.getDurationSeconds());
        return DistractionLogResponseDto.from(logRepository.save(log));
    }

    @Transactional(readOnly = true)
    public List<DistractionLogResponseDto> getLogsForSession(Long sessionId) {
        return logRepository.findBySessionIdOrderByLoggedAtDesc(sessionId).stream()
                .map(DistractionLogResponseDto::from).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DistractionBreakdownResponseDto getDistractionBreakdown(Long practitionerId) {
        List<DistractionBreakdownResponseDto.CategoryStat> stats = new ArrayList<>();
        long totalCount = 0;
        long totalSeconds = 0;
        for (Object[] row : logRepository.findDistractionBreakdownByPractitioner(practitionerId)) {
            long count = ((Number) row[1]).longValue();
            long seconds = row[2] == null ? 0 : ((Number) row[2]).longValue();
            stats.add(new DistractionBreakdownResponseDto.CategoryStat(
                    ((DistractionLog.DistractionCategory) row[0]).name(), count, seconds));
            totalCount += count;
            totalSeconds += seconds;
        }
        return new DistractionBreakdownResponseDto(totalCount, totalSeconds, stats);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getDistractionTrendByWeek(Long practitionerId) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] row : logRepository.findWeeklyDistractionTrend(practitionerId)) {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("week", String.valueOf(row[0]));
            entry.put("count", ((Number) row[1]).longValue());
            result.add(entry);
        }
        return result;
    }

    @Transactional(readOnly = true)
    public Page<DistractionLogResponseDto> getAllLogs(int page, int size) {
        return logRepository.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "loggedAt")))
                .map(DistractionLogResponseDto::from);
    }
}
