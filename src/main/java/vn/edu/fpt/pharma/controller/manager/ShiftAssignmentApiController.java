package vn.edu.fpt.pharma.controller.manager;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.pharma.config.CustomUserDetails;
import vn.edu.fpt.pharma.dto.manager.ShiftAssignmentRequest;
import vn.edu.fpt.pharma.dto.manager.ShiftAssignmentResponse;
import vn.edu.fpt.pharma.dto.manager.UserDto;
import vn.edu.fpt.pharma.entity.ShiftAssignment;
import vn.edu.fpt.pharma.entity.User;
import vn.edu.fpt.pharma.service.ShiftAssignmentService;
import vn.edu.fpt.pharma.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/manager/shifts/{shiftId}")
public class ShiftAssignmentApiController {

    private final ShiftAssignmentService assignmentService;
    private final UserService userService;

    public ShiftAssignmentApiController(ShiftAssignmentService assignmentService, UserService userService) {
        this.assignmentService = assignmentService;
        this.userService = userService;
    }

    @GetMapping("/assignments")
    public ResponseEntity<List<ShiftAssignmentResponse>> listAssignments(@PathVariable Long shiftId) {
        List<ShiftAssignment> list = assignmentService.findAllByShiftId(shiftId);
        List<ShiftAssignmentResponse> res = list.stream().map(this::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(res);
    }

    @PostMapping("/assign")
    public ResponseEntity<ShiftAssignmentResponse> assign(@PathVariable Long shiftId,
                                                          @Valid @RequestBody ShiftAssignmentRequest req) {
        ShiftAssignment sa = assignmentService.createAssignment(shiftId, req.getUserId());
        return ResponseEntity.ok(toDto(sa));
    }

    @DeleteMapping("/remove/{userId}")
    public ResponseEntity<Void> remove(@PathVariable Long shiftId, @PathVariable Long userId) {
        assignmentService.removeAssignment(shiftId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/assign")
    public ResponseEntity<List<UserDto>> listAssignable(@PathVariable Long shiftId,
                                                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        User current = userDetails.getUser();
        // Staff active in branch excluding those already assigned
        List<Long> assignedUserIds = assignmentService.findAllByShiftId(shiftId).stream()
                .map(ShiftAssignment::getUserId)
                .toList();
        List<UserDto> staffs = userService.getStaffsActive(current.getBranchId()).stream()
                .filter(u -> !assignedUserIds.contains(u.getId()))
                .toList();
        return ResponseEntity.ok(staffs);
    }

    private ShiftAssignmentResponse toDto(ShiftAssignment sa) {
        if (sa == null) return null;
        UserDto u = userService.getById(sa.getUserId());
        return ShiftAssignmentResponse.builder()
                .id(sa.getId())
                .userId(sa.getUserId())
                .userFullName(u.getFullName())
                .roleName(u.getRoleName())
                .status("Assigned")
                .createdAt(sa.getCreatedAt() != null ? sa.getCreatedAt().toString() : null)
                .build();
    }
}

