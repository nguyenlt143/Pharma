package vn.edu.fpt.pharma.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import vn.edu.fpt.pharma.BaseServiceTest;
import vn.edu.fpt.pharma.dto.warehouse.CreateReceiptRequest;
import vn.edu.fpt.pharma.dto.warehouse.ReceiptDetailRequest;
import vn.edu.fpt.pharma.entity.*;
import vn.edu.fpt.pharma.repository.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for WarehouseReceiptServiceImpl - 6 tests
 * Strategy: Coverage for createReceipt method
 */
@DisplayName("WarehouseReceiptServiceImpl Tests")
@org.mockito.junit.jupiter.MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
class WarehouseReceiptServiceImplTest extends BaseServiceTest {

    @Mock
    private InventoryMovementRepository movementRepository;

    @Mock
    private InventoryMovementDetailRepository movementDetailRepository;

    @Mock
    private BatchRepository batchRepository;

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private MedicineVariantRepository variantRepository;

    @Mock
    private BranchRepository branchRepository;

    @InjectMocks
    private WarehouseReceiptServiceImpl warehouseReceiptService;

    private Supplier testSupplier;
    private MedicineVariant testVariant;
    private Medicine testMedicine;

    @BeforeEach
    void setUp() {
        testSupplier = new Supplier();
        testSupplier.setId(1L);
        testSupplier.setName("Test Supplier");

        testMedicine = new Medicine();
        testMedicine.setId(1L);
        testMedicine.setName("Paracetamol");

        testVariant = new MedicineVariant();
        testVariant.setId(1L);
        testVariant.setMedicine(testMedicine);
        testVariant.setQuantityPerPackage(10.0);
    }

    @Nested
    @DisplayName("createReceipt() tests - 6 tests")
    class CreateReceiptTests {

        @Test
        @DisplayName("Should throw exception when supplier not found")
        void createReceipt_whenSupplierNotFound_shouldThrowException() {
            // Arrange
            CreateReceiptRequest request = new CreateReceiptRequest();
            request.setSupplierId(999L);
            request.setDetails(Collections.emptyList());

            when(supplierRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> warehouseReceiptService.createReceipt(request))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Không tìm thấy nhà cung cấp");
        }

        @Test
        @DisplayName("Should throw exception when batch code already exists")
        void createReceipt_whenBatchCodeExists_shouldThrowException() {
            // Arrange
            ReceiptDetailRequest detail = new ReceiptDetailRequest();
            detail.setVariantId(1L);
            detail.setBatchCode("EXISTING-BATCH");
            detail.setQuantity(100L);
            detail.setManufactureDate("2025-01-01");
            detail.setExpiryDate("2027-01-01");

            CreateReceiptRequest request = new CreateReceiptRequest();
            request.setSupplierId(1L);
            request.setDetails(Collections.singletonList(detail));

            when(supplierRepository.findById(1L)).thenReturn(Optional.of(testSupplier));
            when(movementRepository.save(any())).thenAnswer(inv -> {
                InventoryMovement m = inv.getArgument(0);
                m.setId(1L);
                return m;
            });
            when(variantRepository.findById(1L)).thenReturn(Optional.of(testVariant));
            when(batchRepository.existsByVariantIdAndBatchCode(1L, "EXISTING-BATCH")).thenReturn(true);

            // Act & Assert
            assertThatThrownBy(() -> warehouseReceiptService.createReceipt(request))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Số lô")
                    .hasMessageContaining("đã tồn tại");
        }

        @Test
        @DisplayName("Should throw exception when variant not found")
        void createReceipt_whenVariantNotFound_shouldThrowException() {
            // Arrange
            ReceiptDetailRequest detail = new ReceiptDetailRequest();
            detail.setVariantId(999L);
            detail.setBatchCode("NEW-BATCH");

            CreateReceiptRequest request = new CreateReceiptRequest();
            request.setSupplierId(1L);
            request.setDetails(Collections.singletonList(detail));

            when(supplierRepository.findById(1L)).thenReturn(Optional.of(testSupplier));
            when(movementRepository.save(any())).thenAnswer(inv -> {
                InventoryMovement m = inv.getArgument(0);
                m.setId(1L);
                return m;
            });
            when(variantRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> warehouseReceiptService.createReceipt(request))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Không tìm thấy biến thể thuốc");
        }

        @Test
        @DisplayName("TC5: Should throw exception when quantity not divisible by package size - Boundary")
        void createReceipt_whenQuantityNotDivisible_shouldThrowException() {
            // Arrange
            ReceiptDetailRequest detail = new ReceiptDetailRequest();
            detail.setVariantId(1L);
            detail.setBatchCode("NEW-BATCH");
            detail.setQuantity(15L); // Not divisible by 10
            detail.setManufactureDate("2025-01-01");
            detail.setExpiryDate("2027-01-01");

            CreateReceiptRequest request = new CreateReceiptRequest();
            request.setSupplierId(1L);
            request.setDetails(Collections.singletonList(detail));

            when(supplierRepository.findById(1L)).thenReturn(Optional.of(testSupplier));
            when(movementRepository.save(any())).thenAnswer(inv -> {
                InventoryMovement m = inv.getArgument(0);
                m.setId(1L);
                return m;
            });
            when(variantRepository.findById(1L)).thenReturn(Optional.of(testVariant));
            when(batchRepository.existsByVariantIdAndBatchCode(any(), any())).thenReturn(false);

            // Act & Assert
            assertThatThrownBy(() -> warehouseReceiptService.createReceipt(request))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("phải chia hết cho số viên trong một hộp");
        }
    }
}

