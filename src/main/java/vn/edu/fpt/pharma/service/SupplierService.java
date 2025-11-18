package vn.edu.fpt.pharma.service;

import vn.edu.fpt.pharma.base.BaseService;
import vn.edu.fpt.pharma.dto.DataTableRequest;
import vn.edu.fpt.pharma.dto.DataTableResponse;
import vn.edu.fpt.pharma.dto.supplier.SupplierRequest;
import vn.edu.fpt.pharma.dto.supplier.SupplierResponse;
import vn.edu.fpt.pharma.entity.Supplier;

import java.util.List;

public interface SupplierService extends BaseService<Supplier, Long> {
    List<Supplier> getAllSuppliers();
    DataTableResponse<SupplierResponse> getSuppliers(DataTableRequest request);
    SupplierResponse createSupplier(SupplierRequest request);
    SupplierResponse updateSupplier(Long id, SupplierRequest request);
    SupplierResponse getSupplierById(Long id);
}
