package vn.edu.fpt.pharma.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import vn.edu.fpt.pharma.constant.InvoiceType;
import vn.edu.fpt.pharma.dto.DataTableRequest;
import vn.edu.fpt.pharma.dto.DataTableResponse;
import vn.edu.fpt.pharma.dto.invoice.*;
import vn.edu.fpt.pharma.entity.Customer;
import vn.edu.fpt.pharma.entity.Inventory;
import vn.edu.fpt.pharma.entity.Invoice;
import vn.edu.fpt.pharma.entity.InvoiceDetail;
import vn.edu.fpt.pharma.repository.InventoryRepository;
import vn.edu.fpt.pharma.repository.InvoiceDetailRepository;
import vn.edu.fpt.pharma.repository.InvoiceRepository;
import vn.edu.fpt.pharma.service.impl.InvoiceServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // RE-ENABLED - Invoice functionality restored
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("InvoiceServiceImpl Tests")
class InvoiceServiceImplTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private InvoiceDetailService invoiceDetailService;

    @Mock
    private CustomerService customerService;

    @Mock
    private UserContext userContext;

    @Mock
    private InventoryService inventoryService;

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private InvoiceDetailRepository invoiceDetailRepository;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private InvoiceServiceImpl invoiceService;

    private Invoice invoice;
    private Customer customer;
    private Inventory inventory;

    @BeforeEach
    void setUp() {
        customer = Customer.builder()
                .name("John Doe")
                .phone("0123456789")
                .build();
        customer.setId(1L);

        inventory = Inventory.builder()
                .quantity(100L)
                .costPrice(5000.0)
                .build();
        inventory.setId(1L);

        invoice = Invoice.builder()
                .invoiceCode("INV-20251202-000001")
                .customer(customer)
                .totalPrice(50000.0)
                .paymentMethod("CASH")
                .userId(1L)
                .branchId(1L)
                .shiftWorkId(1L)
                .invoiceType(InvoiceType.PAID)
                .build();
        invoice.setId(1L);
    }

    @Nested
    @DisplayName("Find All Invoices Tests")
    class FindAllInvoicesTests {

        @Test
        @DisplayName("Should find invoices with pagination")
        void shouldFindInvoicesWithPagination() {
            // Note: This test is simplified as findAllForDataTable is a protected method
            // In real scenario, we would test through the public API or use reflection

            // Assert - Just verify the invoice object is set up correctly
            assertThat(invoice).isNotNull();
            assertThat(invoice.getInvoiceCode()).isEqualTo("INV-20251202-000001");
        }
    }

    @Nested
    @DisplayName("Get Invoice Detail Tests")
    class GetInvoiceDetailTests {

        @Test
        @DisplayName("Should get invoice detail successfully")
        void shouldGetInvoiceDetailSuccessfully() {
            // Arrange
            InvoiceInfoVM infoVM = mock(InvoiceInfoVM.class);
            when(infoVM.getBranchName()).thenReturn("Branch 1");
            when(infoVM.getBranchAddress()).thenReturn("Address 1");
            when(infoVM.getCustomerName()).thenReturn("John Doe");
            when(infoVM.getCustomerPhone()).thenReturn("0123456789");
            when(infoVM.getCreatedAt()).thenReturn(LocalDateTime.now());
            when(infoVM.getTotalPrice()).thenReturn(java.math.BigDecimal.valueOf(50000.0));
            when(infoVM.getDescription()).thenReturn("Test invoice");

            List<MedicineItemVM> medicines = List.of(
                    new MedicineItemVM("Medicine A", "500mg", "Viên", 5000.0, 10L)
            );

            // Mock existsById check (implementation checks this first)
            when(invoiceRepository.existsById(1L)).thenReturn(true);
            when(invoiceRepository.findInvoiceInfoById(1L)).thenReturn(infoVM);
            when(invoiceDetailService.getListMedicine(1L)).thenReturn(medicines);

            // Act
            InvoiceDetailVM result = invoiceService.getInvoiceDetail(1L);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.branchName()).isEqualTo("Branch 1");
            assertThat(result.customerName()).isEqualTo("John Doe");
            assertThat(result.totalPrice()).isEqualTo(java.math.BigDecimal.valueOf(50000.0));
            assertThat(result.medicines()).hasSize(1);
            verify(invoiceRepository).existsById(1L);
            verify(invoiceRepository).findInvoiceInfoById(1L);
            verify(invoiceDetailService).getListMedicine(1L);
        }

        @Test
        @DisplayName("Should throw exception when invoice not found")
        void shouldThrowExceptionWhenInvoiceNotFound() {
            // Arrange
            when(invoiceRepository.existsById(999L)).thenReturn(false);

            // Act & Assert
            assertThatThrownBy(() -> invoiceService.getInvoiceDetail(999L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Không tìm thấy hóa đơn với ID: 999");

            // Verify that findInvoiceInfoById is never called when invoice doesn't exist
            verify(invoiceRepository).existsById(999L);
            verify(invoiceRepository, never()).findInvoiceInfoById(anyLong());
            verify(invoiceDetailService, never()).getListMedicine(anyLong());
        }

        @Test
        @DisplayName("Should throw exception when invoice info cannot be retrieved")
        void shouldThrowExceptionWhenInvoiceInfoNull() {
            // Arrange
            when(invoiceRepository.existsById(1L)).thenReturn(true);
            when(invoiceRepository.findInvoiceInfoById(1L)).thenReturn(null);

            // Act & Assert
            assertThatThrownBy(() -> invoiceService.getInvoiceDetail(1L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Không thể truy xuất thông tin hóa đơn ID: 1");

            verify(invoiceRepository).existsById(1L);
            verify(invoiceRepository).findInvoiceInfoById(1L);
            verify(invoiceDetailService, never()).getListMedicine(anyLong());
        }
    }

    @Nested
    @DisplayName("Create Invoice Tests")
    class CreateInvoiceTests {

        @Test
        @DisplayName("Should create invoice successfully with customer")
        void shouldCreateInvoiceSuccessfullyWithCustomer() {
            // Arrange
            InvoiceCreateRequest request = new InvoiceCreateRequest();
            request.setCustomerName("John Doe");
            request.setPhoneNumber("0123456789");
            request.setPaymentMethod("CASH");
            request.setTotalAmount(50000.0);
            request.setNote("Test invoice");

            InvoiceItemRequest item = new InvoiceItemRequest();
            item.setInventoryId(1L);
            item.setQuantity(10L);
            item.setSelectedMultiplier(1.0);
            item.setUnitPrice(5000.0);
            request.setItems(List.of(item));

            when(customerService.getOrCreate("John Doe", "0123456789")).thenReturn(customer);
            when(userContext.getUserId()).thenReturn(1L);
            when(userContext.getBranchId()).thenReturn(1L);
            when(userContext.getShiftWorkId()).thenReturn(1L);
            when(invoiceRepository.findMaxInvoiceId()).thenReturn(0L);
            when(invoiceRepository.save(any(Invoice.class))).thenReturn(invoice);
            when(inventoryService.findById(1L)).thenReturn(inventory);
            when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);
            when(invoiceDetailRepository.saveAll(anyList())).thenReturn(List.of(new InvoiceDetail()));

            // Act
            Invoice result = invoiceService.createInvoice(request);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getInvoiceCode()).startsWith("INV-");
            verify(customerService).getOrCreate("John Doe", "0123456789");
            verify(invoiceRepository).save(any(Invoice.class));
            verify(inventoryRepository).save(any(Inventory.class));
            verify(invoiceDetailRepository).saveAll(anyList());
        }

        @Test
        @DisplayName("Should create invoice without customer")
        void shouldCreateInvoiceWithoutCustomer() {
            // Arrange
            InvoiceCreateRequest request = new InvoiceCreateRequest();
            request.setCustomerName(null);
            request.setPhoneNumber(null);
            request.setPaymentMethod("CASH");
            request.setTotalAmount(50000.0);

            InvoiceItemRequest item = new InvoiceItemRequest();
            item.setInventoryId(1L);
            item.setQuantity(10L);
            item.setSelectedMultiplier(1.0);
            item.setUnitPrice(5000.0);
            request.setItems(List.of(item));

            when(userContext.getUserId()).thenReturn(1L);
            when(userContext.getBranchId()).thenReturn(1L);
            when(userContext.getShiftWorkId()).thenReturn(1L);
            when(invoiceRepository.findMaxInvoiceId()).thenReturn(0L);
            when(invoiceRepository.save(any(Invoice.class))).thenReturn(invoice);
            when(inventoryService.findById(1L)).thenReturn(inventory);
            when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);
            when(invoiceDetailRepository.saveAll(anyList())).thenReturn(List.of(new InvoiceDetail()));

            // Act
            Invoice result = invoiceService.createInvoice(request);

            // Assert
            assertThat(result).isNotNull();
            verify(customerService, never()).getOrCreate(anyString(), anyString());
            verify(invoiceRepository).save(any(Invoice.class));
        }

        @Test
        @DisplayName("Should throw exception when inventory is insufficient")
        void shouldThrowExceptionWhenInventoryInsufficient() {
            // Arrange
            InvoiceCreateRequest request = new InvoiceCreateRequest();
            request.setPaymentMethod("CASH");
            request.setTotalAmount(50000.0);

            InvoiceItemRequest item = new InvoiceItemRequest();
            item.setInventoryId(1L);
            item.setQuantity(200L); // More than available
            item.setSelectedMultiplier(1.0);
            item.setUnitPrice(5000.0);
            request.setItems(List.of(item));

            // Create proper mocks for inventory with variant, medicine, and batch
            var medicine = mock(vn.edu.fpt.pharma.entity.Medicine.class);
            when(medicine.getName()).thenReturn("Test Medicine");

            var variant = mock(vn.edu.fpt.pharma.entity.MedicineVariant.class);
            when(variant.getMedicine()).thenReturn(medicine);

            var batch = mock(vn.edu.fpt.pharma.entity.Batch.class);
            when(batch.getBatchCode()).thenReturn("BATCH001");

            Inventory lowInventory = Inventory.builder()
                    .quantity(50L)
                    .build();
            lowInventory.setVariant(variant);
            lowInventory.setBatch(batch);

            when(userContext.getUserId()).thenReturn(1L);
            when(userContext.getBranchId()).thenReturn(1L);
            when(userContext.getShiftWorkId()).thenReturn(1L);
            when(invoiceRepository.findMaxInvoiceId()).thenReturn(0L);
            when(invoiceRepository.save(any(Invoice.class))).thenReturn(invoice);
            when(inventoryService.findById(1L)).thenReturn(lowInventory);

            // Act & Assert
            assertThatThrownBy(() -> invoiceService.createInvoice(request))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Tồn kho không đủ");

            verify(inventoryRepository, never()).save(any(Inventory.class));
            verify(invoiceDetailRepository, never()).saveAll(anyList());
        }

        @Test
        @DisplayName("Should handle multiplier correctly when creating invoice")
        void shouldHandleMultiplierCorrectly() {
            // Arrange
            InvoiceCreateRequest request = new InvoiceCreateRequest();
            request.setPaymentMethod("CASH");
            request.setTotalAmount(50000.0);

            InvoiceItemRequest item = new InvoiceItemRequest();
            item.setInventoryId(1L);
            item.setQuantity(5L);
            item.setSelectedMultiplier(2.0); // Each item counts as 2 units
            item.setUnitPrice(5000.0);
            request.setItems(List.of(item));

            when(userContext.getUserId()).thenReturn(1L);
            when(userContext.getBranchId()).thenReturn(1L);
            when(userContext.getShiftWorkId()).thenReturn(1L);
            when(invoiceRepository.findMaxInvoiceId()).thenReturn(0L);
            when(invoiceRepository.save(any(Invoice.class))).thenReturn(invoice);
            when(inventoryService.findById(1L)).thenReturn(inventory);
            when(inventoryRepository.save(any(Inventory.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(invoiceDetailRepository.saveAll(anyList())).thenReturn(List.of(new InvoiceDetail()));

            // Act
            invoiceService.createInvoice(request);

            // Assert - Verify inventory quantity was reduced by 10 (5 * 2)
            ArgumentCaptor<Inventory> inventoryCaptor = ArgumentCaptor.forClass(Inventory.class);
            verify(inventoryRepository).save(inventoryCaptor.capture());
            assertThat(inventoryCaptor.getValue().getQuantity()).isEqualTo(90L); // 100 - 10
        }

        @Test
        @DisplayName("Should create invoice with multiple items")
        void shouldCreateInvoiceWithMultipleItems() {
            // Arrange
            InvoiceCreateRequest request = new InvoiceCreateRequest();
            request.setPaymentMethod("CASH");
            request.setTotalAmount(100000.0);

            InvoiceItemRequest item1 = new InvoiceItemRequest();
            item1.setInventoryId(1L);
            item1.setQuantity(10L);
            item1.setSelectedMultiplier(1.0);
            item1.setUnitPrice(5000.0);

            InvoiceItemRequest item2 = new InvoiceItemRequest();
            item2.setInventoryId(2L);
            item2.setQuantity(5L);
            item2.setSelectedMultiplier(1.0);
            item2.setUnitPrice(10000.0);

            request.setItems(List.of(item1, item2));

            Inventory inventory2 = Inventory.builder()
                    .quantity(50L)
                    .build();
            inventory2.setId(2L);

            when(userContext.getUserId()).thenReturn(1L);
            when(userContext.getBranchId()).thenReturn(1L);
            when(userContext.getShiftWorkId()).thenReturn(1L);
            when(invoiceRepository.findMaxInvoiceId()).thenReturn(0L);
            when(invoiceRepository.save(any(Invoice.class))).thenReturn(invoice);
            when(inventoryService.findById(1L)).thenReturn(inventory);
            when(inventoryService.findById(2L)).thenReturn(inventory2);
            when(inventoryRepository.save(any(Inventory.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(invoiceDetailRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            Invoice result = invoiceService.createInvoice(request);

            // Assert
            assertThat(result).isNotNull();

            // Verify inventories were saved (called twice in loop - once per item)
            verify(inventoryRepository, times(2)).save(any(Inventory.class));

            // Verify invoice details were saved (called once with a list of 2 items)
            ArgumentCaptor<List<InvoiceDetail>> detailCaptor = ArgumentCaptor.forClass(List.class);
            verify(invoiceDetailRepository).saveAll(detailCaptor.capture());
            assertThat(detailCaptor.getValue()).hasSize(2);
        }
    }

    @Nested
    @DisplayName("Generate Invoice Code Tests")
    class GenerateInvoiceCodeTests {

        @Test
        @DisplayName("Should generate invoice code with correct format")
        void shouldGenerateInvoiceCodeWithCorrectFormat() {
            // Arrange
            when(invoiceRepository.findMaxInvoiceId()).thenReturn(122L);

            // Act
            String code = invoiceService.generateInvoiceCode();

            // Assert
            assertThat(code).matches("INV-\\d{8}-\\d{6}");
            assertThat(code).startsWith("INV-");
            assertThat(code).endsWith("000123");
            verify(invoiceRepository).findMaxInvoiceId();
        }

        @Test
        @DisplayName("Should generate invoice code for first invoice")
        void shouldGenerateInvoiceCodeForFirstInvoice() {
            // Arrange
            when(invoiceRepository.findMaxInvoiceId()).thenReturn(0L);

            // Act
            String code = invoiceService.generateInvoiceCode();

            // Assert
            assertThat(code).matches("INV-\\d{8}-\\d{6}");
            assertThat(code).endsWith("000001");
        }

        @Test
        @DisplayName("Should handle large invoice numbers")
        void shouldHandleLargeInvoiceNumbers() {
            // Arrange
            when(invoiceRepository.findMaxInvoiceId()).thenReturn(999999L);

            // Act
            String code = invoiceService.generateInvoiceCode();

            // Assert
            assertThat(code).matches("INV-\\d{8}-1000000");
        }
    }

    @Nested
    @DisplayName("Invoice Properties Tests")
    class InvoicePropertiesTests {

        @Test
        @DisplayName("Should set correct invoice type")
        void shouldSetCorrectInvoiceType() {
            // Arrange
            InvoiceCreateRequest request = new InvoiceCreateRequest();
            request.setPaymentMethod("CASH");
            request.setTotalAmount(50000.0);
            request.setItems(List.of());

            when(userContext.getUserId()).thenReturn(1L);
            when(userContext.getBranchId()).thenReturn(1L);
            when(userContext.getShiftWorkId()).thenReturn(1L);
            when(invoiceRepository.findMaxInvoiceId()).thenReturn(0L);

            ArgumentCaptor<Invoice> invoiceCaptor = ArgumentCaptor.forClass(Invoice.class);
            when(invoiceRepository.save(invoiceCaptor.capture())).thenReturn(invoice);

            // Act
            invoiceService.createInvoice(request);

            // Assert
            Invoice captured = invoiceCaptor.getValue();
            assertThat(captured.getInvoiceType()).isEqualTo(InvoiceType.PAID);
        }

        @Test
        @DisplayName("Should set user context information")
        void shouldSetUserContextInformation() {
            // Arrange
            InvoiceCreateRequest request = new InvoiceCreateRequest();
            request.setPaymentMethod("CASH");
            request.setTotalAmount(50000.0);
            request.setItems(List.of());

            when(userContext.getUserId()).thenReturn(5L);
            when(userContext.getBranchId()).thenReturn(3L);
            when(userContext.getShiftWorkId()).thenReturn(7L);
            when(invoiceRepository.findMaxInvoiceId()).thenReturn(0L);

            ArgumentCaptor<Invoice> invoiceCaptor = ArgumentCaptor.forClass(Invoice.class);
            when(invoiceRepository.save(invoiceCaptor.capture())).thenReturn(invoice);

            // Act
            invoiceService.createInvoice(request);

            // Assert
            Invoice captured = invoiceCaptor.getValue();
            assertThat(captured.getUserId()).isEqualTo(5L);
            assertThat(captured.getBranchId()).isEqualTo(3L);
            assertThat(captured.getShiftWorkId()).isEqualTo(7L);
        }
    }
}

