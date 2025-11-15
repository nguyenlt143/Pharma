package vn.edu.fpt.pharma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import vn.edu.fpt.pharma.entity.RequestDetail;

import java.util.List;

public interface RequestDetailRepository extends JpaRepository<RequestDetail, Long>, JpaSpecificationExecutor<RequestDetail> {
    List<RequestDetail> findByRequestFormId(Long formId);
}