package vn.edu.fpt.pharma.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.fpt.pharma.constant.MovementStatus;
import vn.edu.fpt.pharma.constant.MovementType;
import vn.edu.fpt.pharma.dto.manager.DailyRevenue;
import vn.edu.fpt.pharma.dto.manager.KpiData;
import vn.edu.fpt.pharma.dto.manager.TopProductItem;
import vn.edu.fpt.pharma.entity.InventoryMovement;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, Long>, JpaSpecificationExecutor<InventoryMovement> {

    @Query("SELECT COUNT(im) FROM InventoryMovement im WHERE im.destinationBranchId = :branchId AND im.movementType = :type AND im.movementStatus = :status")
    long countByBranchAndTypeAndStatus(@Param("branchId") Long branchId, @Param("type") MovementType type, @Param("status") MovementStatus status);

    // Count all movements with SHIPPED status from warehouse (source branch = 1)
    @Query("SELECT COUNT(im) FROM InventoryMovement im WHERE im.sourceBranchId = :warehouseBranchId AND im.movementStatus = :status")
    long countShippedFromWarehouse(@Param("warehouseBranchId") Long warehouseBranchId, @Param("status") MovementStatus status);

    @Query("SELECT im FROM InventoryMovement im " +
           "LEFT JOIN FETCH im.inventoryMovementDetails imd " +
           "LEFT JOIN FETCH imd.variant v " +
           "LEFT JOIN FETCH v.medicine m " +
           "LEFT JOIN FETCH v.packageUnitId " +
           "WHERE im.id = :id")
    Optional<InventoryMovement> findByIdWithDetails(@Param("id") Long id);

    // New: fetch movements since a given datetime where the branch is either source or destination
    @Query("SELECT im FROM InventoryMovement im " +
           "WHERE im.createdAt >= :fromDate " +
           "AND ((im.destinationBranchId = :branchId) OR (im.sourceBranchId = :branchId)) " +
           "ORDER BY im.createdAt")
    List<InventoryMovement> findMovementsSinceByBranch(@Param("fromDate") LocalDateTime fromDate, @Param("branchId") Long branchId);

    // New: fetch movements with details for summary (adjustment & expired returns)
    @Query("SELECT DISTINCT im FROM InventoryMovement im " +
           "LEFT JOIN FETCH im.inventoryMovementDetails imd " +
           "WHERE im.createdAt >= :fromDate " +
           "AND im.sourceBranchId = :branchId " +
           "AND im.movementType IN :types " +
           "ORDER BY im.createdAt")
    List<InventoryMovement> findMovementsWithDetailsSinceByBranchAndTypes(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("branchId") Long branchId,
            @Param("types") List<MovementType> types);


    // New: find inventory movement associated with a request form id (with details eagerly loaded)
    @Query("SELECT im FROM InventoryMovement im " +
           "LEFT JOIN FETCH im.inventoryMovementDetails imd " +
           "LEFT JOIN FETCH imd.variant v " +
           "LEFT JOIN FETCH v.medicine m " +
           "LEFT JOIN FETCH v.packageUnitId " +
           "WHERE im.requestForm.id = :requestFormId")
    Optional<InventoryMovement> findByRequestFormIdWithDetails(@Param("requestFormId") Long requestFormId);

    @Query("SELECT DISTINCT im FROM InventoryMovement im " +
           "LEFT JOIN FETCH im.inventoryMovementDetails " +
           "WHERE im.movementType = :type " +
           "AND im.destinationBranchId = :branchId " +
           "AND im.movementStatus = :status")
    List<InventoryMovement> findAllWithDetailsByTypeAndBranchAndStatus(
            @Param("type") MovementType type,
            @Param("branchId") Long branchId,
            @Param("status") MovementStatus status);

    // -------------------------------------------------------------------------
    // OWNER DASHBOARD – REVENUE & PROFIT BASED ON INVENTORY MOVEMENTS
    // doanh thu = phiếu cấp cho chi nhánh (WARE_TO_BR)
    // lợi nhuận = (price - snap_cost) * quantity (giá xuất - giá vốn kho tổng)
    // -------------------------------------------------------------------------

    @Query(value = """
            SELECT
                COALESCE(SUM(imd.price * imd.quantity), 0)          AS total_revenue,
                COALESCE(SUM((imd.price - imd.snap_cost) * imd.quantity), 0) AS total_profit,
                COALESCE(COUNT(DISTINCT im.id), 0)                  AS order_count
            FROM inventory_movements im
            JOIN inventory_movement_details imd ON im.id = imd.movement_id AND imd.deleted = FALSE
            WHERE im.deleted = FALSE
              AND im.movement_type = 'WARE_TO_BR'
              AND im.movement_status = 'RECEIVED'
              AND im.created_at >= :fromDate
              AND im.created_at < :toDate
              AND (:branchId IS NULL OR im.destination_branch_id = :branchId)
            """, nativeQuery = true)
    List<Object[]> sumOwnerRevenueRaw(
            @Param("branchId") Long branchId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );

    default KpiData sumOwnerRevenue(
            Long branchId, LocalDateTime fromDate, LocalDateTime toDate
    ) {
        List<Object[]> rows = sumOwnerRevenueRaw(branchId, fromDate, toDate);
        if (rows == null || rows.isEmpty()) {
            return new KpiData(0.0, 0L, 0.0);
        }
        Object[] r = rows.get(0);
        Double revenue = r[0] != null ? ((Number) r[0]).doubleValue() : 0.0;
        Double profit = r[1] != null ? ((Number) r[1]).doubleValue() : 0.0;
        Long orderCount = r[2] != null ? ((Number) r[2]).longValue() : 0L;
        return new KpiData(revenue, orderCount, profit);
    }

    // Daily revenue for owner dashboard (chart), grouped by movement date
    @Query(value = """
            SELECT
                DATE(im.created_at) AS date,
                COALESCE(SUM(imd.price * imd.quantity), 0) AS revenue
            FROM inventory_movements im
            JOIN inventory_movement_details imd ON im.id = imd.movement_id AND imd.deleted = FALSE
            WHERE im.deleted = FALSE
              AND im.movement_type = 'WARE_TO_BR'
              AND im.movement_status = 'RECEIVED'
              AND im.created_at >= :fromDate
              AND im.created_at < :toDate
              AND (:branchId IS NULL OR im.destination_branch_id = :branchId)
            GROUP BY DATE(im.created_at)
            ORDER BY DATE(im.created_at)
            """, nativeQuery = true)
    List<DailyRevenue> getOwnerDailyRevenue(
            @Param("branchId") Long branchId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );

    // Top product categories by quantity in movements WARE_TO_BR (for product stats)
    @Query("""
        SELECT new vn.edu.fpt.pharma.dto.manager.TopProductItem(
            c.name, SUM(imd.quantity)
        )
        FROM InventoryMovement im
        JOIN im.inventoryMovementDetails imd
        JOIN imd.variant mv
        JOIN mv.medicine m
        JOIN m.category c
        WHERE im.movementType = vn.edu.fpt.pharma.constant.MovementType.WARE_TO_BR
          AND im.movementStatus = vn.edu.fpt.pharma.constant.MovementStatus.RECEIVED
          AND im.createdAt >= :fromDate
          AND im.createdAt < :toDate
          AND (:branchId IS NULL OR im.destinationBranchId = :branchId)
        GROUP BY c.id, c.name
        ORDER BY SUM(imd.quantity) DESC
        """)
    List<TopProductItem> findOwnerTopCategories(
            @Param("branchId") Long branchId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable
    );

    default List<TopProductItem> findOwnerTopCategories(
            Long branchId, LocalDateTime fromDate, LocalDateTime toDate, int limit
    ) {
        if (limit <= 0) {
            return Collections.emptyList();
        }
        return findOwnerTopCategories(branchId, fromDate, toDate,
                org.springframework.data.domain.PageRequest.of(0, limit));
    }
}