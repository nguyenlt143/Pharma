package vn.edu.fpt.pharma.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.pharma.base.BaseServiceImpl;
import vn.edu.fpt.pharma.entity.Shift;
import vn.edu.fpt.pharma.entity.ShiftAssignment;
import vn.edu.fpt.pharma.entity.ShiftWork;
import vn.edu.fpt.pharma.repository.ShiftAssignmentRepository;
import vn.edu.fpt.pharma.repository.ShiftRepository;
import vn.edu.fpt.pharma.repository.ShiftWorkRepository;
import vn.edu.fpt.pharma.service.AuditService;
import vn.edu.fpt.pharma.service.ShiftAssignmentService;
import vn.edu.fpt.pharma.exception.ShiftOverlapException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ShiftAssignmentServiceImpl extends BaseServiceImpl<ShiftAssignment, Long, ShiftAssignmentRepository> implements ShiftAssignmentService  {

    private final ShiftRepository shiftRepository;
    private final ShiftWorkRepository shiftWorkRepository;

    public ShiftAssignmentServiceImpl(ShiftAssignmentRepository repository, AuditService auditService,
                                     ShiftRepository shiftRepository, ShiftWorkRepository shiftWorkRepository) {
        super(repository, auditService);
        this.shiftRepository = shiftRepository;
        this.shiftWorkRepository = shiftWorkRepository;
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
        Shift shift = shiftRepository.findById(shiftId).orElseThrow(() -> new IllegalArgumentException("Ca làm việc không tồn tại"));
        // Prevent duplicate
        ShiftAssignment existing = findByShiftIdAndUserId(shiftId, userId);
        if (existing != null) return existing;

        // Check overlapping assignments for the same user across other shifts
        List<ShiftAssignment> userAssignments = repository.findByUserId(userId);
        for (ShiftAssignment sa : userAssignments) {
            Shift other = sa.getShift();
            if (other == null) continue;
            // Two shifts overlap if start < other.end && end > other.start
            if (shift.getStartTime() != null && shift.getEndTime() != null && other.getStartTime() != null && other.getEndTime() != null) {
                if (shift.getStartTime().isBefore(other.getEndTime()) && shift.getEndTime().isAfter(other.getStartTime())) {
                    throw new ShiftOverlapException("Nhân viên đã được phân công vào ca trùng khớp: " + other.getName());
                }
            }
        }

        ShiftAssignment sa = new ShiftAssignment();
        sa.setShift(shift);
        sa.setUserId(userId);
        sa = repository.save(sa);

        // Tự động tạo 30 ngày ShiftWork từ hôm nay
        extendShiftWorks(sa.getId(), 30);

        return sa;
    }

    @Override
    public void removeAssignment(Long shiftId, Long userId) {
        ShiftAssignment sa = findByShiftIdAndUserId(shiftId, userId);
        if (sa != null) {
            repository.delete(sa);
        }
    }

    @Override
    public void extendShiftWorks(Long assignmentId, int days) {
        ShiftAssignment assignment = repository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("Phân công ca không tồn tại"));

        // Tìm ngày cuối cùng, nếu không có thì bắt đầu từ hôm nay
        LocalDate lastDate = shiftWorkRepository.findLastWorkDateByAssignmentId(assignmentId);
        LocalDate startDate = (lastDate != null) ? lastDate.plusDays(1) : LocalDate.now();

        // Tạo ShiftWork cho số ngày mong muốn
        List<ShiftWork> works = new ArrayList<>();
        for (int i = 0; i < days; i++) {
            ShiftWork sw = ShiftWork.builder()
                    .assignment(assignment)
                    .workDate(startDate.plusDays(i))
                    .build();
            works.add(sw);
        }

        shiftWorkRepository.saveAll(works);
    }

    @Override
    public LocalDate getLastWorkDate(Long assignmentId) {
        return shiftWorkRepository.findLastWorkDateByAssignmentId(assignmentId);
    }

    @Override
    public long getRemainingWorkDays(Long assignmentId) {
        LocalDate today = LocalDate.now();
        return shiftWorkRepository.countRemainingWorkDays(assignmentId, today);
    }
}
