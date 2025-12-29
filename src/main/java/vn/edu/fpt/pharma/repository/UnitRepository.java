package vn.edu.fpt.pharma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.fpt.pharma.entity.Unit;

import java.util.List;
import java.util.Optional;

public interface UnitRepository extends JpaRepository<Unit, Long>, JpaSpecificationExecutor<Unit> {
    @Query("SELECT COUNT(uc) FROM UnitConversion uc WHERE uc.unitId.id = :unitId")
    long countVariantsByUnitId(@Param("unitId") Long unitId);

    @Query("SELECT u FROM Unit u WHERE u.isBase = true")
    List<Unit> findAllBaseUnits();

    @Query("SELECT u FROM Unit u WHERE u.name = :name")
    Optional<Unit> findByName(@Param("name") String name);

    @Query("SELECT u FROM Unit u WHERE u.isBase = true AND u.name = :name")
    Optional<Unit> findBaseUnitByName(@Param("name") String name);

    @Query("SELECT u FROM Unit u WHERE u.id IN :ids")
    List<Unit> findAllByIds(@Param("ids") List<Long> ids);
}