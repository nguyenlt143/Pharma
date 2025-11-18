package vn.edu.fpt.pharma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import vn.edu.fpt.pharma.entity.StockAdjustment;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;



public interface StockAdjustmentRepository extends JpaRepository<StockAdjustment, Long>, JpaSpecificationExecutor<StockAdjustment> {

    @Query(value = """
        SELECT MAX(created_at) FROM stock_adjustments 
        WHERE brand_id != 1
        """, nativeQuery = true)
    LocalDateTime findLastInventoryCheck();

    @Query(value = """
        SELECT 
            created_at as check_date,
            COUNT(DISTINCT variant_id) as checked_count
        FROM stock_adjustments 
        WHERE brand_id = :branchId 
        GROUP BY created_at
        ORDER BY created_at DESC
        """, nativeQuery = true)
    List<Object[]> findInventoryCheckHistoryByBranch(@Param("branchId") Long branchId);

    @Query(value = """
        SELECT * FROM stock_adjustments 
        WHERE brand_id = :branchId 
        AND DATE(created_at) = :checkDate
        ORDER BY created_at DESC
        """, nativeQuery = true)
    List<StockAdjustment> findByBranchIdAndCheckDate(@Param("branchId") Long branchId,
                                                     @Param("checkDate") String checkDate);
}