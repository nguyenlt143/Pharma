package vn.edu.fpt.pharma.dto.invoice;

import vn.edu.fpt.pharma.entity.Invoice;

import java.time.LocalDateTime;

public record InvoiceVM(
        Long id,
        String invoiceCode,
        String customerName,
        String customerPhone,
        Double totalPrice,
        String description,
        String paymentMethod,
        LocalDateTime createdAt
) {
    public InvoiceVM(Invoice invoice) {
        this(
                invoice.getId(),
                invoice.getInvoiceCode(),
                invoice.getCustomer() != null ? invoice.getCustomer().getName() : null,
                invoice.getCustomer() != null ? invoice.getCustomer().getPhone() : null,
                invoice.getTotalPrice(),
                invoice.getDescription(),
                invoice.getPaymentMethod(),
                invoice.getCreatedAt()
        );
    }
}
