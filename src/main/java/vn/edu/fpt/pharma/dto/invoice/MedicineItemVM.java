package vn.edu.fpt.pharma.dto.invoice;

public record MedicineItemVM(
        String medicineName,
        String unit,
        Double costPrice,
        Long quantity
){
}
