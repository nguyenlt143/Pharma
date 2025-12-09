package vn.edu.fpt.pharma.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import vn.edu.fpt.pharma.BaseServiceTest;
import vn.edu.fpt.pharma.entity.MedicineVariant;
import vn.edu.fpt.pharma.entity.Medicine;
import vn.edu.fpt.pharma.repository.MedicineVariantRepository;
import vn.edu.fpt.pharma.service.AuditService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for MedicineVariantServiceImpl - 4 tests
 * Strategy: Coverage for inherited BaseServiceImpl methods
 */
@DisplayName("MedicineVariantServiceImpl Tests")
class MedicineVariantServiceImplTest extends BaseServiceTest {

    @Mock
    private MedicineVariantRepository medicineVariantRepository;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private MedicineVariantServiceImpl medicineVariantService;

    private MedicineVariant testVariant;

    @BeforeEach
    void setUp() {
        Medicine medicine = new Medicine();
        medicine.setId(1L);
        medicine.setName("Paracetamol");

        testVariant = new MedicineVariant();
        testVariant.setId(1L);
        testVariant.setMedicine(medicine);
        testVariant.setStrength("500mg");
    }

    @Nested
    @DisplayName("findById() tests - 2 tests")
    class FindByIdTests {

        @Test
        @DisplayName("Should return variant when found")
        void findById_whenVariantExists_shouldReturnVariant() {
            // Arrange
            when(medicineVariantRepository.findById(1L)).thenReturn(Optional.of(testVariant));

            // Act
            MedicineVariant result = medicineVariantService.findById(1L);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getStrength()).isEqualTo("500mg");
            verify(medicineVariantRepository).findById(1L);
        }

        @Test
        @DisplayName("Should return null when variant not found")
        void findById_whenVariantNotFound_shouldReturnNull() {
            // Arrange
            when(medicineVariantRepository.findById(999L)).thenReturn(Optional.empty());

            // Act
            MedicineVariant result = medicineVariantService.findById(999L);

            // Assert
            assertThat(result).isNull();
            verify(medicineVariantRepository).findById(999L);
        }
    }

    @Nested
    @DisplayName("create() tests - 2 tests")
    class CreateTests {

        @Test
        @DisplayName("Should create variant successfully")
        void create_withValidVariant_shouldCreateSuccessfully() {
            // Arrange
            MedicineVariant newVariant = new MedicineVariant();
            newVariant.setStrength("250mg");
            when(medicineVariantRepository.save(any(MedicineVariant.class))).thenReturn(testVariant);

            // Act
            MedicineVariant result = medicineVariantService.create(newVariant);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            verify(medicineVariantRepository).save(newVariant);
        }

        @Test
        @DisplayName("Should handle repository save failure")
        void create_whenRepositoryFails_shouldPropagateException() {
            // Arrange
            MedicineVariant newVariant = new MedicineVariant();
            newVariant.setStrength("250mg");
            when(medicineVariantRepository.save(any())).thenThrow(new RuntimeException("Database error"));

            // Act & Assert
            assertThatThrownBy(() -> medicineVariantService.create(newVariant))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Database error");
        }
    }
}

