package vn.edu.fpt.pharma.dto.invoice;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record InvoiceDetailVM(
    String branchName,
    String branchAddress,
    String customerName,
    String customerPhone,
    LocalDateTime invoiceDate,
    BigDecimal totalAmount,
    String note,
    List<MedicineItemVM> medicines
){
}
