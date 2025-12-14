package vn.edu.fpt.pharma.dto.invoice;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record InvoiceDetailVM(
    String invoiceCode,
    String branchName,
    String branchAddress,
    String customerName,
    String customerPhone,
    LocalDateTime createdAt,    // Changed from invoiceDate
    BigDecimal totalPrice,     // Changed from totalAmount
    String paymentMethod,
    String description,        // Changed from note
    List<MedicineItemVM> medicines
){
}
