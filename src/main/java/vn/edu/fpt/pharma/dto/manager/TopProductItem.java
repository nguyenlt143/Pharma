package vn.edu.fpt.pharma.dto.manager;

import lombok.Data;

@Data
public class TopProductItem {
    private String name;
    private long value; // Doanh thu hoặc số lượng
    private double percentage; // Tỷ lệ % so với item top 1 (dùng để vẽ thanh progress bar)
    // Getters & Setters
}

