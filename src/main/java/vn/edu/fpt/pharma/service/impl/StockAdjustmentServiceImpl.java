package vn.edu.fpt.pharma.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.pharma.base.BaseServiceImpl;
import vn.edu.fpt.pharma.constant.MovementStatus;
import vn.edu.fpt.pharma.constant.MovementType;
import vn.edu.fpt.pharma.dto.inventorycheck.InventoryCheckHistoryVM;
import vn.edu.fpt.pharma.dto.inventorycheck.StockAdjustmentDetailVM;
import vn.edu.fpt.pharma.entity.*;
import vn.edu.fpt.pharma.repository.InventoryMovementDetailRepository;
import vn.edu.fpt.pharma.repository.InventoryMovementRepository;
import vn.edu.fpt.pharma.repository.InventoryRepository;
import vn.edu.fpt.pharma.repository.StockAdjustmentRepository;
import vn.edu.fpt.pharma.service.AuditService;
import vn.edu.fpt.pharma.service.StockAdjustmentService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StockAdjustmentServiceImpl extends BaseServiceImpl<StockAdjustment, Long, StockAdjustmentRepository> implements StockAdjustmentService {

    private final StockAdjustmentRepository stockAdjustmentRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryMovementRepository inventoryMovementRepository;
    private final InventoryMovementDetailRepository inventoryMovementDetailRepository;

    public StockAdjustmentServiceImpl(StockAdjustmentRepository repository, AuditService auditService,
                                    InventoryRepository inventoryRepository,
                                    InventoryMovementRepository inventoryMovementRepository,
                                    InventoryMovementDetailRepository inventoryMovementDetailRepository) {
        super(repository, auditService);
        this.stockAdjustmentRepository = repository;
        this.inventoryRepository = inventoryRepository;
        this.inventoryMovementRepository = inventoryMovementRepository;
        this.inventoryMovementDetailRepository = inventoryMovementDetailRepository;
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

        // Tạo InventoryMovement cho phiếu kiểm kho
        InventoryMovement movement = InventoryMovement.builder()
                .movementType(MovementType.INVENTORY_ADJUSTMENT)
                .sourceBranchId(branchId)
                .destinationBranchId(branchId)
                .movementStatus(MovementStatus.CLOSED)
                .totalMoney(0.0)
                .build();
        movement = inventoryMovementRepository.save(movement);

        List<InventoryMovementDetail> details = new ArrayList<>();

        // Loop through each item and create a stock adjustment
        for (var item : request.getItems()) {
            Inventory inv = inventoryRepository.findById(item.getInventoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Inventory không tồn tại: " + item.getInventoryId()));
            if (inv.getBranch() == null || !inv.getBranch().getId().equals(branchId)) {
                throw new IllegalStateException("Inventory không thuộc chi nhánh");
            }

            Long before = inv.getQuantity() != null ? inv.getQuantity() : 0L;
            Long after = item.getCountedQuantity() != null ? item.getCountedQuantity() : 0L;

            // Only check for negative quantity - allow surplus (after > before)
            if (after < 0) {
                throw new IllegalArgumentException("Số lượng kiểm không được âm cho thuốc: " + inv.getVariant().getMedicine().getName());
            }

            // REMOVED: Validation preventing surplus
            // Old code blocked: after > before (surplus detection)
            // New behavior: Allow both surplus (after > before) and shortage (after < before)

            long diff = after - before;

            StockAdjustment adj = StockAdjustment.builder()
                    .BrandId(branchId)
                    .variantId(item.getVariantId())
                    .batch(inv.getBatch())
                    .beforeQuantity(before)
                    .afterQuantity(after)
                    .differenceQuantity(diff)
                    .reason(request.getNote() != null ? request.getNote() : "Kiểm kho")
                    .build();
            adj.setCreatedBy(userId);
            stockAdjustmentRepository.save(adj);

            // Tạo InventoryMovementDetail cho từng thuốc bị điều chỉnh
            if (diff != 0) {
                InventoryMovementDetail detail = InventoryMovementDetail.builder()
                        .movement(movement)
                        .variant(inv.getVariant())
                        .batch(inv.getBatch())
                        // Lưu giá trị có dấu: âm nếu giảm, dương nếu tăng
                        .quantity(diff)
                        .price(0.0)
                        .snapCost(inv.getCostPrice() != null ? inv.getCostPrice() : 0.0)
                        .build();
                details.add(detail);
            }

            // Cập nhật inventory với số lượng mới
            inv.setQuantity(after);
            inventoryRepository.save(inv);
        }

        // Lưu tất cả movement details
        if (!details.isEmpty()) {
            inventoryMovementDetailRepository.saveAll(details);
        }

        // --- Tính lại tổng tiền (totalMoney) cho phiếu kiểm kho ---
        List<InventoryMovementDetail> savedDetails = inventoryMovementDetailRepository.findByMovementId(movement.getId());
        double totalMoney = savedDetails.stream()
                .filter(d -> d.getQuantity() != null && d.getSnapCost() != null)
                .mapToDouble(d -> d.getQuantity() * d.getSnapCost()) // Không dùng Math.abs để cho phép giá trị âm
                .sum();
        movement.setTotalMoney(totalMoney);
        inventoryMovementRepository.save(movement);
    }
}