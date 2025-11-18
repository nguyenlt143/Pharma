package vn.edu.fpt.pharma.controller.owner;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.pharma.config.CustomUserDetails;
import vn.edu.fpt.pharma.dto.dashboard.DailyRevenueItem;
import vn.edu.fpt.pharma.dto.dashboard.DashboardRevenueResponse;
import vn.edu.fpt.pharma.repository.InvoiceRepository;
import vn.edu.fpt.pharma.dto.manager.KpiData;
import vn.edu.fpt.pharma.dto.manager.TopProductItem;
import vn.edu.fpt.pharma.dto.manager.DailyRevenue;

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

    private final InvoiceRepository invoiceRepository;

    /**
     * View revenue – Dashboard
     * GET /api/owner/dashboard/revenue?mode=day&period=2024-01-15
     */
    @GetMapping("/revenue")
    public ResponseEntity<DashboardRevenueResponse> getRevenue(
            @RequestParam(required = false) String mode,
            @RequestParam(required = false) String period,
            @RequestParam(required = false) Long shift,
            @RequestParam(required = false) Long employeeId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        // Verify owner role
        if (userDetails == null || !"OWNER".equalsIgnoreCase(userDetails.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        // Owner can view all branches, so branchId is null
        Long branchId = null;

        LocalDateTime fromDate;
        LocalDateTime toDate;
        
        if (mode == null || mode.isBlank() || "day".equalsIgnoreCase(mode)) {
            LocalDate localDate = parseDateOrToday(period);
            fromDate = localDate.atStartOfDay();
            toDate = localDate.plusDays(1).atStartOfDay();
        } else if ("week".equalsIgnoreCase(mode)) {
            LocalDate startOfWeek = parseWeekStart(period);
            fromDate = startOfWeek.atStartOfDay();
            toDate = startOfWeek.plusWeeks(1).atStartOfDay();
        } else if ("month".equalsIgnoreCase(mode)) {
            LocalDate firstOfMonth = parseMonthStart(period);
            fromDate = firstOfMonth.atStartOfDay();
            toDate = firstOfMonth.plusMonths(1).atStartOfDay();
        } else {
            LocalDate localDate = parseDateOrToday(period);
            fromDate = localDate.atStartOfDay();
            toDate = localDate.plusDays(1).atStartOfDay();
        }

        KpiData kpi = invoiceRepository.sumRevenue(branchId, fromDate, toDate, shift, employeeId);
        List<TopProductItem> tops = invoiceRepository.topCategories(branchId, fromDate, toDate, shift, employeeId, 
                org.springframework.data.domain.PageRequest.of(0, 6));
        
        // Get daily revenue data for chart
        List<DailyRevenue> dailyRevenues = invoiceRepository.getDailyRevenueByDate(branchId, fromDate, toDate, shift, employeeId);
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
     * GET /api/owner/dashboard/profit?mode=day&period=2024-01-15
     */
    @GetMapping("/profit")
    public ResponseEntity<Map<String, Object>> getProfit(
            @RequestParam(required = false) String mode,
            @RequestParam(required = false) String period,
            @RequestParam(required = false) Long shift,
            @RequestParam(required = false) Long employeeId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        // Verify owner role
        if (userDetails == null || !"OWNER".equalsIgnoreCase(userDetails.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        // Owner can view all branches, so branchId is null
        Long branchId = null;

        LocalDateTime fromDate;
        LocalDateTime toDate;
        
        if (mode == null || mode.isBlank() || "day".equalsIgnoreCase(mode)) {
            LocalDate localDate = parseDateOrToday(period);
            fromDate = localDate.atStartOfDay();
            toDate = localDate.plusDays(1).atStartOfDay();
        } else if ("week".equalsIgnoreCase(mode)) {
            LocalDate startOfWeek = parseWeekStart(period);
            fromDate = startOfWeek.atStartOfDay();
            toDate = startOfWeek.plusWeeks(1).atStartOfDay();
        } else if ("month".equalsIgnoreCase(mode)) {
            LocalDate firstOfMonth = parseMonthStart(period);
            fromDate = firstOfMonth.atStartOfDay();
            toDate = firstOfMonth.plusMonths(1).atStartOfDay();
        } else {
            LocalDate localDate = parseDateOrToday(period);
            fromDate = localDate.atStartOfDay();
            toDate = localDate.plusDays(1).atStartOfDay();
        }

        KpiData kpi = invoiceRepository.sumRevenue(branchId, fromDate, toDate, shift, employeeId);

        Map<String, Object> response = new HashMap<>();
        response.put("totalProfit", kpi.getProfit());
        response.put("totalRevenue", kpi.getRevenue());
        response.put("totalOrders", kpi.getOrderCount());

        return ResponseEntity.ok(response);
    }

    private LocalDate parseDateOrToday(String dateStr) {
        try {
            if (dateStr == null || dateStr.isBlank()) return LocalDate.now();
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return LocalDate.parse(dateStr, fmt);
        } catch (Exception e) {
            return LocalDate.now();
        }
    }

    private LocalDate parseMonthStart(String ym) {
        try {
            if (ym == null || ym.isBlank()) return LocalDate.now().withDayOfMonth(1);
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM");
            return LocalDate.parse(ym + "-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (Exception e) {
            return LocalDate.now().withDayOfMonth(1);
        }
    }

    private LocalDate parseWeekStart(String yw) {
        try {
            if (yw == null || yw.isBlank()) {
                return LocalDate.now().with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
            }
            String[] parts = yw.split("-W");
            int year = Integer.parseInt(parts[0]);
            int week = Integer.parseInt(parts[1]);
            LocalDate base = LocalDate.of(year, 1, 4);
            LocalDate weekDate = base
                    .with(java.time.temporal.WeekFields.ISO.weekOfWeekBasedYear(), week)
                    .with(java.time.temporal.WeekFields.ISO.weekBasedYear(), year);
            return weekDate.with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        } catch (Exception e) {
            return LocalDate.now().with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        }
    }
}

