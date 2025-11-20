package vn.edu.fpt.pharma.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.pharma.dto.manager.ShiftRequest;
import vn.edu.fpt.pharma.dto.manager.ShiftResponse;
import vn.edu.fpt.pharma.entity.Shift;
import vn.edu.fpt.pharma.repository.ShiftRepository;
import vn.edu.fpt.pharma.service.ShiftService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ShiftServiceImpl implements ShiftService {

    private final ShiftRepository repo;

    public ShiftServiceImpl(ShiftRepository repo) {
        this.repo = repo;
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

        LocalTime st = parseLocalTime(request.getStartTime());
        LocalTime et = parseLocalTime(request.getEndTime());

        // Validate end time is after start time
        if (et.isBefore(st) || et.equals(st)) {
            throw new IllegalArgumentException("Giờ kết thúc phải lớn hơn giờ bắt đầu");
        }

        s.setBranchId(branchId);
        s.setName(request.getName());
        s.setNote(request.getNote());
        s.setStartTime(st);
        s.setEndTime(et);
        s = repo.save(s);
        return toDto(s);
    }

    @Override
    public void delete(Long id) {
        // soft delete handled by @SQLDelete on entity
        repo.deleteById(id);
    }

    @Override
    public void restore(Long id) {
        repo.restoreById(id);
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
        if (str == null) return LocalTime.MIDNIGHT;
        try {
            return LocalTime.parse(str);
        } catch (Exception ex) {
            // try adding seconds
            try {
                return LocalTime.parse(str + ":00");
            } catch (Exception ex2) {
                return LocalTime.MIDNIGHT;
            }
        }
    }
}

