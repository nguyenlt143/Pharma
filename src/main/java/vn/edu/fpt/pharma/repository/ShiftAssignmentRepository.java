package vn.edu.fpt.pharma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.fpt.pharma.entity.ShiftAssignment;

import java.util.List;
import java.util.Optional;

public interface ShiftAssignmentRepository extends JpaRepository<ShiftAssignment, Long>, JpaSpecificationExecutor<ShiftAssignment> {
    @Query("SELECT sa FROM ShiftAssignment sa WHERE sa.shift.id = :shiftId")
    List<ShiftAssignment> findByShiftId(@Param("shiftId") Long shiftId);

    @Query("SELECT sa FROM ShiftAssignment sa WHERE sa.shift.id = :shiftId AND sa.userId = :userId")
    Optional<ShiftAssignment> findByShiftIdAndUserId(@Param("shiftId") Long shiftId, @Param("userId") Long userId);

    List<ShiftAssignment> findByUserId(Long userId);

    boolean existsByUserIdAndDeletedFalse(Long userId);
}
