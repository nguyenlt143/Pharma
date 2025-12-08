package vn.edu.fpt.pharma.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import vn.edu.fpt.pharma.BaseServiceTest;
import vn.edu.fpt.pharma.dto.supplier.SupplierRequest;
import vn.edu.fpt.pharma.dto.supplier.SupplierResponse;
import vn.edu.fpt.pharma.entity.Supplier;
import vn.edu.fpt.pharma.exception.EntityInUseException;
import vn.edu.fpt.pharma.repository.SupplierRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

/**
 * Unit tests for SupplierServiceImpl - 15 tests
 * Strategy: Full coverage for create/update/delete with validation rules
 */
@DisplayName("SupplierServiceImpl Tests")
class SupplierServiceImplTest extends BaseServiceTest {

    @Mock
    private SupplierRepository supplierRepository;

    @InjectMocks
    private SupplierServiceImpl supplierService;

    private Supplier testSupplier;
    private SupplierRequest validRequest;

    @BeforeEach
    void setUp() {
        testSupplier = Supplier.builder()
                .name("DHG Pharma")
                .phone("0123456789")
                .address("123 Nguyen Thi Minh Khai, Q1, TP.HCM")
                .build();
        testSupplier.setId(1L);

        validRequest = SupplierRequest.builder()
                .supplierName("DHG Pharma")
                .phone("0123456789")
                .address("123 Nguyen Thi Minh Khai, Q1, TP.HCM")
                .build();
    }

    @Nested
    @DisplayName("createSupplier() tests - 4 tests")
    class CreateSupplierTests {

        @Test
        @DisplayName("Should create supplier with valid request")
        void createSupplier_withValidRequest_shouldCreateSupplier() {
            // Arrange
            when(supplierRepository.save(any(Supplier.class))).thenAnswer(invocation -> {
                Supplier supplier = invocation.getArgument(0);
                supplier.setId(1L);
                return supplier;
            });

            // Act
            SupplierResponse result = supplierService.createSupplier(validRequest);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getSupplierName()).isEqualTo("DHG Pharma");
            assertThat(result.getPhone()).isEqualTo("0123456789");
            assertThat(result.getAddress()).isEqualTo("123 Nguyen Thi Minh Khai, Q1, TP.HCM");

            verify(supplierRepository).save(argThat(supplier ->
                supplier.getName().equals("DHG Pharma") &&
                supplier.getPhone().equals("0123456789") &&
                supplier.getAddress().equals("123 Nguyen Thi Minh Khai, Q1, TP.HCM")
            ));
        }

        @Test
        @DisplayName("Should create supplier with only required fields")
        void createSupplier_withOnlyRequiredFields_shouldCreateSuccessfully() {
            // Arrange
            SupplierRequest minimalRequest = SupplierRequest.builder()
                    .supplierName("Minimal Supplier")
                    .build();

            when(supplierRepository.save(any(Supplier.class))).thenAnswer(invocation -> {
                Supplier supplier = invocation.getArgument(0);
                supplier.setId(1L);
                return supplier;
            });

            // Act
            SupplierResponse result = supplierService.createSupplier(minimalRequest);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getSupplierName()).isEqualTo("Minimal Supplier");

            verify(supplierRepository).save(argThat(supplier ->
                supplier.getName().equals("Minimal Supplier") &&
                supplier.getPhone() == null &&
                supplier.getAddress() == null
            ));
        }

        @Test
        @DisplayName("Should create supplier with null phone and address")
        void createSupplier_withNullOptionalFields_shouldCreateSuccessfully() {
            // Arrange
            validRequest.setPhone(null);
            validRequest.setAddress(null);

            when(supplierRepository.save(any(Supplier.class))).thenAnswer(invocation -> {
                Supplier supplier = invocation.getArgument(0);
                supplier.setId(1L);
                return supplier;
            });

            // Act
            SupplierResponse result = supplierService.createSupplier(validRequest);

            // Assert
            assertThat(result).isNotNull();
            verify(supplierRepository).save(argThat(supplier ->
                supplier.getPhone() == null &&
                supplier.getAddress() == null
            ));
        }

        @Test
        @DisplayName("Should handle repository save failure")
        void createSupplier_whenRepositorySaveFails_shouldPropagateException() {
            // Arrange
            when(supplierRepository.save(any())).thenThrow(new RuntimeException("Database error"));

            // Act & Assert
            assertThatThrownBy(() -> supplierService.createSupplier(validRequest))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Database error");
        }

        @Test
        @DisplayName("Should accept supplier with valid phone number format")
        void createSupplier_withValidPhoneFormat_shouldSucceed() {
            // Arrange - From PhoneNumberValidatorTest: valid format 0XXXXXXXXX
            SupplierRequest request = SupplierRequest.builder()
                    .supplierName("Test Supplier")
                    .phone("0987654321") // Valid 10 digits starting with 0
                    .address("Test Address")
                    .build();

            when(supplierRepository.save(any(Supplier.class))).thenAnswer(inv -> {
                Supplier s = inv.getArgument(0);
                s.setId(1L);
                return s;
            });

            // Act
            SupplierResponse result = supplierService.createSupplier(request);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getPhone()).isEqualTo("0987654321");
            verify(supplierRepository).save(argThat(supplier ->
                supplier.getPhone().equals("0987654321")
            ));
        }

        @Test
        @DisplayName("Should accept supplier with phone number having +84 prefix")
        void createSupplier_withCountryCodePhone_shouldSucceed() {
            // Arrange - From PhoneNumberValidatorTest: +84XXXXXXXXX format
            SupplierRequest request = SupplierRequest.builder()
                    .supplierName("International Supplier")
                    .phone("+84987654321") // Valid with country code
                    .address("Vietnam")
                    .build();

            when(supplierRepository.save(any(Supplier.class))).thenAnswer(inv -> {
                Supplier s = inv.getArgument(0);
                s.setId(1L);
                return s;
            });

            // Act
            SupplierResponse result = supplierService.createSupplier(request);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getPhone()).isEqualTo("+84987654321");
            verify(supplierRepository).save(argThat(supplier ->
                supplier.getPhone().equals("+84987654321")
            ));
        }
    }

    @Nested
    @DisplayName("updateSupplier() tests - 5 tests")
    class UpdateSupplierTests {

        @Test
        @DisplayName("Should update supplier with valid request")
        void updateSupplier_withValidRequest_shouldUpdateSupplier() {
            // Arrange
            validRequest.setSupplierName("Updated Supplier Name");
            validRequest.setPhone("0987654321");
            validRequest.setAddress("New Address");

            when(supplierRepository.findById(1L)).thenReturn(Optional.of(testSupplier));
            when(supplierRepository.save(any(Supplier.class))).thenReturn(testSupplier);

            // Act
            SupplierResponse result = supplierService.updateSupplier(1L, validRequest);

            // Assert
            assertThat(result).isNotNull();
            verify(supplierRepository).findById(1L);
            verify(supplierRepository).save(testSupplier);
            assertThat(testSupplier.getName()).isEqualTo("Updated Supplier Name");
            assertThat(testSupplier.getPhone()).isEqualTo("0987654321");
            assertThat(testSupplier.getAddress()).isEqualTo("New Address");
        }

        @Test
        @DisplayName("Should throw exception when supplier not found")
        void updateSupplier_whenSupplierNotFound_shouldThrowException() {
            // Arrange
            when(supplierRepository.findById(1L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> supplierService.updateSupplier(1L, validRequest))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Supplier not found");

            verify(supplierRepository).findById(1L);
            verify(supplierRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should update only non-null fields")
        void updateSupplier_withPartialData_shouldUpdateOnlyNonNullFields() {
            // Arrange
            SupplierRequest partialRequest = SupplierRequest.builder()
                    .supplierName("New Name Only")
                    .build(); // phone and address are null

            when(supplierRepository.findById(1L)).thenReturn(Optional.of(testSupplier));
            when(supplierRepository.save(any(Supplier.class))).thenReturn(testSupplier);

            // Act
            SupplierResponse result = supplierService.updateSupplier(1L, partialRequest);

            // Assert
            assertThat(result).isNotNull();
            assertThat(testSupplier.getName()).isEqualTo("New Name Only");
            // Phone and address should remain unchanged
            verify(supplierRepository).save(testSupplier);
        }

        @Test
        @DisplayName("Should not update name when name is null")
        void updateSupplier_withNullName_shouldNotUpdateName() {
            // Arrange
            SupplierRequest partialRequest = SupplierRequest.builder()
                    .phone("0999999999")
                    .build(); // supplierName is null

            when(supplierRepository.findById(1L)).thenReturn(Optional.of(testSupplier));
            when(supplierRepository.save(any(Supplier.class))).thenReturn(testSupplier);

            // Act
            SupplierResponse result = supplierService.updateSupplier(1L, partialRequest);

            // Assert
            assertThat(result).isNotNull();
            assertThat(testSupplier.getPhone()).isEqualTo("0999999999");
            verify(supplierRepository).save(testSupplier);
        }

        @Test
        @DisplayName("Should update all fields when all are provided")
        void updateSupplier_withAllFields_shouldUpdateAllFields() {
            // Arrange
            validRequest.setSupplierName("Complete Update");
            validRequest.setPhone("0111111111");
            validRequest.setAddress("Complete Address");

            when(supplierRepository.findById(1L)).thenReturn(Optional.of(testSupplier));
            when(supplierRepository.save(any(Supplier.class))).thenReturn(testSupplier);

            // Act
            SupplierResponse result = supplierService.updateSupplier(1L, validRequest);

            // Assert
            assertThat(result).isNotNull();
            assertThat(testSupplier.getName()).isEqualTo("Complete Update");
            assertThat(testSupplier.getPhone()).isEqualTo("0111111111");
            assertThat(testSupplier.getAddress()).isEqualTo("Complete Address");
        }
    }

    @Nested
    @DisplayName("deleteById() tests - 4 tests")
    class DeleteByIdTests {

        @Test
        @DisplayName("Should delete supplier when no batches or movements exist")
        void deleteById_whenNoRelatedRecordsExist_shouldDeleteSuccessfully() {
            // Arrange
            when(supplierRepository.countBatchesBySupplierId(1L)).thenReturn(0L);
            when(supplierRepository.countInventoryMovementsBySupplierId(1L)).thenReturn(0L);

            // Act
            supplierService.deleteById(1L);

            // Assert
            verify(supplierRepository).countBatchesBySupplierId(1L);
            verify(supplierRepository).countInventoryMovementsBySupplierId(1L);
        }

        @Test
        @DisplayName("Should throw exception when supplier has batches")
        void deleteById_whenSupplierHasBatches_shouldThrowException() {
            // Arrange
            when(supplierRepository.countBatchesBySupplierId(1L)).thenReturn(5L);
            when(supplierRepository.countInventoryMovementsBySupplierId(1L)).thenReturn(0L);

            // Act & Assert
            assertThatThrownBy(() -> supplierService.deleteById(1L))
                    .isInstanceOf(EntityInUseException.class)
                    .hasMessageContaining("Nhà cung cấp")
                    .hasMessageContaining("5 lô");

            verify(supplierRepository).countBatchesBySupplierId(1L);
            verify(supplierRepository, never()).deleteById(any());
        }

        @Test
        @DisplayName("Should throw exception when supplier has inventory movements")
        void deleteById_whenSupplierHasMovements_shouldThrowException() {
            // Arrange
            when(supplierRepository.countBatchesBySupplierId(1L)).thenReturn(0L);
            when(supplierRepository.countInventoryMovementsBySupplierId(1L)).thenReturn(3L);

            // Act & Assert
            assertThatThrownBy(() -> supplierService.deleteById(1L))
                    .isInstanceOf(EntityInUseException.class)
                    .hasMessageContaining("Nhà cung cấp")
                    .hasMessageContaining("3 phiếu");

            verify(supplierRepository).countBatchesBySupplierId(1L);
            verify(supplierRepository).countInventoryMovementsBySupplierId(1L);
            verify(supplierRepository, never()).deleteById(any());
        }

        @Test
        @DisplayName("Should throw exception when supplier has both batches and movements")
        void deleteById_whenSupplierHasBothBatchesAndMovements_shouldThrowException() {
            // Arrange
            when(supplierRepository.countBatchesBySupplierId(1L)).thenReturn(10L);
            when(supplierRepository.countInventoryMovementsBySupplierId(1L)).thenReturn(5L);

            // Act & Assert
            assertThatThrownBy(() -> supplierService.deleteById(1L))
                    .isInstanceOf(EntityInUseException.class)
                    .hasMessageContaining("Nhà cung cấp")
                    .hasMessageContaining("10 lô")
                    .hasMessageContaining("5 phiếu");

            verify(supplierRepository).countBatchesBySupplierId(1L);
            verify(supplierRepository).countInventoryMovementsBySupplierId(1L);
            verify(supplierRepository, never()).deleteById(any());
        }
    }

    @Nested
    @DisplayName("getSupplierById() tests - 2 tests")
    class GetSupplierByIdTests {

        @Test
        @DisplayName("Should return supplier when found")
        void getSupplierById_whenSupplierExists_shouldReturnSupplier() {
            // Arrange
            when(supplierRepository.findById(1L)).thenReturn(Optional.of(testSupplier));

            // Act
            SupplierResponse result = supplierService.getSupplierById(1L);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getSupplierName()).isEqualTo("DHG Pharma");
            assertThat(result.getPhone()).isEqualTo("0123456789");
            assertThat(result.getAddress()).isEqualTo("123 Nguyen Thi Minh Khai, Q1, TP.HCM");
            verify(supplierRepository).findById(1L);
        }

        @Test
        @DisplayName("Should throw exception when supplier not found")
        void getSupplierById_whenSupplierNotFound_shouldThrowException() {
            // Arrange
            when(supplierRepository.findById(1L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> supplierService.getSupplierById(1L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Supplier not found");

            verify(supplierRepository).findById(1L);
        }
    }
}

