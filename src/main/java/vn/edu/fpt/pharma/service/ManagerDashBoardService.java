package vn.edu.fpt.pharma.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import vn.edu.fpt.pharma.dto.manager.DailyRevenue;
import vn.edu.fpt.pharma.dto.manager.DashboardData;
import vn.edu.fpt.pharma.dto.manager.KpiData;
import vn.edu.fpt.pharma.dto.manager.TopProductItem;
import vn.edu.fpt.pharma.repository.InvoiceRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ManagerDashBoardService {

    final private InvoiceRepository invoiceRepository;

    public ManagerDashBoardService(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    public DashboardData getDashboardDataByPeriod(int days, Long branchId) {
        DashboardData data = new DashboardData();
        data.setDays(days);

        LocalDate today = LocalDate.now();
        // Ensure we take exactly 'days' days including today. If days=0 -> only today.
        LocalDate from = days > 0 ? today.minusDays(days - 1) : today;
        LocalDateTime fromDate = from.atStartOfDay();
        LocalDateTime toDate = today.plusDays(1).atStartOfDay();

        data.setKpis(getKpi(branchId, fromDate, toDate));
        data.setDailyRevenues(getDailyRevenue(branchId, from, today));
        data.setProductStats(getProductStats(branchId, fromDate, toDate));
        return data;
    }

    private KpiData getKpi(Long branchId, LocalDateTime fromDate, LocalDateTime toDate) {
        return invoiceRepository.sumRevenue(branchId, fromDate, toDate);
    }

    private List<DailyRevenue> getDailyRevenue(Long branchId, LocalDate fromDate, LocalDate toDate) {
        // Query only existing revenue days
        List<DailyRevenue> queried = invoiceRepository.getDailyRevenueByDate(
                branchId,
                fromDate.atStartOfDay(),
                toDate.plusDays(1).atStartOfDay()
        );

        // Map existing revenues by date for fast lookup
        Map<LocalDate, Double> revenueMap = new HashMap<>();
        for (DailyRevenue dr : queried) {
            revenueMap.put(dr.getDate(), dr.getRevenue() == null ? 0.0 : dr.getRevenue());
        }

        // Build continuous list including zero-value days
        List<DailyRevenue> filled = new ArrayList<>();
        LocalDate cursor = fromDate;
        while (!cursor.isAfter(toDate)) {
            Double rev = revenueMap.getOrDefault(cursor, 0.0);
            filled.add(new SimpleDailyRevenue(cursor, rev));
            cursor = cursor.plusDays(1);
        }
        return filled;
    }

    private List<Map<String, Object>> getProductStats(Long branchId, LocalDateTime fromDate, LocalDateTime toDate) {
        List<TopProductItem> tops = invoiceRepository.topCategories(
                branchId,
                fromDate,
                toDate,
                null, // shiftId
                null, // employeeId
                PageRequest.of(0, 6)
        );

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

        return productStats;
    }

    // Simple implementation of DailyRevenue projection for filled days
    private static class SimpleDailyRevenue implements DailyRevenue {
        private final LocalDate date;
        private final Double revenue;

        public SimpleDailyRevenue(LocalDate date, Double revenue) {
            this.date = date;
            this.revenue = revenue;
        }

        @Override
        public LocalDate getDate() {
            return date;
        }

        @Override
        public Double getRevenue() {
            return revenue;
        }
    }
}
