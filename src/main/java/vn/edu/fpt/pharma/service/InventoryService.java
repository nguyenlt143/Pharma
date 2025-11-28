package vn.edu.fpt.pharma.service;

import vn.edu.fpt.pharma.base.BaseService;
import vn.edu.fpt.pharma.dto.inventory.InventoryMedicineVM;
import vn.edu.fpt.pharma.dto.inventory.MedicineSearchDTO;
import vn.edu.fpt.pharma.entity.Inventory;

import java.util.List;

public interface InventoryService extends BaseService<Inventory, Long> {
    List<InventoryMedicineVM> getInventoryMedicinesByBranch(Long branchId);
    List<MedicineSearchDTO> searchMedicinesInWarehouse(String query);

    // Delete inventory items with quantity = 0 from branch
    int deleteOutOfStockFromBranch(Long branchId);
}
