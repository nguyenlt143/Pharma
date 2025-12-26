package vn.edu.fpt.pharma.dto.warehouse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.fpt.pharma.entity.MedicineVariant;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedicineVariantDTO {
    private Long id;
    private String medicineName;
    private String unit;
    private String concentration;
    private String barcode;
    private String packageUnit;  // Đơn vị nhập (đơn vị lớn)
    private String baseUnit;      // Đơn vị cơ bản (đơn vị nhỏ)
    private Double quantityPerPackage; // Tỉ lệ quy đổi
    private List<UnitConversionInfo> unitConversions; // Danh sách tất cả các đơn vị quy đổi

    public MedicineVariantDTO(MedicineVariant variant) {
        this.id = variant.getId();
        this.medicineName = variant.getMedicine() != null ? variant.getMedicine().getName() : "";
        this.unit = ""; // Will be populated by service layer from UnitConversion
        this.concentration = variant.getStrength() != null ? variant.getStrength() : "";
        this.barcode = variant.getBarcode();
        this.packageUnit = "";
        this.baseUnit = "";
        this.quantityPerPackage = null;
        this.unitConversions = new ArrayList<>();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UnitConversionInfo {
        private Long unitId;
        private String unitName;
        private Double multiplier;
        private String note;
    }
}

