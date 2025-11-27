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

        // Map to ReceiptListItem with branch name and sort by newest first
        return movements.stream()
                .sorted((m1, m2) -> {
                    if (m1.getCreatedAt() == null && m2.getCreatedAt() == null) return 0;
                    if (m1.getCreatedAt() == null) return 1;
                    if (m2.getCreatedAt() == null) return -1;
                    return m2.getCreatedAt().compareTo(m1.getCreatedAt()); // Descending order (newest first)
                })
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
                                String concentration = detail.getVariant().getStrength() != null
                                    ? detail.getVariant().getStrength()
                                    : "N/A";
                                String unit = detail.getVariant().getPackageUnitId() != null
                                    ? detail.getVariant().getPackageUnitId().getName()
                                    : "N/A";
                                Integer quantity = detail.getQuantity() != null
                                    ? detail.getQuantity().intValue()
                                    : 0;

                                // Use 4-parameter constructor for warehouse (backward compatible)
                                return new ReceiptDetailVM(medicineName, concentration, unit, quantity);
                            })
                            .collect(Collectors.toList());
                })
                .orElse(List.of());
    }

    // For inventory role - includes full medicine details
    public List<ReceiptDetailVM> getReceiptDetailsForBranch(Long id) {
        return movementRepository.findByIdWithDetails(id)
                .map(movement -> {
                    if (movement.getInventoryMovementDetails() == null || movement.getInventoryMovementDetails().isEmpty()) {
                        return List.<ReceiptDetailVM>of();
                    }
                    return movement.getInventoryMovementDetails().stream()
                            .filter(detail -> detail.getVariant() != null)
                            .map(detail -> {
                                String medicineName = "N/A";
                                String activeIngredient = "-";
                                String concentration = "N/A";
                                String dosageForm = "N/A";
                                String categoryName = "N/A";
                                String unit = "N/A";
                                Integer quantity = 0;
                                String batchCode = "-";

                                if (detail.getVariant() != null) {
                                    MedicineVariant variant = detail.getVariant();
                                    Medicine medicine = variant.getMedicine();

                                    if (medicine != null) {
                                        medicineName = medicine.getName() != null ? medicine.getName() : "N/A";
                                        if (medicine.getActiveIngredient() != null && !medicine.getActiveIngredient().isBlank()) {
                                            activeIngredient = medicine.getActiveIngredient();
                                        }
                                        if (medicine.getCategory() != null && medicine.getCategory().getName() != null) {
                                            categoryName = medicine.getCategory().getName();
                                        }
                                    }

                                    concentration = variant.getStrength() != null ? variant.getStrength() : "N/A";
                                    dosageForm = variant.getDosage_form() != null ? variant.getDosage_form() : "N/A";

                                    if (variant.getPackageUnitId() != null) {
                                        unit = variant.getPackageUnitId().getName() != null ? variant.getPackageUnitId().getName() : "N/A";
                                    }

                                    quantity = detail.getQuantity() != null ? detail.getQuantity().intValue() : 0;

                                    // Get batch code from this detail
                                    if (detail.getBatch() != null && detail.getBatch().getBatchCode() != null) {
                                        batchCode = detail.getBatch().getBatchCode();
                                    }
                                }

                                return new ReceiptDetailVM(medicineName, activeIngredient, concentration, dosageForm, categoryName, unit, quantity, batchCode);
                            })
                            .collect(Collectors.toList());
                })
                .orElse(List.of());
    }

    @Override
    public void approveReceipt(Long id) {
        InventoryMovement movement = movementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Receipt not found"));

        if (movement.getMovementStatus() == MovementStatus.DRAFT) {
            movement.setMovementStatus(MovementStatus.APPROVED);
            movementRepository.save(movement);
        } else {
            throw new IllegalStateException("Receipt cannot be approved in its current state: " + movement.getMovementStatus());
        }
    }

    @Override
    public void shipReceipt(Long id) {
        InventoryMovement movement = movementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Receipt not found"));

        if (movement.getMovementStatus() == MovementStatus.APPROVED) {
            movement.setMovementStatus(MovementStatus.SHIPPED);
            movementRepository.save(movement);
        } else {
            throw new IllegalStateException("Receipt cannot be shipped in its current state: " + movement.getMovementStatus());
        }
    }

    @Override
    @Transactional
    public void receiveReceipt(Long id) {
        InventoryMovement movement = movementRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Receipt not found"));

        if (movement.getMovementStatus() != MovementStatus.SHIPPED) {
            throw new IllegalStateException("Receipt cannot be received in its current state: " + movement.getMovementStatus());
        }

        // 1. Update status to RECEIVED
        movement.setMovementStatus(MovementStatus.RECEIVED);
        movementRepository.save(movement);
        log.info("Updated movement {} status to RECEIVED", id);

        // 2. Add inventory to destination branch (only for WARE_TO_BR type)
        if (movement.getMovementType() == MovementType.WARE_TO_BR && movement.getDestinationBranchId() != null) {
            Branch destinationBranch = branchRepository.findById(movement.getDestinationBranchId())
                    .orElseThrow(() -> new RuntimeException("Destination branch not found"));

            for (InventoryMovementDetail detail : movement.getInventoryMovementDetails()) {
                // Find or create inventory at destination branch
                Inventory branchInventory = inventoryRepository
                        .findByBranchIdAndVariantIdAndBatchId(
                                movement.getDestinationBranchId(),
                                detail.getVariant().getId(),
                                detail.getBatch().getId()
                        )
                        .orElseGet(() -> {
                            // Create new inventory if not exists
                            Inventory newInventory = Inventory.builder()
                                    .branch(destinationBranch)
                                    .variant(detail.getVariant())
                                    .batch(detail.getBatch())
                                    .quantity(0L)
                                    .costPrice(detail.getSnapCost()) // Use original cost from warehouse
                                    .build();
                            log.info("Created new inventory for branch {} variant {} batch {}",
                                    destinationBranch.getName(),
                                    detail.getVariant().getId(),
                                    detail.getBatch().getBatchCode());
                            return newInventory;
                        });

                // Add quantity to branch inventory
                branchInventory.setQuantity(branchInventory.getQuantity() + detail.getQuantity());
                inventoryRepository.save(branchInventory);

                log.info("Added {} units of variant {} batch {} to branch {} inventory (new total: {})",
                        detail.getQuantity(),
                        detail.getVariant().getId(),
                        detail.getBatch().getBatchCode(),
                        destinationBranch.getName(),
                        branchInventory.getQuantity());
            }

            log.info("Completed inventory addition for movement {} to branch {}",
                    id, destinationBranch.getName());
        }
    }

    @Override
    public void closeReceipt(Long id) {
        InventoryMovement movement = movementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Receipt not found"));

        if (movement.getMovementStatus() == MovementStatus.RECEIVED) {
            movement.setMovementStatus(MovementStatus.CLOSED);
            movementRepository.save(movement);
        } else {
            throw new IllegalStateException("Receipt cannot be closed in its current state: " + movement.getMovementStatus());
        }
    }

    @Override
    public void cancelReceipt(Long id) {
        InventoryMovement movement = movementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Receipt not found"));

        if (movement.getMovementStatus() == MovementStatus.DRAFT) {
            movement.setMovementStatus(MovementStatus.CANCELLED);
            movementRepository.save(movement);
        } else {
            throw new IllegalStateException("Receipt cannot be cancelled in its current state: " + movement.getMovementStatus());
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

        // 5. Create InventoryMovement with SHIPPED status (đã giao)
        InventoryMovement movement = InventoryMovement.builder()
                .movementType(MovementType.WARE_TO_BR)
                .sourceBranchId(warehouseBranch.getId())
                .destinationBranchId(destinationBranch.getId())
                .requestForm(requestForm)
                .movementStatus(MovementStatus.SHIPPED)
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

    @Override
    @Transactional(readOnly = true)
    public List<vn.edu.fpt.pharma.dto.inventory.ConfirmImportVM> getConfirmImportList(Long branchId) {
        // Get all movements WARE_TO_BR that are SHIPPED (waiting for branch to receive)
        // Using JOIN FETCH to avoid LazyInitializationException
        List<InventoryMovement> movements = movementRepository
                .findAllWithDetailsByTypeAndBranchAndStatus(
                        MovementType.WARE_TO_BR,
                        branchId,
                        MovementStatus.SHIPPED
                );

        // Sort by createdAt DESC
        return movements.stream()
                .sorted((m1, m2) -> {
                    if (m1.getCreatedAt() == null) return 1;
                    if (m2.getCreatedAt() == null) return -1;
                    return m2.getCreatedAt().compareTo(m1.getCreatedAt());
                })
                .map(movement -> {
                    Long count = movement.getInventoryMovementDetails() != null
                            ? (long) movement.getInventoryMovementDetails().size()
                            : 0L;
                    return vn.edu.fpt.pharma.dto.inventory.ConfirmImportVM.from(movement, count);
                })
                .toList();
    }

    @Override
    public vn.edu.fpt.pharma.dto.inventory.ConfirmImportVM getConfirmImportDetail(Long id) {
        InventoryMovement movement = movementRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Movement not found"));

        Long count = movement.getInventoryMovementDetails() != null
                ? (long) movement.getInventoryMovementDetails().size()
                : 0L;

        return vn.edu.fpt.pharma.dto.inventory.ConfirmImportVM.from(movement, count);
    }

    @Override
    @Transactional
    public void confirmImportReceipt(Long id, Long branchId) {
        log.info("Branch {} confirming import receipt {}", branchId, id);

        InventoryMovement movement = movementRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Movement not found"));

        if (!movement.getDestinationBranchId().equals(branchId)) {
            throw new IllegalArgumentException("Movement does not belong to this branch");
        }

        if (movement.getMovementType() != MovementType.WARE_TO_BR) {
            throw new IllegalArgumentException("Movement is not WARE_TO_BR type");
        }

        if (movement.getMovementStatus() != MovementStatus.SHIPPED) {
            throw new IllegalStateException("Movement is not in SHIPPED status");
        }

        // Update status to RECEIVED
        movement.setMovementStatus(MovementStatus.RECEIVED);
        movementRepository.save(movement);
        log.info("Updated movement {} status to RECEIVED", id);

        // Add inventory to branch
        Branch destinationBranch = branchRepository.findById(branchId)
                .orElseThrow(() -> new RuntimeException("Branch not found"));

        for (InventoryMovementDetail detail : movement.getInventoryMovementDetails()) {
            // Find or create inventory at branch
            var branchInventory = inventoryRepository
                    .findByBranchIdAndVariantIdAndBatchId(
                            branchId,
                            detail.getVariant().getId(),
                            detail.getBatch().getId()
                    )
                    .orElseGet(() -> {
                        Inventory newInventory = Inventory.builder()
                                .branch(destinationBranch)
                                .variant(detail.getVariant())
                                .batch(detail.getBatch())
                                .quantity(0L)
                                .costPrice(detail.getSnapCost())
                                .minStock(0L)
                                .build();
                        log.info("Created new inventory for branch {} variant {} batch {}",
                                destinationBranch.getName(),
                                detail.getVariant().getId(),
                                detail.getBatch().getBatchCode());
                        return newInventory;
                    });

            // Add quantity
            branchInventory.setQuantity(branchInventory.getQuantity() + detail.getQuantity());
            inventoryRepository.save(branchInventory);

            log.info("Added {} units of variant {} batch {} to branch {} (new total: {})",
                    detail.getQuantity(),
                    detail.getVariant().getId(),
                    detail.getBatch().getBatchCode(),
                    destinationBranch.getName(),
                    branchInventory.getQuantity());
        }

        log.info("Branch {} confirmed import receipt {} successfully", branchId, id);
    }

    @Override
    @Transactional
    public Long createReturnMovement(vn.edu.fpt.pharma.dto.inventory.ReturnRequestDTO dto) {
        log.info("Creating return movement for branch {} with {} items", dto.getBranchId(), dto.getItems().size());

        // 1. Validate branch exists
        Branch sourceBranch = branchRepository.findById(dto.getBranchId())
                .orElseThrow(() -> new RuntimeException("Branch not found: " + dto.getBranchId()));

        // 2. Find warehouse branch (destination)
        Branch warehouseBranch = branchRepository.findAll().stream()
                .filter(b -> b.getBranchType() == BranchType.HEAD_QUARTER)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Warehouse branch not found"));

        // 3. Create InventoryMovement with SHIPPED status (đã gửi)
        InventoryMovement movement = InventoryMovement.builder()
                .movementType(MovementType.BR_TO_WARE)
                .sourceBranchId(sourceBranch.getId())
                .destinationBranchId(warehouseBranch.getId())
                .movementStatus(MovementStatus.SHIPPED)
                .totalMoney(0.0) // Return doesn't have price
                .build();

        // Link movement to request form if provided
        if (dto.getRequestId() != null) {
            RequestForm linked = requestFormRepository.findById(dto.getRequestId()).orElse(null);
            movement.setRequestForm(linked);
        }

        InventoryMovement savedMovement = movementRepository.save(movement);
        log.info("Created return movement with ID: {}, status: SHIPPED", savedMovement.getId());

        // 4. Create InventoryMovementDetails and decrease branch inventory
        for (vn.edu.fpt.pharma.dto.inventory.ReturnRequestDTO.ReturnItemDTO item : dto.getItems()) {
            // Get entities
            MedicineVariant variant = variantRepository.findById(item.getVariantId())
                    .orElseThrow(() -> new RuntimeException("Variant not found: " + item.getVariantId()));

            Batch batch = batchRepository.findById(item.getBatchId())
                    .orElseThrow(() -> new RuntimeException("Batch not found: " + item.getBatchId()));

            Inventory branchInventory = inventoryRepository.findById(item.getInventoryId())
                    .orElseThrow(() -> new RuntimeException("Inventory not found: " + item.getInventoryId()));

            // Validate inventory has enough quantity
            if (branchInventory.getQuantity() < item.getQuantity()) {
                throw new RuntimeException(String.format(
                    "Insufficient inventory: batch %s has %d but requested %d",
                    batch.getBatchCode(), branchInventory.getQuantity(), item.getQuantity()
                ));
            }

            // Get cost price from branch inventory
            Double snapCost = branchInventory.getCostPrice();

            // Create movement detail
            InventoryMovementDetail movementDetail = InventoryMovementDetail.builder()
                    .movement(savedMovement)
                    .variant(variant)
                    .batch(batch)
                    .quantity(item.getQuantity().longValue())
                    .price(0.0)  // Return doesn't have sale price
                    .snapCost(snapCost)
                    .build();

            movementDetailRepository.save(movementDetail);
            log.info("Created return movement detail: variant={}, batch={}, qty={}",
                    variant.getId(), batch.getBatchCode(), item.getQuantity());

            // 5. Decrease branch inventory immediately (trừ tồn kho chi nhánh)
            branchInventory.setQuantity(branchInventory.getQuantity() - item.getQuantity());
            inventoryRepository.save(branchInventory);
            log.info("Decreased branch inventory: {} from {} to {}",
                    batch.getBatchCode(),
                    branchInventory.getQuantity() + item.getQuantity(),
                    branchInventory.getQuantity());
        }

        log.info("Return movement {} created successfully with {} items",
                savedMovement.getId(), dto.getItems().size());

        return savedMovement.getId();
    }
}
