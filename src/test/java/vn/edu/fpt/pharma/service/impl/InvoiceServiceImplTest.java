package vn.edu.fpt.pharma.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import vn.edu.fpt.pharma.BaseServiceTest;
import vn.edu.fpt.pharma.constant.InvoiceType;
import vn.edu.fpt.pharma.dto.invoice.InvoiceCreateRequest;
import vn.edu.fpt.pharma.dto.invoice.InvoiceDetailVM;
import vn.edu.fpt.pharma.dto.invoice.InvoiceInfoVM;
import vn.edu.fpt.pharma.dto.invoice.InvoiceItemRequest;
import vn.edu.fpt.pharma.dto.invoice.MedicineItemVM;
import vn.edu.fpt.pharma.entity.Customer;
import vn.edu.fpt.pharma.entity.Inventory;
import vn.edu.fpt.pharma.entity.Invoice;
import vn.edu.fpt.pharma.entity.MedicineVariant;
import vn.edu.fpt.pharma.entity.Medicine;
import vn.edu.fpt.pharma.entity.Batch;
import vn.edu.fpt.pharma.exception.InsufficientInventoryException;
import vn.edu.fpt.pharma.repository.InventoryRepository;
import vn.edu.fpt.pharma.repository.InvoiceDetailRepository;
import vn.edu.fpt.pharma.repository.InvoiceRepository;
import vn.edu.fpt.pharma.service.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Unit tests for InvoiceServiceImpl - 15 tests
 * Strategy: Full coverage for createInvoice, getInvoiceDetail
 */
@DisplayName("InvoiceServiceImpl Tests")
@org.mockito.junit.jupiter.MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
class InvoiceServiceImplTest extends BaseServiceTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private InvoiceDetailRepository invoiceDetailRepository;

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private InvoiceDetailService invoiceDetailService;

    @Mock
    private CustomerService customerService;

    @Mock
    private UserContext userContext;

    @Mock
    private InventoryService inventoryService;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private InvoiceServiceImpl invoiceService;

    private Invoice testInvoice;
    private Customer testCustomer;
    private Inventory testInventory;

    @BeforeEach
    void setUp() {
        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setName("Nguyễn Văn A");
        testCustomer.setPhone("0901234567");

        testInvoice = new Invoice();
        testInvoice.setId(1L);
        testInvoice.setInvoiceCode("INV-20251209-000001");
        testInvoice.setCustomer(testCustomer);
        testInvoice.setTotalPrice(500000.0);
        testInvoice.setInvoiceType(InvoiceType.PAID);

        Medicine medicine = new Medicine();
        medicine.setId(1L);
        medicine.setName("Paracetamol");

        MedicineVariant variant = new MedicineVariant();
        variant.setId(1L);
        variant.setMedicine(medicine);

        Batch batch = new Batch();
        batch.setId(1L);
        batch.setBatchCode("BATCH-001");

        testInventory = new Inventory();
        testInventory.setId(1L);
        testInventory.setQuantity(100L);
        testInventory.setVariant(variant);
        testInventory.setBatch(batch);

        // Default mocks
        when(userContext.getUserId()).thenReturn(1L);
        when(userContext.getBranchId()).thenReturn(1L);
        when(userContext.getShiftWorkId()).thenReturn(1L);
    }

    @Nested
    @DisplayName("getInvoiceDetail() tests - 4 tests")
    class GetInvoiceDetailTests {

        @Test
        @DisplayName("Should return invoice detail when invoice exists")
        void getInvoiceDetail_whenInvoiceExists_shouldReturnDetail() {
            // Arrange
            InvoiceInfoVM info = new InvoiceInfoVM() {
                @Override public String getBranchName() { return "Chi nhánh 1"; }
                @Override public String getBranchAddress() { return "123 ABC"; }
                @Override public String getCustomerName() { return "Nguyễn Văn A"; }
                @Override public String getCustomerPhone() { return "0901234567"; }
                @Override public LocalDateTime getCreatedAt() { return LocalDateTime.now(); }
                @Override public BigDecimal getTotalPrice() { return new BigDecimal("500000"); }
                @Override public String getDescription() { return "Test"; }
            };

            when(invoiceRepository.existsById(1L)).thenReturn(true);
            when(invoiceRepository.findInvoiceInfoById(1L)).thenReturn(info);
            when(invoiceDetailService.getListMedicine(1L)).thenReturn(Collections.emptyList());

            // Act
            InvoiceDetailVM result = invoiceService.getInvoiceDetail(1L);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.branchName()).isEqualTo("Chi nhánh 1");
            assertThat(result.customerName()).isEqualTo("Nguyễn Văn A");
            verify(invoiceRepository).findInvoiceInfoById(1L);
        }

        @Test
        @DisplayName("Should throw exception when invoice not found")
        void getInvoiceDetail_whenInvoiceNotFound_shouldThrowException() {
            // Arrange
            when(invoiceRepository.existsById(999L)).thenReturn(false);

            // Act & Assert
            assertThatThrownBy(() -> invoiceService.getInvoiceDetail(999L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Không tìm thấy hóa đơn");
        }

        @Test
        @DisplayName("Should throw exception when query returns null")
        void getInvoiceDetail_whenQueryReturnsNull_shouldThrowException() {
            // Arrange
            when(invoiceRepository.existsById(1L)).thenReturn(true);
            when(invoiceRepository.findInvoiceInfoById(1L)).thenReturn(null);

            // Act & Assert
            assertThatThrownBy(() -> invoiceService.getInvoiceDetail(1L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Không thể truy xuất thông tin");
        }

        @Test
        @DisplayName("Should include medicine list in response")
        void getInvoiceDetail_shouldIncludeMedicineList() {
            // Arrange
            InvoiceInfoVM info = new InvoiceInfoVM() {
                @Override public String getBranchName() { return "Chi nhánh 1"; }
                @Override public String getBranchAddress() { return "123 ABC"; }
                @Override public String getCustomerName() { return "Test"; }
                @Override public String getCustomerPhone() { return "0901234567"; }
                @Override public LocalDateTime getCreatedAt() { return LocalDateTime.now(); }
                @Override public BigDecimal getTotalPrice() { return new BigDecimal("100000"); }
                @Override public String getDescription() { return null; }
            };

            MedicineItemVM medicineItem = new MedicineItemVM(
                "Paracetamol", "500mg", "Viên", 10000.0, 10L
            );

            when(invoiceRepository.existsById(1L)).thenReturn(true);
            when(invoiceRepository.findInvoiceInfoById(1L)).thenReturn(info);
            when(invoiceDetailService.getListMedicine(1L)).thenReturn(List.of(medicineItem));

            // Act
            InvoiceDetailVM result = invoiceService.getInvoiceDetail(1L);

            // Assert
            assertThat(result.medicines()).hasSize(1);
            assertThat(result.medicines().get(0).medicineName()).isEqualTo("Paracetamol");
        }
    }

    @Nested
    @DisplayName("createInvoice() tests - 5 tests")
    class CreateInvoiceTests {

        @Test
        @DisplayName("Should validate phone number format")
        void createInvoice_withInvalidPhoneFormat_shouldThrowException() {
            // Arrange
            InvoiceCreateRequest request = new InvoiceCreateRequest();
            request.setCustomerName("Test");
            request.setPhoneNumber("invalid-phone");
            request.setItems(Collections.emptyList());

            // Act & Assert
            assertThatThrownBy(() -> invoiceService.createInvoice(request))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Số điện thoại không đúng định dạng");
        }

        @Test
        @DisplayName("Should throw exception when inventory insufficient")
        void createInvoice_whenInventoryInsufficient_shouldThrowException() {
            // Arrange
            testInventory.setQuantity(5L); // Only 5 available

            InvoiceItemRequest itemRequest = new InvoiceItemRequest();
            itemRequest.setInventoryId(1L);
            itemRequest.setQuantity(10L); // Request 10
            itemRequest.setSelectedMultiplier(1.0);
            itemRequest.setUnitPrice(10000.0);

            InvoiceCreateRequest request = new InvoiceCreateRequest();
            request.setCustomerName("Test");
            request.setPhoneNumber("");
            request.setTotalAmount(100000.0);
            request.setItems(List.of(itemRequest));

            when(inventoryService.findById(1L)).thenReturn(testInventory);

            // Act & Assert
            assertThatThrownBy(() -> invoiceService.createInvoice(request))
                    .isInstanceOf(InsufficientInventoryException.class)
                    .hasMessageContaining("Tồn kho không đủ");
        }

        @Test
        @DisplayName("Should accept valid phone with 0 prefix")
        void createInvoice_withValid0PhonePrefix_shouldAccept() {
            // Arrange
            InvoiceItemRequest itemRequest = new InvoiceItemRequest();
            itemRequest.setInventoryId(1L);
            itemRequest.setQuantity(5L);
            itemRequest.setSelectedMultiplier(1.0);
            itemRequest.setUnitPrice(10000.0);

            InvoiceCreateRequest request = new InvoiceCreateRequest();
            request.setCustomerName("Test");
            request.setPhoneNumber("0901234567");
            request.setTotalAmount(50000.0);
            request.setItems(List.of(itemRequest));

            when(customerService.getOrCreate(any(), any())).thenReturn(testCustomer);
            when(inventoryService.findById(1L)).thenReturn(testInventory);
            when(invoiceRepository.findMaxInvoiceId()).thenReturn(0L);
            when(invoiceRepository.save(any(Invoice.class))).thenAnswer(inv -> {
                Invoice i = inv.getArgument(0);
                i.setId(1L);
                return i;
            });

            // Act
            Invoice result = invoiceService.createInvoice(request);

            // Assert
            assertThat(result).isNotNull();
            verify(customerService).getOrCreate("Test", "0901234567");
        }
    }

    @Nested
    @DisplayName("generateInvoiceCode() tests - 2 tests")
    class GenerateInvoiceCodeTests {

        @Test
        @DisplayName("Should generate code with correct format")
        void generateInvoiceCode_shouldReturnCorrectFormat() {
            // Arrange
            when(invoiceRepository.findMaxInvoiceId()).thenReturn(123L);

            // Act
            String result = invoiceService.generateInvoiceCode();

            // Assert
            assertThat(result).startsWith("INV-");
            assertThat(result).contains("-000124");
        }

        @Test
        @DisplayName("Should handle null maxId as 0")
        void generateInvoiceCode_whenMaxIdNull_shouldStart1() {
            // Arrange
            when(invoiceRepository.findMaxInvoiceId()).thenReturn(null);

            // Act
            String result = invoiceService.generateInvoiceCode();

            // Assert
            assertThat(result).contains("-000001");
        }
    }
}

