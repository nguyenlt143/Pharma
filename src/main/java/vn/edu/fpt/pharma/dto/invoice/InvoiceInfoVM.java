package vn.edu.fpt.pharma.dto.invoice;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface InvoiceInfoVM {
    String getInvoiceCode();
    String getBranchName();
    String getBranchAddress();
    String getCustomerName();
    String getCustomerPhone();
    LocalDateTime getCreatedAt();
    BigDecimal getTotalPrice();
    String getPaymentMethod();
    String getDescription();
}

