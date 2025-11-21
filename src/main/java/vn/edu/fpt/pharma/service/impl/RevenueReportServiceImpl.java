package vn.edu.fpt.pharma.service.impl;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import vn.edu.fpt.pharma.dto.manager.InvoiceListItem;
import vn.edu.fpt.pharma.dto.manager.InvoiceWithProfitListItem;
import vn.edu.fpt.pharma.dto.manager.KpiData;
import vn.edu.fpt.pharma.dto.manager.TopProductItem;
import vn.edu.fpt.pharma.repository.InvoiceRepository;
import vn.edu.fpt.pharma.service.RevenueReportService;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.*;

@Service
public class RevenueReportServiceImpl implements RevenueReportService {

    private final InvoiceRepository invoiceRepository;

    public RevenueReportServiceImpl(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    @Override
    public Map<String, Object> getRevenueReport(Long branchId,
                                                String date,
                                                String mode,
                                                String period,
                                                Long shift,
                                                Long employeeId) {
        // Determine date range
        LocalDateTime[] dateRange = determineDateRange(mode, period, date);
        LocalDateTime fromDate = dateRange[0];
        LocalDateTime toDate = dateRange[1];

        // KPI
        KpiData kpi = invoiceRepository.sumRevenue(branchId, fromDate, toDate, shift, employeeId);

        // Product stats
        Map<String, Object> productStatsData = getProductStatistics(branchId, fromDate, toDate, shift, employeeId);

        // Use projection that matches listing needs
        List<InvoiceWithProfitListItem> items = invoiceRepository.findInvoicesWithProfit(branchId, fromDate, toDate, shift, employeeId);
        List<Map<String, Object>> invoices = new ArrayList<>();
        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        for (InvoiceWithProfitListItem it : items) {
            Map<String, Object> row = new HashMap<>();
            row.put("time", it.getCreatedAt() != null ? it.getCreatedAt().format(timeFmt) : "");
            row.put("code", it.getInvoiceCode());
            row.put("customer", it.getCustomerName());
            row.put("paymentLabel", it.getPaymentMethod());
            row.put("amount", it.getTotalPrice());
            row.put("profit", it.getProfit());
            invoices.add(row);
        }

        Map<String, Object> body = new HashMap<>();
        body.put("totalInvoices", kpi.getOrderCount());
        body.put("totalRevenue", kpi.getRevenue());
        body.put("totalProfit", kpi.getProfit());
        body.put("invoices", invoices);
        body.put("productStats", productStatsData.get("productStats"));
        body.put("totalProducts", productStatsData.get("totalProducts"));
        return body;
    }

    // Helpers migrated from controller
    private LocalDateTime[] determineDateRange(String mode, String period, String date) {
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

        return new LocalDateTime[]{fromDate, toDate};
    }

    private Map<String, Object> getProductStatistics(Long branchId, LocalDateTime fromDate, LocalDateTime toDate, Long shift, Long employeeId) {
        // Use quantity-based top categories like dashboard
        List<TopProductItem> tops = invoiceRepository.topCategories(branchId, fromDate, toDate, shift, employeeId, PageRequest.of(0, 6));
        long totalCount = tops.stream().mapToLong(TopProductItem::getValue).sum();

        List<Map<String, Object>> productStats = new ArrayList<>();
        String[] colors = new String[]{"#4CAF50", "#2196F3", "#FF9800", "#9C27B0", "#E91E63", "#009688"};

        for (int i = 0; i < tops.size(); i++) {
            TopProductItem t = tops.get(i);
            double percent = totalCount > 0 ? (t.getValue() * 100.0 / totalCount) : 0.0;
            Map<String, Object> item = new HashMap<>();
            item.put("name", t.getName());
            item.put("percent", Math.round(percent));
            item.put("color", colors[i % colors.length]);
            productStats.add(item);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("productStats", productStats);
        result.put("totalProducts", totalCount);
        return result;
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
            return LocalDate.parse(ym + "-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (Exception e) {
            return LocalDate.now().withDayOfMonth(1);
        }
    }

    private LocalDate parseWeekStart(String yw) {
        try {
            if (yw == null || yw.isBlank()) {
                return LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            }
            String[] parts = yw.split("-W");
            int year = Integer.parseInt(parts[0]);
            int week = Integer.parseInt(parts[1]);
            LocalDate base = LocalDate.of(year, 1, 4);
            LocalDate weekDate = base
                    .with(WeekFields.ISO.weekOfWeekBasedYear(), week)
                    .with(WeekFields.ISO.weekBasedYear(), year);
            return weekDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        } catch (Exception e) {
            return LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        }
    }
}
