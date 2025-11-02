package vn.edu.fpt.pharma.controller.manager;

import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.pharma.dto.manager.ShiftWorkAssignRequest;
import vn.edu.fpt.pharma.dto.manager.ShiftWorkResponse;
import vn.edu.fpt.pharma.service.ShiftWorkService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/manager")
public class ShiftWorkApiController {

    private final ShiftWorkService shiftWorkService;

    public ShiftWorkApiController(ShiftWorkService shiftWorkService) {
        this.shiftWorkService = shiftWorkService;
    }

    @GetMapping("/shifts/{shiftId}/works")
    public ResponseEntity<List<ShiftWorkResponse>> listWorks(@PathVariable Long shiftId,
                                                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDate d = date == null ? LocalDate.now() : date;
        return ResponseEntity.ok(shiftWorkService.findByShiftAndDate(shiftId, d));
    }

    @PostMapping("/shifts/{shiftId}/works")
    public ResponseEntity<ShiftWorkResponse> assign(@PathVariable Long shiftId,
                                                    @Valid @RequestBody ShiftWorkAssignRequest req) {
        // req.workDate expected "YYYY-MM-DD"
        ShiftWorkResponse res = shiftWorkService.assignToShift(shiftId, req);
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/shift-works/{id}")
    public ResponseEntity<Void> deleteWork(@PathVariable Long id) {
        shiftWorkService.removeShiftWork(id);
        return ResponseEntity.noContent().build();
    }
}
