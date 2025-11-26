package vn.edu.fpt.pharma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.fpt.pharma.entity.Medicine;

import java.util.List;

public interface MedicineRepository extends JpaRepository<Medicine, Long>, JpaSpecificationExecutor<Medicine> {
    @Query("SELECT COUNT(mv) FROM MedicineVariant mv WHERE mv.medicine.id = :medicineId")
    long countVariantsByMedicineId(@Param("medicineId") Long medicineId);

    @Query(value = """
        SELECT DISTINCT
            m.id,
            m.name,
            m.active_ingredient,
            m.brand_name,
            m.manufacturer,
            m.country
        FROM medicines m
        WHERE m.deleted = false
          AND (m.name LIKE CONCAT('%', :keyword, '%')
               OR m.active_ingredient LIKE CONCAT('%', :keyword, '%'))
        ORDER BY m.name
        LIMIT 20
        """, nativeQuery = true)
    List<Object[]> searchMedicinesByKeyword(@Param("keyword") String keyword);
}