package vn.edu.fpt.pharma.service;

import vn.edu.fpt.pharma.entity.Supplier;

import java.util.List;

public interface SupplierService {
    List<Supplier> getAllSuppliers();
    Supplier saveSupplier(Supplier supplier);
    Supplier updateSupplier(Long id, Supplier supplier);
    Supplier getSupplierById(Long id);
}
