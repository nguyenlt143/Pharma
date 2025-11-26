package vn.edu.fpt.pharma.dto.invoice;

import java.util.List;

public record InvoiceCustomerVM(
        String customerName,
        String phone,
        String note,
        String paymentMethod,
        List<InvoiceDetailDTO>list
) {
}
