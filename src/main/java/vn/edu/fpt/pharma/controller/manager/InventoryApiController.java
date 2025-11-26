package vn.edu.fpt.pharma.controller.manager;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.fpt.pharma.config.CustomUserDetails;
import vn.edu.fpt.pharma.service.InventoryReportService;
import java.util.Map;

@RestController
@RequestMapping("/api/manager/report/inventory")
public class InventoryApiController {

    private final InventoryReportService inventoryReportService;

    public InventoryApiController(InventoryReportService inventoryReportService) {
        this.inventoryReportService = inventoryReportService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> summary(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long branchId = userDetails != null && userDetails.getUser() != null ? userDetails.getUser().getBranchId() : null;
        Map<String, Object> summary = inventoryReportService.getInventorySummary(branchId);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/details")
    public ResponseEntity<java.util.List<java.util.Map<String, Object>>> details(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long branchId = userDetails != null && userDetails.getUser() != null ? userDetails.getUser().getBranchId() : null;
        java.util.List<java.util.Map<String, Object>> details = inventoryReportService.getInventoryDetails(branchId);
        return ResponseEntity.ok(details);
    }

    @GetMapping("/categories")
    public ResponseEntity<java.util.List<java.util.Map<String, Object>>> categories(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long branchId = userDetails != null && userDetails.getUser() != null ? userDetails.getUser().getBranchId() : null;
        java.util.List<java.util.Map<String, Object>> categories = inventoryReportService.getAllCategories(branchId);
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/statistics")
    public ResponseEntity<java.util.List<java.util.Map<String, Object>>> statistics(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long branchId = userDetails != null && userDetails.getUser() != null ? userDetails.getUser().getBranchId() : null;
        java.util.List<java.util.Map<String, Object>> statistics = inventoryReportService.getCategoryStatistics(branchId);
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/search")
    public ResponseEntity<java.util.List<java.util.Map<String, Object>>> search(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String status,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long branchId = userDetails != null && userDetails.getUser() != null ? userDetails.getUser().getBranchId() : null;
        java.util.List<java.util.Map<String, Object>> results = inventoryReportService.searchInventory(branchId, query, categoryId, status);
        return ResponseEntity.ok(results);
    }

}

