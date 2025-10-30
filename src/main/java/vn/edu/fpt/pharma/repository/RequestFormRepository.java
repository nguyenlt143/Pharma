package vn.edu.fpt.pharma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import vn.edu.fpt.pharma.entity.RequestForm;

public interface RequestFormRepository extends JpaRepository<RequestForm, Long>, JpaSpecificationExecutor<RequestForm> {
    int countByRequestStatus(String requestStatus);
}