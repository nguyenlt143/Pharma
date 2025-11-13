package vn.edu.fpt.pharma.dto.warehouse;


import vn.edu.fpt.pharma.entity.InventoryMovementDetail;
import java.time.format.DateTimeFormatter;

public record InventoryMovementDetailVM(
        Long id,
        Long variantId,
        Long quantity,
        String price,
        String mfgDate,
        String expiryDate,
        String batchCode
) {
    public InventoryMovementDetailVM(InventoryMovementDetail entity) {
        this(
                entity.getId(),
                entity.getVariantId() != null ?  entity.getVariantId() : null,
                entity.getQuantity(),
                entity.getPrice() != null ? String.format("%,.0f", entity.getPrice()) : "0",
                entity.getBatchId() != null && entity.getBatchId().getMfgDate() != null
                        ? entity.getBatchId().getMfgDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                        : "",
                entity.getBatchId() != null && entity.getBatchId().getExpiryDate() != null
                        ? entity.getBatchId().getExpiryDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                        : "",
                entity.getBatchId() != null ? entity.getBatchId().getBatchCode() : ""
        );
    }
}
