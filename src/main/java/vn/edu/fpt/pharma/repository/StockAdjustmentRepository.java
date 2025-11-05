package vn.edu.fpt.pharma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import vn.edu.fpt.pharma.entity.StockAdjustment;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.Query;



public interface StockAdjustmentRepository extends JpaRepository<StockAdjustment, Long>, JpaSpecificationExecutor<StockAdjustment> {
    @Query(value = """
        SELECT MAX(created_at) FROM stock_adjustments 
        WHERE brand_id != 1
        """, nativeQuery = true)
    LocalDateTime findLastInventoryCheck();
}