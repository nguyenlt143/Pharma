package vn.edu.fpt.pharma.service.impl;

import org.springframework.stereotype.Service;
import vn.edu.fpt.pharma.base.BaseServiceImpl;
import vn.edu.fpt.pharma.dto.requestform.RequestFormVM;
import vn.edu.fpt.pharma.entity.RequestForm;
import vn.edu.fpt.pharma.repository.RequestFormRepository;
import vn.edu.fpt.pharma.service.AuditService;
import vn.edu.fpt.pharma.service.RequestFormService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RequestFormServiceImpl extends BaseServiceImpl<RequestForm, Long, RequestFormRepository> implements RequestFormService {

    public RequestFormServiceImpl(RequestFormRepository repository, AuditService auditService) {
        super(repository, auditService);
        this.repository = repository;
        this.auditService = auditService;
    }

    private final RequestFormRepository repository;
    private final AuditService auditService;

    @Override
    public List<RequestFormVM> getRequestFormsByBranch(Long branchId) {
        return repository.findByBranchId(branchId)
                .stream()
                .map(RequestFormVM::new)
                .collect(Collectors.toList());
    }
}
