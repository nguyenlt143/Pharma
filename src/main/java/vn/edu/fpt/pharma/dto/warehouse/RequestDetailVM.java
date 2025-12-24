package vn.edu.fpt.pharma.dto.warehouse;
import vn.edu.fpt.pharma.entity.RequestDetail;


public record RequestDetailVM(
        Long id,
        Long variantId,
        String medicineName,
        String activeIngredient,
        String strength,
        String dosageForm,
        String unit,
        Long quantity,
        String categoryName,
        Long batchCount,
        String packageUnitName,
        String baseUnitName,
        Double quantityPerPackage
) {
    public RequestDetailVM(RequestDetail entity, String medicineName, String activeIngredient, String strength, String dosageForm, String unit,
                           String categoryName, Long batchCount) {
        this(
                entity.getId(),
                entity.getVariantId(),
                medicineName,
                activeIngredient,
                strength,
                dosageForm,
                unit,
                entity.getQuantity(),
                categoryName,
                batchCount,
                entity.getPackageUnitId() != null ? entity.getPackageUnitId().getName() : null,
                entity.getBaseUnitId() != null ? entity.getBaseUnitId().getName() : null,
                entity.getQuantityPerPackage()
        );
    }

}
