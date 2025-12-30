package vn.edu.fpt.pharma.dto.warehouse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.fpt.pharma.entity.MedicineVariant;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedicineVariantDTO {
    private Long id;

    // Medicine info
    private String medicineName;
    private String categoryName;
    private String activeIngredient;
    private String manufacturer;
    private String country;

    // DosageForm info
    private String dosageFormName;

    // Variant info
    private String registrationNumber;
    private String concentration;
    private String barcode;

    // Unit info
    private String baseUnit;
    private String importUnit;
    private Double conversionRatio;
    private String packagingSpec;

    // Unit conversions list
    private List<UnitConversionInfo> unitConversions;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UnitConversionInfo {
        private Long unitId;
        private String unitName;
        private Double multiplier;
        private Boolean isSale;
    }

    public MedicineVariantDTO(MedicineVariant variant) {
        this.id = variant.getId();

        // Medicine info
        if (variant.getMedicine() != null) {
            this.medicineName = variant.getMedicine().getName();
            this.activeIngredient = variant.getMedicine().getActiveIngredient();
            this.manufacturer = variant.getMedicine().getManufacturer();
            this.country = variant.getMedicine().getCountry();

            if (variant.getMedicine().getCategory() != null) {
                this.categoryName = variant.getMedicine().getCategory().getName();
            }
        }

        // DosageForm info
        if (variant.getDosageForm() != null) {
            this.dosageFormName = variant.getDosageForm().getDisplayName();
        }

        // Variant info
        this.registrationNumber = variant.getRegistrationNumber();
        this.concentration = variant.getStrength() != null ? variant.getStrength() : "";
        this.barcode = variant.getBarcode() != null ? variant.getBarcode() : "";
    }
}
