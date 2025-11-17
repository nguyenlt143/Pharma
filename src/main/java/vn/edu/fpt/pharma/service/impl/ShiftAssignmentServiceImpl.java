package vn.edu.fpt.pharma.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.pharma.base.BaseServiceImpl;
import vn.edu.fpt.pharma.entity.Shift;
import vn.edu.fpt.pharma.entity.ShiftAssignment;
import vn.edu.fpt.pharma.repository.ShiftAssignmentRepository;
import vn.edu.fpt.pharma.repository.ShiftRepository;
import vn.edu.fpt.pharma.service.AuditService;
import vn.edu.fpt.pharma.service.ShiftAssignmentService;

import java.util.List;

@Service
@Transactional
public class ShiftAssignmentServiceImpl extends BaseServiceImpl<ShiftAssignment, Long, ShiftAssignmentRepository> implements ShiftAssignmentService  {

    private final ShiftRepository shiftRepository;

    public ShiftAssignmentServiceImpl(ShiftAssignmentRepository repository, AuditService auditService, ShiftRepository shiftRepository) {
        super(repository, auditService);
        this.shiftRepository = shiftRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public ShiftAssignment findByShiftIdAndUserId(Long shiftId, Long userId) {
        return repository.findByShiftIdAndUserId(shiftId, userId).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public ShiftAssignment findByShiftId(Long shiftId) {
        // If multiple assignments per shift, return first; adjust as needed.
        List<ShiftAssignment> list = repository.findByShiftId(shiftId);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShiftAssignment> findAllByShiftId(Long shiftId) {
        return repository.findByShiftId(shiftId);
    }

    @Override
    public ShiftAssignment createAssignment(Long shiftId, Long userId) {
        Shift shift = shiftRepository.findById(shiftId).orElseThrow(() -> new IllegalArgumentException("Shift not found"));
        // Prevent duplicate
        ShiftAssignment existing = findByShiftIdAndUserId(shiftId, userId);
        if (existing != null) return existing;
        ShiftAssignment sa = new ShiftAssignment();
        sa.setShift(shift);
        sa.setUserId(userId);
        return repository.save(sa);
    }

    @Override
    public void removeAssignment(Long shiftId, Long userId) {
        ShiftAssignment sa = findByShiftIdAndUserId(shiftId, userId);
        if (sa != null) {
            repository.delete(sa);
        }
    }
}
