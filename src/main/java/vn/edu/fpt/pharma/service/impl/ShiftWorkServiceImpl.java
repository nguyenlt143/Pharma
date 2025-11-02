package vn.edu.fpt.pharma.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.pharma.constant.WorkType;
import vn.edu.fpt.pharma.dto.manager.ShiftWorkAssignRequest;
import vn.edu.fpt.pharma.dto.manager.ShiftWorkResponse;
import vn.edu.fpt.pharma.entity.Shift;
import vn.edu.fpt.pharma.entity.ShiftWork;
import vn.edu.fpt.pharma.entity.User;
import vn.edu.fpt.pharma.repository.ShiftRepository;
import vn.edu.fpt.pharma.repository.ShiftWorkRepository;
import vn.edu.fpt.pharma.repository.UserRepository;
import vn.edu.fpt.pharma.service.ShiftWorkService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ShiftWorkServiceImpl implements ShiftWorkService {

    private final ShiftWorkRepository shiftWorkRepo;
    private final ShiftRepository shiftRepo;
    private final UserRepository userRepo;

    public ShiftWorkServiceImpl(ShiftWorkRepository shiftWorkRepo, ShiftRepository shiftRepo, UserRepository userRepo) {
        this.shiftWorkRepo = shiftWorkRepo;
        this.shiftRepo = shiftRepo;
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
                .ifPresent(sw -> { throw new IllegalArgumentException("User already assigned to this shift on date"); });

        ShiftWork sw = new ShiftWork();
        sw.setShift(shift);
        sw.setBranchId(shift.getBranchId());
        sw.setUserId(user.getId());
        sw.setWorkDate(date);

        // parse workType, default NOT_STARTED
        try {
            sw.setWorkType(req.getWorkType() == null ? WorkType.NOT_STARTED : WorkType.valueOf(req.getWorkType()));
        } catch (Exception ex) {
            sw.setWorkType(WorkType.NOT_STARTED);
        }

        ShiftWork saved = shiftWorkRepo.save(sw);
        return toDto(saved);
    }

    @Override
    public void removeShiftWork(Long id) {
        shiftWorkRepo.deleteById(id);
    }

    private ShiftWorkResponse toDto(ShiftWork sw) {
        User u = userRepo.findById(sw.getUserId()).orElse(null);
        return ShiftWorkResponse.builder()
                .id(sw.getId())
                .userId(sw.getUserId())
                .userFullName(u != null ? u.getFullName() : null)
                .userName(u != null ? u.getUserName() : null)
                .phoneNumber(u != null ? u.getPhoneNumber() : null)
                .workType(sw.getWorkType() != null ? sw.getWorkType().name() : null)
                .createdAt(sw.getCreatedAt() != null ? sw.getCreatedAt().toString() : null)
                .build();
    }
}
