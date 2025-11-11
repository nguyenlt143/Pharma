package vn.edu.fpt.pharma.dto.invoice;

public record MedicineItemVM(
        String medicineName,
        String unit,
        Long quantity,
        Double unitPrice,
        Double amount
){
}
