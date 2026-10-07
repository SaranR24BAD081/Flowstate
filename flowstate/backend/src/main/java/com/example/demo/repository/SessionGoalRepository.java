package com.example.demo.repository;

import com.example.demo.entity.SessionGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SessionGoalRepository extends JpaRepository<SessionGoal, Long> {

    List<SessionGoal> findBySessionId(Long sessionId);

    List<SessionGoal> findBySessionIdAndStatus(Long sessionId, SessionGoal.GoalStatus status);

    @Query("SELECT COUNT(g) FROM SessionGoal g WHERE g.session.practitioner.id = :practitionerId "
            + "AND g.status = com.example.demo.entity.SessionGoal$GoalStatus.ACHIEVED")
    long countAchievedGoalsByPractitioner(@Param("practitionerId") Long practitionerId);

    @Query("SELECT COUNT(g) FROM SessionGoal g WHERE g.session.practitioner.id = :practitionerId "
            + "AND g.status != com.example.demo.entity.SessionGoal$GoalStatus.PENDING")
    long countResolvedGoalsByPractitioner(@Param("practitionerId") Long practitionerId);

    @Query("SELECT g.status, COUNT(g) FROM SessionGoal g WHERE g.session.practitioner.id = :practitionerId "
            + "GROUP BY g.status")
    List<Object[]> countGoalsByStatusForPractitioner(@Param("practitionerId") Long practitionerId);
}
