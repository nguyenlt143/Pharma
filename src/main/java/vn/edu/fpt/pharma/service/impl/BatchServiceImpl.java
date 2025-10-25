package vn.edu.fpt.pharma.service.impl;

import org.springframework.stereotype.Service;
import vn.edu.fpt.pharma.base.BaseServiceImpl;
import vn.edu.fpt.pharma.entity.Batch;
import vn.edu.fpt.pharma.repository.BatchRepository;
import vn.edu.fpt.pharma.service.AuditService;
import vn.edu.fpt.pharma.service.BatchService;

@Service
public class BatchServiceImpl extends BaseServiceImpl<Batch, Long, BatchRepository> implements BatchService {

    public BatchServiceImpl(BatchRepository repository, AuditService auditService) {
        super(repository, auditService);
    }
}
