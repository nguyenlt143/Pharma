package vn.edu.fpt.pharma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import vn.edu.fpt.pharma.entity.RequestForm;

import java.util.List;

public interface RequestFormRepository extends JpaRepository<RequestForm, Long>, JpaSpecificationExecutor<RequestForm> {

    @Query(value = """
        SELECT COUNT(*) FROM request_forms 
        WHERE branch_id != 1
          AND request_type = 'IMPORT' 
          AND request_status IN ('REQUESTED', 'RECEIVED')
        """, nativeQuery = true)
    int countWaitingOrders();


    @Query(value = """
        SELECT * FROM request_forms 
        WHERE branch_id = ?1 
        ORDER BY created_at DESC
        """, nativeQuery = true)
    List<RequestForm> findRequestFormsByBranch(Long branchId);

}
