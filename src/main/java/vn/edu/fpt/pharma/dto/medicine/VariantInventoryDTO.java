package vn.edu.fpt.pharma.dto.medicine;

import java.util.List;

public record VariantInventoryDTO(
        Long variantId,
        String dosageForm,
        String dosage,
        String strength,
        String barcode,
        String registrationNumber,
        String storageConditions,
        String instructions,
        boolean prescriptionRequire,
        String indications,
        String contraindications,
        String sideEffects,
        String uses,
        String country,
        String manufacturer,
        List<InventoryDetailDTO> inventories,
        List<UnitConversionVM> unitConversion
) {
}

