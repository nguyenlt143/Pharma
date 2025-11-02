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
    public List<ShiftResponse> listShifts(String q, Long branchId) {
        List<Shift> list = repo.search(q, branchId);
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

        s.setBranchId(branchId);
        s.setName(request.getName());
        s.setNote(request.getNote());

        // parse LocalTime from request (expect HH:mm or HH:mm:ss)
        LocalTime st = parseLocalTime(request.getStartTime());
        LocalTime et = parseLocalTime(request.getEndTime());
        // combine with today's date
        LocalDate today = LocalDate.now();
        s.setStartTime(LocalDateTime.of(today, st));
        s.setEndTime(LocalDateTime.of(today, et));

        s = repo.save(s);
        return toDto(s);
    }

    @Override
    public void delete(Long id) {
        // soft delete handled by @SQLDelete on entity
        repo.deleteById(id);
    }

    private ShiftResponse toDto(Shift s) {
        return ShiftResponse.builder()
                .id(s.getId())
                .name(s.getName())
                .startTime(s.getStartTime() != null ? s.getStartTime().toLocalTime().toString() : null)
                .endTime(s.getEndTime() != null ? s.getEndTime().toLocalTime().toString() : null)
                .note(s.getNote())
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

