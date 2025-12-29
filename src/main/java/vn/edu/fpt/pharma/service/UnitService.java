package vn.edu.fpt.pharma.service;

import vn.edu.fpt.pharma.base.BaseService;
import vn.edu.fpt.pharma.constant.DosageForm;
import vn.edu.fpt.pharma.dto.DataTableRequest;
import vn.edu.fpt.pharma.dto.DataTableResponse;
import vn.edu.fpt.pharma.dto.unit.UnitRequest;
import vn.edu.fpt.pharma.dto.unit.UnitResponse;
import vn.edu.fpt.pharma.entity.Unit;

import java.util.List;

public interface UnitService extends BaseService<Unit, Long> {
    DataTableResponse<UnitResponse> getUnits(DataTableRequest request);
    UnitResponse createUnit(UnitRequest request);
    UnitResponse updateUnit(Long id, UnitRequest request);
    UnitResponse getUnitById(Long id);
    List<UnitResponse> getAllBaseUnits();
    List<UnitResponse> getAvailableUnitsForDosageForm(DosageForm dosageForm);
    Unit getBaseUnitForDosageForm(DosageForm dosageForm);
    boolean isValidBaseUnit(DosageForm dosageForm, Long unitId);
}

