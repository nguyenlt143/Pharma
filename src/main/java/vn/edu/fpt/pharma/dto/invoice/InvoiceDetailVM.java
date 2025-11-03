package vn.edu.fpt.pharma.dto.invoice;

import java.time.LocalDateTime;
import java.util.List;

public record InvoiceDetailVM(
    String branchName,
    String branchAddress,
    String customerName,
    String customerPhone,
    LocalDateTime invoiceDate,
    Double totalAAmount,
    String note,
    List<MedicineItemVM> medicines
){
}
