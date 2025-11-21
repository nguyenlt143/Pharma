package vn.edu.fpt.pharma.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.pharma.base.BaseServiceImpl;
import vn.edu.fpt.pharma.dto.inventorycheck.InventoryCheckHistoryVM;
import vn.edu.fpt.pharma.dto.inventorycheck.StockAdjustmentDetailVM;
import vn.edu.fpt.pharma.entity.Inventory;
import vn.edu.fpt.pharma.entity.StockAdjustment;
import vn.edu.fpt.pharma.repository.InventoryRepository;
import vn.edu.fpt.pharma.repository.StockAdjustmentRepository;
import vn.edu.fpt.pharma.service.AuditService;
import vn.edu.fpt.pharma.service.StockAdjustmentService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StockAdjustmentServiceImpl extends BaseServiceImpl<StockAdjustment, Long, StockAdjustmentRepository> implements StockAdjustmentService {

    private final StockAdjustmentRepository stockAdjustmentRepository;
    private final InventoryRepository inventoryRepository;

    public StockAdjustmentServiceImpl(StockAdjustmentRepository repository, AuditService auditService, InventoryRepository inventoryRepository) {
        super(repository, auditService);
        this.stockAdjustmentRepository = repository;
        this.inventoryRepository = inventoryRepository;
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

    @Override
    @Transactional
    public void performInventoryCheck(Long branchId, Long userId, vn.edu.fpt.pharma.dto.inventory.InventoryCheckRequestDTO request) {
        if (request == null || request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("Danh sách kiểm kho trống");
        }
        // Loop through each item and create a stock adjustment
        for (var item : request.getItems()) {
            Inventory inv = inventoryRepository.findById(item.getInventoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Inventory không tồn tại: " + item.getInventoryId()));
            if (inv.getBranch() == null || !inv.getBranch().getId().equals(branchId)) {
                throw new IllegalStateException("Inventory không thuộc chi nhánh");
            }
            Long before = inv.getQuantity() != null ? inv.getQuantity() : 0L;
            Long after = item.getCountedQuantity() != null ? item.getCountedQuantity() : before;
            Long diff = after - before;

            // Update inventory quantity to counted value
            inv.setQuantity(after);
            inventoryRepository.save(inv);

            StockAdjustment adj = StockAdjustment.builder()
                    .BrandId(branchId)
                    .variantId(item.getVariantId())
                    .batch(inv.getBatch())
                    .beforeQuantity(before)
                    .afterQuantity(after)
                    .differenceQuantity(diff)
                    .reason(request.getNote() != null ? request.getNote() : "Inventory check")
                    .build();
            stockAdjustmentRepository.save(adj);
        }
    }
}