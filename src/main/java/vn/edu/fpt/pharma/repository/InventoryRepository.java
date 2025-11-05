package vn.edu.fpt.pharma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import vn.edu.fpt.pharma.entity.Inventory;
import org.springframework.data.jpa.repository.Query;


public interface InventoryRepository extends JpaRepository<Inventory, Long>, JpaSpecificationExecutor<Inventory> {
    @Query(value = """
        SELECT COUNT(*) FROM inventory 
        WHERE branch_id != 1 
          AND quantity <= min_stock
        """, nativeQuery = true)
    int countLowStock();
}