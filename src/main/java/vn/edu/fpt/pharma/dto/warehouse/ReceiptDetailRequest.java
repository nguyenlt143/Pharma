package vn.edu.fpt.pharma.dto.warehouse;

import lombok.Data;

@Data
public class ReceiptDetailRequest {
    private Long variantId;
    private String batchCode;
    private String manufactureDate;
    private String expiryDate;
    private Long quantity;
    private Double price;
    private Double snapCost;
}

