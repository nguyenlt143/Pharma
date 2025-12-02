package vn.edu.fpt.pharma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.fpt.pharma.constant.RequestType;
import vn.edu.fpt.pharma.entity.RequestForm;

import java.time.LocalDate;
import java.util.List;

public interface RequestFormRepository extends JpaRepository<RequestForm, Long>, JpaSpecificationExecutor<RequestForm> {

    // Generalized search method for both IMPORT and EXPORT forms
    @Query(value = """
        SELECT * FROM request_forms 
        WHERE branch_id = :branchId
          AND request_type = :requestType
          AND (:id IS NULL OR id = :id)
          AND (:createdAt IS NULL OR DATE(created_at) = :createdAt)
        ORDER BY created_at DESC
    """, nativeQuery = true)
    List<RequestForm> searchFormsByType(@Param("branchId") Long branchId,
                                         @Param("requestType") String requestType,
                                         @Param("id") Long id,
                                         @Param("createdAt") LocalDate createdAt);

    // Convenience methods
    default List<RequestForm> searchImportForms(Long branchId, Long id, LocalDate createdAt) {
        return searchFormsByType(branchId, "IMPORT", id, createdAt);
    }

    default List<RequestForm> searchExportForms(Long branchId, Long id, LocalDate createdAt) {
        return searchFormsByType(branchId, "EXPORT", id, createdAt);
    }


    List<RequestForm> findAllByBranchId(Long branchId);

    List<RequestForm> findByRequestType(RequestType requestType);

    List<RequestForm> findByBranchIdAndRequestType(Long branchId, RequestType requestType);
    // IMPORT / RETURN

    // Generalized count pending method by type
    @Query(value = "SELECT COUNT(*) FROM request_forms WHERE branch_id = :branchId AND request_type = :requestType AND request_status IN ('REQUESTED','RECEIVED')", nativeQuery = true)
    int countPendingByTypeForBranch(@Param("branchId") Long branchId, @Param("requestType") String requestType);

    // Convenience methods
    default int countPendingInboundForBranch(Long branchId) {
        return countPendingByTypeForBranch(branchId, "IMPORT");
    }

    default int countPendingOutboundForBranch(Long branchId) {
        return countPendingByTypeForBranch(branchId, "EXPORT");
    }
}
