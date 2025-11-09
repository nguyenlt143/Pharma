package vn.edu.fpt.pharma.dto.manager;

import lombok.Data;

@Data
public class KpiData {
    private Double revenue = 0.0;
    private Long orderCount = 0L;
    private Double profit = 0.0;

    public KpiData(Double revenue, Long orderCount, Double profit) {
        this.revenue = revenue != null ? revenue : 0.0;
        this.orderCount = orderCount != null ? orderCount : 0L;
        this.profit = profit != null ? profit : 0.0;
    }

    public KpiData() {}
}



