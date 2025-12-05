package vn.edu.fpt.pharma.dto.invoice;

public record MedicineItemVM(
        String medicineName,
        String strength,        // Changed from unit
        String unitName,        // Unit name (e.g., Viên, Hộp, Chai)
        Double unitPrice,       // Changed from costPrice
        Long quantity
){
}
