package vn.edu.fpt.pharma.dto.invoice;

import lombok.Data;

import java.util.List;

@Data
public class InvoiceCreateRequest {
    private String customerName;
    private String phoneNumber;
    private Double totalPrice;
    private String paymentMethod;
    private String note;
    private List<InvoiceItemRequest> item;
}
