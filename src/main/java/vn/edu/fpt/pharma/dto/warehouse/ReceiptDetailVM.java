package vn.edu.fpt.pharma.dto.warehouse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptDetailVM {
    private String medicineName;
    private String concentration;
    private String unit;
    private Integer quantity;
}

