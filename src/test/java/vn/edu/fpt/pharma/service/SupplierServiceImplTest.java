package vn.edu.fpt.pharma.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import vn.edu.fpt.pharma.dto.DataTableRequest;
import vn.edu.fpt.pharma.dto.DataTableResponse;
import vn.edu.fpt.pharma.dto.supplier.SupplierRequest;
import vn.edu.fpt.pharma.dto.supplier.SupplierResponse;
import vn.edu.fpt.pharma.entity.Supplier;
import vn.edu.fpt.pharma.exception.EntityInUseException;
import vn.edu.fpt.pharma.repository.SupplierRepository;
import vn.edu.fpt.pharma.service.impl.SupplierServiceImpl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SupplierServiceImpl Tests")
class SupplierServiceImplTest {

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private SupplierServiceImpl supplierService;

    private Supplier supplier;

    @BeforeEach
    void setUp() {
        supplier = Supplier.builder()
                .name("ABC Pharma Co.")
                .phone("0123456789")
                .address("123 Nguyen Trai, Ha Noi")
                .build();
        supplier.setId(1L);
    }

    @Nested
    @DisplayName("Get All Suppliers Tests")
    class GetAllSuppliersTests {

        @Test
        @DisplayName("Should get all suppliers")
        void shouldGetAllSuppliers() {
            // Arrange
            List<Supplier> suppliers = List.of(supplier);
            when(supplierRepository.findAll()).thenReturn(suppliers);

            // Act
            List<Supplier> result = supplierService.getAllSuppliers();

            // Assert
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getName()).isEqualTo("ABC Pharma Co.");
            verify(supplierRepository).findAll();
        }

        @Test
        @DisplayName("Should return empty list when no suppliers exist")
        void shouldReturnEmptyListWhenNoSuppliers() {
            // Arrange
            when(supplierRepository.findAll()).thenReturn(List.of());

            // Act
            List<Supplier> result = supplierService.getAllSuppliers();

            // Assert
            assertThat(result).isEmpty();
            verify(supplierRepository).findAll();
        }
    }

    @Nested
    @DisplayName("Get Suppliers with DataTable Tests")
    class GetSuppliersDataTableTests {

        @Test
        @DisplayName("Should get suppliers with pagination")
        void shouldGetSuppliersWithPagination() {
            // Arrange
            DataTableRequest request = new DataTableRequest(0, 0, 10, null, null, "asc");

            List<Supplier> suppliers = List.of(supplier);
            Page<Supplier> page = new PageImpl<>(suppliers);

            when(supplierRepository.findAll(any(Pageable.class))).thenReturn(page);
            when(supplierRepository.count()).thenReturn(1L);

            // Act
            DataTableResponse<SupplierResponse> response = supplierService.getSuppliers(request);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.data()).hasSize(1);
            assertThat(response.data().get(0).getSupplierName()).isEqualTo("ABC Pharma Co.");
            verify(supplierRepository).findAll(any(Pageable.class));
        }

        @Test
        @DisplayName("Should search suppliers with keyword")
        void shouldSearchSuppliersWithKeyword() {
            // Arrange
            DataTableRequest request = new DataTableRequest(0, 0, 10, "ABC", null, "asc");

            List<Supplier> suppliers = List.of(supplier);
            Page<Supplier> page = new PageImpl<>(suppliers);

            when(supplierRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
            when(supplierRepository.count()).thenReturn(1L);

            // Act
            DataTableResponse<SupplierResponse> response = supplierService.getSuppliers(request);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.data()).hasSize(1);
            verify(supplierRepository).findAll(any(Specification.class), any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("Create Supplier Tests")
    class CreateSupplierTests {

        @Test
        @DisplayName("Should create supplier successfully")
        void shouldCreateSupplierSuccessfully() {
            // Arrange
            SupplierRequest request = new SupplierRequest();
            request.setSupplierName("New Supplier");
            request.setPhone("0987654321");
            request.setAddress("456 Le Loi, HCMC");

            when(supplierRepository.save(any(Supplier.class))).thenReturn(supplier);

            // Act
            SupplierResponse response = supplierService.createSupplier(request);

            // Assert
            assertThat(response).isNotNull();
            verify(supplierRepository).save(any(Supplier.class));
        }

        @Test
        @DisplayName("Should create supplier with all fields")
        void shouldCreateSupplierWithAllFields() {
            // Arrange
            SupplierRequest request = new SupplierRequest();
            request.setSupplierName("Full Supplier");
            request.setPhone("0123456789");
            request.setAddress("789 Tran Hung Dao, Da Nang");

            Supplier fullSupplier = Supplier.builder()
                    .name("Full Supplier")
                    .phone("0123456789")
                    .address("789 Tran Hung Dao, Da Nang")
                    .build();
            fullSupplier.setId(2L);

            when(supplierRepository.save(any(Supplier.class))).thenReturn(fullSupplier);

            // Act
            SupplierResponse response = supplierService.createSupplier(request);

            // Assert
            assertThat(response).isNotNull();
            verify(supplierRepository).save(any(Supplier.class));
        }
    }

    @Nested
    @DisplayName("Update Supplier Tests")
    class UpdateSupplierTests {

        @Test
        @DisplayName("Should update supplier successfully")
        void shouldUpdateSupplierSuccessfully() {
            // Arrange
            SupplierRequest request = new SupplierRequest();
            request.setSupplierName("Updated Supplier");
            request.setPhone("0999999999");
            request.setAddress("New Address");

            when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
            when(supplierRepository.save(any(Supplier.class))).thenReturn(supplier);

            // Act
            SupplierResponse response = supplierService.updateSupplier(1L, request);

            // Assert
            assertThat(response).isNotNull();
            verify(supplierRepository).findById(1L);
            verify(supplierRepository).save(any(Supplier.class));
        }

        @Test
        @DisplayName("Should throw exception when supplier not found")
        void shouldThrowExceptionWhenSupplierNotFound() {
            // Arrange
            SupplierRequest request = new SupplierRequest();
            when(supplierRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> supplierService.updateSupplier(999L, request))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Supplier not found");
        }

        @Test
        @DisplayName("Should update only provided fields")
        void shouldUpdateOnlyProvidedFields() {
            // Arrange
            SupplierRequest request = new SupplierRequest();
            request.setSupplierName("Updated Name Only");
            request.setPhone(null);
            request.setAddress(null);

            when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
            when(supplierRepository.save(any(Supplier.class))).thenReturn(supplier);

            // Act
            SupplierResponse response = supplierService.updateSupplier(1L, request);

            // Assert
            assertThat(response).isNotNull();
            verify(supplierRepository).save(any(Supplier.class));
        }
    }

    @Nested
    @DisplayName("Get Supplier By Id Tests")
    class GetSupplierByIdTests {

        @Test
        @DisplayName("Should get supplier by id successfully")
        void shouldGetSupplierByIdSuccessfully() {
            // Arrange
            when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));

            // Act
            SupplierResponse response = supplierService.getSupplierById(1L);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.getSupplierName()).isEqualTo("ABC Pharma Co.");
            verify(supplierRepository).findById(1L);
        }

        @Test
        @DisplayName("Should throw exception when supplier not found")
        void shouldThrowExceptionWhenSupplierNotFound() {
            // Arrange
            when(supplierRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> supplierService.getSupplierById(999L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Supplier not found");
        }
    }

    @Nested
    @DisplayName("Delete Supplier Tests")
    class DeleteSupplierTests {

        @Test
        @DisplayName("Should delete supplier successfully when no related data exists")
        void shouldDeleteSupplierSuccessfully() {
            // Arrange
            when(supplierRepository.countBatchesBySupplierId(1L)).thenReturn(0L);
            when(supplierRepository.countInventoryMovementsBySupplierId(1L)).thenReturn(0L);
            doNothing().when(supplierRepository).deleteById(1L);

            // Act
            supplierService.deleteById(1L);

            // Assert
            verify(supplierRepository).countBatchesBySupplierId(1L);
            verify(supplierRepository).countInventoryMovementsBySupplierId(1L);
            verify(supplierRepository).deleteById(1L);
        }

        @Test
        @DisplayName("Should throw EntityInUseException when batches exist")
        void shouldThrowExceptionWhenBatchesExist() {
            // Arrange
            when(supplierRepository.countBatchesBySupplierId(1L)).thenReturn(3L);
            when(supplierRepository.countInventoryMovementsBySupplierId(1L)).thenReturn(0L);

            // Act & Assert
            assertThatThrownBy(() -> supplierService.deleteById(1L))
                    .isInstanceOf(EntityInUseException.class)
                    .hasMessageContaining("Nhà cung cấp")
                    .hasMessageContaining("3 lô");
        }

        @Test
        @DisplayName("Should throw EntityInUseException when inventory movements exist")
        void shouldThrowExceptionWhenInventoryMovementsExist() {
            // Arrange
            when(supplierRepository.countBatchesBySupplierId(1L)).thenReturn(0L);
            when(supplierRepository.countInventoryMovementsBySupplierId(1L)).thenReturn(2L);

            // Act & Assert
            assertThatThrownBy(() -> supplierService.deleteById(1L))
                    .isInstanceOf(EntityInUseException.class)
                    .hasMessageContaining("Nhà cung cấp")
                    .hasMessageContaining("2 phiếu");
        }

        @Test
        @DisplayName("Should throw EntityInUseException when both batches and movements exist")
        void shouldThrowExceptionWhenBothExist() {
            // Arrange
            when(supplierRepository.countBatchesBySupplierId(1L)).thenReturn(5L);
            when(supplierRepository.countInventoryMovementsBySupplierId(1L)).thenReturn(3L);

            // Act & Assert
            assertThatThrownBy(() -> supplierService.deleteById(1L))
                    .isInstanceOf(EntityInUseException.class)
                    .hasMessageContaining("Nhà cung cấp")
                    .hasMessageContaining("5 lô")
                    .hasMessageContaining("3 phiếu");
        }
    }
}
