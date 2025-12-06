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
            c.id as categoryId,
            i.branch_id as branchId,
            i.min_stock as minStock
        FROM inventory i
        LEFT JOIN batches b ON i.batch_id = b.id
        LEFT JOIN medicine_variant mv ON i.variant_id = mv.id
        LEFT JOIN medicines m ON mv.medicine_id = m.id
        LEFT JOIN categorys c ON m.category_id = c.id
        LEFT JOIN units u ON mv.base_unit_id = u.id
        WHERE i.branch_id = :branchId 
          AND i.deleted = false
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
            FORMATDATETIME(b.expiry_date, 'dd/MM/yyyy') as expiryDate,
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
          AND (m.name LIKE CONCAT('%', :query, '%') 
               OR m.active_ingredient LIKE CONCAT('%', :query, '%')
               OR b.batch_code LIKE CONCAT('%', :query, '%'))
        ORDER BY m.name, b.expiry_date
        LIMIT 20
        """, nativeQuery = true)
    List<Object[]> searchMedicinesInWarehouse(@Param("query") String query);
    Optional<Inventory> findByBranchAndVariantAndBatch(Branch branch, MedicineVariant variant, Batch batch);

    @Query("SELECT i FROM Inventory i WHERE i.branch.id = :branchId AND i.variant.id = :variantId AND i.batch.id = :batchId AND i.deleted = false")
    Optional<Inventory> findByBranchIdAndVariantIdAndBatchId(@Param("branchId") Long branchId, @Param("variantId") Long variantId, @Param("batchId") Long batchId);

    // Inventory Report Queries
    @Query(value = """
        SELECT COUNT(DISTINCT i.variant_id) 
        FROM inventory i 
        WHERE i.branch_id = :branchId 
          AND i.deleted = false 
          AND i.quantity > 0
        """, nativeQuery = true)
    int countTotalItems(@Param("branchId") Long branchId);

    @Query(value = """
        SELECT COUNT(*) 
        FROM inventory i 
        WHERE i.branch_id = :branchId 
          AND i.deleted = false 
          AND i.quantity < COALESCE(i.min_stock, 1000)
        """, nativeQuery = true)
    int countLowStockItems(@Param("branchId") Long branchId);

    @Query(value = """
        SELECT 
            i.id,
            b.batch_code,
            b.expiry_date,
            i.quantity,
            i.cost_price,
            s.name as supplierName
        FROM inventory i
        LEFT JOIN batches b ON i.batch_id = b.id
        LEFT JOIN suppliers s ON b.supplier_id = s.id
        WHERE i.variant_id = :variantId
          AND i.deleted = false
          AND i.quantity > 0
        ORDER BY b.expiry_date
        """, nativeQuery = true)
    List<Object[]> findInventoryByVariantId(@Param("variantId") Long variantId);

    @Query(value = """
        SELECT COUNT(*) 
        FROM inventory i 
        WHERE i.branch_id = :branchId 
          AND i.deleted = false 
          AND i.quantity <= 0
        """, nativeQuery = true)
    int countOutOfStockItems(@Param("branchId") Long branchId);

    @Query(value = """
        SELECT COALESCE(SUM(i.quantity * i.cost_price), 0) 
        FROM inventory i 
        WHERE i.branch_id = :branchId 
          AND i.deleted = false 
          AND i.quantity > 0
        """, nativeQuery = true)
    Double calculateTotalValue(@Param("branchId") Long branchId);

    // Generalized expiry item counting with customizable days threshold
    @Query(value = """
        SELECT COUNT(*) 
        FROM inventory i 
        JOIN batches b ON i.batch_id = b.id 
        WHERE i.branch_id = :branchId 
          AND i.deleted = false 
          AND i.quantity > 0
          AND (
              (:checkExpired = true AND b.expiry_date < CURDATE()) OR
              (:checkExpired = false AND :daysThreshold IS NOT NULL AND b.expiry_date <= DATE_ADD(CURDATE(), INTERVAL :daysThreshold DAY) AND b.expiry_date >= CURDATE())
          )
        """, nativeQuery = true)
    int countItemsByExpiry(@Param("branchId") Long branchId, @Param("daysThreshold") Integer daysThreshold, @Param("checkExpired") boolean checkExpired);

    // Convenience methods
    default int countNearExpiryItems(Long branchId) {
        return countItemsByExpiry(branchId, 30, false);
    }

    default int countExpiredItems(Long branchId) {
        return countItemsByExpiry(branchId, null, true);
    }

    // Category Statistics
    @Query(value = """
        SELECT 
            c.name as categoryName,
            COUNT(DISTINCT i.variant_id) as itemCount,
            COALESCE(SUM(i.quantity), 0) as totalQuantity,
            COALESCE(SUM(i.quantity * i.cost_price), 0) as totalValue
        FROM inventory i
        JOIN medicine_variant mv ON i.variant_id = mv.id
        JOIN medicines m ON mv.medicine_id = m.id
        JOIN categorys c ON m.category_id = c.id
        WHERE i.branch_id = :branchId 
          AND i.deleted = false 
        GROUP BY c.id, c.name
        ORDER BY totalValue DESC
        """, nativeQuery = true)
    List<Object[]> getCategoryStatistics(@Param("branchId") Long branchId);

    // Get all categories
    @Query(value = """
        SELECT DISTINCT c.id, c.name
        FROM categorys c
        JOIN medicines m ON m.category_id = c.id
        JOIN medicine_variant mv ON mv.medicine_id = m.id
        JOIN inventory i ON i.variant_id = mv.id
        WHERE i.branch_id = :branchId 
          AND i.deleted = false
        ORDER BY c.name
        """, nativeQuery = true)
    List<Object[]> getAllCategories(@Param("branchId") Long branchId);
}
