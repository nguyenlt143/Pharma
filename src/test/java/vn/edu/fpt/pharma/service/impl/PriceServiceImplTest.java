package vn.edu.fpt.pharma.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import vn.edu.fpt.pharma.BaseServiceTest;
import vn.edu.fpt.pharma.dto.price.PriceRequest;
import vn.edu.fpt.pharma.dto.price.PriceResponse;
import vn.edu.fpt.pharma.entity.MedicineVariant;
import vn.edu.fpt.pharma.entity.Price;
import vn.edu.fpt.pharma.repository.MedicineVariantRepository;
import vn.edu.fpt.pharma.repository.PriceRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

/**
 * Unit tests for PriceServiceImpl - 12 tests
 * Strategy: Full coverage for createOrUpdate/get with validation rules
 */
@DisplayName("PriceServiceImpl Tests")
class PriceServiceImplTest extends BaseServiceTest {

    @Mock
    private PriceRepository priceRepository;

    @Mock
    private MedicineVariantRepository variantRepository;

    @InjectMocks
    private PriceServiceImpl priceService;

    private Price testPrice;
    private MedicineVariant testVariant;
    private PriceRequest validRequest;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();

        testVariant = MedicineVariant.builder()
                .dosage("500mg")
                .dosage_form("Viên nén")
                .build();
        testVariant.setId(1L);

        testPrice = Price.builder()
                .variantId(1L)
                .branchId(1L)
                .salePrice(50000.0)
                .branchPrice(45000.0)
                .startDate(now)
                .endDate(now.plusMonths(1))
                .build();
        testPrice.setId(1L);

        validRequest = PriceRequest.builder()
                .variantId(1L)
                .branchId(1L)
                .salePrice(50000.0)
                .branchPrice(45000.0)
                .startDate(now)
                .endDate(now.plusMonths(1))
                .build();
    }

    @Nested
    @DisplayName("createOrUpdatePrice() - Create tests - 5 tests")
    class CreatePriceTests {

        @Test
        @DisplayName("Should create new price with valid request")
        void createOrUpdatePrice_withNewPrice_shouldCreatePrice() {
            // Arrange
            validRequest.setId(null); // New price
            when(priceRepository.save(any(Price.class))).thenAnswer(invocation -> {
                Price price = invocation.getArgument(0);
                price.setId(1L);
                return price;
            });
            when(variantRepository.findById(1L)).thenReturn(Optional.of(testVariant));

            // Act
            PriceResponse result = priceService.createOrUpdatePrice(validRequest);

            // Assert
            assertThat(result).isNotNull();
            verify(priceRepository).save(argThat(price ->
                price.getVariantId().equals(1L) &&
                price.getBranchId().equals(1L) &&
                price.getSalePrice().equals(50000.0) &&
                price.getBranchPrice().equals(45000.0)
            ));
            verify(variantRepository).findById(1L);
        }

        @Test
        @DisplayName("Should create price with all fields")
        void createOrUpdatePrice_withAllFields_shouldCreateSuccessfully() {
            // Arrange
            validRequest.setId(null);
            when(priceRepository.save(any(Price.class))).thenAnswer(invocation -> {
                Price price = invocation.getArgument(0);
                price.setId(1L);
                return price;
            });
            when(variantRepository.findById(1L)).thenReturn(Optional.of(testVariant));

            // Act
            PriceResponse result = priceService.createOrUpdatePrice(validRequest);

            // Assert
            assertThat(result).isNotNull();
            verify(priceRepository).save(argThat(price ->
                price.getStartDate() != null &&
                price.getEndDate() != null
            ));
        }

        @Test
        @DisplayName("Should create price without optional fields")
        void createOrUpdatePrice_withoutOptionalFields_shouldCreateSuccessfully() {
            // Arrange
            PriceRequest minimalRequest = PriceRequest.builder()
                    .variantId(1L)
                    .salePrice(50000.0)
                    .build();

            when(priceRepository.save(any(Price.class))).thenAnswer(invocation -> {
                Price price = invocation.getArgument(0);
                price.setId(1L);
                return price;
            });
            when(variantRepository.findById(1L)).thenReturn(Optional.of(testVariant));

            // Act
            PriceResponse result = priceService.createOrUpdatePrice(minimalRequest);

            // Assert
            assertThat(result).isNotNull();
            verify(priceRepository).save(argThat(price ->
                price.getVariantId().equals(1L) &&
                price.getSalePrice().equals(50000.0) &&
                price.getBranchId() == null &&
                price.getBranchPrice() == null
            ));
        }

        @Test
        @DisplayName("Should handle repository save failure")
        void createOrUpdatePrice_whenRepositorySaveFails_shouldPropagateException() {
            // Arrange
            validRequest.setId(null);
            when(priceRepository.save(any())).thenThrow(new RuntimeException("Database error"));

            // Act & Assert
            assertThatThrownBy(() -> priceService.createOrUpdatePrice(validRequest))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Database error");
        }

        @Test
        @DisplayName("Should return PriceResponse with variant details")
        void createOrUpdatePrice_shouldReturnPriceResponseWithVariantDetails() {
            // Arrange
            validRequest.setId(null);
            when(priceRepository.save(any(Price.class))).thenAnswer(invocation -> {
                Price price = invocation.getArgument(0);
                price.setId(1L);
                return price;
            });
            when(variantRepository.findById(1L)).thenReturn(Optional.of(testVariant));

            // Act
            PriceResponse result = priceService.createOrUpdatePrice(validRequest);

            // Assert
            assertThat(result).isNotNull();
            verify(variantRepository).findById(1L);
        }
    }

    @Nested
    @DisplayName("createOrUpdatePrice() - Update tests - 4 tests")
    class UpdatePriceTests {

        @Test
        @DisplayName("Should update existing price when id is provided")
        void createOrUpdatePrice_withExistingId_shouldUpdatePrice() {
            // Arrange
            validRequest.setId(1L);
            validRequest.setSalePrice(60000.0);
            validRequest.setBranchPrice(55000.0);

            when(priceRepository.findById(1L)).thenReturn(Optional.of(testPrice));
            when(priceRepository.save(any(Price.class))).thenReturn(testPrice);
            when(variantRepository.findById(1L)).thenReturn(Optional.of(testVariant));

            // Act
            PriceResponse result = priceService.createOrUpdatePrice(validRequest);

            // Assert
            assertThat(result).isNotNull();
            verify(priceRepository).findById(1L);
            verify(priceRepository).save(testPrice);
            assertThat(testPrice.getSalePrice()).isEqualTo(60000.0);
            assertThat(testPrice.getBranchPrice()).isEqualTo(55000.0);
        }

        @Test
        @DisplayName("Should update all price fields")
        void createOrUpdatePrice_shouldUpdateAllFields() {
            // Arrange
            LocalDateTime newStartDate = now.plusDays(1);
            LocalDateTime newEndDate = now.plusMonths(2);

            validRequest.setId(1L);
            validRequest.setVariantId(2L);
            validRequest.setBranchId(2L);
            validRequest.setSalePrice(70000.0);
            validRequest.setBranchPrice(65000.0);
            validRequest.setStartDate(newStartDate);
            validRequest.setEndDate(newEndDate);

            when(priceRepository.findById(1L)).thenReturn(Optional.of(testPrice));
            when(priceRepository.save(any(Price.class))).thenReturn(testPrice);
            when(variantRepository.findById(2L)).thenReturn(Optional.of(testVariant));

            // Act
            PriceResponse result = priceService.createOrUpdatePrice(validRequest);

            // Assert
            assertThat(result).isNotNull();
            assertThat(testPrice.getVariantId()).isEqualTo(2L);
            assertThat(testPrice.getBranchId()).isEqualTo(2L);
            assertThat(testPrice.getSalePrice()).isEqualTo(70000.0);
            assertThat(testPrice.getBranchPrice()).isEqualTo(65000.0);
            assertThat(testPrice.getStartDate()).isEqualTo(newStartDate);
            assertThat(testPrice.getEndDate()).isEqualTo(newEndDate);
        }

        @Test
        @DisplayName("Should create new price when id not found")
        void createOrUpdatePrice_whenIdNotFound_shouldCreateNewPrice() {
            // Arrange
            validRequest.setId(999L); // Non-existent id
            when(priceRepository.findById(999L)).thenReturn(Optional.empty());
            when(priceRepository.save(any(Price.class))).thenAnswer(invocation -> {
                Price price = invocation.getArgument(0);
                price.setId(999L);
                return price;
            });
            when(variantRepository.findById(1L)).thenReturn(Optional.of(testVariant));

            // Act
            PriceResponse result = priceService.createOrUpdatePrice(validRequest);

            // Assert
            assertThat(result).isNotNull();
            verify(priceRepository).findById(999L);
            verify(priceRepository).save(any(Price.class));
        }

        @Test
        @DisplayName("Should update price dates correctly")
        void createOrUpdatePrice_shouldUpdateDatesCorrectly() {
            // Arrange
            LocalDateTime newStart = LocalDateTime.of(2025, 1, 1, 0, 0);
            LocalDateTime newEnd = LocalDateTime.of(2025, 12, 31, 23, 59);

            validRequest.setId(1L);
            validRequest.setStartDate(newStart);
            validRequest.setEndDate(newEnd);

            when(priceRepository.findById(1L)).thenReturn(Optional.of(testPrice));
            when(priceRepository.save(any(Price.class))).thenReturn(testPrice);
            when(variantRepository.findById(1L)).thenReturn(Optional.of(testVariant));

            // Act
            PriceResponse result = priceService.createOrUpdatePrice(validRequest);

            // Assert
            assertThat(result).isNotNull();
            assertThat(testPrice.getStartDate()).isEqualTo(newStart);
            assertThat(testPrice.getEndDate()).isEqualTo(newEnd);
        }
    }

    @Nested
    @DisplayName("getPriceById() tests - 3 tests")
    class GetPriceByIdTests {

        @Test
        @DisplayName("Should return price when found")
        void getPriceById_whenPriceExists_shouldReturnPrice() {
            // Arrange
            when(priceRepository.findById(1L)).thenReturn(Optional.of(testPrice));
            when(variantRepository.findById(1L)).thenReturn(Optional.of(testVariant));

            // Act
            PriceResponse result = priceService.getPriceById(1L);

            // Assert
            assertThat(result).isNotNull();
            verify(priceRepository).findById(1L);
            verify(variantRepository).findById(1L);
        }

        @Test
        @DisplayName("Should return null when price not found")
        void getPriceById_whenPriceNotFound_shouldReturnNull() {
            // Arrange
            when(priceRepository.findById(1L)).thenReturn(Optional.empty());

            // Act
            PriceResponse result = priceService.getPriceById(1L);

            // Assert
            assertThat(result).isNull();
            verify(priceRepository).findById(1L);
            verify(variantRepository, never()).findById(any());
        }

        @Test
        @DisplayName("Should return price even when variant not found")
        void getPriceById_whenVariantNotFound_shouldReturnPriceWithoutVariant() {
            // Arrange
            when(priceRepository.findById(1L)).thenReturn(Optional.of(testPrice));
            when(variantRepository.findById(1L)).thenReturn(Optional.empty());

            // Act
            PriceResponse result = priceService.getPriceById(1L);

            // Assert
            assertThat(result).isNotNull();
            verify(priceRepository).findById(1L);
            verify(variantRepository).findById(1L);
        }
    }
}

