package vn.edu.fpt.pharma.service.impl;

//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import vn.edu.fpt.pharma.base.BaseServiceImpl;
//import vn.edu.fpt.pharma.constant.MovementStatus;
//import vn.edu.fpt.pharma.constant.MovementType;
//import vn.edu.fpt.pharma.constant.RequestStatus;
//import vn.edu.fpt.pharma.dto.warehouse.InventoryMovementVM;
//import vn.edu.fpt.pharma.entity.*;
//import vn.edu.fpt.pharma.repository.*;
//import vn.edu.fpt.pharma.service.AuditService;
//import vn.edu.fpt.pharma.service.InventoryMovementService;
//
//import java.util.List;
//
//
//@Service
//public class InventoryMovementServiceImpl extends BaseServiceImpl<InventoryMovement, Long, InventoryMovementRepository>
//        implements InventoryMovementService {
//
//    private final InventoryMovementRepository movementRepository;
//    private final RequestFormRepository requestFormRepository;
//    private final RequestDetailRepository requestDetailRepository;
//    private final InventoryMovementDetailRepository movementDetailRepository;
//    private final InventoryRepository inventoryRepository;
//    private final PriceRepository priceRepository;
//    private final BranchRepository branchRepository;
//
//    public InventoryMovementServiceImpl(
//            InventoryMovementRepository repository,
//            AuditService auditService,
//            RequestFormRepository requestFormRepository,
//            RequestDetailRepository requestDetailRepository,
//            InventoryMovementDetailRepository movementDetailRepository,
//            InventoryRepository inventoryRepository,
//            PriceRepository priceRepository,
//            BranchRepository branchRepository
//    ) {
//        super(repository, auditService);
//        this.movementRepository = repository;
//        this.requestFormRepository = requestFormRepository;
//        this.requestDetailRepository = requestDetailRepository;
//        this.movementDetailRepository = movementDetailRepository;
//        this.inventoryRepository = inventoryRepository;
//        this.priceRepository = priceRepository;
//        this.branchRepository = branchRepository;
//    }
//
//    @Override
//    public List<InventoryMovementVM> getAllMovements() {
//        return movementRepository.findAll().stream()
//                .map(InventoryMovementVM::new)
//                .toList();
//    }
//
//    @Override
//    public InventoryMovementVM getMovementById(Long id) {
//        return movementRepository.findById(id)
//                .map(InventoryMovementVM::new)
//                .orElseThrow(() -> new RuntimeException("Movement not found"));
//    }
//
//    @Override
//    @Transactional
//    public void confirmReturnRequest(Long requestFormId) {
//        // Get request form
//        RequestForm requestForm = requestFormRepository.findById(requestFormId)
//                .orElseThrow(() -> new IllegalArgumentException("Request form not found"));
//
//        if (!requestForm.getRequestType().name().equals("RETURN")) {
//            throw new IllegalArgumentException("Request is not a RETURN request");
//        }
//
//        if (!requestForm.getRequestStatus().equals(RequestStatus.REQUESTED)) {
//            throw new IllegalStateException("Request is not in REQUESTED status");
//        }
//
//        // Get request details
//        List<RequestDetail> details = requestDetailRepository.findByRequestFormId(requestFormId);
//        if (details.isEmpty()) {
//            throw new IllegalArgumentException("Request has no details");
//        }
//
//        // Create InventoryMovement
//        InventoryMovement movement = InventoryMovement.builder()
//                .movementType(MovementType.BR_TO_WARE)
//                .sourceBranchId(requestForm.getBranchId())
//                .destinationBranchId(1L) // Warehouse is branch 1
//                .requestForm(requestForm)
//                .movementStatus(MovementStatus.RECEIVED)
//                .totalMoney(0.0)
//                .build();
//
//        movement = movementRepository.save(movement);
//
//        double totalMoney = 0.0;
//
//        // Create InventoryMovementDetails and update inventory
//        for (RequestDetail detail : details) {
//            // Find inventory items in branch
//            List<Inventory> branchInventories = inventoryRepository.findByBranchIdAndVariantId(
//                    requestForm.getBranchId(),
//                    detail.getVariantId()
//            );
//
//            if (branchInventories.isEmpty()) {
//                throw new IllegalStateException("No inventory found for variant " + detail.getVariantId() + " in branch " + requestForm.getBranchId());
//            }
//
//            Long remainingQty = detail.getQuantity();
//
//            for (Inventory branchInv : branchInventories) {
//                if (remainingQty <= 0) break;
//
//                Long qtyToReturn = Math.min(remainingQty, branchInv.getQuantity());
//
//                // Get price
//                Double price = priceRepository.findBranchPriceByVariantId(detail.getVariantId())
//                        .orElse(0.0);
//
//                // Create movement detail
//                InventoryMovementDetail movementDetail = InventoryMovementDetail.builder()
//                        .movement(movement)
//                        .variant(branchInv.getVariant())
//                        .batch(branchInv.getBatch())
//                        .quantity(qtyToReturn)
//                        .price(price)
//                        .snapCost(branchInv.getCostPrice())
//                        .build();
//
//                movementDetailRepository.save(movementDetail);
//
//                totalMoney += qtyToReturn * price;
//
//                // Update branch inventory (decrease)
//                branchInv.setQuantity(branchInv.getQuantity() - qtyToReturn);
//                inventoryRepository.save(branchInv);
//
//                // Update warehouse inventory (increase)
//                Inventory warehouseInv = inventoryRepository
//                        .findByBranchIdAndVariantIdAndBatchId(1L, detail.getVariantId(), branchInv.getBatch().getId())
//                        .orElse(null);
//
//                if (warehouseInv != null) {
//                    warehouseInv.setQuantity(warehouseInv.getQuantity() + qtyToReturn);
//                    inventoryRepository.save(warehouseInv);
//                } else {
//                    // Create new warehouse inventory
//                    Branch warehouseBranch = branchRepository.findById(1L)
//                            .orElseThrow(() -> new IllegalStateException("Warehouse branch not found"));
//
//                    Inventory newWarehouseInv = Inventory.builder()
//                            .branch(warehouseBranch)
//                            .variant(branchInv.getVariant())
//                            .batch(branchInv.getBatch())
//                            .quantity(qtyToReturn)
//                            .costPrice(branchInv.getCostPrice())
//                            .minStock(0L)
//                            .build();
//                    inventoryRepository.save(newWarehouseInv);
//                }
//
//                remainingQty -= qtyToReturn;
//            }
//
//            if (remainingQty > 0) {
//                throw new IllegalStateException("Not enough inventory to return for variant " + detail.getVariantId());
//            }
//        }
//
//        // Update total money
//        movement.setTotalMoney(totalMoney);
//        movementRepository.save(movement);
//
//        // Update request status
//        requestForm.setRequestStatus(RequestStatus.CONFIRMED);
//        requestFormRepository.save(requestForm);
//    }
//}



