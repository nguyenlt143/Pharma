package vn.edu.fpt.pharma.controller.manager;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.fpt.pharma.config.CustomUserDetails;
import vn.edu.fpt.pharma.dto.manager.InvoiceSummary;
import vn.edu.fpt.pharma.dto.manager.KpiData;
import vn.edu.fpt.pharma.dto.manager.TopProductItem;
import vn.edu.fpt.pharma.repository.InvoiceRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.DayOfWeek;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/manager/report/revenue")
public class RevenueApiController {

    private final InvoiceRepository invoiceRepository;

    public RevenueApiController(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
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
        // Determine time range based on mode/period (day/week/month). Backward compatible with `date` (day).
        LocalDateTime fromDate;
        LocalDateTime toDate;
        if (mode == null || mode.isBlank() || "day".equalsIgnoreCase(mode)) {
            LocalDate localDate = parseDateOrToday(date != null ? date : period);
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
            LocalDate localDate = parseDateOrToday(date);
            fromDate = localDate.atStartOfDay();
            toDate = localDate.plusDays(1).atStartOfDay();
        }

        // KPI (filter by optional shift and employee)
        KpiData kpi = invoiceRepository.sumRevenue(branchId, fromDate, toDate, shift, employeeId);

        // Product stats (by category) - take top 6 for donut (with same filters)
        List<TopProductItem> tops = invoiceRepository.topCategories(branchId, fromDate, toDate, shift, employeeId, PageRequest.of(0, 6));
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
// Invoice summaries (for table/chart)
        List<InvoiceSummary> invoices = invoiceRepository.findInvoicesForReport(branchId, fromDate, toDate, shift, employeeId);

// Đưa vào response
        Map<String, Object> body = new HashMap<>();
        body.put("totalInvoices", kpi.getOrderCount());
        body.put("totalRevenue", kpi.getRevenue());
        body.put("totalProfit", kpi.getProfit());
        body.put("invoices", invoices);
        body.put("productStats", productStats);
        body.put("totalProducts", totalProducts);

        return ResponseEntity.ok(body);
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
                // default to current week (Monday)
                return LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            }
            String[] parts = yw.split("-W");
            int year = Integer.parseInt(parts[0]);
            int week = Integer.parseInt(parts[1]);
            LocalDate base = LocalDate.of(year, 1, 4); // ISO week 1 contains Jan 4th
            LocalDate weekDate = base
                    .with(WeekFields.ISO.weekOfWeekBasedYear(), week)
                    .with(WeekFields.ISO.weekBasedYear(), year);
            return weekDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        } catch (Exception e) {
            return LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        }
    }
}
