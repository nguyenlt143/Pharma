package vn.edu.fpt.pharma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import vn.edu.fpt.pharma.entity.InventoryMovementDetail;

import java.util.List;


public interface InventoryMovementDetailRepository extends JpaRepository<InventoryMovementDetail, Long>, JpaSpecificationExecutor<InventoryMovementDetail> {
    List<InventoryMovementDetail> findByMovementId(Long movementId);
    long countByMovementId(Long movementId);
}