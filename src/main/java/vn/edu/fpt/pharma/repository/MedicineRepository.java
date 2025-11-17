package vn.edu.fpt.pharma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.fpt.pharma.entity.Medicine;

public interface MedicineRepository extends JpaRepository<Medicine, Long>, JpaSpecificationExecutor<Medicine> {
    @Query("SELECT COUNT(mv) FROM MedicineVariant mv WHERE mv.medicine.id = :medicineId")
    long countVariantsByMedicineId(@Param("medicineId") Long medicineId);
}