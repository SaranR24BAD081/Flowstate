package com.example.demo.repository;

import com.example.demo.entity.FocusSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FocusSessionRepository extends JpaRepository<FocusSession, Long> {

    Page<FocusSession> findByPractitionerId(Long practitionerId, Pageable pageable);

    List<FocusSession> findByPractitionerIdAndStatus(Long practitionerId, FocusSession.SessionStatus status); // [REQ-REPO-04]

    Optional<FocusSession> findByIdAndPractitionerId(Long id, Long practitionerId);

    long countByStatus(FocusSession.SessionStatus status);

    // [REQ-REPO-05]
    @Query("SELECT fs FROM FocusSession fs WHERE fs.practitioner.id = :practitionerId "
            + "AND (:status IS NULL OR fs.status = :status) "
            + "AND (:title IS NULL OR LOWER(fs.title) LIKE LOWER(CONCAT('%', :title, '%')))")
    Page<FocusSession> findByPractitionerIdAndStatusAndTitle(@Param("practitionerId") Long practitionerId,
                                                             @Param("status") FocusSession.SessionStatus status,
                                                             @Param("title") String title,
                                                             Pageable pageable);

    @Query("SELECT fs FROM FocusSession fs WHERE fs.practitioner.id = :practitionerId "
            + "AND fs.status IN (com.example.demo.entity.FocusSession$SessionStatus.SCHEDULED, "
            + "com.example.demo.entity.FocusSession$SessionStatus.IN_PROGRESS) "
            + "AND ((fs.plannedStart < :plannedEnd AND fs.plannedEnd > :plannedStart))")
    List<FocusSession> findConflictingSessions(@Param("practitionerId") Long practitionerId,
                                               @Param("plannedStart") LocalDateTime plannedStart,
                                               @Param("plannedEnd") LocalDateTime plannedEnd);

    @Query("SELECT AVG(fs.focusScore) FROM FocusSession fs WHERE fs.practitioner.id = :practitionerId "
            + "AND fs.status = com.example.demo.entity.FocusSession$SessionStatus.COMPLETED")
    Double findAverageFocusScore(@Param("practitionerId") Long practitionerId);

    @Query("SELECT SUM(fs.focusScore) FROM FocusSession fs WHERE fs.practitioner.id = :practitionerId")
    Long findTotalFocusMinutes(@Param("practitionerId") Long practitionerId);

    @Query("SELECT fs.sessionType, COUNT(fs) FROM FocusSession fs WHERE fs.practitioner.id = :practitionerId "
            + "GROUP BY fs.sessionType")
    List<Object[]> countBySessionType(@Param("practitionerId") Long practitionerId);

    @Query("SELECT fs FROM FocusSession fs WHERE fs.practitioner.id = :practitionerId ORDER BY fs.createdAt DESC")
    List<FocusSession> findRecentSessions(@Param("practitionerId") Long practitionerId, Pageable pageable);

    @Query("SELECT fs FROM FocusSession fs WHERE (:status IS NULL OR fs.status = :status) "
            + "AND (:title IS NULL OR LOWER(fs.title) LIKE LOWER(CONCAT('%', :title, '%'))) "
            + "ORDER BY fs.createdAt DESC")
    Page<FocusSession> findAllWithStatusAndTitleFilter(@Param("status") FocusSession.SessionStatus status,
                                                       @Param("title") String title,
                                                       Pageable pageable);
}
