package vn.edu.fpt.pharma.service;

import vn.edu.fpt.pharma.base.BaseService;
import vn.edu.fpt.pharma.dto.medicine.MedicineVariantRequest;
import vn.edu.fpt.pharma.dto.medicine.MedicineVariantResponse;
import vn.edu.fpt.pharma.dto.medicine.SearchMedicineVM;
import vn.edu.fpt.pharma.entity.MedicineVariant;

import java.util.List;

public interface MedicineVariantService extends BaseService<MedicineVariant, Long> {
    List<MedicineVariantResponse> getVariantsByMedicineId(Long medicineId);
    MedicineVariantResponse createVariant(MedicineVariantRequest request);
    MedicineVariantResponse updateVariant(Long id, MedicineVariantRequest request);
    MedicineVariantResponse getVariantById(Long id);

    List<SearchMedicineVM> findByKeyword(String keyword);
}
