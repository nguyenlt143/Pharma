package vn.edu.fpt.pharma.dto.medicine;

public record SearchMedicineVM(
        Long id,
        String name,
        String activeIngredient,
        String manufacturer,
        String strength,
        String country,
        Long quantity,
        String uses,
        String contraindications,
        String sideEffect
) {
}
