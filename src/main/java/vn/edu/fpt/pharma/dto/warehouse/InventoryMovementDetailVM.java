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
                entity.getVariant() != null ? entity.getVariant().getId() : null,
                entity.getQuantity(),
                entity.getPrice() != null ? String.format("%,.0f", entity.getPrice()) : "0",
                entity.getBatch() != null && entity.getBatch().getMfgDate() != null
                        ? entity.getBatch().getMfgDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                        : "",
                entity.getBatch() != null && entity.getBatch().getExpiryDate() != null
                        ? entity.getBatch().getExpiryDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                        : "",
                entity.getBatch() != null ? entity.getBatch().getBatchCode() : ""
        );
    }
}

