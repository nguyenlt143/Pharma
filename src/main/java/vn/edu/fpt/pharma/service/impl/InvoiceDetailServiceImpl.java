package vn.edu.fpt.pharma.service.impl;

import org.springframework.stereotype.Service;
import vn.edu.fpt.pharma.base.BaseServiceImpl;
import vn.edu.fpt.pharma.entity.InvoiceDetail;
import vn.edu.fpt.pharma.repository.InvoiceDetailRepository;
import vn.edu.fpt.pharma.service.AuditService;
import vn.edu.fpt.pharma.service.InvoiceDetailService;

@Service
public class InvoiceDetailServiceImpl extends BaseServiceImpl<InvoiceDetail, Long, InvoiceDetailRepository> implements InvoiceDetailService {

    public InvoiceDetailServiceImpl(InvoiceDetailRepository repository, AuditService auditService) {
        super(repository, auditService);
    }
}
