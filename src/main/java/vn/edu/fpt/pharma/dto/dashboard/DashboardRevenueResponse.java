package vn.edu.fpt.pharma.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardRevenueResponse {
    private Double totalRevenue;
    private Double totalProfit;
    private Long totalOrders;
    private List<DailyRevenueItem> dailyRevenues;
    private List<Map<String, Object>> productStats;
    private Long totalProducts;
}


