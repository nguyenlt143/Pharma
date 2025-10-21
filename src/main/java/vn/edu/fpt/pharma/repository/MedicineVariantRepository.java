package vn.edu.fpt.pharma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import vn.edu.fpt.pharma.entity.MedicineVariant;


public interface MedicineVariantRepository extends JpaRepository<MedicineVariant, Long>, JpaSpecificationExecutor<MedicineVariant> {
}