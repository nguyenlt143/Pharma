package vn.edu.fpt.pharma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.fpt.pharma.entity.InventoryMovement;
import vn.edu.fpt.pharma.constant.MovementStatus;
import vn.edu.fpt.pharma.constant.MovementType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, Long>, JpaSpecificationExecutor<InventoryMovement> {

    @Query("SELECT COUNT(im) FROM InventoryMovement im WHERE im.destinationBranchId = :branchId AND im.movementType = :type AND im.movementStatus = :status")
    long countByBranchAndTypeAndStatus(@Param("branchId") Long branchId, @Param("type") MovementType type, @Param("status") MovementStatus status);

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

    // New: find inventory movement associated with a request form id (with details eagerly loaded)
    @Query("SELECT im FROM InventoryMovement im " +
           "LEFT JOIN FETCH im.inventoryMovementDetails imd " +
           "LEFT JOIN FETCH imd.variant v " +
           "LEFT JOIN FETCH v.medicine m " +
           "LEFT JOIN FETCH v.packageUnitId " +
           "WHERE im.requestForm.id = :requestFormId")
    Optional<InventoryMovement> findByRequestFormIdWithDetails(@Param("requestFormId") Long requestFormId);

}