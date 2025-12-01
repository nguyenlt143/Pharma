package vn.edu.fpt.pharma.service;

import vn.edu.fpt.pharma.dto.manager.ShiftRequest;
import vn.edu.fpt.pharma.dto.manager.ShiftResponse;
import vn.edu.fpt.pharma.entity.Shift;

import java.util.List;
import java.util.Optional;

public interface ShiftService {
    List<ShiftResponse> listShifts(String q, Long branchId, boolean includeDeleted);
    Optional<ShiftResponse> findById(Long id);
    ShiftResponse save(ShiftRequest request, Long branchId);
    void delete(Long id);
    void restore(Long id);

    Optional<Shift> getCurrentShift(Long userId, Long branchId);
}
