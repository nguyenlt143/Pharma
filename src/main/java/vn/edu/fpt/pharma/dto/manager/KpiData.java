package vn.edu.fpt.pharma.dto.manager;

import lombok.Data;

@Data
public class KpiData {
    private double revenue;
    private long orderCount;
    private double profit;
    public KpiData(double revenue, long orderCount,double profit) {
        this.revenue = revenue;
        this.orderCount = orderCount;
        this.profit = profit;
    }
}


