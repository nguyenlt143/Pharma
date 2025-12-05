package vn.edu.fpt.pharma.dto.invoice;

public record MedicineItemVM(
        String medicineName,
        String strength,        // Changed from unit
        Double unitPrice,       // Changed from costPrice
        Long quantity
){
}
