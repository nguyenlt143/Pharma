package vn.edu.fpt.pharma.controller.manager;

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

    @GetMapping
    public List<UserDto> getAll(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long branchId = userDetails.getUser().getBranchId();
        return userService.getStaffs(branchId);
    }


    @GetMapping("/{id}")
    public UserDto getById(@PathVariable Long id) {
        return userService.getById(id);
    }

    @PostMapping
    public UserDto create(@RequestBody UserRequest req,
                          @AuthenticationPrincipal CustomUserDetails userDetails) {
        req.setBranchId(userDetails.getUser().getBranchId()); // ép branch theo manager
        return userService.create(req);
    }

    @PutMapping("/{id}")
    public UserDto update(@PathVariable Long id, @RequestBody UserRequest req) {
        return userService.update(id, req);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }
}


