package vn.edu.fpt.pharma.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.pharma.dto.manager.ShiftWorkAssignRequest;
import vn.edu.fpt.pharma.dto.manager.ShiftWorkResponse;
import vn.edu.fpt.pharma.entity.Shift;
import vn.edu.fpt.pharma.entity.ShiftAssignment;
import vn.edu.fpt.pharma.entity.ShiftWork;
import vn.edu.fpt.pharma.entity.User;
import vn.edu.fpt.pharma.repository.ShiftAssignmentRepository;
import vn.edu.fpt.pharma.repository.ShiftRepository;
import vn.edu.fpt.pharma.repository.ShiftWorkRepository;
import vn.edu.fpt.pharma.repository.UserRepository;
import vn.edu.fpt.pharma.service.ShiftWorkService;

import java.time.LocalDate;
import java.util.List;
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
        Shift shift = shiftRepo.findById(shiftId).orElseThrow(() -> new IllegalArgumentException("Shift not found"));
        User user = userRepo.findById(req.getUserId()).orElseThrow(() -> new IllegalArgumentException("User not found"));

        LocalDate date = LocalDate.parse(req.getWorkDate());

        // prevent duplicates
        shiftWorkRepo.findByShiftIdAndUserIdAndWorkDate(shiftId, req.getUserId(), date)
                .ifPresent(_ -> { throw new IllegalArgumentException("User already assigned to this shift on date"); });

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
}
