package vn.edu.fpt.pharma.controller.manager;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import vn.edu.fpt.pharma.config.CustomUserDetails;
import vn.edu.fpt.pharma.dto.manager.ShiftRequest;
import vn.edu.fpt.pharma.dto.manager.ShiftResponse;
import vn.edu.fpt.pharma.entity.User;
import vn.edu.fpt.pharma.service.ShiftService;

import java.util.List;

@RestController
@RequestMapping("/api/manager/shifts")
public class ShiftApiController {

    private final ShiftService shiftService;

    public ShiftApiController(ShiftService shiftService) {
        this.shiftService = shiftService;
    }

    @GetMapping
    public List<ShiftResponse> list(@RequestParam(required = false) String q,
                                    @AuthenticationPrincipal CustomUserDetails userDetails) {
        User u = userDetails.getUser();
        return shiftService.listShifts(q, u.getBranchId());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShiftResponse> get(@PathVariable Long id) {
        return shiftService.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ShiftResponse> create(@Valid @RequestBody ShiftRequest req,
                                                @AuthenticationPrincipal CustomUserDetails userDetails) {
        User u = userDetails.getUser();
        ShiftResponse s = shiftService.save(req, u.getBranchId());
        return ResponseEntity.ok(s);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ShiftResponse> update(@PathVariable Long id, @Valid @RequestBody ShiftRequest req,
                                                @AuthenticationPrincipal CustomUserDetails userDetails) {
        User u = userDetails.getUser();
        req.setId(id);
        ShiftResponse s = shiftService.save(req, u.getBranchId());
        return ResponseEntity.ok(s);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        // Here we perform soft delete via JPA @SQLDelete configured on entity
        shiftService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

