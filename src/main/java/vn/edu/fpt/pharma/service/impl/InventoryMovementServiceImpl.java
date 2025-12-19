package vn.edu.fpt.pharma.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.pharma.base.BaseServiceImpl;
import vn.edu.fpt.pharma.constant.BranchType;
import vn.edu.fpt.pharma.constant.MovementStatus;
import vn.edu.fpt.pharma.constant.MovementType;
import vn.edu.fpt.pharma.dto.common.PageResponse;
import vn.edu.fpt.pharma.dto.warehouse.ExportSubmitDTO;
import vn.edu.fpt.pharma.dto.warehouse.InventoryMovementVM;
import vn.edu.fpt.pharma.dto.warehouse.ReceiptDetailVM;
import vn.edu.fpt.pharma.dto.warehouse.ReceiptInfo;
import vn.edu.fpt.pharma.dto.warehouse.ReceiptListItem;
import vn.edu.fpt.pharma.entity.*;
import vn.edu.fpt.pharma.repository.*;
import vn.edu.fpt.pharma.service.AuditService;
import vn.edu.fpt.pharma.service.InventoryMovementService;

import java.util.Collections;
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
        // For return to warehouse (BR_TO_WARE) or expired goods (BR_TO_WARE2), show source branch
        else if (movement.getMovementType() == MovementType.BR_TO_WARE || 
                 movement.getMovementType() == MovementType.BR_TO_WARE2) {
            branchId = movement.getSourceBranchId();
        }
        // For disposal (DISPOSAL) or inventory adjustment (INVENTORY_ADJUSTMENT), show "Kho Tổng"
        else if (movement.getMovementType() == MovementType.DISPOSAL || 
                 movement.getMovementType() == MovementType.INVENTORY_ADJUSTMENT) {
            return "Kho Tổng";
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

                                // Get batch information
                                String batchCode = detail.getBatch() != null && detail.getBatch().getBatchCode() != null
                                    ? detail.getBatch().getBatchCode()
                                    : "N/A";
                                java.time.LocalDate mfgDate = detail.getBatch() != null
                                    ? detail.getBatch().getMfgDate()
                                    : null;
                                java.time.LocalDate expiryDate = detail.getBatch() != null
                                    ? detail.getBatch().getExpiryDate()
                                    : null;

                                // Get import price
                                Double importPrice = detail.getPrice() != null
                                    ? detail.getPrice()
                                    : 0.0;

                                // Use full constructor with batch info and price
                                return new ReceiptDetailVM(medicineName, concentration, unit, quantity,
                                        batchCode, mfgDate, expiryDate, importPrice);
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

        // 2. Add inventory to destination branch based on movement type
        if (movement.getMovementType() == MovementType.WARE_TO_BR && movement.getDestinationBranchId() != null) {
            // Warehouse to Branch: Add to branch inventory
            addInventoryToBranch(movement);
        } else if ((movement.getMovementType() == MovementType.BR_TO_WARE ||
                    movement.getMovementType() == MovementType.BR_TO_WARE2) &&
                   movement.getDestinationBranchId() != null) {
            // Branch to Warehouse (return): Add to warehouse inventory
            addInventoryToWarehouse(movement);
        }
    }

    private void addInventoryToBranch(InventoryMovement movement) {
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
                movement.getId(), destinationBranch.getName());
    }

    private void addInventoryToWarehouse(InventoryMovement movement) {
        Branch warehouseBranch = branchRepository.findById(movement.getDestinationBranchId())
                .orElseThrow(() -> new RuntimeException("Warehouse branch not found"));

        for (InventoryMovementDetail detail : movement.getInventoryMovementDetails()) {
            // Find or create inventory at warehouse
            Inventory warehouseInventory = inventoryRepository
                    .findByBranchIdAndVariantIdAndBatchId(
                            movement.getDestinationBranchId(),
                            detail.getVariant().getId(),
                            detail.getBatch().getId()
                    )
                    .orElseGet(() -> {
                        // Create new inventory if not exists
                        Inventory newInventory = Inventory.builder()
                                .branch(warehouseBranch)
                                .variant(detail.getVariant())
                                .batch(detail.getBatch())
                                .quantity(0L)
                                .costPrice(detail.getSnapCost())
                                .build();
                        log.info("Created new warehouse inventory for variant {} batch {}",
                                detail.getVariant().getId(),
                                detail.getBatch().getBatchCode());
                        return newInventory;
                    });

            // Add quantity to warehouse inventory (return from branch)
            warehouseInventory.setQuantity(warehouseInventory.getQuantity() + detail.getQuantity());
            inventoryRepository.save(warehouseInventory);

            log.info("Added {} units of variant {} batch {} to warehouse inventory (new total: {}) - returned from branch {}",
                    detail.getQuantity(),
                    detail.getVariant().getId(),
                    detail.getBatch().getBatchCode(),
                    warehouseInventory.getQuantity(),
                    movement.getSourceBranchId());
        }

        log.info("Completed inventory return for movement {} to warehouse from branch {}",
                movement.getId(), movement.getSourceBranchId());
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
    @Transactional
    public void cancelReceipt(Long id) {
        System.out.println("==========================================");
        System.out.println("=== CANCEL RECEIPT START: id=" + id + " ===");
        System.out.println("==========================================");

        InventoryMovement movement = movementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Receipt not found"));

        System.out.println("Movement found:");
        System.out.println("  - ID: " + movement.getId());
        System.out.println("  - Type: " + movement.getMovementType());
        System.out.println("  - Status: " + movement.getMovementStatus());
        System.out.println("  - Source Branch: " + movement.getSourceBranchId());
        System.out.println("  - Dest Branch: " + movement.getDestinationBranchId());

        if (movement.getMovementStatus() == MovementStatus.DRAFT) {
            // If this is a return movement (BR_TO_WARE or BR_TO_WARE2), restore branch inventory
            boolean isReturnMovement = (movement.getMovementType() == MovementType.BR_TO_WARE ||
                                       movement.getMovementType() == MovementType.BR_TO_WARE2);

            System.out.println("Is return movement (BR_TO_WARE/BR_TO_WARE2)? " + isReturnMovement);

            if (isReturnMovement) {
                System.out.println(">>> Calling restoreBranchInventoryFromCancelledReturn...");
                restoreBranchInventoryFromCancelledReturn(movement);
                System.out.println(">>> Finished restoreBranchInventoryFromCancelledReturn");
            }

            movement.setMovementStatus(MovementStatus.CANCELLED);
            movementRepository.save(movement);
            log.info("Cancelled receipt {} with type {}", id, movement.getMovementType());
            System.out.println("Receipt cancelled successfully");
        } else {
            System.out.println("ERROR: Cannot cancel - status is " + movement.getMovementStatus() + " (expected DRAFT)");
            throw new IllegalStateException("Receipt cannot be cancelled in its current state: " + movement.getMovementStatus());
        }

        System.out.println("==========================================");
        System.out.println("=== CANCEL RECEIPT END: id=" + id + " ===");
        System.out.println("==========================================");
    }

    private void restoreBranchInventoryFromCancelledReturn(InventoryMovement movement) {
        System.out.println("  >>> restoreBranchInventoryFromCancelledReturn START");
        System.out.println("  >>> Movement ID: " + movement.getId());

        if (movement.getSourceBranchId() == null) {
            System.out.println("  >>> ERROR: No source branch ID!");
            log.warn("Cannot restore inventory - movement {} has no source branch", movement.getId());
            return;
        }

        System.out.println("  >>> Source branch ID: " + movement.getSourceBranchId());

        Branch sourceBranch = branchRepository.findById(movement.getSourceBranchId())
                .orElseThrow(() -> new RuntimeException("Source branch not found: " + movement.getSourceBranchId()));

        System.out.println("  >>> Source branch found: " + sourceBranch.getName() + " (id=" + sourceBranch.getId() + ")");

        // Load movement details
        System.out.println("  >>> Loading movement details...");
        List<InventoryMovementDetail> details = movementDetailRepository
                .findByMovementId(movement.getId());

        System.out.println("  >>> Found " + details.size() + " movement details");

        if (details.isEmpty()) {
            System.out.println("  >>> ERROR: No movement details found!");
            log.warn("No movement details found for movement {}", movement.getId());
            return;
        }

        log.info("Restoring inventory to branch {} for {} cancelled return items",
                sourceBranch.getName(), details.size());
        System.out.println("  >>> Starting to restore " + details.size() + " items...");

        for (int i = 0; i < details.size(); i++) {
            InventoryMovementDetail detail = details.get(i);
            System.out.println("  >>> Processing item " + (i+1) + "/" + details.size());
            System.out.println("      - Variant ID: " + detail.getVariant().getId());
            System.out.println("      - Batch ID: " + detail.getBatch().getId());
            System.out.println("      - Batch Code: " + detail.getBatch().getBatchCode());
            System.out.println("      - Quantity to restore: " + detail.getQuantity());

            // Find or recreate inventory at source branch
            System.out.println("      - Looking for inventory: branchId=" + movement.getSourceBranchId()
                    + ", variantId=" + detail.getVariant().getId()
                    + ", batchId=" + detail.getBatch().getId());

            Inventory branchInventory = inventoryRepository
                    .findByBranchIdAndVariantIdAndBatchId(
                            movement.getSourceBranchId(),
                            detail.getVariant().getId(),
                            detail.getBatch().getId()
                    )
                    .orElseGet(() -> {
                        // Recreate inventory record if it was deleted (e.g., expired items)
                        System.out.println("      - Inventory NOT FOUND! Creating new record...");
                        Inventory newInventory = Inventory.builder()
                                .branch(sourceBranch)
                                .variant(detail.getVariant())
                                .batch(detail.getBatch())
                                .quantity(0L)
                                .costPrice(detail.getSnapCost())
                                .build();
                        log.info("Recreated inventory record for branch {} variant {} batch {} (was deleted)",
                                sourceBranch.getName(),
                                detail.getVariant().getId(),
                                detail.getBatch().getBatchCode());
                        Inventory saved = inventoryRepository.save(newInventory);
                        System.out.println("      - Created new inventory with ID: " + saved.getId());
                        return saved;
                    });

            System.out.println("      - Found inventory ID: " + branchInventory.getId());
            System.out.println("      - Current quantity: " + branchInventory.getQuantity());

            // Restore the quantity that was deducted during return creation
            long previousQty = branchInventory.getQuantity();
            long newQty = previousQty + detail.getQuantity();
            branchInventory.setQuantity(newQty);
            inventoryRepository.save(branchInventory);

            System.out.println("      - ✓ SAVED! New quantity: " + newQty + " (was " + previousQty + ", added " + detail.getQuantity() + ")");

            log.info("Restored {} units of variant {} batch {} to branch {} inventory (from {} to {})",
                    detail.getQuantity(),
                    detail.getVariant().getId(),
                    detail.getBatch().getBatchCode(),
                    sourceBranch.getName(),
                    previousQty,
                    branchInventory.getQuantity());
        }

        log.info("Completed inventory restoration for cancelled return movement {} to branch {}",
                movement.getId(), sourceBranch.getName());
        System.out.println("  >>> restoreBranchInventoryFromCancelledReturn COMPLETED");
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

        // 8. Update request status to RECEIVED when export slip is created successfully
        if (requestForm != null && requestForm.getRequestStatus() == vn.edu.fpt.pharma.constant.RequestStatus.CONFIRMED) {
            requestForm.setRequestStatus(vn.edu.fpt.pharma.constant.RequestStatus.RECEIVED);
            requestFormRepository.save(requestForm);
            log.info("Updated request form {} to RECEIVED after creating export slip", requestForm.getId());
        }

        log.info("Export movement {} created successfully with {} items",
                savedMovement.getId(), dto.getDetails().size());

        return savedMovement.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public List<vn.edu.fpt.pharma.dto.inventory.ConfirmImportVM> getConfirmImportList(Long branchId) {
        // Find warehouse branch (HEAD_QUARTER)
        Long warehouseId = branchRepository.findAll().stream()
                .filter(b -> b.getBranchType() == BranchType.HEAD_QUARTER)
                .map(Branch::getId)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Warehouse branch not found"));

        // Get all movements WARE_TO_BR (không filter status ở đây, sẽ filter sau)
        // Query tất cả để lấy cả SHIPPED và RECEIVED
        List<InventoryMovement> movements = movementRepository.findAll()
                .stream()
                .filter(m -> m.getMovementType() == MovementType.WARE_TO_BR) // WARE_TO_BR
                .filter(m -> m.getSourceBranchId() != null && m.getSourceBranchId().equals(warehouseId)) // Warehouse
                .filter(m -> m.getDestinationBranchId() != null && m.getDestinationBranchId().equals(branchId)) // Chi nhánh hiện tại
                .filter(m -> m.getMovementStatus() == MovementStatus.SHIPPED || m.getMovementStatus() == MovementStatus.RECEIVED) // SHIPPED hoặc RECEIVED
                .collect(Collectors.toList());

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

        // 3. Create InventoryMovement with DRAFT status (chờ warehouse duyệt)
        // Determine movement type from DTO or default to BR_TO_WARE
        MovementType movementType;
        try {
            movementType = dto.getMovementType() != null ? 
                MovementType.valueOf(dto.getMovementType()) : 
                MovementType.BR_TO_WARE;
        } catch (IllegalArgumentException e) {
            log.warn("Invalid movement type: {}, using BR_TO_WARE", dto.getMovementType());
            movementType = MovementType.BR_TO_WARE;
        }

        InventoryMovement movement = InventoryMovement.builder()
                .movementType(movementType)
                .sourceBranchId(sourceBranch.getId())
                .destinationBranchId(warehouseBranch.getId())
                .movementStatus(MovementStatus.DRAFT)
                .totalMoney(0.0) // Return doesn't have price
                .build();

        // Link movement to request form if provided
        if (dto.getRequestId() != null) {
            RequestForm linked = requestFormRepository.findById(dto.getRequestId()).orElse(null);
            movement.setRequestForm(linked);
        }

        InventoryMovement savedMovement = movementRepository.save(movement);
        log.info("Created return movement with ID: {}, status: DRAFT", savedMovement.getId());

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
            long previousQty = branchInventory.getQuantity();
            branchInventory.setQuantity(previousQty - item.getQuantity());
            inventoryRepository.save(branchInventory);
            log.info("Decreased branch inventory: {} from {} to {}",
                    batch.getBatchCode(), previousQty, branchInventory.getQuantity());

            // 6. If batch is expired as of today, remove from branch inventory entirely
            if (batch.getExpiryDate() != null) {
                java.time.LocalDate expired = batch.getExpiryDate();
                java.time.LocalDate today = java.time.LocalDate.now();
                if (!expired.isAfter(today)) { // expired on or before today
                    try {
                        Long invId = branchInventory.getId();
                        inventoryRepository.delete(branchInventory);
                        log.warn("Deleted expired inventory entry id={} for branch {} variant {} batch {} (expired on {})",
                                invId,
                                sourceBranch.getName(),
                                variant.getId(),
                                batch.getBatchCode(),
                                expired);
                    } catch (Exception ex) {
                        log.error("Failed to delete expired inventory entry id={}: {}", branchInventory.getId(), ex.getMessage());
                        // As a fallback, ensure quantity is zero
                        branchInventory.setQuantity(0L);
                        inventoryRepository.save(branchInventory);
                    }
                }
            }
        }

        log.info("Return movement {} created successfully with {} items",
                savedMovement.getId(), dto.getItems().size());

        // --- Tính lại tổng tiền (totalMoney) cho phiếu trả hàng ---
        List<InventoryMovementDetail> details = movementDetailRepository.findByMovementId(savedMovement.getId());
        double totalMoney = details.stream()
                .filter(d -> d.getQuantity() != null && d.getSnapCost() != null)
                .mapToDouble(d -> d.getQuantity() * d.getSnapCost())
                .sum();
        savedMovement.setTotalMoney(totalMoney);
        movementRepository.save(savedMovement);

        return savedMovement.getId();
    }

    @Override
    @Transactional
    public Long createDisposalMovement(vn.edu.fpt.pharma.dto.warehouse.DisposalRequestDTO dto) {
        log.info("Creating disposal movement with {} items", dto.getItems().size());

        // 1. Source: Warehouse (branch_id = 1)
        Long warehouseBranchId = 1L;
        Branch warehouseBranch = branchRepository.findById(warehouseBranchId)
                .orElseThrow(() -> new RuntimeException("Warehouse branch not found"));

        // 2. Destination: Disposal warehouse (branch_id = 2)
        Long disposalBranchId = 2L;
        Branch disposalBranch = branchRepository.findById(disposalBranchId)
                .orElseThrow(() -> new RuntimeException("Disposal warehouse not found"));

        // 3. Create InventoryMovement with DISPOSAL type and CLOSED status
        InventoryMovement movement = InventoryMovement.builder()
                .movementType(MovementType.DISPOSAL)
                .sourceBranchId(warehouseBranch.getId())
                .destinationBranchId(disposalBranch.getId())
                .movementStatus(MovementStatus.CLOSED)
                .totalMoney(0.0)
                .build();

        InventoryMovement savedMovement = movementRepository.save(movement);
        log.info("Created disposal movement with ID: {}, status: CLOSED", savedMovement.getId());

        // 4. Create InventoryMovementDetails and update inventories
        for (vn.edu.fpt.pharma.dto.warehouse.DisposalRequestDTO.DisposalItemDTO item : dto.getItems()) {
            // Get entities
            MedicineVariant variant = variantRepository.findById(item.getVariantId())
                    .orElseThrow(() -> new RuntimeException("Variant not found: " + item.getVariantId()));

            Batch batch = batchRepository.findById(item.getBatchId())
                    .orElseThrow(() -> new RuntimeException("Batch not found: " + item.getBatchId()));

            Inventory warehouseInventory = inventoryRepository.findById(item.getInventoryId())
                    .orElseThrow(() -> new RuntimeException("Inventory not found: " + item.getInventoryId()));

            // Validate warehouse inventory has enough quantity
            if (warehouseInventory.getQuantity() < item.getQuantity()) {
                throw new RuntimeException(String.format(
                    "Insufficient warehouse inventory: batch %s has %d but requested %d",
                    batch.getBatchCode(), warehouseInventory.getQuantity(), item.getQuantity()
                ));
            }

            // Get cost price from warehouse inventory
            Double snapCost = warehouseInventory.getCostPrice();

            // Create movement detail
            InventoryMovementDetail movementDetail = InventoryMovementDetail.builder()
                    .movement(savedMovement)
                    .variant(variant)
                    .batch(batch)
                    .quantity(item.getQuantity().longValue())
                    .price(0.0)
                    .snapCost(snapCost)
                    .build();

            movementDetailRepository.save(movementDetail);
            log.info("Created disposal movement detail: variant={}, batch={}, qty={}",
                    variant.getId(), batch.getBatchCode(), item.getQuantity());

            // 5. Decrease warehouse inventory
            warehouseInventory.setQuantity(warehouseInventory.getQuantity() - item.getQuantity());
            inventoryRepository.save(warehouseInventory);
            log.info("Decreased warehouse inventory: {} from {} to {}",
                    batch.getBatchCode(),
                    warehouseInventory.getQuantity() + item.getQuantity(),
                    warehouseInventory.getQuantity());

            // 6. Increase disposal warehouse inventory (or create if not exists)
            Inventory disposalInventory = inventoryRepository.findByBranchIdAndVariantIdAndBatchId(
                    disposalBranch.getId(), variant.getId(), batch.getId()
            ).orElse(null);

            if (disposalInventory == null) {
                // Create new inventory in disposal warehouse
                disposalInventory = Inventory.builder()
                        .batch(batch)
                        .branch(disposalBranch)
                        .quantity(item.getQuantity().longValue())
                        .costPrice(snapCost)
                        .build();
                log.info("Created new disposal inventory for batch {}", batch.getBatchCode());
            } else {
                // Update existing inventory
                disposalInventory.setQuantity(disposalInventory.getQuantity() + item.getQuantity());
                log.info("Increased disposal inventory: {} from {} to {}",
                        batch.getBatchCode(),
                        disposalInventory.getQuantity() - item.getQuantity(),
                        disposalInventory.getQuantity());
            }
            inventoryRepository.save(disposalInventory);
        }

        log.info("Disposal movement {} created successfully with {} items",
                savedMovement.getId(), dto.getItems().size());

        return savedMovement.getId();
    }

    @Override
    public PageResponse<ReceiptListItem> getReceiptListPaginated(MovementType movementType, Long branchId, String status, int page, int size) {
        List<ReceiptListItem> allReceipts = getReceiptList(movementType, branchId, status);

        long totalElements = allReceipts.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);

        // Calculate start and end indices
        int start = page * size;
        int end = Math.min(start + size, allReceipts.size());

        // Get the sublist for current page
        List<ReceiptListItem> pageContent = (start < allReceipts.size())
            ? allReceipts.subList(start, end)
            : Collections.emptyList();

        return PageResponse.of(pageContent, totalElements, page, size);
    }
}
