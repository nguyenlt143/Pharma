package vn.edu.fpt.pharma.service;

import vn.edu.fpt.pharma.base.BaseService;
import vn.edu.fpt.pharma.entity.ShiftAssignment;

import java.util.List;

public interface ShiftAssignmentService extends BaseService<ShiftAssignment, Long> {
    ShiftAssignment findByShiftIdAndUserId(Long shiftId, Long userId);
    ShiftAssignment findByShiftId(Long shiftId);
    List<ShiftAssignment> findAllByShiftId(Long shiftId);
    ShiftAssignment createAssignment(Long shiftId, Long userId);
    void removeAssignment(Long shiftId, Long userId);
}
