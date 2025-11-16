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
}