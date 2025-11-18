package vn.edu.fpt.pharma.dto.warehouse;
import vn.edu.fpt.pharma.entity.RequestDetail;


public record RequestDetailVM(
        Long id,
//        Long requestFormId,
        Long variantId,
        Long quantity
) {
    public RequestDetailVM(RequestDetail entity) {
        this(
                entity.getId(),
                entity.getVariant_id(),
                entity.getQuantity()
        );
    }

}
