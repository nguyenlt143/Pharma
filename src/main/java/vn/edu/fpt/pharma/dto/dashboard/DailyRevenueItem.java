package vn.edu.fpt.pharma.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyRevenueItem {
    private String date;
    private Double revenue;
    private Double profit;
    private Long orderCount;
}

