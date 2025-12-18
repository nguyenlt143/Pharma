package vn.edu.fpt.pharma.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.pharma.dto.manager.ShiftWorkAssignRequest;
import vn.edu.fpt.pharma.dto.manager.ShiftWorkResponse;
import vn.edu.fpt.pharma.dto.shifts.ShiftSummaryVM;
import vn.edu.fpt.pharma.entity.Shift;
import vn.edu.fpt.pharma.entity.ShiftAssignment;
import vn.edu.fpt.pharma.entity.ShiftWork;
import vn.edu.fpt.pharma.entity.User;
import vn.edu.fpt.pharma.exception.NoActiveShiftException;
import vn.edu.fpt.pharma.repository.ShiftAssignmentRepository;
import vn.edu.fpt.pharma.repository.ShiftRepository;
import vn.edu.fpt.pharma.repository.ShiftWorkRepository;
import vn.edu.fpt.pharma.repository.UserRepository;
import vn.edu.fpt.pharma.service.ShiftWorkService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class ShiftWorkServiceImpl implements ShiftWorkService {

    private final ShiftWorkRepository shiftWorkRepo;
    private final ShiftRepository shiftRepo;
    private final ShiftAssignmentRepository assignmentRepo;
    private final UserRepository userRepo;

    public ShiftWorkServiceImpl(ShiftWorkRepository shiftWorkRepo, ShiftRepository shiftRepo,
                                ShiftAssignmentRepository assignmentRepo, UserRepository userRepo) {
        this.shiftWorkRepo = shiftWorkRepo;
        this.shiftRepo = shiftRepo;
        this.assignmentRepo = assignmentRepo;
        this.userRepo = userRepo;
    }

    @Override
    public List<ShiftWorkResponse> findByShiftAndDate(Long shiftId, LocalDate date) {
        List<ShiftWork> list = shiftWorkRepo.findByShiftIdAndWorkDate(shiftId, date);
        return list.stream().map(this::toDto).collect(Collectors.toList());
    }


    @Override
    public ShiftWorkResponse assignToShift(Long shiftId, ShiftWorkAssignRequest req) {
        // validate shift exists
        Shift shift = shiftRepo.findById(shiftId).orElseThrow(() -> new IllegalArgumentException("Ca làm việc không tồn tại"));
        User user = userRepo.findById(req.getUserId()).orElseThrow(() -> new IllegalArgumentException("Nhân viên không tồn tại"));

        LocalDate date = LocalDate.parse(req.getWorkDate());

        // prevent duplicates
        shiftWorkRepo.findByShiftIdAndUserIdAndWorkDate(shiftId, req.getUserId(), date)
                .ifPresent(existing -> { throw new IllegalArgumentException("Nhân viên đã được phân công vào ca này trong ngày đã chọn"); });

        // Find or create ShiftAssignment
        ShiftAssignment assignment = assignmentRepo.findByShiftIdAndUserId(shiftId, req.getUserId())
                .orElseGet(() -> {
                    ShiftAssignment newAssignment = new ShiftAssignment();
                    newAssignment.setShift(shift);
                    newAssignment.setUserId(user.getId());
                    return assignmentRepo.save(newAssignment);
                });

        // Create ShiftWork
        ShiftWork sw = new ShiftWork();
        sw.setAssignment(assignment);
        sw.setWorkDate(date);


        ShiftWork saved = shiftWorkRepo.save(sw);
        return toDto(saved);
    }

    @Override
    public void removeShiftWork(Long id) {
        shiftWorkRepo.deleteById(id);
    }

    @Override
    public void restore(Long id) {
        // Find the deleted ShiftWork
        ShiftWork deletedWork = shiftWorkRepo.findByIdIncludingDeleted(id)
                .orElseThrow(() -> new IllegalArgumentException("Ca làm việc không tồn tại"));

        // Get shift assignment and validate
        ShiftAssignment assignment = deletedWork.getAssignment();
        if (assignment == null) {
            throw new IllegalArgumentException("Ca làm việc không có phân công hợp lệ");
        }

        Shift shift = assignment.getShift();
        if (shift == null) {
            throw new IllegalArgumentException("Không tìm thấy thông tin ca làm việc");
        }

        Long userId = assignment.getUserId();
        Long shiftId = shift.getId();
        LocalDate workDate = deletedWork.getWorkDate();

        // Check if there's already an active ShiftWork for this user on this shift and date
        shiftWorkRepo.findActiveByShiftIdAndUserIdAndWorkDate(shiftId, userId, workDate)
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Nhân viên đã được phân công vào ca này trong ngày đã chọn. Không thể khôi phục.");
                });

        // Restore the ShiftWork
        shiftWorkRepo.restoreById(id);
    }


    private ShiftWorkResponse toDto(ShiftWork sw) {
        ShiftAssignment assignment = sw.getAssignment();
        User u = assignment != null ? userRepo.findById(assignment.getUserId()).orElse(null) : null;
        return ShiftWorkResponse.builder()
                .id(sw.getId())
                .userId(assignment != null ? assignment.getUserId() : null)
                .userFullName(u != null ? u.getFullName() : null)
                .userName(u != null ? u.getUserName() : null)
                .phoneNumber(u != null ? u.getPhoneNumber() : null)
                .workType(null) // workType is no longer in ShiftWork entity
                .createdAt(sw.getCreatedAt() != null ? sw.getCreatedAt().toString() : null)
                .build();
    }

    @Override
    public List<ShiftSummaryVM> getSummary(Long branchId, Long userId, LocalDate start, LocalDate end) {

        List<Object[]> rows = shiftWorkRepo.getShiftSummary(branchId, userId, start, end);

        Map<Long, ShiftSummaryVM> result = new LinkedHashMap<>();

        rows.forEach(r -> {
            Long shiftId = ((Number) r[0]).longValue();
            String name = (String) r[1];
            LocalTime startTime = ((java.sql.Time) r[2]).toLocalTime();
            LocalTime endTime = ((java.sql.Time) r[3]).toLocalTime();
            LocalDate workDate = r[4] != null ? ((java.sql.Date) r[4]).toLocalDate() : null;

            result.putIfAbsent(shiftId,
                    new ShiftSummaryVM(
                            name + " (" + startTime + "-" + endTime + ")",
                            new LinkedHashMap<>()
                    )
            );

            if (workDate != null) {
                result.get(shiftId).days().put(workDate, true);
            }
        });

        // fill missing days = false
        for (ShiftSummaryVM dto : result.values()) {
            for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
                dto.days().putIfAbsent(d, false);
            }
        }

        return new ArrayList<>(result.values());
    }

    @Override
    public Long getCurrentShiftWorkId(Long userId, Long branchId) {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        return shiftWorkRepo.findShiftWork(userId, branchId, today, now)
                .map(ShiftWork::getId)
                .orElseThrow(() -> new NoActiveShiftException("Nhân viên không có ca làm hiện tại"));
    }
}
