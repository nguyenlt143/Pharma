package vn.edu.fpt.pharma.service;

import vn.edu.fpt.pharma.base.BaseService;
import vn.edu.fpt.pharma.dto.DataTableRequest;
import vn.edu.fpt.pharma.dto.DataTableResponse;
import vn.edu.fpt.pharma.dto.medicine.MedicineRequest;
import vn.edu.fpt.pharma.dto.medicine.MedicineResponse;
import vn.edu.fpt.pharma.dto.medicine.MedicineSearchDTO;
import vn.edu.fpt.pharma.entity.Medicine;

import java.util.List;

public interface MedicineService extends BaseService<Medicine, Long> {
    DataTableResponse<MedicineResponse> getMedicines(DataTableRequest request, Integer status);
    MedicineResponse createMedicine(MedicineRequest request);
    MedicineResponse updateMedicine(Long id, MedicineRequest request);
    MedicineResponse getMedicineById(Long id);
    List<MedicineSearchDTO> searchMedicinesByKeyword(String keyword);
}
