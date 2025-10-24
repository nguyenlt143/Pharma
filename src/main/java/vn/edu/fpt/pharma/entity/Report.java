package vn.edu.fpt.pharma.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import vn.edu.fpt.pharma.base.BaseEntity;
import vn.edu.fpt.pharma.constant.ReportType;
import vn.edu.fpt.pharma.constant.WorkType;

import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "UPDATE reports SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class Report extends BaseEntity<Long> {
    private Long branchId;
    @Enumerated(EnumType.STRING)
    private ReportType reportType;
    private LocalDateTime reportDate;
    private Double totalRevenue;
    private Double totalProfit;
    private Double totalSales;
}
