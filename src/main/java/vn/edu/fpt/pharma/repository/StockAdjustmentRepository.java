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
        """, nativeQuery = true)
    LocalDateTime findLastInventoryCheck();

    @Query(value = """
        SELECT MAX(created_at) FROM stock_adjustments
        WHERE brand_id = :branchId
        """, nativeQuery = true)
    LocalDateTime findLastInventoryCheckByBranch(@Param("branchId") Long branchId);

    @Query(value = """
        SELECT 
            DATE_FORMAT(created_at, '%Y-%m-%d %H:%i:%s') as check_date,
            COUNT(DISTINCT variant_id) as checked_count
        FROM stock_adjustments 
        WHERE brand_id = :branchId 
        GROUP BY DATE_FORMAT(created_at, '%Y-%m-%d %H:%i:%s')
        ORDER BY check_date DESC
        """, nativeQuery = true)
    List<Object[]> findInventoryCheckHistoryByBranch(@Param("branchId") Long branchId);

    @Query("""
        SELECT sa FROM StockAdjustment sa
        LEFT JOIN FETCH sa.batch b
        LEFT JOIN FETCH b.variant v
        LEFT JOIN FETCH v.medicine m
        WHERE sa.BrandId = :branchId 
        AND FUNCTION('DATE_FORMAT', sa.createdAt, '%Y-%m-%d %H:%i:%s') = :checkDate
        ORDER BY sa.createdAt DESC
        """)
    List<StockAdjustment> findByBranchIdAndCheckDate(@Param("branchId") Long branchId,
                                                     @Param("checkDate") String checkDate);
}