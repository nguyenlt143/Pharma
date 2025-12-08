package vn.edu.fpt.pharma.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.edu.fpt.pharma.dto.medicine.MedicineSearchDTO;
import vn.edu.fpt.pharma.dto.medicine.VariantInventoryDTO;
import vn.edu.fpt.pharma.entity.MedicineVariant;
import vn.edu.fpt.pharma.repository.InventoryRepository;
import vn.edu.fpt.pharma.repository.MedicineVariantRepository;
import vn.edu.fpt.pharma.service.impl.MedicineVariantServiceImpl;
import vn.edu.fpt.pharma.testutil.TestDataFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

/**
 * Unit tests for MedicineVariantService - Happy Path Only
 * Strategy: 1 test per method to achieve 100% line coverage
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("MedicineVariantService Tests - Happy Path Only")
class MedicineVariantServiceTest {

    @Mock
    private MedicineVariantRepository medicineVariantRepository;

    @Mock
    private InventoryRepository inventoryRepository;

    private MedicineVariantService medicineVariantService;

    @BeforeEach
    void setUp() {
        medicineVariantService = new MedicineVariantServiceImpl(
                medicineVariantRepository,
                inventoryRepository
        );
    }

    @Test
    @DisplayName("searchMedicines - should return results")
    void searchMedicines_happyPath() {
        // Arrange
        String keyword = "paracetamol";
        List<MedicineSearchDTO> expected = List.of(
                new MedicineSearchDTO(1L, "Paracetamol 500mg", "Hạ sốt, giảm đau")
        );
        when(medicineVariantRepository.searchByKeyword(keyword))
                .thenReturn(expected);

        // Act
        List<MedicineSearchDTO> result = medicineVariantService.searchMedicines(keyword);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("getVariantsWithInventory - should return variants with inventory")
    void getVariantsWithInventory_happyPath() {
        // Arrange
        Long medicineId = 1L;
        List<VariantInventoryDTO> expected = List.of(
                new VariantInventoryDTO(/* fill with data */)
        );
        when(medicineVariantRepository.findVariantsWithInventoryByMedicineId(medicineId))
                .thenReturn(expected);

        // Act
        List<VariantInventoryDTO> result = medicineVariantService.getVariantsWithInventory(medicineId);

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("findVariantById - should return variant")
    void findVariantById_happyPath() {
        // Arrange
        Long variantId = 1L;
        MedicineVariant variant = TestDataFactory.createMedicineVariant();
        when(medicineVariantRepository.findById(variantId))
                .thenReturn(Optional.of(variant));

        // Act
        MedicineVariant result = medicineVariantService.findVariantById(variantId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(variantId);
    }

    @Test
    @DisplayName("checkVariantAvailability - should return true when available")
    void checkVariantAvailability_happyPath() {
        // Arrange
        Long variantId = 1L;
        when(inventoryRepository.getTotalQuantityByVariant(variantId))
                .thenReturn(100L);

        // Act
        boolean available = medicineVariantService.checkVariantAvailability(variantId);

        // Assert
        assertThat(available).isTrue();
    }

    @Test
    @DisplayName("getVariantsByBranch - should return variants for branch")
    void getVariantsByBranch_happyPath() {
        // Arrange
        Long branchId = 1L;
        List<MedicineVariant> variants = List.of(TestDataFactory.createMedicineVariant());
        when(medicineVariantRepository.findByBranchId(branchId))
                .thenReturn(variants);

        // Act
        List<MedicineVariant> result = medicineVariantService.getVariantsByBranch(branchId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("searchByActiveIngredient - should return variants by ingredient")
    void searchByActiveIngredient_happyPath() {
        // Arrange
        String ingredient = "paracetamol";
        List<MedicineVariant> variants = List.of(TestDataFactory.createMedicineVariant());
        when(medicineVariantRepository.searchByActiveIngredient(ingredient))
                .thenReturn(variants);

        // Act
        List<MedicineVariant> result = medicineVariantService.searchByActiveIngredient(ingredient);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("getExpiringSoonVariants - should return expiring variants")
    void getExpiringSoonVariants_happyPath() {
        // Arrange
        LocalDate threshold = LocalDate.now().plusMonths(3);
        List<VariantInventoryDTO> expected = List.of();
        when(inventoryRepository.findExpiringSoon(threshold))
                .thenReturn(expected);

        // Act
        List<VariantInventoryDTO> result = medicineVariantService.getExpiringSoonVariants(threshold);

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("getLowStockVariants - should return low stock variants")
    void getLowStockVariants_happyPath() {
        // Arrange
        Integer threshold = 10;
        List<VariantInventoryDTO> expected = List.of();
        when(inventoryRepository.findLowStock(threshold))
                .thenReturn(expected);

        // Act
        List<VariantInventoryDTO> result = medicineVariantService.getLowStockVariants(threshold);

        // Assert
        assertThat(result).isNotNull();
    }
}

