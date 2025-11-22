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
}