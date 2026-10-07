package com.example.demo.repository;

import com.example.demo.entity.CoachRecommendation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CoachRecommendationRepository extends JpaRepository<CoachRecommendation, Long> {

    Page<CoachRecommendation> findByPractitionerIdOrderByCreatedAtDesc(Long practitionerId, Pageable pageable);

    Page<CoachRecommendation> findByCoachIdOrderByCreatedAtDesc(Long coachId, Pageable pageable);

    List<CoachRecommendation> findByPractitionerIdAndStatus(Long practitionerId,
                                                            CoachRecommendation.RecommendationStatus status);

    long countByPractitionerIdAndStatus(Long practitionerId, CoachRecommendation.RecommendationStatus status);

    boolean existsByIdAndCoachId(Long id, Long coachId);

    boolean existsByIdAndPractitionerId(Long id, Long practitionerId);

    @Query("SELECT cr.status, COUNT(cr) FROM CoachRecommendation cr WHERE cr.coach.id = :coachId GROUP BY cr.status")
    List<Object[]> countByStatusForCoach(@Param("coachId") Long coachId);
}
