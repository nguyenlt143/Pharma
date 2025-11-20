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

    @Query(value = "SELECT request_form_id, SUM(quantity * price) AS total FROM request_details WHERE request_form_id IN (:ids) AND deleted = false GROUP BY request_form_id", nativeQuery = true)
    List<Object[]> getTotalPriceByRequestIds(@Param("ids") List<Long> ids);
}