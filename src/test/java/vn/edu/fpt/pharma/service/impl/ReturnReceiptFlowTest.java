package vn.edu.fpt.pharma.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import vn.edu.fpt.pharma.BaseServiceTest;
import vn.edu.fpt.pharma.constant.*;
import vn.edu.fpt.pharma.entity.*;
import vn.edu.fpt.pharma.repository.*;
import vn.edu.fpt.pharma.service.AuditService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Integration tests for Return Receipt Flow
 * Tests the complete flow: Create → Confirm → Receive
 */
@DisplayName("Return Receipt Flow Tests")
@org.mockito.junit.jupiter.MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
class ReturnReceiptFlowTest extends BaseServiceTest {

    @Mock
    private RequestFormRepository requestFormRepository;

    @Mock
    private InventoryMovementRepository movementRepository;

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private BranchRepository branchRepository;

    @Mock
    private RequestDetailRepository requestDetailRepository;

    @Mock
    private MedicineVariantRepository medicineVariantRepository;

    @Mock
    private PriceRepository priceRepository;

    @Mock
    private InventoryMovementDetailRepository movementDetailRepository;

    @Mock
    private BatchRepository batchRepository;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private RequestFormServiceImpl requestFormService;

    @InjectMocks
    private InventoryMovementServiceImpl inventoryMovementService;

    private RequestForm returnRequest;
    private InventoryMovement returnReceipt;
    private Branch branch;
    private Branch warehouse;

    @BeforeEach
    void setUp() {
        // Setup branch
        branch = new Branch();
        branch.setId(1L);
        branch.setName("Chi nhánh 1");
        branch.setBranchType(BranchType.BRANCH);

        // Setup warehouse
        warehouse = new Branch();
        warehouse.setId(2L);
        warehouse.setName("Kho Tổng");
        warehouse.setBranchType(BranchType.HEAD_QUARTER);

        // Setup return request
        returnRequest = new RequestForm();
        returnRequest.setId(1L);
        returnRequest.setBranchId(1L);
        returnRequest.setRequestType(RequestType.RETURN);
        returnRequest.setRequestStatus(RequestStatus.REQUESTED);

        // Setup return receipt
        returnReceipt = new InventoryMovement();
        returnReceipt.setId(1L);
        returnReceipt.setMovementType(MovementType.BR_TO_WARE);
        returnReceipt.setSourceBranchId(1L);
        returnReceipt.setDestinationBranchId(2L);
        returnReceipt.setRequestForm(returnRequest);
        returnReceipt.setMovementStatus(MovementStatus.DRAFT);
        returnReceipt.setInventoryMovementDetails(new ArrayList<>());
    }

    @Test
    @DisplayName("TC1: Confirm return request should update receipt status from DRAFT to SHIPPED")
    void confirmReturnRequest_shouldUpdateReceiptToShipped() {
        // Arrange
        when(requestFormRepository.findById(1L)).thenReturn(Optional.of(returnRequest));
        when(requestFormRepository.save(any(RequestForm.class))).thenReturn(returnRequest);

        List<InventoryMovement> movements = List.of(returnReceipt);
        when(movementRepository.findAll()).thenReturn(movements);
        when(movementRepository.save(any(InventoryMovement.class))).thenReturn(returnReceipt);

        // Act
        requestFormService.confirmRequest(1L);

        // Assert
        verify(requestFormRepository).save(argThat(req ->
            req.getRequestStatus() == RequestStatus.CONFIRMED
        ));

        verify(movementRepository).save(argThat(mvt ->
            mvt.getMovementStatus() == MovementStatus.SHIPPED
        ));
    }

    @Test
    @DisplayName("TC2: Cancel return request should cancel corresponding receipt")
    void cancelReturnRequest_shouldCancelReceipt() {
        // Arrange
        when(requestFormRepository.findById(1L)).thenReturn(Optional.of(returnRequest));
        when(requestFormRepository.save(any(RequestForm.class))).thenReturn(returnRequest);

        List<InventoryMovement> movements = List.of(returnReceipt);
        when(movementRepository.findAll()).thenReturn(movements);
        when(movementRepository.save(any(InventoryMovement.class))).thenReturn(returnReceipt);

        // Act
        requestFormService.cancelRequest(1L);

        // Assert
        verify(requestFormRepository).save(argThat(req ->
            req.getRequestStatus() == RequestStatus.CANCELLED
        ));

        verify(movementRepository).save(argThat(mvt ->
            mvt.getMovementStatus() == MovementStatus.CANCELLED
        ));
    }

    @Test
    @DisplayName("TC3: Receive BR_TO_WARE receipt should add inventory to warehouse")
    void receiveReturnReceipt_shouldAddInventoryToWarehouse() {
        // Arrange
        returnReceipt.setMovementStatus(MovementStatus.SHIPPED);

        MedicineVariant variant = new MedicineVariant();
        variant.setId(1L);

        Batch batch = new Batch();
        batch.setId(1L);
        batch.setBatchCode("BATCH001");

        InventoryMovementDetail detail = new InventoryMovementDetail();
        detail.setId(1L);
        detail.setVariant(variant);
        detail.setBatch(batch);
        detail.setQuantity(10L);
        detail.setSnapCost(50.0);

        returnReceipt.setInventoryMovementDetails(List.of(detail));

        Inventory warehouseInventory = new Inventory();
        warehouseInventory.setId(1L);
        warehouseInventory.setBranch(warehouse);
        warehouseInventory.setVariant(variant);
        warehouseInventory.setBatch(batch);
        warehouseInventory.setQuantity(100L);
        warehouseInventory.setCostPrice(50.0);

        when(movementRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(returnReceipt));
        when(movementRepository.save(any(InventoryMovement.class))).thenReturn(returnReceipt);
        when(branchRepository.findById(2L)).thenReturn(Optional.of(warehouse));
        when(inventoryRepository.findByBranchIdAndVariantIdAndBatchId(2L, 1L, 1L))
            .thenReturn(Optional.of(warehouseInventory));
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(warehouseInventory);

        // Act
        inventoryMovementService.receiveReceipt(1L);

        // Assert
        verify(movementRepository).save(argThat(mvt ->
            mvt.getMovementStatus() == MovementStatus.RECEIVED
        ));

        verify(inventoryRepository).save(argThat(inv ->
            inv.getQuantity() == 110L // 100 + 10
        ));
    }

    @Test
    @DisplayName("TC4: Confirm should not update non-return requests")
    void confirmImportRequest_shouldNotUpdateReceipt() {
        // Arrange
        returnRequest.setRequestType(RequestType.IMPORT); // Not a return request

        when(requestFormRepository.findById(1L)).thenReturn(Optional.of(returnRequest));
        when(requestFormRepository.save(any(RequestForm.class))).thenReturn(returnRequest);

        // Act
        requestFormService.confirmRequest(1L);

        // Assert
        verify(requestFormRepository).save(argThat(req ->
            req.getRequestStatus() == RequestStatus.CONFIRMED
        ));

        // Should NOT update any movement
        verify(movementRepository, never()).save(any(InventoryMovement.class));
    }

    @Test
    @DisplayName("TC5: Confirm should handle missing receipt gracefully")
    void confirmReturnRequest_withNoReceipt_shouldLogWarning() {
        // Arrange
        when(requestFormRepository.findById(1L)).thenReturn(Optional.of(returnRequest));
        when(requestFormRepository.save(any(RequestForm.class))).thenReturn(returnRequest);
        when(movementRepository.findAll()).thenReturn(List.of()); // No receipts

        // Act & Assert - Should not throw exception
        assertThatCode(() -> requestFormService.confirmRequest(1L))
            .doesNotThrowAnyException();

        verify(requestFormRepository).save(argThat(req ->
            req.getRequestStatus() == RequestStatus.CONFIRMED
        ));
    }

    @Test
    @DisplayName("TC6: Should not receive receipt that is not in SHIPPED status")
    void receiveReceipt_notShipped_shouldThrowException() {
        // Arrange
        returnReceipt.setMovementStatus(MovementStatus.DRAFT); // Not SHIPPED

        when(movementRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(returnReceipt));

        // Act & Assert
        assertThatThrownBy(() -> inventoryMovementService.receiveReceipt(1L))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("cannot be received");
    }

    @Test
    @DisplayName("TC7: BR_TO_WARE2 (expired) should also work for receive")
    void receiveExpiredReturnReceipt_shouldAddInventoryToWarehouse() {
        // Arrange
        returnReceipt.setMovementType(MovementType.BR_TO_WARE2); // Expired goods
        returnReceipt.setMovementStatus(MovementStatus.SHIPPED);

        MedicineVariant variant = new MedicineVariant();
        variant.setId(1L);

        Batch batch = new Batch();
        batch.setId(1L);
        batch.setBatchCode("BATCH002");

        InventoryMovementDetail detail = new InventoryMovementDetail();
        detail.setId(1L);
        detail.setVariant(variant);
        detail.setBatch(batch);
        detail.setQuantity(5L);
        detail.setSnapCost(30.0);

        returnReceipt.setInventoryMovementDetails(List.of(detail));

        when(movementRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(returnReceipt));
        when(movementRepository.save(any(InventoryMovement.class))).thenReturn(returnReceipt);
        when(branchRepository.findById(2L)).thenReturn(Optional.of(warehouse));
        when(inventoryRepository.findByBranchIdAndVariantIdAndBatchId(2L, 1L, 1L))
            .thenReturn(Optional.empty());
        when(inventoryRepository.save(any(Inventory.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        inventoryMovementService.receiveReceipt(1L);

        // Assert
        verify(movementRepository).save(argThat(mvt ->
            mvt.getMovementStatus() == MovementStatus.RECEIVED
        ));

        // Should create new inventory entry
        verify(inventoryRepository).save(argThat(inv ->
            inv.getQuantity() == 5L && inv.getBatch().getBatchCode().equals("BATCH002")
        ));
    }

    @Test
    @DisplayName("TC8: Cancel BR_TO_WARE should restore branch inventory")
    void cancelReturnReceipt_shouldRestoreBranchInventory() {
        // Arrange
        MedicineVariant variant = new MedicineVariant();
        variant.setId(1L);

        Batch batch = new Batch();
        batch.setId(1L);
        batch.setBatchCode("BATCH001");

        InventoryMovementDetail detail = new InventoryMovementDetail();
        detail.setId(1L);
        detail.setVariant(variant);
        detail.setBatch(batch);
        detail.setQuantity(10L);
        detail.setSnapCost(50.0);
        detail.setMovement(returnReceipt);

        returnReceipt.setInventoryMovementDetails(List.of(detail));

        // Branch inventory after return was created (already decreased)
        Inventory branchInventory = new Inventory();
        branchInventory.setId(1L);
        branchInventory.setBranch(branch);
        branchInventory.setVariant(variant);
        branchInventory.setBatch(batch);
        branchInventory.setQuantity(90L); // Was 100, decreased by 10
        branchInventory.setCostPrice(50.0);

        when(movementRepository.findById(1L)).thenReturn(Optional.of(returnReceipt));
        when(movementRepository.save(any(InventoryMovement.class))).thenReturn(returnReceipt);
        when(branchRepository.findById(1L)).thenReturn(Optional.of(branch));
        when(movementDetailRepository.findByMovementId(1L)).thenReturn(List.of(detail));
        when(inventoryRepository.findByBranchIdAndVariantIdAndBatchId(1L, 1L, 1L))
            .thenReturn(Optional.of(branchInventory));
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(branchInventory);

        // Act
        inventoryMovementService.cancelReceipt(1L);

        // Assert
        verify(movementRepository).save(argThat(mvt ->
            mvt.getMovementStatus() == MovementStatus.CANCELLED
        ));

        // Should restore the 10 units back to branch (90 + 10 = 100)
        verify(inventoryRepository).save(argThat(inv ->
            inv.getQuantity() == 100L
        ));
    }

    @Test
    @DisplayName("TC9: Cancel BR_TO_WARE2 (expired) should restore branch inventory")
    void cancelExpiredReturnReceipt_shouldRestoreBranchInventory() {
        // Arrange
        returnReceipt.setMovementType(MovementType.BR_TO_WARE2);

        MedicineVariant variant = new MedicineVariant();
        variant.setId(1L);

        Batch batch = new Batch();
        batch.setId(1L);
        batch.setBatchCode("BATCH_EXPIRED");

        InventoryMovementDetail detail = new InventoryMovementDetail();
        detail.setId(1L);
        detail.setVariant(variant);
        detail.setBatch(batch);
        detail.setQuantity(5L);
        detail.setSnapCost(30.0);
        detail.setMovement(returnReceipt);

        // Branch inventory after return was created (already decreased)
        Inventory branchInventory = new Inventory();
        branchInventory.setId(1L);
        branchInventory.setBranch(branch);
        branchInventory.setVariant(variant);
        branchInventory.setBatch(batch);
        branchInventory.setQuantity(15L); // Was 20, decreased by 5
        branchInventory.setCostPrice(30.0);

        when(movementRepository.findById(1L)).thenReturn(Optional.of(returnReceipt));
        when(movementRepository.save(any(InventoryMovement.class))).thenReturn(returnReceipt);
        when(branchRepository.findById(1L)).thenReturn(Optional.of(branch));
        when(movementDetailRepository.findByMovementId(1L)).thenReturn(List.of(detail));
        when(inventoryRepository.findByBranchIdAndVariantIdAndBatchId(1L, 1L, 1L))
            .thenReturn(Optional.of(branchInventory));
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(branchInventory);

        // Act
        inventoryMovementService.cancelReceipt(1L);

        // Assert
        verify(movementRepository).save(argThat(mvt ->
            mvt.getMovementStatus() == MovementStatus.CANCELLED
        ));

        // Should restore the 5 units back to branch (15 + 5 = 20)
        verify(inventoryRepository).save(argThat(inv ->
            inv.getQuantity() == 20L
        ));
    }

    @Test
    @DisplayName("TC10: Cancel return should recreate deleted inventory record")
    void cancelReturnReceipt_withDeletedInventory_shouldRecreateAndRestore() {
        // Arrange - Simulating case where expired inventory was deleted during return creation
        MedicineVariant variant = new MedicineVariant();
        variant.setId(1L);

        Batch batch = new Batch();
        batch.setId(1L);
        batch.setBatchCode("BATCH_DELETED");

        InventoryMovementDetail detail = new InventoryMovementDetail();
        detail.setId(1L);
        detail.setVariant(variant);
        detail.setBatch(batch);
        detail.setQuantity(8L);
        detail.setSnapCost(40.0);
        detail.setMovement(returnReceipt);

        when(movementRepository.findById(1L)).thenReturn(Optional.of(returnReceipt));
        when(movementRepository.save(any(InventoryMovement.class))).thenReturn(returnReceipt);
        when(branchRepository.findById(1L)).thenReturn(Optional.of(branch));
        when(movementDetailRepository.findByMovementId(1L)).thenReturn(List.of(detail));
        when(inventoryRepository.findByBranchIdAndVariantIdAndBatchId(1L, 1L, 1L))
            .thenReturn(Optional.empty()); // Inventory was deleted
        when(inventoryRepository.save(any(Inventory.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        inventoryMovementService.cancelReceipt(1L);

        // Assert
        verify(movementRepository).save(argThat(mvt ->
            mvt.getMovementStatus() == MovementStatus.CANCELLED
        ));

        // Should recreate inventory with restored quantity (0 + 8 = 8)
        verify(inventoryRepository, atLeast(1)).save(argThat(inv ->
            inv.getBranch().getId().equals(1L) &&
            inv.getVariant().getId().equals(1L) &&
            inv.getBatch().getBatchCode().equals("BATCH_DELETED") &&
            inv.getQuantity() == 8L &&
            inv.getCostPrice() == 40.0
        ));
    }

    @Test
    @DisplayName("TC11: Cancel return with multiple items should restore all")
    void cancelReturnReceipt_withMultipleItems_shouldRestoreAll() {
        // Arrange
        MedicineVariant variant1 = new MedicineVariant();
        variant1.setId(1L);
        MedicineVariant variant2 = new MedicineVariant();
        variant2.setId(2L);

        Batch batch1 = new Batch();
        batch1.setId(1L);
        batch1.setBatchCode("BATCH001");
        Batch batch2 = new Batch();
        batch2.setId(2L);
        batch2.setBatchCode("BATCH002");

        InventoryMovementDetail detail1 = new InventoryMovementDetail();
        detail1.setId(1L);
        detail1.setVariant(variant1);
        detail1.setBatch(batch1);
        detail1.setQuantity(10L);
        detail1.setSnapCost(50.0);

        InventoryMovementDetail detail2 = new InventoryMovementDetail();
        detail2.setId(2L);
        detail2.setVariant(variant2);
        detail2.setBatch(batch2);
        detail2.setQuantity(5L);
        detail2.setSnapCost(30.0);

        Inventory inventory1 = new Inventory();
        inventory1.setId(1L);
        inventory1.setBranch(branch);
        inventory1.setVariant(variant1);
        inventory1.setBatch(batch1);
        inventory1.setQuantity(40L);
        inventory1.setCostPrice(50.0);

        Inventory inventory2 = new Inventory();
        inventory2.setId(2L);
        inventory2.setBranch(branch);
        inventory2.setVariant(variant2);
        inventory2.setBatch(batch2);
        inventory2.setQuantity(20L);
        inventory2.setCostPrice(30.0);

        when(movementRepository.findById(1L)).thenReturn(Optional.of(returnReceipt));
        when(movementRepository.save(any(InventoryMovement.class))).thenReturn(returnReceipt);
        when(branchRepository.findById(1L)).thenReturn(Optional.of(branch));
        when(movementDetailRepository.findByMovementId(1L)).thenReturn(List.of(detail1, detail2));
        when(inventoryRepository.findByBranchIdAndVariantIdAndBatchId(1L, 1L, 1L))
            .thenReturn(Optional.of(inventory1));
        when(inventoryRepository.findByBranchIdAndVariantIdAndBatchId(1L, 2L, 2L))
            .thenReturn(Optional.of(inventory2));
        when(inventoryRepository.save(any(Inventory.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        inventoryMovementService.cancelReceipt(1L);

        // Assert
        verify(movementRepository).save(argThat(mvt ->
            mvt.getMovementStatus() == MovementStatus.CANCELLED
        ));

        // Should restore both items
        verify(inventoryRepository).save(argThat(inv ->
            inv.getId().equals(1L) && inv.getQuantity() == 50L // 40 + 10
        ));
        verify(inventoryRepository).save(argThat(inv ->
            inv.getId().equals(2L) && inv.getQuantity() == 25L // 20 + 5
        ));
    }

    @Test
    @DisplayName("TC12: Cancel non-DRAFT receipt should throw exception")
    void cancelShippedReceipt_shouldThrowException() {
        // Arrange
        returnReceipt.setMovementStatus(MovementStatus.SHIPPED);

        when(movementRepository.findById(1L)).thenReturn(Optional.of(returnReceipt));

        // Act & Assert
        assertThatThrownBy(() -> inventoryMovementService.cancelReceipt(1L))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("cannot be cancelled");

        // Should not restore any inventory
        verify(inventoryRepository, never()).save(any(Inventory.class));
    }
}

