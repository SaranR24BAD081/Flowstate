package com.example.demo.repository;

import com.example.demo.entity.DistractionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DistractionLogRepository extends JpaRepository<DistractionLog, Long> {

    List<DistractionLog> findBySessionIdOrderByLoggedAtDesc(Long sessionId);

    long countBySessionId(Long sessionId);

    @Query("SELECT dl.category, COUNT(dl), SUM(dl.durationSeconds) FROM DistractionLog dl "
            + "WHERE dl.session.practitioner.id = :practitionerId GROUP BY dl.category")
    List<Object[]> findDistractionBreakdownByPractitioner(@Param("practitionerId") Long practitionerId);

    // NOTE: native SQL must use COUNT(dl.id) (COUNT(dl) is JPQL-only syntax).
    @Query(value = "SELECT YEARWEEK(dl.logged_at) AS wk, COUNT(dl.id) FROM distraction_log dl "
            + "JOIN focus_session fs ON dl.session_id = fs.id "
            + "WHERE fs.practitioner_id = :practitionerId GROUP BY wk ORDER BY wk DESC LIMIT 8",
            nativeQuery = true)
    List<Object[]> findWeeklyDistractionTrend(@Param("practitionerId") Long practitionerId);

    @Query("SELECT COALESCE(SUM(dl.durationSeconds), 0) FROM DistractionLog dl WHERE dl.session.id = :sessionId")
    Long sumDurationSecondsBySessionId(@Param("sessionId") Long sessionId);
}
