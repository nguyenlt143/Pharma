package vn.edu.fpt.pharma.controller.manager;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.pharma.config.CustomUserDetails;
import vn.edu.fpt.pharma.dto.manager.UserDto;
import vn.edu.fpt.pharma.dto.manager.UserRequest;
import vn.edu.fpt.pharma.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/manager/staffs")
public class StaffApiController {

    private final UserService userService;

    public StaffApiController(UserService userService) {
        this.userService = userService;
    }

    // ✅ Lấy tất cả staff thuộc branch của manager (mặc định ẩn đã xóa)
    @GetMapping
    public ResponseEntity<List<UserDto>> getAll(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                @RequestParam(name = "showDeleted", defaultValue = "false") boolean showDeleted) {
        Long branchId = userDetails.getUser().getBranchId();
        if (showDeleted) {
            return ResponseEntity.ok(userService.getStaffs(branchId)); // includes deleted
        }
        return ResponseEntity.ok(userService.getStaffsActive(branchId)); // active only
    }

    // ✅ Lấy danh sách Dược sĩ (roleId=6) thuộc branch của manager
    @GetMapping("/pharmacists")
    public ResponseEntity<List<UserDto>> getPharmacists(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long branchId = userDetails.getUser().getBranchId();
        return ResponseEntity.ok(userService.getPharmacists(branchId));
    }

    // ✅ Get by ID
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    // ✅ Create staff — ép branchId từ user login
    @PostMapping
    public ResponseEntity<UserDto> create(@Valid @RequestBody UserRequest req,
                                          @AuthenticationPrincipal CustomUserDetails userDetails) {
        req.setBranchId(userDetails.getUser().getBranchId());
        return ResponseEntity.ok(userService.create(req));
    }

    // ✅ Update staff — không cho sửa branchId
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> update(@PathVariable Long id, @Valid @RequestBody UserRequest req) {
        return ResponseEntity.ok(userService.update(id, req));
    }

    // ✅ Delete staff
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            userService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    // ✅ Restore staff (soft-deleted)
    @PatchMapping("/{id}/restore")
    public ResponseEntity<Void> restore(@PathVariable Long id) {
        userService.restore(id);
        return ResponseEntity.noContent().build();
    }
}

