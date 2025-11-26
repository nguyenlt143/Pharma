package vn.edu.fpt.pharma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.fpt.pharma.entity.Price;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PriceRepository extends JpaRepository<Price, Long>, JpaSpecificationExecutor<Price> {

    @Query("SELECT p FROM Price p WHERE p.variantId = :variantId AND p.startDate <= :now AND (p.endDate IS NULL OR p.endDate >= :now) ORDER BY p.startDate DESC")
    Optional<Price> findCurrentPriceForVariantAndBranch(@Param("variantId") Long variantId, @Param("now") LocalDateTime now);

}

