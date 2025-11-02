package vn.edu.fpt.pharma.service;

import vn.edu.fpt.pharma.dto.manager.ShiftWorkAssignRequest;
import vn.edu.fpt.pharma.dto.manager.ShiftWorkResponse;

import java.time.LocalDate;
import java.util.List;

public interface ShiftWorkService {
    List<ShiftWorkResponse> findByShiftAndDate(Long shiftId, LocalDate date);
    ShiftWorkResponse assignToShift(Long shiftId, ShiftWorkAssignRequest req);
    void removeShiftWork(Long id);
}
