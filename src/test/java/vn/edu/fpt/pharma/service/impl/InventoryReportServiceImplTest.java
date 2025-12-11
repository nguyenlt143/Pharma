package vn.edu.fpt.pharma.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import vn.edu.fpt.pharma.BaseServiceTest;
import vn.edu.fpt.pharma.repository.InventoryRepository;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for InventoryReportServiceImpl - 5 tests
 * Strategy: Coverage only (1 test per method - basic happy path)
 */
@DisplayName("InventoryReportServiceImpl Tests")
class InventoryReportServiceImplTest extends BaseServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private InventoryReportServiceImpl service;

    private Long branchId;

    @BeforeEach
    void setUp() {
        branchId = 1L;
    }

    @Test
    @DisplayName("Should return inventory summary")
    void getInventorySummary_withValidBranchId_shouldReturnSummary() {
        // Arrange
        when(inventoryRepository.countTotalItems(branchId)).thenReturn(100);
        when(inventoryRepository.countLowStockItems(branchId)).thenReturn(10);
        when(inventoryRepository.calculateTotalValue(branchId)).thenReturn(5000000.0);
        when(inventoryRepository.countNearExpiryItems(branchId)).thenReturn(5);
        when(inventoryRepository.countExpiredItems(branchId)).thenReturn(2);

        // Act
        Map<String, Object> result = service.getInventorySummary(branchId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.get("totalItems")).isEqualTo(100);
        assertThat(result.get("lowStock")).isEqualTo(10);
        assertThat(result.get("totalValue")).isEqualTo(5000000.0);
        assertThat(result.get("nearExpiry")).isEqualTo(5);
        assertThat(result.get("expired")).isEqualTo(2);
        assertThat(result).containsKey("lastUpdated");

        verify(inventoryRepository).countTotalItems(branchId);
        verify(inventoryRepository).countLowStockItems(branchId);
        verify(inventoryRepository).calculateTotalValue(branchId);
        verify(inventoryRepository).countNearExpiryItems(branchId);
        verify(inventoryRepository).countExpiredItems(branchId);
    }

    @Test
    @DisplayName("Should filter inventory by medicine name")
    void searchInventory_withValidQuery_shouldFilterByMedicineName() {
        // Arrange
        String query = "Paracetamol";
        Long categoryId = null;
        String status = null;

        Object[] mockRow = new Object[]{
                1L, 1L, 1L, "Paracetamol 500mg", "Paracetamol", "500mg",
                "Viên nén", "Manufacturer A", "BATCH001", null, 100L, "Viên",
                "Thuốc giảm đau", 1L, 10L
        };
        List<Object[]> mockDetails = Collections.singletonList(mockRow);
        when(inventoryRepository.findMedicinesByBranch(branchId)).thenReturn(mockDetails);

        // Act
        List<Map<String, Object>> result = service.searchInventory(branchId, query, categoryId, status);

        // Assert
        assertThat(result).isNotNull();
        verify(inventoryRepository).findMedicinesByBranch(branchId);
    }

    @Test
    @DisplayName("Should return all categories with statistics")
    void getCategoryStatistics_shouldReturnAllCategories() {
        // Arrange
        List<Object[]> mockStats = java.util.Arrays.asList(
                new Object[]{"Thuốc giảm đau", 50, 1000, 5000000.0},
                new Object[]{"Kháng sinh", 30, 500, 3000000.0}
        );

        when(inventoryRepository.getCategoryStatistics(branchId)).thenReturn(mockStats);

        // Act
        List<Map<String, Object>> result = service.getCategoryStatistics(branchId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        verify(inventoryRepository).getCategoryStatistics(branchId);
    }

    @Test
    @DisplayName("Should return all inventory fields")
    void getInventoryDetails_shouldReturnAllFields() {
        // Arrange
        Object[] mockRow = new Object[]{
                1L, 1L, 1L, "Paracetamol 500mg", "Paracetamol", "500mg",
                "Viên nén", "Manufacturer A", "BATCH001", null, 100L, "Viên",
                "Thuốc giảm đau", 1L, 10L
        };
        List<Object[]> mockDetails = Collections.singletonList(mockRow);

        when(inventoryRepository.findMedicinesByBranch(branchId)).thenReturn(mockDetails);

        // Act
        List<Map<String, Object>> result = service.getInventoryDetails(branchId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        verify(inventoryRepository).findMedicinesByBranch(branchId);
    }

    @Test
    @DisplayName("Should return category id and name")
    void getAllCategories_shouldReturnIdAndName() {
        // Arrange
        List<Object[]> mockCategories = java.util.Arrays.asList(
                new Object[]{1L, "Thuốc giảm đau"},
                new Object[]{2L, "Kháng sinh"},
                new Object[]{3L, "Vitamin"}
        );

        when(inventoryRepository.getAllCategories(branchId)).thenReturn(mockCategories);

        // Act
        List<Map<String, Object>> result = service.getAllCategories(branchId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(3);
        assertThat(result.get(0)).containsKeys("id", "name");
        verify(inventoryRepository).getAllCategories(branchId);
    }
}
