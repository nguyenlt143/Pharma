package vn.edu.fpt.pharma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import vn.edu.fpt.pharma.entity.Batch;
import java.time.LocalDateTime;
import java.util.Optional;


public interface BatchRepository extends JpaRepository<Batch, Long>, JpaSpecificationExecutor<Batch> {
    @Query(value = """
        SELECT COUNT(*) FROM batches b
        JOIN inventory i ON i.batch_id = b.id
        WHERE i.branch_id != 1
          AND b.expiry_date BETWEEN NOW() AND DATE_ADD(NOW(), INTERVAL 90 DAY)
        """, nativeQuery = true)
    int countNearlyExpired();

    Optional<Batch> findByVariantId(Long variantId);
}