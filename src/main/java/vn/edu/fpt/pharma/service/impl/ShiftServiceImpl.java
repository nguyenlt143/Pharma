package vn.edu.fpt.pharma.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.pharma.dto.manager.ShiftRequest;
import vn.edu.fpt.pharma.dto.manager.ShiftResponse;
import vn.edu.fpt.pharma.entity.Shift;
import vn.edu.fpt.pharma.repository.ShiftAssignmentRepository;
import vn.edu.fpt.pharma.repository.ShiftRepository;
import vn.edu.fpt.pharma.service.ShiftService;

import vn.edu.fpt.pharma.exception.InvalidTimeRangeException;
import vn.edu.fpt.pharma.exception.ShiftOverlapException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Locale;

@Service
@Transactional
public class ShiftServiceImpl implements ShiftService {

    private final ShiftRepository repo;
    private final ShiftAssignmentRepository shiftAssignmentRepository;

    public ShiftServiceImpl(ShiftRepository repo, ShiftAssignmentRepository shiftAssignmentRepository) {
        this.repo = repo;
        this.shiftAssignmentRepository = shiftAssignmentRepository;
    }

    @Override
    public List<ShiftResponse> listShifts(String q, Long branchId, boolean includeDeleted) {
        List<Shift> list;
        if (includeDeleted) {
            list = repo.searchIncludingDeleted(q, branchId);
        } else {
            list = repo.search(q, branchId);
        }
        return list.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public Optional<ShiftResponse> findById(Long id) {
        return repo.findById(id).map(this::toDto);
    }

    @Override
    public ShiftResponse save(ShiftRequest request, Long branchId) {
        Shift s;
        if (request.getId() != null) {
            s = repo.findById(request.getId()).orElse(new Shift());
        } else {
            s = new Shift();
        }

        String trimmedName = request.getName().trim();
        // Validate duplicate name
        repo.findByNameAndBranchId(trimmedName, branchId).ifPresent(existing -> {
            if (!existing.getId().equals(request.getId())) {
                throw new IllegalArgumentException("Tên ca làm việc đã tồn tại");
            }
        });

        LocalTime st = parseLocalTime(request.getStartTime());
        LocalTime et = parseLocalTime(request.getEndTime());

        // Validate end time is after start time
        if (et.isBefore(st) || et.equals(st)) {
            throw new InvalidTimeRangeException("Giờ kết thúc phải lớn hơn giờ bắt đầu");
        }

        // Validate no overlapping shifts
        List<Shift> overlapping = repo.findOverlappingShifts(branchId, st, et, request.getId());
        if (!overlapping.isEmpty()) {
            String overlappingNames = overlapping.stream()
                    .map(shift -> shift.getName() + " (" + shift.getStartTime() + " - " + shift.getEndTime() + ")")
                    .collect(Collectors.joining(", "));
            throw new ShiftOverlapException("Ca làm việc bị trùng với: " + overlappingNames);
        }

        s.setBranchId(branchId);
        s.setName(trimmedName);
        s.setNote(request.getNote());
        s.setStartTime(st);
        s.setEndTime(et);
        s = repo.save(s);
        return toDto(s);
    }

    @Override
    public void delete(Long id) {
        if (!shiftAssignmentRepository.findByShiftId(id).isEmpty()) {
            throw new IllegalStateException("Ca làm việc có nhân viên, không thể xóa");
        }
        // soft delete handled by @SQLDelete on entity
        repo.deleteById(id);
    }

    @Override
    public void restore(Long id) {
        Shift shiftToRestore = repo.findById(id).orElseThrow(() -> new RuntimeException("Ca làm việc không tồn tại"));
        List<Shift> overlapping = repo.findOverlappingShifts(shiftToRestore.getBranchId(), shiftToRestore.getStartTime(), shiftToRestore.getEndTime(), id);
        if (!overlapping.isEmpty()) {
            String overlappingNames = overlapping.stream()
                    .map(shift -> shift.getName() + " (" + shift.getStartTime() + " - " + shift.getEndTime() + ")")
                    .collect(Collectors.joining(", "));
            throw new ShiftOverlapException(overlappingNames);
        }
        repo.restoreById(id);
    }

    @Override
    public Optional<Shift> getCurrentShift(Long userId, Long branchId) {
        LocalDate today = LocalDate.now();
        LocalTime nowTime = LocalTime.now();
        return repo.findCurrentShift(userId, branchId, today, nowTime);
    }

    private ShiftResponse toDto(Shift s) {
        return ShiftResponse.builder()
                .id(s.getId())
                .name(s.getName())
                .startTime(s.getStartTime() != null ? s.getStartTime().toString() : null)
                .endTime(s.getEndTime() != null ? s.getEndTime().toString() : null)
                .note(s.getNote())
                .deleted(s.isDeleted())
                .build();
    }

    private LocalTime parseLocalTime(String str) {
        if (str == null || str.isBlank()) {
            throw new IllegalArgumentException("Thời gian không được để trống");
        }

        // Normalize whitespace
        String s = str.trim();

        // Try multiple patterns: 24-hour and 12-hour (AM/PM) formats
        DateTimeFormatter[] formatters = new DateTimeFormatter[] {
                DateTimeFormatter.ofPattern("H:mm"),
                DateTimeFormatter.ofPattern("HH:mm:ss"),
                DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH),
                DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH),
                DateTimeFormatter.ofPattern("h a", Locale.ENGLISH),
                DateTimeFormatter.ofPattern("ha", Locale.ENGLISH),
                DateTimeFormatter.ofPattern("h:mm:ss a", Locale.ENGLISH)
        };

        for (DateTimeFormatter fmt : formatters) {
            try {
                return LocalTime.parse(s, fmt);
            } catch (DateTimeParseException ignored) {
                // try next
            }
        }

        // As a last resort, try parsing ISO LocalTime
        try {
            return LocalTime.parse(s);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Định dạng thời gian không hợp lệ. Vui lòng sử dụng định dạng giờ hợp lệ (ví dụ: 08:00, 20:30, 12:00 AM, 12:00 PM)");
        }
    }
}
