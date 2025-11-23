package vn.edu.fpt.pharma.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.pharma.base.BaseServiceImpl;
import vn.edu.fpt.pharma.constant.BranchType;
import vn.edu.fpt.pharma.constant.MovementStatus;
import vn.edu.fpt.pharma.constant.MovementType;
import vn.edu.fpt.pharma.constant.RequestStatus;
import vn.edu.fpt.pharma.dto.warehouse.ExportSubmitDTO;
import vn.edu.fpt.pharma.dto.warehouse.InventoryMovementVM;
import vn.edu.fpt.pharma.dto.warehouse.ReceiptDetailVM;
import vn.edu.fpt.pharma.dto.warehouse.ReceiptInfo;
import vn.edu.fpt.pharma.dto.warehouse.ReceiptListItem;
import vn.edu.fpt.pharma.entity.*;
import vn.edu.fpt.pharma.repository.*;
import vn.edu.fpt.pharma.service.AuditService;
import vn.edu.fpt.pharma.service.InventoryMovementService;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
public class InventoryMovementServiceImpl extends BaseServiceImpl<InventoryMovement, Long, InventoryMovementRepository>
        implements InventoryMovementService {

    private final InventoryMovementRepository movementRepository;
    private final BranchRepository branchRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryMovementDetailRepository movementDetailRepository;
    private final BatchRepository batchRepository;
    private final MedicineVariantRepository variantRepository;
    private final RequestFormRepository requestFormRepository;

    public InventoryMovementServiceImpl(
            InventoryMovementRepository repository,
            AuditService auditService,
            BranchRepository branchRepository,
            InventoryRepository inventoryRepository,
            InventoryMovementDetailRepository movementDetailRepository,
            BatchRepository batchRepository,
            MedicineVariantRepository variantRepository,
            RequestFormRepository requestFormRepository
    ) {
        super(repository, auditService);
        this.movementRepository = repository;
        this.branchRepository = branchRepository;
        this.inventoryRepository = inventoryRepository;
        this.movementDetailRepository = movementDetailRepository;
        this.batchRepository = batchRepository;
        this.variantRepository = variantRepository;
        this.requestFormRepository = requestFormRepository;
    }

    @Override
    public List<InventoryMovementVM> getAllMovements() {
        return movementRepository.findAll().stream()
                .map(InventoryMovementVM::new)
                .toList();
    }

    @Override
    public InventoryMovementVM getMovementById(Long id) {
        return movementRepository.findById(id)
                .map(InventoryMovementVM::new)
                .orElseThrow(() -> new RuntimeException("Movement not found"));
    }

    @Override
    public List<ReceiptListItem> getReceiptList(MovementType movementType, Long branchId, String status) {
        List<InventoryMovement> movements = movementRepository.findAll();

        // Filter by movement type if specified
        if (movementType != null) {
            movements = movements.stream()
                    .filter(m -> m.getMovementType() == movementType)
                    .collect(Collectors.toList());
        }

        // Filter by branch if specified (source or destination)
        if (branchId != null) {
            movements = movements.stream()
                    .filter(m -> (m.getSourceBranchId() != null && m.getSourceBranchId().equals(branchId)) ||
                            (m.getDestinationBranchId() != null && m.getDestinationBranchId().equals(branchId)))
                    .collect(Collectors.toList());
        }

        // Filter by status if specified
        if (status != null && !status.isEmpty()) {
            try {
                MovementStatus movementStatus = MovementStatus.valueOf(status);
                movements = movements.stream()
                        .filter(m -> m.getMovementStatus() == movementStatus)
                        .collect(Collectors.toList());
            } catch (IllegalArgumentException e) {
                // Invalid status, ignore filter
            }
        }

        // Map to ReceiptListItem with branch name
        return movements.stream()
                .map(movement -> {
                    String branchName = getBranchName(movement);
                    return new ReceiptListItem(movement, branchName);
                })
                .collect(Collectors.toList());
    }

    private String getBranchName(InventoryMovement movement) {
        Long branchId = null;

        // For import (SUP_TO_WARE), show destination
        if (movement.getMovementType() == MovementType.SUP_TO_WARE) {
            branchId = movement.getDestinationBranchId();
        }
        // For export (WARE_TO_BR), show destination branch
        else if (movement.getMovementType() == MovementType.WARE_TO_BR) {
            branchId = movement.getDestinationBranchId();
        }

        if (branchId != null) {
            return branchRepository.findById(branchId)
                    .map(Branch::getName)
                    .orElse("N/A");
        }

        return "N/A";
    }

    @Override
    public ReceiptInfo getReceiptInfo(Long id) {
        return movementRepository.findById(id)
                .map(movement -> new ReceiptInfo(
                        movement.getId(),
                        getBranchName(movement),
                        movement.getMovementType().name(),
                        movement.getMovementStatus().name()
                ))
                .orElseThrow(() -> new RuntimeException("Receipt not found"));
    }

    @Override
    public List<ReceiptDetailVM> getReceiptDetails(Long id) {
        return movementRepository.findByIdWithDetails(id)
                .map(movement -> {
                    if (movement.getInventoryMovementDetails() == null || movement.getInventoryMovementDetails().isEmpty()) {
                        return List.<ReceiptDetailVM>of();
                    }
                    return movement.getInventoryMovementDetails().stream()
                            .filter(detail -> detail.getVariant() != null)
                            .map(detail -> {
                                String medicineName = detail.getVariant().getMedicine() != null
                                    ? detail.getVariant().getMedicine().getName()
                                    : "N/A";
                                String strength = detail.getVariant().getStrength() != null
                                    ? detail.getVariant().getStrength()
                                    : "N/A";
                                String unitName = detail.getVariant().getPackageUnitId() != null
                                    ? detail.getVariant().getPackageUnitId().getName()
                                    : "N/A";
                                Integer quantity = detail.getQuantity() != null
                                    ? detail.getQuantity().intValue()
                                    : 0;

                                return new ReceiptDetailVM(medicineName, strength, unitName, quantity);
                            })
                            .collect(Collectors.toList());
                })
                .orElse(List.of());
    }

    @Override
    public void receiveReceipt(Long id) {
        InventoryMovement movement = movementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Receipt not found"));

        if (movement.getMovementStatus() == MovementStatus.APPROVED) {
            movement.setMovementStatus(MovementStatus.RECEIVED);
            movementRepository.save(movement);
        } else {
            throw new IllegalStateException("Receipt cannot be received in its current state: " + movement.getMovementStatus());
        }
    }

    @Override
    @Transactional
    public Long createExportMovement(ExportSubmitDTO dto) {
        log.info("Creating export movement for branch {} with {} details", dto.getBranchId(), dto.getDetails().size());

        // 1. Validate branch exists
        Branch destinationBranch = branchRepository.findById(dto.getBranchId())
                .orElseThrow(() -> new RuntimeException("Branch not found: " + dto.getBranchId()));

        // 2. Find warehouse branch (source)
        Branch warehouseBranch = branchRepository.findAll().stream()
                .filter(b -> b.getBranchType() == BranchType.HEAD_QUARTER)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Warehouse branch not found"));

        // 3. Get request form if provided
        RequestForm requestForm = null;
        if (dto.getRequestId() != null) {
            requestForm = requestFormRepository.findById(dto.getRequestId())
                    .orElse(null);
        }

        // 4. Calculate total money
        double totalMoney = dto.getDetails().stream()
                .mapToDouble(detail -> detail.getQuantity() * detail.getPrice())
                .sum();

        // 5. Create InventoryMovement with SHIPPED status (đang giao)
        InventoryMovement movement = InventoryMovement.builder()
                .movementType(MovementType.WARE_TO_BR)
                .sourceBranchId(warehouseBranch.getId())
                .destinationBranchId(destinationBranch.getId())
                .requestForm(requestForm)
                .movementStatus(MovementStatus.SHIPPED)  // Đang giao
                .totalMoney(totalMoney)
                .build();

        InventoryMovement savedMovement = movementRepository.save(movement);
        log.info("Created movement with ID: {}, status: SHIPPED", savedMovement.getId());

        // 6. Create InventoryMovementDetails and update warehouse inventory
        for (ExportSubmitDTO.ExportDetailItem detail : dto.getDetails()) {
            // Get entities
            MedicineVariant variant = variantRepository.findById(detail.getVariantId())
                    .orElseThrow(() -> new RuntimeException("Variant not found: " + detail.getVariantId()));

            Batch batch = batchRepository.findById(detail.getBatchId())
                    .orElseThrow(() -> new RuntimeException("Batch not found: " + detail.getBatchId()));

            Inventory warehouseInventory = inventoryRepository.findById(detail.getInventoryId())
                    .orElseThrow(() -> new RuntimeException("Inventory not found: " + detail.getInventoryId()));

            // Validate inventory has enough quantity
            if (warehouseInventory.getQuantity() < detail.getQuantity()) {
                throw new RuntimeException(String.format(
                    "Insufficient inventory: batch %s has %d but requested %d",
                    batch.getBatchCode(), warehouseInventory.getQuantity(), detail.getQuantity()
                ));
            }

            // Get snap_cost from warehouse inventory (original cost)
            Double snapCost = warehouseInventory.getCostPrice();

            // Create movement detail
            InventoryMovementDetail movementDetail = InventoryMovementDetail.builder()
                    .movement(savedMovement)
                    .variant(variant)
                    .batch(batch)
                    .quantity(detail.getQuantity())
                    .price(detail.getPrice())  // Branch price
                    .snapCost(snapCost)        // Original cost for audit
                    .build();

            movementDetailRepository.save(movementDetail);
            log.info("Created movement detail: variant={}, batch={}, qty={}",
                    variant.getId(), batch.getBatchCode(), detail.getQuantity());

            // 7. Decrease warehouse inventory (giảm tồn kho)
            warehouseInventory.setQuantity(warehouseInventory.getQuantity() - detail.getQuantity());
            inventoryRepository.save(warehouseInventory);
            log.info("Decreased warehouse inventory: {} from {} to {}",
                    batch.getBatchCode(),
                    warehouseInventory.getQuantity() + detail.getQuantity(),
                    warehouseInventory.getQuantity());

            // NOTE: Inventory at destination branch will be created when status changes to RECEIVED
            // For now, we just mark as SHIPPED (đang giao)
        }

        // 8. Update request status if exists
        if (requestForm != null) {
            requestForm.setRequestStatus(RequestStatus.RECEIVED);
            requestFormRepository.save(requestForm);
            log.info("Updated request form {} to RECEIVED", requestForm.getId());
        }

        log.info("Export movement {} created successfully with {} items",
                savedMovement.getId(), dto.getDetails().size());

        return savedMovement.getId();
    }
}
