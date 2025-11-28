package vn.edu.fpt.pharma.dto.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.fpt.pharma.entity.InventoryMovement;

import java.time.format.DateTimeFormatter;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmImportVM {
    private Long id;
    private String code;
    private String createdAt;
    private String status;
    private Long medicineCount;
    private Double totalMoney;

    public static ConfirmImportVM from(InventoryMovement movement, Long medicineCount) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        return ConfirmImportVM.builder()
                .id(movement.getId())
                .code("#MV" + String.format("%03d", movement.getId()))
                .createdAt(movement.getCreatedAt() != null ? movement.getCreatedAt().format(fmt) : "")
                .status(movement.getMovementStatus() != null ? movement.getMovementStatus().name() : "N/A")
                .medicineCount(medicineCount)
                .totalMoney(movement.getTotalMoney())
                .build();
    }
}

