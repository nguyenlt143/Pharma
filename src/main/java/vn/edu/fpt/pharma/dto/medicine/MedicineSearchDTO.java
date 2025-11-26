package vn.edu.fpt.pharma.dto.medicine;

public record MedicineSearchDTO(
        Long id,
        String name,
        String activeIngredient,
        String brandName,
        String manufacturer,
        String country
) {
}


