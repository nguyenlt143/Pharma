package vn.edu.fpt.pharma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.fpt.pharma.entity.RequestDetail;

import java.util.List;

public interface RequestDetailRepository extends JpaRepository<RequestDetail, Long>, JpaSpecificationExecutor<RequestDetail> {
    List<RequestDetail> findByRequestFormId(Long formId);

    @Query(value = "SELECT request_form_id AS requestFormId, COUNT(DISTINCT variant_id) AS medicineCount FROM request_details WHERE request_form_id IN (:ids) AND deleted = false GROUP BY request_form_id", nativeQuery = true)
    List<Object[]> countDistinctMedicineByRequestIds(@Param("ids") List<Long> ids);

    // Compute total using branch_price from prices table for matching variant & branch, choosing valid price (date range) or latest
    @Query(value = """
        SELECT rd.request_form_id,
               SUM(rd.quantity * COALESCE(
                     (
                       SELECT p.branch_price
                       FROM prices p
                       JOIN request_forms rf2 ON rf2.id = rd.request_form_id
                       WHERE p.variant_id = rd.variant_id
                         AND (p.branch_id IS NULL OR p.branch_id = rf2.branch_id)
                         AND (p.start_date IS NULL OR p.start_date <= NOW())
                         AND (p.end_date IS NULL OR p.end_date >= NOW())
                         AND p.deleted = false
                       ORDER BY p.start_date DESC
                       LIMIT 1
                     ), 0)) AS total
        FROM request_details rd
        WHERE rd.request_form_id IN (:ids)
          AND rd.deleted = false
        GROUP BY rd.request_form_id
        """, nativeQuery = true)
    List<Object[]> getTotalPriceByRequestIds(@Param("ids") List<Long> ids);

    @Query(value = """
        SELECT rd.request_form_id,
               COUNT(DISTINCT rd.variant_id) AS medicine_types,
               COALESCE(SUM(rd.quantity),0) AS total_units,
               COALESCE(SUM(rd.quantity * COALESCE(
                 (
                   SELECT p.branch_price
                   FROM prices p
                   WHERE p.variant_id = rd.variant_id
                     AND (p.branch_id IS NULL OR p.branch_id = rf.branch_id)
                     AND p.deleted = false
                     AND (p.start_date IS NULL OR p.start_date <= NOW())
                     AND (p.end_date IS NULL OR p.end_date >= NOW())
                   ORDER BY p.start_date DESC
                   LIMIT 1
                 ), 0)),0) AS total_cost
        FROM request_details rd
        JOIN request_forms rf ON rf.id = rd.request_form_id
        WHERE rd.request_form_id IN (:ids)
          AND rd.deleted = false
        GROUP BY rd.request_form_id
        """, nativeQuery = true)
    List<Object[]> getAggregatedStatsByRequestIds(@Param("ids") List<Long> ids);
}