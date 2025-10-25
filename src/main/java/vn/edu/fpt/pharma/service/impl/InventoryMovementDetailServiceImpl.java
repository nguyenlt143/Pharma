package vn.edu.fpt.pharma.service.impl;

import org.springframework.stereotype.Service;
import vn.edu.fpt.pharma.base.BaseServiceImpl;
import vn.edu.fpt.pharma.entity.InventoryMovementDetail;
import vn.edu.fpt.pharma.repository.InventoryMovementDetailRepository;
import vn.edu.fpt.pharma.service.AuditService;
import vn.edu.fpt.pharma.service.InventoryMovementDetailService;

@Service
public class InventoryMovementDetailServiceImpl extends BaseServiceImpl<InventoryMovementDetail, Long, InventoryMovementDetailRepository> implements InventoryMovementDetailService {

    public InventoryMovementDetailServiceImpl(InventoryMovementDetailRepository repository, AuditService auditService) {
        super(repository, auditService);
    }
}
