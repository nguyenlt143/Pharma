package vn.edu.fpt.pharma.dto.warehouse;


import vn.edu.fpt.pharma.entity.InventoryMovement;
import java.time.format.DateTimeFormatter;

public record InventoryMovementVM(
        Long id,
        String movementType,
        String supplierName,
        String sourceBranch,
        String destinationBranch,
        Long requestFormCode,
        String movementStatus,
        Double totalMoney,
        String createdAt
) {
    public InventoryMovementVM(InventoryMovement entity) {
        this(
                entity.getId(),
                entity.getMovementType() != null ? entity.getMovementType().name() : "N/A",
                entity.getSupplier().getName() != null ? entity.getSupplier().getName() : "N/A",
                entity.getSourceBranchId() != null ? "CN#" + entity.getSourceBranchId() : "N/A",
                entity.getDestinationBranchId() != null ? "CN#" + entity.getDestinationBranchId() : "N/A",
                entity.getRequestForm() != null ? entity.getRequestForm().getId() :null,
                entity.getMovementStatus() != null ? entity.getMovementStatus().name() : "N/A",
                entity.getTotalMoney() != null ? entity.getTotalMoney() : 0,
                entity.getCreatedAt() != null
                        ? entity.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                        : ""
        );
    }
}

