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
}