package vn.edu.fpt.pharma.dto.invoice;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface InvoiceInfoVM {
    String getBranchName();
    String getBranchAddress();
    String getCustomerName();
    String getCustomerPhone();
    LocalDateTime getCreatedAt();  // hoặc Date tuỳ kiểu dữ liệu DB
    BigDecimal getTotalPrice();
    String getDescription();
}

