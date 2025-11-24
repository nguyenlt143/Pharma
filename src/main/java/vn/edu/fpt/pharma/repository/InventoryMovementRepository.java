package vn.edu.fpt.pharma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.fpt.pharma.entity.InventoryMovement;
import vn.edu.fpt.pharma.constant.MovementStatus;
import vn.edu.fpt.pharma.constant.MovementType;

public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, Long>, JpaSpecificationExecutor<InventoryMovement> {

    @Query("SELECT COUNT(im) FROM InventoryMovement im WHERE im.destinationBranchId = :branchId AND im.movementType = :type AND im.movementStatus = :status")
    long countByBranchAndTypeAndStatus(@Param("branchId") Long branchId, @Param("type") MovementType type, @Param("status") MovementStatus status);

}