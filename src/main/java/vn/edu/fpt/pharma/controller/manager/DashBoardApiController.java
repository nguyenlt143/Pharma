package vn.edu.fpt.pharma.controller.manager;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.fpt.pharma.config.CustomUserDetails;
import vn.edu.fpt.pharma.dto.manager.DashboardData;
import vn.edu.fpt.pharma.entity.User;
import vn.edu.fpt.pharma.service.ManagerDashBoardService;

@RestController
@RequestMapping("/api/manager/dashboard")
public class DashBoardApiController {
    private final ManagerDashBoardService managerDashBoardService;
    public DashBoardApiController(ManagerDashBoardService managerDashBoardService) {
        this.managerDashBoardService = managerDashBoardService;
    }


    @GetMapping
    public ResponseEntity<DashboardData> getDashboardData(
            @RequestParam(name = "days", defaultValue = "0") int days,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        User u = userDetails.getUser();
        Long branchId = userDetails.getUser().getBranchId();
        DashboardData data = managerDashBoardService.getDashboardDataByPeriod(days,branchId);
        return ResponseEntity.ok(data);
    }
}

