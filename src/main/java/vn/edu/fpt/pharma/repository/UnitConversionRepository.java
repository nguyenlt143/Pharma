package vn.edu.fpt.pharma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.pharma.entity.UnitConversion;

import java.util.List;

@Repository
public interface UnitConversionRepository extends JpaRepository<UnitConversion, Long> {
    List<UnitConversion> findByVariantId(Long variantId);
    void deleteByVariantIdId(Long variantId);
}

