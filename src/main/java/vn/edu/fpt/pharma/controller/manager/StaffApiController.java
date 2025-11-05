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

    // ✅ Lấy tất cả staff thuộc branch của manager
    @GetMapping
    public ResponseEntity<List<UserDto>> getAll(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long branchId = userDetails.getUser().getBranchId();
        return ResponseEntity.ok(userService.getStaffs(branchId));
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
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

