package vn.edu.fpt.pharma.dto.invoice;

import lombok.Data;

import java.util.List;

@Data
public class InvoiceCreateRequest {
    private String customerName;
    private String phoneNumber;
    private Double totalAmount;
    private String paymentMethod;
    private String note;
    private List<InvoiceItemRequest> items;
}
