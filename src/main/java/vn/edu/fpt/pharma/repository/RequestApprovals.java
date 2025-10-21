package vn.edu.fpt.pharma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import vn.edu.fpt.pharma.entity.RequestApproval;

public interface RequestApprovals extends JpaRepository<RequestApproval, Long>, JpaSpecificationExecutor<RequestApproval> {
}