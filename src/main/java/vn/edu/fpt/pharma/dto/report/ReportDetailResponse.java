package vn.edu.fpt.pharma.dto.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportDetailResponse {
    private Long reportId;
    private Long branchId;
    private String branchName;
    private String reportType;
    private LocalDate reportDate;
    private Double totalRevenue;
    private Double totalProfit;
    private Double totalSales;
    private List<ReportItem> items;
    private LocalDate createdAt;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class ReportItem {
    private String itemName;
    private Double amount;
    private Long quantity;
}

