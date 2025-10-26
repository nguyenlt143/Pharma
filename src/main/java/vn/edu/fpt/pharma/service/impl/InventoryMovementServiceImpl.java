package vn.edu.fpt.pharma.service.impl;

import org.springframework.stereotype.Service;
import vn.edu.fpt.pharma.base.BaseServiceImpl;
import vn.edu.fpt.pharma.entity.InventoryMovement;
import vn.edu.fpt.pharma.repository.InventoryMovementRepository;
import vn.edu.fpt.pharma.service.AuditService;
import vn.edu.fpt.pharma.service.InventoryMovementService;

@Service
public class InventoryMovementServiceImpl extends BaseServiceImpl<InventoryMovement, Long, InventoryMovementRepository> implements InventoryMovementService {

    public InventoryMovementServiceImpl(InventoryMovementRepository repository, AuditService auditService) {
        super(repository, auditService);
    }
}
