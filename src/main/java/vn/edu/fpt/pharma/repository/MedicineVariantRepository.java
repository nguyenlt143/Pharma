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
        v.quantity_per_package,
        m.uses,
        m.contraindications,
        m.side_effects
    FROM medicine_variant v
    JOIN medicines m 
        ON v.medicine_id = m.id
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
            v.barcode,
            v.registration_number,
            v.storage_conditions,
            m.indications,
            m.contraindications,
            m.side_effects,
            v.instructions,
            v.prescription_require,
            m.uses,
            m.country,
            m.manufacturer
        FROM medicine_variant v
        JOIN medicines m ON v.medicine_id = m.id
        WHERE v.medicine_id = :medicineId
          AND v.deleted = false
        ORDER BY v.strength
        """, nativeQuery = true)
    List<Object[]> findVariantsByMedicineIdWithDetails(@Param("medicineId") Long medicineId);

    @Query("""
        SELECT COUNT(v) FROM MedicineVariant v
        WHERE v.medicine.id = :medicineId
          AND LOWER(COALESCE(v.dosage_form, '')) = LOWER(COALESCE(:dosageForm, ''))
          AND LOWER(COALESCE(v.dosage, '')) = LOWER(COALESCE(:dosage, ''))
          AND LOWER(COALESCE(v.strength, '')) = LOWER(COALESCE(:strength, ''))
    """)
    long countDuplicateVariant(
            @Param("medicineId") Long medicineId,
            @Param("dosageForm") String dosageForm,
            @Param("dosage") String dosage,
            @Param("strength") String strength
    );
}