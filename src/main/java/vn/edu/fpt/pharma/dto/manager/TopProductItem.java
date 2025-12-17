package vn.edu.fpt.pharma.dto.manager;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class TopProductItem {
    private String name;
    private long value; // Doanh thu hoặc số lượng
    private double percentage; // Tỷ lệ % so với item top 1 (dùng để vẽ thanh progress bar)
    // Getters & Setters


    public TopProductItem(String name, long value) {
        this.name = name;
        this.value = value;
        this.percentage = 0.0;
    }

    public TopProductItem(String name, Double value) {
        this.name = name;
        this.value = value != null ? value.longValue() : 0L;
        this.percentage = 0.0;
    }

    public TopProductItem(String name, BigDecimal value) {
        this.name = name;
        this.value = value != null ? value.longValue() : 0L;
        this.percentage = 0.0;
    }

    public TopProductItem(String name, Number value) {
        this.name = name;
        this.value = value != null ? value.longValue() : 0L;
        this.percentage = 0.0;
    }
}

