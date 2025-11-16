package vn.edu.fpt.pharma.service.impl;

import org.springframework.stereotype.Service;
import vn.edu.fpt.pharma.base.BaseServiceImpl;
import vn.edu.fpt.pharma.dto.inventorycheck.InventoryCheckHistoryVM;
import vn.edu.fpt.pharma.dto.inventorycheck.StockAdjustmentDetailVM;
import vn.edu.fpt.pharma.entity.StockAdjustment;
import vn.edu.fpt.pharma.repository.StockAdjustmentRepository;
import vn.edu.fpt.pharma.service.AuditService;
import vn.edu.fpt.pharma.service.StockAdjustmentService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StockAdjustmentServiceImpl extends BaseServiceImpl<StockAdjustment, Long, StockAdjustmentRepository> implements StockAdjustmentService {

    private final StockAdjustmentRepository stockAdjustmentRepository;

    public StockAdjustmentServiceImpl(StockAdjustmentRepository repository, AuditService auditService) {
        super(repository, auditService);
        this.stockAdjustmentRepository = repository;
    }

    @Override
    public List<InventoryCheckHistoryVM> getInventoryCheckHistory(Long branchId) {
        return stockAdjustmentRepository.findInventoryCheckHistoryByBranch(branchId)
                .stream()
                .map(InventoryCheckHistoryVM::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<StockAdjustmentDetailVM> getInventoryCheckDetails(Long branchId, String checkDate) {
        return stockAdjustmentRepository.findByBranchIdAndCheckDate(branchId, checkDate)
                .stream()
                .map(StockAdjustmentDetailVM::new)
                .collect(Collectors.toList());
    }
}