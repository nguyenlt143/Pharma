package vn.edu.fpt.pharma.service;

import vn.edu.fpt.pharma.dto.manager.ShiftWorkAssignRequest;
import vn.edu.fpt.pharma.dto.manager.ShiftWorkResponse;
import vn.edu.fpt.pharma.dto.shifts.ShiftSummaryVM;

import java.time.LocalDate;
import java.util.List;

public interface ShiftWorkService {
    List<ShiftWorkResponse> findByShiftAndDate(Long shiftId, LocalDate date);
    ShiftWorkResponse assignToShift(Long shiftId, ShiftWorkAssignRequest req);
    void removeShiftWork(Long id);

    List<ShiftSummaryVM> getSummary(Long branchId, Long userId, LocalDate start, LocalDate end);

    Long getCurrentShiftWorkId(Long userId, Long branchId);
}
