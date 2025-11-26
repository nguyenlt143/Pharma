package vn.edu.fpt.pharma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import vn.edu.fpt.pharma.entity.Price;

public interface PriceRepository extends JpaRepository<Price, Long>, JpaSpecificationExecutor<Price> {
    // Price không có foreign key từ bảng khác, chỉ có variantId là Long, không phải relationship
    // Nên không cần kiểm tra
    
    @org.springframework.data.jpa.repository.Query("SELECT p.branchPrice FROM Price p WHERE p.variantId = :variantId AND p.deleted = false AND (p.startDate IS NULL OR p.startDate <= CURRENT_TIMESTAMP) AND (p.endDate IS NULL OR p.endDate >= CURRENT_TIMESTAMP) ORDER BY p.startDate DESC")
    java.util.Optional<Double> findBranchPriceByVariantId(@org.springframework.data.repository.query.Param("variantId") Long variantId);
}