package vn.edu.fpt.pharma.dto.medicine;

import vn.edu.fpt.pharma.entity.UnitConversion;

public record UnitConversionVM(
        Long unitId,
        String unitName,
        Double multiplier,
        Boolean isSale
) {
    public UnitConversionVM(UnitConversion unitConversion){
        this(
                unitConversion.getId(),
                unitConversion.getUnitId().getName() != null ? unitConversion.getUnitId().getName() : null,
                unitConversion.getMultiplier(),
                unitConversion.getIsSale()
        );
    }
}
