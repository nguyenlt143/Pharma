package vn.edu.fpt.pharma.dto.inventorycheck;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.fpt.pharma.entity.StockAdjustment;

import java.time.format.DateTimeFormatter;

/**
 * ViewModel cho chi tiết điều chỉnh kho
 * Hiển thị: Tên thuốc (medicine.name + medicineVariant.strength)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockAdjustmentDetailVM {
    private Long id;
    private String medicineName;
    private String batchCode;
    private String expiryDate;
    private Long beforeQuantity;
    private Long afterQuantity;
    private Long differenceQuantity;
    private String reason;
    private String adjustmentTime;


    public StockAdjustmentDetailVM(StockAdjustment entity) {
        this.id = entity.getId();

        // TODO: Cần join với MedicineVariant và Medicine để lấy tên đầy đủ
        // Format: medicine.name + " " + medicineVariant.strength
        // VD: "Paracetamol 500mg"
        this.medicineName = "Thuốc #" + (entity.getVariantId() != null ? entity.getVariantId() : "N/A");

        this.batchCode = entity.getBatch() != null
                ? entity.getBatch().getBatchCode()
                : "N/A";

        this.expiryDate = entity.getBatch() != null && entity.getBatch().getExpiryDate() != null
                ? entity.getBatch().getExpiryDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                : "N/A";

        this.beforeQuantity = entity.getBeforeQuantity();
        this.afterQuantity = entity.getAfterQuantity();
        this.differenceQuantity = entity.getDifferenceQuantity();
        this.reason = entity.getReason() != null ? entity.getReason() : "";

        this.adjustmentTime = entity.getCreatedAt() != null
                ? entity.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                : "";
    }
}