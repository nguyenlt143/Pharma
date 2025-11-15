package vn.edu.fpt.pharma.dto.manager;

import java.time.LocalDateTime;

public interface InvoiceListItem {
    LocalDateTime getCreatedAt();
    String getInvoiceCode();
    String getCustomerName();
    String getPaymentMethod();
    Double getTotalPrice();
    Double getProfit();
}