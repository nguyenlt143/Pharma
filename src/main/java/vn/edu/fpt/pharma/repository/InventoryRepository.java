package vn.edu.fpt.pharma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import vn.edu.fpt.pharma.entity.Inventory;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


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
}