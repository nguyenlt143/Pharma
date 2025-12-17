package vn.edu.fpt.pharma.controller.owner;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.pharma.config.CustomUserDetails;
import vn.edu.fpt.pharma.dto.manager.KpiData;
import vn.edu.fpt.pharma.dto.manager.TopProductItem;
import vn.edu.fpt.pharma.repository.InventoryMovementRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/owner/report")
@RequiredArgsConstructor
public class ReportApiController {

    private final InventoryMovementRepository inventoryMovementRepository;

    /**
     * View detail revenue – Report
     * GET /api/owner/report/revenue?period=2024-01&branchId=1
     * Only supports monthly revenue by branch
     */
    @GetMapping("/revenue")
    public ResponseEntity<Map<String, Object>> getDetailRevenue(
            @RequestParam(required = false) String period,
            @RequestParam(required = false) Long branchId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        // Verify owner role
        if (userDetails == null || !"OWNER".equalsIgnoreCase(userDetails.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Only calculate monthly revenue
        LocalDate firstOfMonth = parseMonthStart(period);
        LocalDateTime from = firstOfMonth.atStartOfDay();
        LocalDateTime to = firstOfMonth.plusMonths(1).atStartOfDay();

        // Tổng quan KPI (doanh thu, lợi nhuận, số đơn) - dựa trên inventory movements như Dashboard
        KpiData kpi = inventoryMovementRepository.sumOwnerRevenue(branchId, from, to);

        // Doanh thu theo danh mục thuốc - dựa trên inventory movements
        List<TopProductItem> topCategories = inventoryMovementRepository.findOwnerTopCategories(branchId, from, to, 20);
        List<Map<String, Object>> categories = new ArrayList<>();
        for (TopProductItem item : topCategories) {
            Map<String, Object> cat = new HashMap<>();
            cat.put("categoryName", item.getName());
            cat.put("revenue", item.getValue());
            categories.add(cat);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("totalRevenue", kpi.getRevenue());
        response.put("totalProfit", kpi.getProfit());
        response.put("totalOrders", kpi.getOrderCount());
        response.put("categories", categories);

        return ResponseEntity.ok(response);
    }

    /**
     * View detail profit – Report
     * GET /api/owner/report/profit?period=2024-01&branchId=1
     * Only supports monthly profit by branch
     */
    @GetMapping("/profit")
    public ResponseEntity<Map<String, Object>> getDetailProfit(
            @RequestParam(required = false) String period,
            @RequestParam(required = false) Long branchId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        // Verify owner role
        if (userDetails == null || !"OWNER".equalsIgnoreCase(userDetails.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Only calculate monthly profit
        LocalDate firstOfMonth = parseMonthStart(period);
        LocalDateTime from = firstOfMonth.atStartOfDay();
        LocalDateTime to = firstOfMonth.plusMonths(1).atStartOfDay();

        // Sử dụng inventory movements như Dashboard
        KpiData kpi = inventoryMovementRepository.sumOwnerRevenue(branchId, from, to);

        Map<String, Object> response = new HashMap<>();
        response.put("totalProfit", kpi.getProfit());
        response.put("totalRevenue", kpi.getRevenue());
        response.put("totalOrders", kpi.getOrderCount());

        return ResponseEntity.ok(response);
    }

    private LocalDate parseMonthStart(String ym) {
        try {
            if (ym == null || ym.isBlank()) return LocalDate.now().withDayOfMonth(1);
            // Support both yyyy-MM and yyyy-MM-dd formats
            if (ym.length() == 7) {
                // yyyy-MM format
                return LocalDate.parse(ym + "-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } else {
                // yyyy-MM-dd format, extract month
                LocalDate date = LocalDate.parse(ym, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                return date.withDayOfMonth(1);
            }
        } catch (Exception e) {
            return LocalDate.now().withDayOfMonth(1);
        }
    }
}

