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
}

