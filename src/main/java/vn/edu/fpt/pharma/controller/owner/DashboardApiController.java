package vn.edu.fpt.pharma.controller.owner;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.pharma.config.CustomUserDetails;
import vn.edu.fpt.pharma.dto.dashboard.DailyRevenueItem;
import vn.edu.fpt.pharma.dto.dashboard.DashboardRevenueResponse;
import vn.edu.fpt.pharma.dto.manager.DailyRevenue;
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
@RequestMapping("/api/owner/dashboard")
@RequiredArgsConstructor
public class DashboardApiController {

    // Owner dashboard: use inventory movements (WARE_TO_BR) instead of invoices
    private final InventoryMovementRepository inventoryMovementRepository;

    /**
     * View revenue – Dashboard
     * GET /api/owner/dashboard/revenue?period=2024-01&branchId=1
     * Only supports monthly revenue by branch
     */
    @GetMapping("/revenue")
    public ResponseEntity<DashboardRevenueResponse> getRevenue(
            @RequestParam(required = false) String period,
            @RequestParam(required = false) Long branchId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        // Verify owner role
        if (userDetails == null || !"OWNER".equalsIgnoreCase(userDetails.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Only calculate monthly revenue
        LocalDate firstOfMonth = parseMonthStart(period);
        LocalDateTime fromDate = firstOfMonth.atStartOfDay();
        LocalDateTime toDate = firstOfMonth.plusMonths(1).atStartOfDay();

        // Doanh thu & lợi nhuận dựa trên phiếu cấp cho chi nhánh (inventory_movements)
        KpiData kpi = inventoryMovementRepository.sumOwnerRevenue(branchId, fromDate, toDate);
        List<TopProductItem> tops = inventoryMovementRepository.findOwnerTopCategories(branchId, fromDate, toDate, 6);
        
        // Get daily revenue data for chart (monthly breakdown)
        List<DailyRevenue> dailyRevenues = inventoryMovementRepository.getOwnerDailyRevenue(branchId, fromDate, toDate);
        List<DailyRevenueItem> dailyRevenueItems = new ArrayList<>();
        for (DailyRevenue dr : dailyRevenues) {
            DailyRevenueItem item = DailyRevenueItem.builder()
                    .date(dr.getDate().toString())
                    .revenue(dr.getRevenue() != null ? dr.getRevenue() : 0.0)
                    .build();
            dailyRevenueItems.add(item);
        }
        
        long totalProducts = tops.stream().mapToLong(TopProductItem::getValue).sum();
        List<Map<String, Object>> productStats = new ArrayList<>();
        String[] colors = new String[]{"#4CAF50", "#2196F3", "#FF9800", "#9C27B0", "#E91E63", "#009688"};
        
        for (int i = 0; i < tops.size(); i++) {
            TopProductItem t = tops.get(i);
            double percent = totalProducts > 0 ? (t.getValue() * 100.0 / totalProducts) : 0.0;
            Map<String, Object> item = new HashMap<>();
            item.put("name", t.getName());
            item.put("percent", Math.round(percent));
            item.put("color", colors[i % colors.length]);
            productStats.add(item);
        }

        DashboardRevenueResponse response = DashboardRevenueResponse.builder()
                .totalRevenue(kpi.getRevenue())
                .totalProfit(kpi.getProfit())
                .totalOrders(kpi.getOrderCount())
                .dailyRevenues(dailyRevenueItems)
                .productStats(productStats)
                .totalProducts(totalProducts)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * View profit – Dashboard
     * GET /api/owner/dashboard/profit?period=2024-01&branchId=1
     * Only supports monthly profit by branch
     */
    @GetMapping("/profit")
    public ResponseEntity<Map<String, Object>> getProfit(
            @RequestParam(required = false) String period,
            @RequestParam(required = false) Long branchId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        // Verify owner role
        if (userDetails == null || !"OWNER".equalsIgnoreCase(userDetails.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Only calculate monthly profit
        LocalDate firstOfMonth = parseMonthStart(period);
        LocalDateTime fromDate = firstOfMonth.atStartOfDay();
        LocalDateTime toDate = firstOfMonth.plusMonths(1).atStartOfDay();

        KpiData kpi = inventoryMovementRepository.sumOwnerRevenue(branchId, fromDate, toDate);

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

