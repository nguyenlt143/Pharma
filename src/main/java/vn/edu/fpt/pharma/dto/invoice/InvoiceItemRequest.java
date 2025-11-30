package vn.edu.fpt.pharma.dto.invoice;

import lombok.Data;

@Data
public class InvoiceItemRequest {
    private Long inventoryId;
    private Long quantity;
    private Double unitPrice;
    private Double selectedMultiplier;
}
