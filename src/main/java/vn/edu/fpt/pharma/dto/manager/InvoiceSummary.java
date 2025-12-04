package vn.edu.fpt.pharma.dto.manager;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
public class InvoiceSummary {
    private Long id;
    private String code;
    private String fullName;
    private String shiftName;
    private LocalDateTime createdAt;
    private Double totalAmount;
    private Double profit;
    private String paymentMethod;

    public InvoiceSummary(Long id, String code, String fullName, String shiftName,
                          LocalDateTime createdAt, Double totalAmount, Double profit, String paymentMethod) {
        this.id = id;
        this.code = code;
        this.fullName = fullName;
        this.shiftName = shiftName;
        this.createdAt = createdAt;
        this.totalAmount = totalAmount;
        this.profit = profit;
        this.paymentMethod = paymentMethod;
    }

    // Getters & setters
}
