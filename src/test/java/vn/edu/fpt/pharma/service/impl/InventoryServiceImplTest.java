package vn.edu.fpt.pharma.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import vn.edu.fpt.pharma.BaseServiceTest;
import vn.edu.fpt.pharma.dto.inventory.InventoryMedicineVM;
import vn.edu.fpt.pharma.entity.Batch;
import vn.edu.fpt.pharma.entity.Branch;
import vn.edu.fpt.pharma.entity.Inventory;
import vn.edu.fpt.pharma.entity.MedicineVariant;
import vn.edu.fpt.pharma.repository.InventoryRepository;
import vn.edu.fpt.pharma.service.AuditService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for InventoryServiceImpl - 10 tests
 * Strategy: Full coverage for custom methods
 */
@DisplayName("InventoryServiceImpl Tests")
class InventoryServiceImplTest extends BaseServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    private Inventory testInventory;
    private Branch testBranch;

    @BeforeEach
    void setUp() {
        testBranch = new Branch();
        testBranch.setId(1L);
        testBranch.setName("Chi nhánh test");

        testInventory = new Inventory();
        testInventory.setId(1L);
        testInventory.setQuantity(100L);
        testInventory.setBranch(testBranch);
    }

    @Nested
    @DisplayName("getInventoryMedicinesByBranch() tests - 4 tests")
    class GetInventoryMedicinesByBranchTests {

        @Test
        @DisplayName("Should return inventory medicines when data exists")
        void getInventoryMedicinesByBranch_whenDataExists_shouldReturnList() {
            // Arrange
            Object[] row = new Object[]{
                1L, 1L, 1L, "Paracetamol", "Paracetamol", "500mg",
                "Viên nén", "DHG Pharma", "BATCH-001",
                java.sql.Date.valueOf(LocalDate.now().plusYears(1)),
                100L, "Viên", "Thuốc giảm đau", 1L
            };
            when(inventoryRepository.findMedicinesByBranch(1L))
                    .thenReturn(Collections.singletonList(row));

            // Act
            List<InventoryMedicineVM> result = inventoryService.getInventoryMedicinesByBranch(1L);

            // Assert
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getMedicineName()).isEqualTo("Paracetamol");
            assertThat(result.get(0).getQuantity()).isEqualTo(100L);
            verify(inventoryRepository).findMedicinesByBranch(1L);
        }

        @Test
        @DisplayName("Should return empty list when no data exists")
        void getInventoryMedicinesByBranch_whenNoData_shouldReturnEmptyList() {
            // Arrange
            when(inventoryRepository.findMedicinesByBranch(1L))
                    .thenReturn(Collections.emptyList());

            // Act
            List<InventoryMedicineVM> result = inventoryService.getInventoryMedicinesByBranch(1L);

            // Assert
            assertThat(result).isEmpty();
            verify(inventoryRepository).findMedicinesByBranch(1L);
        }

        @Test
        @DisplayName("Should handle null values in row")
        void getInventoryMedicinesByBranch_withNullValues_shouldHandleGracefully() {
            // Arrange
            Object[] row = new Object[]{
                1L, null, null, null, null, null,
                null, null, null, null,
                0L, "", "", null
            };
            when(inventoryRepository.findMedicinesByBranch(1L))
                    .thenReturn(Collections.singletonList(row));

            // Act
            List<InventoryMedicineVM> result = inventoryService.getInventoryMedicinesByBranch(1L);

            // Assert
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getMedicineName()).isEqualTo("");
        }

        @Test
        @DisplayName("Should return empty list on exception")
        void getInventoryMedicinesByBranch_whenExceptionOccurs_shouldReturnEmptyList() {
            // Arrange
            when(inventoryRepository.findMedicinesByBranch(1L))
                    .thenThrow(new RuntimeException("Database error"));

            // Act
            List<InventoryMedicineVM> result = inventoryService.getInventoryMedicinesByBranch(1L);

            // Assert
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("deleteOutOfStockFromBranch() tests - 3 tests")
    class DeleteOutOfStockTests {

        @Test
        @DisplayName("Should delete out of stock items and return count")
        void deleteOutOfStockFromBranch_withOutOfStockItems_shouldDeleteAndReturnCount() {
            // Arrange
            Inventory outOfStock = new Inventory();
            outOfStock.setId(2L);
            outOfStock.setQuantity(0L);
            outOfStock.setBranch(testBranch);

            when(inventoryRepository.findAll())
                    .thenReturn(Arrays.asList(testInventory, outOfStock));

            // Act
            int result = inventoryService.deleteOutOfStockFromBranch(1L);

            // Assert
            assertThat(result).isEqualTo(1);
            verify(inventoryRepository).deleteAll(argThat(list ->
                ((List<?>) list).size() == 1
            ));
        }

        @Test
        @DisplayName("Should return 0 when no out of stock items")
        void deleteOutOfStockFromBranch_withNoOutOfStock_shouldReturn0() {
            // Arrange
            when(inventoryRepository.findAll())
                    .thenReturn(Collections.singletonList(testInventory));

            // Act
            int result = inventoryService.deleteOutOfStockFromBranch(1L);

            // Assert
            assertThat(result).isEqualTo(0);
            verify(inventoryRepository).deleteAll(argThat(list ->
                ((List<?>) list).isEmpty()
            ));
        }

        @Test
        @DisplayName("Should handle null quantity as out of stock")
        void deleteOutOfStockFromBranch_withNullQuantity_shouldTreatAsOutOfStock() {
            // Arrange
            Inventory nullQty = new Inventory();
            nullQty.setId(3L);
            nullQty.setQuantity(null);
            nullQty.setBranch(testBranch);

            when(inventoryRepository.findAll())
                    .thenReturn(Collections.singletonList(nullQty));

            // Act
            int result = inventoryService.deleteOutOfStockFromBranch(1L);

            // Assert
            assertThat(result).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("findById() tests - 2 tests")
    class FindByIdTests {

        @Test
        @DisplayName("Should return inventory when found")
        void findById_whenInventoryExists_shouldReturnInventory() {
            // Arrange
            when(inventoryRepository.findById(1L)).thenReturn(Optional.of(testInventory));

            // Act
            Inventory result = inventoryService.findById(1L);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            verify(inventoryRepository).findById(1L);
        }

        @Test
        @DisplayName("Should return null when inventory not found")
        void findById_whenInventoryNotFound_shouldReturnNull() {
            // Arrange
            when(inventoryRepository.findById(999L)).thenReturn(Optional.empty());

            // Act
            Inventory result = inventoryService.findById(999L);

            // Assert
            assertThat(result).isNull();
            verify(inventoryRepository).findById(999L);
        }
    }
}

