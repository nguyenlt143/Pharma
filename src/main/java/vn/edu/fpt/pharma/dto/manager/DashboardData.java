package vn.edu.fpt.pharma.dto.manager;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class DashboardData {
    private int days;
    private KpiData kpis;
    private List<DailyRevenue> dailyRevenues;
    private List<TopProductItem> topSellingProducts;
    private List<Map<String, Object>> productStats;
    // Getters & Setters
}

