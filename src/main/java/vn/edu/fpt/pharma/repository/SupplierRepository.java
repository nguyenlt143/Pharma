package vn.edu.fpt.pharma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.fpt.pharma.entity.Supplier;

public interface SupplierRepository extends JpaRepository<Supplier, Long>, JpaSpecificationExecutor<Supplier> {
    @Query("SELECT COUNT(b) FROM Batch b WHERE b.supplier.id = :supplierId")
    long countBatchesBySupplierId(@Param("supplierId") Long supplierId);
    
    @Query("SELECT COUNT(im) FROM InventoryMovement im WHERE im.supplier.id = :supplierId")
    long countInventoryMovementsBySupplierId(@Param("supplierId") Long supplierId);

    boolean existsByNameIgnoreCase(String name);

    boolean existsByPhone(String phone);
}