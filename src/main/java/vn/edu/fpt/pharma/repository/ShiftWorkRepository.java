package vn.edu.fpt.pharma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import vn.edu.fpt.pharma.entity.ShiftWork;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ShiftWorkRepository extends JpaRepository<ShiftWork, Long>, JpaSpecificationExecutor<ShiftWork> {
    List<ShiftWork> findByShiftIdAndWorkDate(Long shiftId, LocalDate workDate);
    List<ShiftWork> findByShiftId(Long shiftId);
    Optional<ShiftWork> findByShiftIdAndUserIdAndWorkDate(Long shiftId, Long userId, LocalDate workDate);
}