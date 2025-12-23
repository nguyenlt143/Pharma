package vn.edu.fpt.pharma.dto.warehouse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.fpt.pharma.entity.MedicineVariant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedicineVariantDTO {
    private Long id;
    private String medicineName;
    private String unit;
    private String concentration;
    private String barcode;

    public MedicineVariantDTO(MedicineVariant variant) {
        this.id = variant.getId();
        this.medicineName = variant.getMedicine() != null ? variant.getMedicine().getName() : "";
        this.unit = ""; // Will be populated by service layer from UnitConversion
        this.concentration = variant.getStrength() != null ? variant.getStrength() : "";
        this.barcode = variant.getBarcode();
    }
}

