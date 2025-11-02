package vn.edu.fpt.pharma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import vn.edu.fpt.pharma.entity.RequestForm;

public interface RequestFormRepository extends JpaRepository<RequestForm, Long>, JpaSpecificationExecutor<RequestForm> {

    @Query(value = """
        SELECT COUNT(*) FROM request_forms 
        WHERE branch_id != 1
          AND request_type = 'IMPORT' 
          AND request_status IN ('REQUESTED', 'RECEIVED')
        """, nativeQuery = true)
    int countWaitingOrders();
}
