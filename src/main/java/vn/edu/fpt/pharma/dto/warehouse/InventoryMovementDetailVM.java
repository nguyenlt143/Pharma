package vn.edu.fpt.pharma.dto.warehouse;


import vn.edu.fpt.pharma.entity.InventoryMovementDetail;
import java.time.format.DateTimeFormatter;

public record InventoryMovementDetailVM(
        Long id,
        Long variantId,
        String medicineName,
        String unit,
        String concentration,
        Long quantity,
        Double price,
        String manufactureDate,
        String expiryDate,
        String batchCode
) {

    public InventoryMovementDetailVM(InventoryMovementDetail entity) {
        this(
                entity.getId(),
                entity.getVariant() != null ? entity.getVariant().getId() : null,
                entity.getVariant() != null && entity.getVariant().getMedicine() != null
                        ? entity.getVariant().getMedicine().getName() : "N/A",
                "", // Unit will be populated by service layer from UnitConversion
                entity.getVariant() != null && entity.getVariant().getStrength() != null
                        ? entity.getVariant().getStrength() : "",
                entity.getQuantity(),
                entity.getPrice(),
                entity.getBatch() != null && entity.getBatch().getMfgDate() != null
                        ? entity.getBatch().getMfgDate().toString()
                        : "",
                entity.getBatch() != null && entity.getBatch().getExpiryDate() != null
                        ? entity.getBatch().getExpiryDate().toString()
                        : "",
                entity.getBatch() != null ? entity.getBatch().getBatchCode() : ""
        );
    }
}

