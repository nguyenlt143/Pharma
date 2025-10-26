package vn.edu.fpt.pharma.service.impl;

import org.springframework.stereotype.Service;
import vn.edu.fpt.pharma.base.BaseServiceImpl;
import vn.edu.fpt.pharma.entity.Branch;
import vn.edu.fpt.pharma.repository.BranchRepository;
import vn.edu.fpt.pharma.service.AuditService;
import vn.edu.fpt.pharma.service.BranchService;

@Service
public class BranchServiceImpl extends BaseServiceImpl<Branch, Long, BranchRepository> implements BranchService {

    public BranchServiceImpl(BranchRepository repository, AuditService auditService) {
        super(repository, auditService);
    }
}
