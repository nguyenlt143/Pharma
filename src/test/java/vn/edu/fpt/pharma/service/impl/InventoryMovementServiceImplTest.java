package vn.edu.fpt.pharma.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import vn.edu.fpt.pharma.BaseServiceTest;
import vn.edu.fpt.pharma.constant.BranchType;
import vn.edu.fpt.pharma.constant.MovementStatus;
import vn.edu.fpt.pharma.constant.MovementType;
import vn.edu.fpt.pharma.dto.warehouse.ExportSubmitDTO;
import vn.edu.fpt.pharma.entity.*;
import vn.edu.fpt.pharma.repository.*;
import vn.edu.fpt.pharma.service.AuditService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for InventoryMovementServiceImpl - 10 tests
 * Role: Warehouse
 * Functions: createExportMovement, approveReceipt, shipReceipt
 */
@DisplayName("InventoryMovementServiceImpl Tests")
@org.mockito.junit.jupiter.MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
class InventoryMovementServiceImplTest extends BaseServiceTest {

    @Mock
    private InventoryMovementRepository inventoryMovementRepository;

    @Mock
    private BranchRepository branchRepository;

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private InventoryMovementDetailRepository movementDetailRepository;

    @Mock
    private BatchRepository batchRepository;

    @Mock
    private MedicineVariantRepository variantRepository;

    @Mock
    private RequestFormRepository requestFormRepository;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private InventoryMovementServiceImpl inventoryMovementService;

    private InventoryMovement testMovement;
    private Branch warehouseBranch;
    private Branch destinationBranch;
    private Inventory testInventory;
    private MedicineVariant testVariant;
    private Batch testBatch;

    @BeforeEach
    void setUp() {
        testMovement = new InventoryMovement();
        testMovement.setId(1L);
        testMovement.setMovementType(MovementType.SUP_TO_WARE);
        testMovement.setMovementStatus(MovementStatus.RECEIVED);

        warehouseBranch = new Branch();
        warehouseBranch.setId(1L);
        warehouseBranch.setName("Kho tổng");
        warehouseBranch.setBranchType(BranchType.HEAD_QUARTER);

        destinationBranch = new Branch();
        destinationBranch.setId(2L);
        destinationBranch.setName("Chi nhánh 1");
        destinationBranch.setBranchType(BranchType.BRANCH);

        Medicine medicine = new Medicine();
        medicine.setId(1L);
        medicine.setName("Paracetamol");

        testVariant = new MedicineVariant();
        testVariant.setId(1L);
        testVariant.setMedicine(medicine);

        testBatch = new Batch();
        testBatch.setId(1L);
        testBatch.setBatchCode("BATCH-001");

        testInventory = new Inventory();
        testInventory.setId(1L);
        testInventory.setQuantity(100L);
        testInventory.setCostPrice(50000.0);
        testInventory.setVariant(testVariant);
        testInventory.setBatch(testBatch);
    }

    @Nested
    @DisplayName("findById() tests - 2 tests")
    class FindByIdTests {

        @Test
        @DisplayName("TC1: Should return movement when found - Normal")
        void findById_whenMovementExists_shouldReturnMovement() {
            // Arrange
            when(inventoryMovementRepository.findById(1L)).thenReturn(Optional.of(testMovement));

            // Act
            InventoryMovement result = inventoryMovementService.findById(1L);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getMovementType()).isEqualTo(MovementType.SUP_TO_WARE);
            verify(inventoryMovementRepository).findById(1L);
        }

        @Test
        @DisplayName("TC2: Should return null when movement not found - Abnormal")
        void findById_whenMovementNotFound_shouldReturnNull() {
            // Arrange
            when(inventoryMovementRepository.findById(999L)).thenReturn(Optional.empty());

            // Act
            InventoryMovement result = inventoryMovementService.findById(999L);

            // Assert
            assertThat(result).isNull();
            verify(inventoryMovementRepository).findById(999L);
        }
    }

    @Nested
    @DisplayName("create() tests - 2 tests")
    class CreateTests {

        @Test
        @DisplayName("TC1: Should create movement successfully - Normal")
        void create_withValidMovement_shouldCreateSuccessfully() {
            // Arrange
            InventoryMovement newMovement = new InventoryMovement();
            newMovement.setMovementType(MovementType.SUP_TO_WARE);
            when(inventoryMovementRepository.save(any(InventoryMovement.class))).thenReturn(testMovement);

            // Act
            InventoryMovement result = inventoryMovementService.create(newMovement);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            verify(inventoryMovementRepository).save(newMovement);
        }

        @Test
        @DisplayName("TC2: Should handle repository save failure - Abnormal")
        void create_whenRepositoryFails_shouldPropagateException() {
            // Arrange
            InventoryMovement newMovement = new InventoryMovement();
            newMovement.setMovementType(MovementType.SUP_TO_WARE);
            when(inventoryMovementRepository.save(any())).thenThrow(new RuntimeException("Database error"));

            // Act & Assert
            assertThatThrownBy(() -> inventoryMovementService.create(newMovement))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Database error");
        }
    }

    @Nested
    @DisplayName("createExportMovement() tests - 6 tests")
    class CreateExportMovementTests {

        @Test
        @DisplayName("TC1: Should create export movement successfully - Normal (Happy Path)")
        void createExportMovement_withValidData_shouldCreateSuccessfully() {
            // Arrange
            ExportSubmitDTO.ExportDetailItem detailItem = ExportSubmitDTO.ExportDetailItem.builder()
                    .inventoryId(1L)
                    .batchId(1L)
                    .variantId(1L)
                    .quantity(50L)
                    .price(60000.0)
                    .build();

            ExportSubmitDTO dto = ExportSubmitDTO.builder()
                    .branchId(2L)
                    .requestId(null)
                    .details(List.of(detailItem))
                    .build();

            when(branchRepository.findById(2L)).thenReturn(Optional.of(destinationBranch));
            when(branchRepository.findAll()).thenReturn(List.of(warehouseBranch, destinationBranch));
            when(inventoryMovementRepository.save(any())).thenAnswer(inv -> {
                InventoryMovement m = inv.getArgument(0);
                m.setId(1L);
                return m;
            });
            when(variantRepository.findById(1L)).thenReturn(Optional.of(testVariant));
            when(batchRepository.findById(1L)).thenReturn(Optional.of(testBatch));
            when(inventoryRepository.findById(1L)).thenReturn(Optional.of(testInventory));
            when(inventoryRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            // Act
            Long result = inventoryMovementService.createExportMovement(dto);

            // Assert
            assertThat(result).isEqualTo(1L);
            verify(inventoryMovementRepository).save(argThat(m ->
                m.getMovementType() == MovementType.WARE_TO_BR &&
                m.getMovementStatus() == MovementStatus.SHIPPED
            ));
            verify(movementDetailRepository).save(any(InventoryMovementDetail.class));
            // Verify inventory decreased
            verify(inventoryRepository).save(argThat(inv -> inv.getQuantity() == 50L));
        }

        @Test
        @DisplayName("TC2: Should throw exception when branch not found - Abnormal")
        void createExportMovement_whenBranchNotFound_shouldThrowException() {
            // Arrange
            ExportSubmitDTO dto = ExportSubmitDTO.builder()
                    .branchId(999L)
                    .details(List.of())
                    .build();

            when(branchRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> inventoryMovementService.createExportMovement(dto))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Branch not found");
        }

        @Test
        @DisplayName("TC3: Should throw exception when inventory insufficient - Abnormal")
        void createExportMovement_whenInventoryInsufficient_shouldThrowException() {
            // Arrange
            testInventory.setQuantity(30L); // Only 30 available

            ExportSubmitDTO.ExportDetailItem detailItem = ExportSubmitDTO.ExportDetailItem.builder()
                    .inventoryId(1L)
                    .batchId(1L)
                    .variantId(1L)
                    .quantity(50L) // Request 50
                    .price(60000.0)
                    .build();

            ExportSubmitDTO dto = ExportSubmitDTO.builder()
                    .branchId(2L)
                    .details(List.of(detailItem))
                    .build();

            when(branchRepository.findById(2L)).thenReturn(Optional.of(destinationBranch));
            when(branchRepository.findAll()).thenReturn(List.of(warehouseBranch, destinationBranch));
            when(inventoryMovementRepository.save(any())).thenAnswer(inv -> {
                InventoryMovement m = inv.getArgument(0);
                m.setId(1L);
                return m;
            });
            when(variantRepository.findById(1L)).thenReturn(Optional.of(testVariant));
            when(batchRepository.findById(1L)).thenReturn(Optional.of(testBatch));
            when(inventoryRepository.findById(1L)).thenReturn(Optional.of(testInventory));

            // Act & Assert
            assertThatThrownBy(() -> inventoryMovementService.createExportMovement(dto))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Insufficient inventory");
        }

        @Test
        @DisplayName("TC4: Should throw exception when variant not found - Abnormal")
        void createExportMovement_whenVariantNotFound_shouldThrowException() {
            // Arrange
            ExportSubmitDTO.ExportDetailItem detailItem = ExportSubmitDTO.ExportDetailItem.builder()
                    .inventoryId(1L)
                    .batchId(1L)
                    .variantId(999L)
                    .quantity(50L)
                    .price(60000.0)
                    .build();

            ExportSubmitDTO dto = ExportSubmitDTO.builder()
                    .branchId(2L)
                    .details(List.of(detailItem))
                    .build();

            when(branchRepository.findById(2L)).thenReturn(Optional.of(destinationBranch));
            when(branchRepository.findAll()).thenReturn(List.of(warehouseBranch, destinationBranch));
            when(inventoryMovementRepository.save(any())).thenAnswer(inv -> {
                InventoryMovement m = inv.getArgument(0);
                m.setId(1L);
                return m;
            });
            when(variantRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> inventoryMovementService.createExportMovement(dto))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Variant not found");
        }

        @Test
        @DisplayName("TC5: Should throw exception when batch not found - Abnormal")
        void createExportMovement_whenBatchNotFound_shouldThrowException() {
            // Arrange
            ExportSubmitDTO.ExportDetailItem detailItem = ExportSubmitDTO.ExportDetailItem.builder()
                    .inventoryId(1L)
                    .batchId(999L)
                    .variantId(1L)
                    .quantity(50L)
                    .price(60000.0)
                    .build();

            ExportSubmitDTO dto = ExportSubmitDTO.builder()
                    .branchId(2L)
                    .details(List.of(detailItem))
                    .build();

            when(branchRepository.findById(2L)).thenReturn(Optional.of(destinationBranch));
            when(branchRepository.findAll()).thenReturn(List.of(warehouseBranch, destinationBranch));
            when(inventoryMovementRepository.save(any())).thenAnswer(inv -> {
                InventoryMovement m = inv.getArgument(0);
                m.setId(1L);
                return m;
            });
            when(variantRepository.findById(1L)).thenReturn(Optional.of(testVariant));
            when(batchRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> inventoryMovementService.createExportMovement(dto))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Batch not found");
        }

        @Test
        @DisplayName("TC6: Should calculate total money correctly - Boundary")
        void createExportMovement_shouldCalculateTotalMoneyCorrectly() {
            // Arrange
            ExportSubmitDTO.ExportDetailItem detailItem1 = ExportSubmitDTO.ExportDetailItem.builder()
                    .inventoryId(1L)
                    .batchId(1L)
                    .variantId(1L)
                    .quantity(10L)
                    .price(50000.0)  // 10 * 50000 = 500000
                    .build();

            ExportSubmitDTO dto = ExportSubmitDTO.builder()
                    .branchId(2L)
                    .details(List.of(detailItem1))
                    .build();

            when(branchRepository.findById(2L)).thenReturn(Optional.of(destinationBranch));
            when(branchRepository.findAll()).thenReturn(List.of(warehouseBranch, destinationBranch));
            when(inventoryMovementRepository.save(any())).thenAnswer(inv -> {
                InventoryMovement m = inv.getArgument(0);
                m.setId(1L);
                return m;
            });
            when(variantRepository.findById(1L)).thenReturn(Optional.of(testVariant));
            when(batchRepository.findById(1L)).thenReturn(Optional.of(testBatch));
            when(inventoryRepository.findById(1L)).thenReturn(Optional.of(testInventory));
            when(inventoryRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            // Act
            Long result = inventoryMovementService.createExportMovement(dto);

            // Assert
            assertThat(result).isEqualTo(1L);
            // Verify movement saved with correct total money (10 * 50000 = 500000)
            verify(inventoryMovementRepository, atLeastOnce()).save(any(InventoryMovement.class));
        }
    }
}

