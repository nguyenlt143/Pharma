package vn.edu.fpt.pharma.controller.manager;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.fpt.pharma.config.CustomUserDetails;
import vn.edu.fpt.pharma.service.RevenueReportService;

import java.util.Map;

@RestController
@RequestMapping("/api/manager/report/revenue")
public class RevenueApiController {

    private final RevenueReportService revenueReportService;

    public RevenueApiController(RevenueReportService revenueReportService) {
        this.revenueReportService = revenueReportService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getRevenue(
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String mode,
            @RequestParam(required = false) String period,
            @RequestParam(required = false) Long shift,
            @RequestParam(required = false, name = "employeeId") Long employeeId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long branchId = userDetails != null && userDetails.getUser() != null ? userDetails.getUser().getBranchId() : null;

        Map<String, Object> body = revenueReportService.getRevenueReport(branchId, date, mode, period, shift, employeeId);
        return ResponseEntity.ok(body);
    }
}
