package vn.edu.fpt.pharma.service.impl;

import org.springframework.stereotype.Service;
import vn.edu.fpt.pharma.base.BaseServiceImpl;
import vn.edu.fpt.pharma.entity.Inventory;
import vn.edu.fpt.pharma.repository.InventoryRepository;
import vn.edu.fpt.pharma.service.AuditService;
import vn.edu.fpt.pharma.service.InventoryService;

@Service
public class InventoryServiceImpl extends BaseServiceImpl<Inventory, Long, InventoryRepository> implements InventoryService {

    public InventoryServiceImpl(InventoryRepository repository, AuditService auditService) {
        super(repository, auditService);
    }
}
