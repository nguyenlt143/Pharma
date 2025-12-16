package vn.edu.fpt.pharma.controller.owner;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.fpt.pharma.config.CustomUserDetails;
import vn.edu.fpt.pharma.service.InventoryReportService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/owner/inventory/current")
@RequiredArgsConstructor
public class OwnerInventoryCurrentApiController {

    private final InventoryReportService inventoryReportService;

    private boolean isOwner(CustomUserDetails userDetails) {
        if (userDetails == null) return false;
        String role = userDetails.getRole();
        return role != null && (role.equalsIgnoreCase("BUSINESS_OWNER") || role.equalsIgnoreCase("OWNER"));
    }

    /**
     * Summary KPIs for current stock of a branch
     * GET /api/owner/inventory/current?branchId=1
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> summary(
            @RequestParam Long branchId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (!isOwner(userDetails)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Map<String, Object> summary = inventoryReportService.getInventorySummary(branchId);
        return ResponseEntity.ok(summary);
    }

    /**
     * Detailed current stock (per batch) for a branch
     * GET /api/owner/inventory/current/details?branchId=1
     */
    @GetMapping("/details")
    public ResponseEntity<List<Map<String, Object>>> details(
            @RequestParam Long branchId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (!isOwner(userDetails)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<Map<String, Object>> details = inventoryReportService.getInventoryDetails(branchId);
        return ResponseEntity.ok(details);
    }

    /**
     * Category list for a branch's inventory
     * GET /api/owner/inventory/current/categories?branchId=1
     */
    @GetMapping("/categories")
    public ResponseEntity<List<Map<String, Object>>> categories(
            @RequestParam Long branchId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (!isOwner(userDetails)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<Map<String, Object>> categories = inventoryReportService.getAllCategories(branchId);
        return ResponseEntity.ok(categories);
    }

    /**
     * Category statistics for charts
     * GET /api/owner/inventory/current/statistics?branchId=1
     */
    @GetMapping("/statistics")
    public ResponseEntity<List<Map<String, Object>>> statistics(
            @RequestParam Long branchId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (!isOwner(userDetails)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<Map<String, Object>> statistics = inventoryReportService.getCategoryStatistics(branchId);
        return ResponseEntity.ok(statistics);
    }

    /**
     * Search current stock in a branch
     * GET /api/owner/inventory/current/search?branchId=1&query=...&categoryId=...&status=...
     */
    @GetMapping("/search")
    public ResponseEntity<List<Map<String, Object>>> search(
            @RequestParam Long branchId,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String status,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (!isOwner(userDetails)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<Map<String, Object>> results = inventoryReportService.searchInventory(branchId, query, categoryId, status);
        return ResponseEntity.ok(results);
    }
}


