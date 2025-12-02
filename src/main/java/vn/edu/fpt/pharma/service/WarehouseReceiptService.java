package vn.edu.fpt.pharma.service;

import vn.edu.fpt.pharma.dto.warehouse.CreateReceiptRequest;
import vn.edu.fpt.pharma.dto.warehouse.MedicineVariantDTO;
import vn.edu.fpt.pharma.dto.supplier.SupplierResponse;
import vn.edu.fpt.pharma.entity.InventoryMovement;

import java.util.List;

public interface WarehouseReceiptService {
    InventoryMovement createReceipt(CreateReceiptRequest request);
    List<SupplierResponse> searchSuppliers(String query);
    List<MedicineVariantDTO> searchMedicineVariants(String query);
}

