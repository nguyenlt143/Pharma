package vn.edu.fpt.pharma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.fpt.pharma.constant.RequestType;
import vn.edu.fpt.pharma.entity.RequestForm;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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


    @Query(value = """
        SELECT * FROM request_forms 
        WHERE branch_id = :branchId
          AND request_type = 'IMPORT'
          AND (:id IS NULL OR id = :id)
          AND (:createdAt IS NULL OR DATE(created_at) = :createdAt)
        ORDER BY created_at DESC
    """, nativeQuery = true)
        List<RequestForm> searchImportForms(@Param("branchId") Long branchId,
                                             @Param("id") Long id,
                                             @Param("createdAt") LocalDate createdAt);

    @Query(value = """
        SELECT * FROM request_forms 
        WHERE branch_id = :branchId
          AND request_type = 'EXPORT'
          AND (:id IS NULL OR id = :id)
          AND (:createdAt IS NULL OR DATE(created_at) = :createdAt)
        ORDER BY created_at DESC
    """, nativeQuery = true)
        List<RequestForm> searchExportForms(@Param("branchId") Long branchId,
                                            @Param("id") Long id,
                                            @Param("createdAt") LocalDate createdAt);


    List<RequestForm> findAllByBranchId(Long branchId);

    List<RequestForm> findByRequestType(RequestType requestType);
    // IMPORT / RETURN

}
