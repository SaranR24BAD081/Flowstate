package com.example.demo.repository;

import com.example.demo.entity.FocusBlock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalTime;
import java.util.List;

public interface FocusBlockRepository extends JpaRepository<FocusBlock, Long> {

    List<FocusBlock> findByPractitionerIdOrderByDayOfWeekAscBlockStartTimeAsc(Long practitionerId);

    List<FocusBlock> findByPractitionerIdAndDayOfWeek(Long practitionerId, String dayOfWeek);

    long countByPractitionerIdAndIsProtected(Long practitionerId, Boolean isProtected);

    @Query("SELECT fb FROM FocusBlock fb WHERE fb.practitioner.id = :practitionerId "
            + "AND fb.dayOfWeek = :dayOfWeek "
            + "AND ((fb.blockStartTime < :blockEndTime AND fb.blockEndTime > :blockStartTime))")
    List<FocusBlock> findOverlappingBlocks(@Param("practitionerId") Long practitionerId,
                                           @Param("dayOfWeek") String dayOfWeek,
                                           @Param("blockStartTime") LocalTime blockStartTime,
                                           @Param("blockEndTime") LocalTime blockEndTime);

    @Query("SELECT fb FROM FocusBlock fb WHERE fb.practitioner.id = :practitionerId "
            + "AND fb.dayOfWeek = :dayOfWeek AND fb.isProtected = true")
    List<FocusBlock> findProtectedBlocksForDay(@Param("practitionerId") Long practitionerId,
                                               @Param("dayOfWeek") String dayOfWeek);
}
