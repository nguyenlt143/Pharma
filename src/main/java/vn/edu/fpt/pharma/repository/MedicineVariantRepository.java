package vn.edu.fpt.pharma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.fpt.pharma.entity.MedicineVariant;

import java.util.List;

public interface MedicineVariantRepository extends JpaRepository<MedicineVariant, Long>, JpaSpecificationExecutor<MedicineVariant> {
    @Query("SELECT v FROM MedicineVariant v WHERE v.medicine.id = :medicineId")
    List<MedicineVariant> findByMedicineId(@Param("medicineId") Long medicineId);

    @Query(value = """
    SELECT
        v.id,
        m.name,
        m.active_ingredient,
        m.manufacturer,
        v.strength,
        m.country,
        u_pkg.name AS package_unit_name,
        v.quantity_per_package,
        u_base.name AS base_unit_name,
        v.uses,
        v.contraindications,
        v.side_effects
    FROM medicine_variant v
    JOIN medicines m 
        ON v.medicine_id = m.id
    JOIN units u_pkg 
        ON v.package_unit_id = u_pkg.id
    JOIN units u_base
        ON v.base_unit_id = u_base.id
    WHERE m.name LIKE CONCAT('%', :keyword, '%')
       OR m.active_ingredient LIKE CONCAT('%', :keyword, '%')
    """, nativeQuery = true)
        List<Object[]> findByNameActiveIngredient(@Param("keyword") String keyword);

    @Query(value = """
        SELECT 
            v.id as variantId,
            v.dosage_form,
            v.dosage,
            v.strength,
            u_pkg.name as packageUnitName,
            u_base.name as baseUnitName,
            v.quantity_per_package,
            v.barcode,
            v.registration_number,
            v.storage_conditions,
            v.indications,
            v.contraindications,
            v.side_effects,
            v.instructions,
            v.prescription_require,
            v.uses,
            m.country,
            m.manufacturer
        FROM medicine_variant v
        JOIN medicines m ON v.medicine_id = m.id
        LEFT JOIN units u_pkg ON v.package_unit_id = u_pkg.id
        LEFT JOIN units u_base ON v.base_unit_id = u_base.id
        WHERE v.medicine_id = :medicineId
          AND v.deleted = false
        ORDER BY v.strength
        """, nativeQuery = true)
    List<Object[]> findVariantsByMedicineIdWithDetails(@Param("medicineId") Long medicineId);
}