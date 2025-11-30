package vn.edu.fpt.pharma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.pharma.entity.Shift;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface ShiftRepository extends JpaRepository<Shift, Long>, JpaSpecificationExecutor<Shift> {
    @Query("select s from Shift s where (:q is null or lower(s.name) like lower(concat('%', :q, '%'))) and (:branchId is null or s.branchId = :branchId)")
    List<Shift> search(@Param("q") String q, @Param("branchId") Long branchId);

    @Query(value = "SELECT * FROM shifts s WHERE (:q IS NULL OR lower(s.name) LIKE lower(concat('%', :q, '%'))) AND (:branchId IS NULL OR s.branch_id = :branchId)", nativeQuery = true)
    List<Shift> searchIncludingDeleted(@Param("q") String q, @Param("branchId") Long branchId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE shifts SET deleted = false WHERE id = :id", nativeQuery = true)
    void restoreById(@Param("id") Long id);

    @Query("""
    SELECT s
    FROM Shift s
    JOIN ShiftAssignment a ON a.shift = s
    JOIN ShiftWork w ON w.assignment = a
    WHERE a.userId = :userId
      AND s.branchId = :branchId
      AND w.workDate = :today
      AND :nowTime >= s.startTime
      AND :nowTime < s.endTime
""")
    Optional<Shift> findCurrentShift(Long userId,
                                     Long branchId,
                                     LocalDate today,
                                     LocalTime nowTime);
}