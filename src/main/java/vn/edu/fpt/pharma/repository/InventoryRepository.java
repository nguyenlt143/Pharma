package vn.edu.fpt.pharma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import vn.edu.fpt.pharma.entity.Inventory;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


import java.util.Optional;
import vn.edu.fpt.pharma.entity.Branch;
import vn.edu.fpt.pharma.entity.MedicineVariant;
import vn.edu.fpt.pharma.entity.Batch;

public interface InventoryRepository extends JpaRepository<Inventory, Long>, JpaSpecificationExecutor<Inventory> {
    @Query(value = """
        SELECT COUNT(*) FROM inventory 
        WHERE branch_id != 1 
          AND quantity <= min_stock
        """, nativeQuery = true)
    int countLowStock();

    @Query(value = """
        SELECT 
            i.id as inventoryId,
            i.variant_id as variantId,
            i.batch_id as batchId,
            COALESCE(m.name, 'N/A') as medicineName,
            COALESCE(m.active_ingredient, '') as activeIngredient,
            COALESCE(mv.strength, '') as strength,
            COALESCE(mv.dosage_form, '') as dosageForm,
            COALESCE(m.manufacturer, '') as manufacturer,
            COALESCE(b.batch_code, '') as batchCode,
            b.expiry_date as expiryDate,
            i.quantity as quantity,
            COALESCE(u.name, '') as unit,
            COALESCE(c.name, '') as categoryName,
            i.branch_id as branchId
        FROM inventory i
        LEFT JOIN batches b ON i.batch_id = b.id
        LEFT JOIN medicine_variant mv ON i.variant_id = mv.id
        LEFT JOIN medicines m ON mv.medicine_id = m.id
        LEFT JOIN categorys c ON m.category_id = c.id
        LEFT JOIN units u ON mv.base_unit_id = u.id
        WHERE i.branch_id = :branchId 
          AND i.deleted = false
          AND i.quantity > 0
        ORDER BY m.name, b.expiry_date
        """, nativeQuery = true)
    List<Object[]> findMedicinesByBranch(@Param("branchId") Long branchId);

    @Query(value = """
        SELECT 
            i.variant_id as variantId,
            i.batch_id as batchId,
            m.name as medicineName,
            COALESCE(m.active_ingredient, '') as activeIngredient,
            COALESCE(mv.strength, '') as strength,
            COALESCE(mv.dosage_form, '') as dosageForm,
            COALESCE(b.batch_code, '') as batchCode,
            DATE_FORMAT(b.expiry_date, '%d/%m/%Y') as expiryDate,
            i.quantity as currentStock,
            COALESCE(u.name, '') as unit,
            COALESCE(m.manufacturer, '') as manufacturer
        FROM inventory i
        LEFT JOIN batches b ON i.batch_id = b.id
        LEFT JOIN medicine_variant mv ON i.variant_id = mv.id
        LEFT JOIN medicines m ON mv.medicine_id = m.id
        LEFT JOIN units u ON mv.base_unit_id = u.id
        WHERE i.branch_id = 1
          AND i.deleted = false
          AND i.quantity > 0
          AND (m.name LIKE CONCAT('%', :query, '%') 
               OR m.active_ingredient LIKE CONCAT('%', :query, '%')
               OR b.batch_code LIKE CONCAT('%', :query, '%'))
        ORDER BY m.name, b.expiry_date
        LIMIT 20
        """, nativeQuery = true)
    List<Object[]> searchMedicinesInWarehouse(@Param("query") String query);
    Optional<Inventory> findByBranchAndVariantAndBatch(Branch branch, MedicineVariant variant, Batch batch);

    @Query("SELECT i FROM Inventory i WHERE i.branch.id = :branchId AND i.variant.id = :variantId AND i.deleted = false ORDER BY i.batch.expiryDate ASC")
    List<Inventory> findByBranchIdAndVariantId(@Param("branchId") Long branchId, @Param("variantId") Long variantId);

    @Query("SELECT i FROM Inventory i WHERE i.branch.id = :branchId AND i.variant.id = :variantId AND i.batch.id = :batchId AND i.deleted = false")
    Optional<Inventory> findByBranchIdAndVariantIdAndBatchId(@Param("branchId") Long branchId, @Param("variantId") Long variantId, @Param("batchId") Long batchId);
}