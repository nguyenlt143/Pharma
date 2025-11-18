package vn.edu.fpt.pharma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.fpt.pharma.entity.Unit;

public interface UnitRepository extends JpaRepository<Unit, Long>, JpaSpecificationExecutor<Unit> {
    @Query("SELECT COUNT(mv) FROM MedicineVariant mv WHERE mv.packageUnitId.id = :unitId OR mv.baseUnitId.id = :unitId")
    long countVariantsByUnitId(@Param("unitId") Long unitId);
}