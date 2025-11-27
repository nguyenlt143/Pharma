package vn.edu.fpt.pharma.service.impl;

import org.springframework.stereotype.Service;
import vn.edu.fpt.pharma.base.BaseServiceImpl;
import vn.edu.fpt.pharma.dto.inventory.InventoryMedicineVM;
import vn.edu.fpt.pharma.entity.Inventory;
import vn.edu.fpt.pharma.repository.InventoryRepository;
import vn.edu.fpt.pharma.service.AuditService;
import vn.edu.fpt.pharma.service.InventoryService;

import java.sql.Date;
import java.util.List;

@Service
public class InventoryServiceImpl extends BaseServiceImpl<Inventory, Long, InventoryRepository> implements InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryServiceImpl(InventoryRepository repository, AuditService auditService) {
        super(repository, auditService);
        this.inventoryRepository = repository;
    }

    @Override
    public List<InventoryMedicineVM> getInventoryMedicinesByBranch(Long branchId) {
        try {
            List<Object[]> results = inventoryRepository.findMedicinesByBranch(branchId);

            return results.stream().map(row -> {
                try {
                    InventoryMedicineVM vm = new InventoryMedicineVM();

                    // Safe conversion for numeric fields
                    vm.setInventoryId(row[0] != null ? ((Number) row[0]).longValue() : null);
                    vm.setVariantId(row[1] != null ? ((Number) row[1]).longValue() : null);
                    vm.setBatchId(row[2] != null ? ((Number) row[2]).longValue() : null);

                    // String fields
                    vm.setMedicineName(row[3] != null ? row[3].toString() : "");
                    vm.setActiveIngredient(row[4] != null ? row[4].toString() : "");
                    vm.setStrength(row[5] != null ? row[5].toString() : "");
                    vm.setDosageForm(row[6] != null ? row[6].toString() : "");
                    vm.setManufacturer(row[7] != null ? row[7].toString() : "");
                    vm.setBatchCode(row[8] != null ? row[8].toString() : "");

                    // Date field - handle multiple date types
                    if (row[9] != null) {
                        if (row[9] instanceof Date) {
                            vm.setExpiryDate(((Date) row[9]).toLocalDate());
                        } else if (row[9] instanceof java.time.LocalDate) {
                            vm.setExpiryDate((java.time.LocalDate) row[9]);
                        } else if (row[9] instanceof java.sql.Timestamp) {
                            vm.setExpiryDate(((java.sql.Timestamp) row[9]).toLocalDateTime().toLocalDate());
                        }
                    }

                    vm.setQuantity(row[10] != null ? ((Number) row[10]).longValue() : 0L);
                    vm.setUnit(row[11] != null ? row[11].toString() : "");
                    vm.setCategoryName(row[12] != null ? row[12].toString() : "");
                    vm.setBranchId(row[13] != null ? ((Number) row[13]).longValue() : null);

                    return vm;
                } catch (Exception e) {
                    // Log individual row error and return empty VM
                    System.err.println("Error mapping row: " + e.getMessage());
                    return new InventoryMedicineVM();
                }
            }).toList();
        } catch (Exception e) {
            System.err.println("Error fetching inventory medicines: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    @Override
    public List<vn.edu.fpt.pharma.dto.inventory.MedicineSearchDTO> searchMedicinesInWarehouse(String query) {
        List<Object[]> results = inventoryRepository.searchMedicinesInWarehouse(query);
        return results.stream().map(row -> {
            vn.edu.fpt.pharma.dto.inventory.MedicineSearchDTO dto = new vn.edu.fpt.pharma.dto.inventory.MedicineSearchDTO();
            dto.setVariantId(row[0] != null ? ((Number) row[0]).longValue() : null);
            dto.setBatchId(row[1] != null ? ((Number) row[1]).longValue() : null);
            dto.setMedicineName(row[2] != null ? row[2].toString() : "");
            dto.setActiveIngredient(row[3] != null ? row[3].toString() : "");
            dto.setStrength(row[4] != null ? row[4].toString() : "");
            dto.setDosageForm(row[5] != null ? row[5].toString() : "");
            dto.setBatchCode(row[6] != null ? row[6].toString() : "");
            dto.setExpiryDate(row[7] != null ? row[7].toString() : "");
            dto.setCurrentStock(row[8] != null ? ((Number) row[8]).longValue() : 0L);
            dto.setUnit(row[9] != null ? row[9].toString() : "");
            dto.setManufacturer(row[10] != null ? row[10].toString() : "");
            return dto;
        }).toList();
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public int deleteOutOfStockFromBranch(Long branchId) {
        // Find all inventory items with quantity = 0 for this branch
        List<Inventory> outOfStockItems = inventoryRepository.findAll().stream()
                .filter(inv -> inv.getBranch() != null
                        && inv.getBranch().getId().equals(branchId)
                        && (inv.getQuantity() == null || inv.getQuantity() == 0))
                .toList();

        // Delete them
        int count = outOfStockItems.size();
        inventoryRepository.deleteAll(outOfStockItems);

        return count;
    }
}
