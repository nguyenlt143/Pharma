package vn.edu.fpt.pharma.dto.medicine;

public record SearchMedicineVM(
        Long id,
        String name,
        String activeIngredient,
        String manufacturer,
        String strength,
        String country,
        String packaging,  // Changed from Long quantity to String packaging
        String uses,
        String contraindications,
        String sideEffect
) {
}
