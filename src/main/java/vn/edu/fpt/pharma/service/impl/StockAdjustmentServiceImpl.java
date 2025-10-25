package vn.edu.fpt.pharma.service.impl;

import vn.edu.fpt.pharma.base.BaseServiceImpl;
import vn.edu.fpt.pharma.entity.StockAdjustment;
import vn.edu.fpt.pharma.repository.StockAdjustmentRepository;
import vn.edu.fpt.pharma.service.AuditService;
import vn.edu.fpt.pharma.service.StockAdjustmentService;

public class StockAdjustmentServiceImpl extends BaseServiceImpl<StockAdjustment,Long, StockAdjustmentRepository> implements StockAdjustmentService {

    public StockAdjustmentServiceImpl(StockAdjustmentRepository repository, AuditService auditService) {
        super(repository, auditService);
    }
}
