package vn.edu.fpt.pharma.dto.warehouse;
import vn.edu.fpt.pharma.entity.RequestDetail;


public record RequestDetailVM(
        Long id,
        Long variantId,
        String medicineName,
        String strength,
        String dosageForm,
        String unit,
        Long quantity
) {
    public RequestDetailVM(RequestDetail entity, String medicineName, String strength, String dosageForm, String unit) {
        this(
                entity.getId(),
                entity.getVariantId(),
                medicineName,
                strength,
                dosageForm,
                unit,
                entity.getQuantity()
        );
    }

}
