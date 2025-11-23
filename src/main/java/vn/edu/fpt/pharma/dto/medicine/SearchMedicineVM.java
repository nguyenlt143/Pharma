package vn.edu.fpt.pharma.dto.medicine;

public record SearchMedicineVM(
        Long id,
        String name,
        String activeIngredient,
        String manufacturer,
        String strength,
        String country,
        String packageUnitName,
        Long quantity,
        String baseUnitName,
        String uses,
        String contraindications,
        String sideEffect
) {
}
