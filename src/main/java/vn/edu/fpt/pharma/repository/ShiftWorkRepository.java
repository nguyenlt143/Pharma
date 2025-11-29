package vn.edu.fpt.pharma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.fpt.pharma.entity.ShiftWork;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ShiftWorkRepository extends JpaRepository<ShiftWork, Long>, JpaSpecificationExecutor<ShiftWork> {
    @Query("SELECT sw FROM ShiftWork sw WHERE sw.assignment.shift.id = :shiftId AND sw.workDate = :workDate")
    List<ShiftWork> findByShiftIdAndWorkDate(@Param("shiftId") Long shiftId, @Param("workDate") LocalDate workDate);

    @Query("SELECT sw FROM ShiftWork sw WHERE sw.assignment.shift.id = :shiftId AND sw.assignment.userId = :userId AND sw.workDate = :workDate")
    Optional<ShiftWork> findByShiftIdAndUserIdAndWorkDate(@Param("shiftId") Long shiftId, @Param("userId") Long userId, @Param("workDate") LocalDate workDate);

    @Query("SELECT MAX(sw.workDate) FROM ShiftWork sw WHERE sw.assignment.id = :assignmentId")
    LocalDate findLastWorkDateByAssignmentId(@Param("assignmentId") Long assignmentId);

    @Query("SELECT COUNT(sw) FROM ShiftWork sw WHERE sw.assignment.id = :assignmentId AND sw.workDate >= :fromDate")
    long countRemainingWorkDays(@Param("assignmentId") Long assignmentId, @Param("fromDate") LocalDate fromDate);

    @Query(value = """
    SELECT 
        s.id AS shift_id,
        s.name,
        s.start_time,
        s.end_time,
        w.work_date
    FROM shifts s
    LEFT JOIN shift_assignments a
        ON a.shift_id = s.id AND a.user_id = :userId
    LEFT JOIN shift_works w
        ON w.assignment_id = a.id 
            AND w.work_date BETWEEN :startDate AND :endDate
    WHERE s.branch_id = :branchId
    ORDER BY s.start_time
    """, nativeQuery = true)
    List<Object[]> getShiftSummary(
            @Param("branchId") Long branchId,
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}