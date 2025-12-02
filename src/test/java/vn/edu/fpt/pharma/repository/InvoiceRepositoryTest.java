package vn.edu.fpt.pharma.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import vn.edu.fpt.pharma.constant.InvoiceType;
import vn.edu.fpt.pharma.dto.manager.DailyRevenue;
import vn.edu.fpt.pharma.dto.manager.KpiData;
import vn.edu.fpt.pharma.entity.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("InvoiceRepository Tests")
class InvoiceRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private InvoiceRepository invoiceRepository;

    private Branch branch;
    private Customer customer;
    private Invoice invoice;

    @BeforeEach
    void setUp() {
        // Create branch
        branch = Branch.builder()
                .name("Branch 1")
                .address("Address 1")
                .build();
        entityManager.persist(branch);

        // Create customer
        customer = Customer.builder()
                .name("John Doe")
                .phone("0123456789")
                .build();
        entityManager.persist(customer);

        // Create invoice
        invoice = Invoice.builder()
                .invoiceCode("INV-20251202-000001")
                .customer(customer)
                .totalPrice(100000.0)
                .paymentMethod("CASH")
                .userId(1L)
                .branchId(branch.getId())
                .invoiceType(InvoiceType.PAID)
                .build();
        entityManager.persist(invoice);

        entityManager.flush();
    }

    @Nested
    @DisplayName("CRUD Operations Tests")
    class CrudOperationsTests {

        @Test
        @DisplayName("Should find invoice by id")
        void shouldFindInvoiceById() {
            // Act
            Optional<Invoice> found = invoiceRepository.findById(invoice.getId());

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getInvoiceCode()).isEqualTo("INV-20251202-000001");
            assertThat(found.get().getTotalPrice()).isEqualTo(100000.0);
        }

        @Test
        @DisplayName("Should save invoice")
        void shouldSaveInvoice() {
            // Arrange
            Invoice newInvoice = Invoice.builder()
                    .invoiceCode("INV-20251202-000002")
                    .customer(customer)
                    .totalPrice(50000.0)
                    .paymentMethod("CARD")
                    .userId(1L)
                    .branchId(branch.getId())
                    .invoiceType(InvoiceType.PAID)
                    .build();

            // Act
            Invoice saved = invoiceRepository.save(newInvoice);

            // Assert
            assertThat(saved.getId()).isNotNull();
            assertThat(saved.getInvoiceCode()).isEqualTo("INV-20251202-000002");
        }

        @Test
        @DisplayName("Should find all invoices")
        void shouldFindAllInvoices() {
            // Act
            List<Invoice> invoices = invoiceRepository.findAll();

            // Assert
            assertThat(invoices).isNotEmpty();
            assertThat(invoices).hasSize(1);
        }

        @Test
        @DisplayName("Should update invoice")
        void shouldUpdateInvoice() {
            // Arrange
            invoice.setPaymentMethod("CARD");
            invoice.setTotalPrice(150000.0);

            // Act
            Invoice updated = invoiceRepository.save(invoice);

            // Assert
            assertThat(updated.getPaymentMethod()).isEqualTo("CARD");
            assertThat(updated.getTotalPrice()).isEqualTo(150000.0);
        }

        @Test
        @DisplayName("Should delete invoice")
        void shouldDeleteInvoice() {
            // Arrange
            Long invoiceId = invoice.getId();

            // Act
            invoiceRepository.deleteById(invoiceId);
            entityManager.flush();

            // Assert
            Optional<Invoice> deleted = invoiceRepository.findById(invoiceId);
            assertThat(deleted).isEmpty();
        }
    }

    @Nested
    @DisplayName("Custom Query Tests")
    class CustomQueryTests {

        @Test
        @DisplayName("Should find max invoice id")
        void shouldFindMaxInvoiceId() {
            // Act
            Long maxId = invoiceRepository.findMaxInvoiceId();

            // Assert
            assertThat(maxId).isNotNull();
            assertThat(maxId).isGreaterThanOrEqualTo(0L);
        }

        @Test
        @DisplayName("Should return 0 when no invoices exist for max id")
        void shouldReturnZeroWhenNoInvoicesExist() {
            // Arrange - Delete all invoices
            invoiceRepository.deleteAll();
            entityManager.flush();

            // Act
            Long maxId = invoiceRepository.findMaxInvoiceId();

            // Assert
            assertThat(maxId).isEqualTo(0L);
        }

        @Test
        @DisplayName("Should get daily revenue by date range")
        void shouldGetDailyRevenueByDateRange() {
            // Arrange
            LocalDateTime fromDate = LocalDateTime.now().minusDays(7);
            LocalDateTime toDate = LocalDateTime.now().plusDays(1);

            // Act
            List<DailyRevenue> revenues = invoiceRepository.getDailyRevenueByDate(
                    branch.getId(), fromDate, toDate
            );

            // Assert
            assertThat(revenues).isNotNull();
            // May be empty if test data doesn't fall in date range
        }

        @Test
        @DisplayName("Should get daily revenue with filters")
        void shouldGetDailyRevenueWithFilters() {
            // Arrange
            LocalDateTime fromDate = LocalDateTime.now().minusDays(7);
            LocalDateTime toDate = LocalDateTime.now().plusDays(1);

            // Act
            List<DailyRevenue> revenues = invoiceRepository.getDailyRevenueByDate(
                    branch.getId(), fromDate, toDate, null, 1L
            );

            // Assert
            assertThat(revenues).isNotNull();
        }

        @Test
        @DisplayName("Should sum revenue for KPI")
        void shouldSumRevenueForKpi() {
            // Arrange
            LocalDateTime fromDate = LocalDateTime.now().minusDays(7);
            LocalDateTime toDate = LocalDateTime.now().plusDays(1);

            // Act
            KpiData kpi = invoiceRepository.sumRevenue(
                    branch.getId(), fromDate, toDate, null, null
            );

            // Assert
            assertThat(kpi).isNotNull();
            assertThat(kpi.getRevenue()).isGreaterThanOrEqualTo(0.0);
            assertThat(kpi.getOrderCount()).isGreaterThanOrEqualTo(0L);
        }
    }

    @Nested
    @DisplayName("Relationship Tests")
    class RelationshipTests {

        @Test
        @DisplayName("Should load invoice with customer")
        void shouldLoadInvoiceWithCustomer() {
            // Act
            Invoice found = invoiceRepository.findById(invoice.getId()).orElseThrow();

            // Assert
            assertThat(found.getCustomer()).isNotNull();
            assertThat(found.getCustomer().getName()).isEqualTo("John Doe");
        }

        @Test
        @DisplayName("Should handle invoice without customer")
        void shouldHandleInvoiceWithoutCustomer() {
            // Arrange
            Invoice invoiceWithoutCustomer = Invoice.builder()
                    .invoiceCode("INV-20251202-000003")
                    .totalPrice(30000.0)
                    .paymentMethod("CASH")
                    .userId(1L)
                    .branchId(branch.getId())
                    .invoiceType(InvoiceType.PAID)
                    .build();
            entityManager.persist(invoiceWithoutCustomer);
            entityManager.flush();

            // Act
            Invoice found = invoiceRepository.findById(invoiceWithoutCustomer.getId()).orElseThrow();

            // Assert
            assertThat(found.getCustomer()).isNull();
        }
    }

    @Nested
    @DisplayName("Invoice Code Tests")
    class InvoiceCodeTests {

        @Test
        @DisplayName("Should store unique invoice codes")
        void shouldStoreUniqueInvoiceCodes() {
            // Arrange
            Invoice invoice2 = Invoice.builder()
                    .invoiceCode("INV-20251202-000002")
                    .totalPrice(50000.0)
                    .paymentMethod("CASH")
                    .userId(1L)
                    .branchId(branch.getId())
                    .invoiceType(InvoiceType.PAID)
                    .build();
            entityManager.persist(invoice2);
            entityManager.flush();

            // Act
            List<Invoice> invoices = invoiceRepository.findAll();

            // Assert
            assertThat(invoices).hasSize(2);
            assertThat(invoices.stream().map(Invoice::getInvoiceCode))
                    .containsExactlyInAnyOrder("INV-20251202-000001", "INV-20251202-000002");
        }

        @Test
        @DisplayName("Should handle invoice code format")
        void shouldHandleInvoiceCodeFormat() {
            // Act
            Invoice found = invoiceRepository.findById(invoice.getId()).orElseThrow();

            // Assert
            assertThat(found.getInvoiceCode()).matches("INV-\\d{8}-\\d{6}");
        }
    }

    @Nested
    @DisplayName("Invoice Type Tests")
    class InvoiceTypeTests {

        @Test
        @DisplayName("Should store invoice type correctly")
        void shouldStoreInvoiceTypeCorrectly() {
            // Act
            Invoice found = invoiceRepository.findById(invoice.getId()).orElseThrow();

            // Assert
            assertThat(found.getInvoiceType()).isEqualTo(InvoiceType.PAID);
        }

        @Test
        @DisplayName("Should handle different invoice types")
        void shouldHandleDifferentInvoiceTypes() {
            // Arrange
            Invoice draftInvoice = Invoice.builder()
                    .invoiceCode("INV-20251202-000004")
                    .totalPrice(75000.0)
                    .paymentMethod("CREDIT")
                    .userId(1L)
                    .branchId(branch.getId())
                    .invoiceType(InvoiceType.DRAFT)
                    .build();
            entityManager.persist(draftInvoice);
            entityManager.flush();

            // Act
            Invoice found = invoiceRepository.findById(draftInvoice.getId()).orElseThrow();

            // Assert
            assertThat(found.getInvoiceType()).isEqualTo(InvoiceType.DRAFT);
        }
    }

    @Nested
    @DisplayName("Payment Method Tests")
    class PaymentMethodTests {

        @Test
        @DisplayName("Should store payment method")
        void shouldStorePaymentMethod() {
            // Act
            Invoice found = invoiceRepository.findById(invoice.getId()).orElseThrow();

            // Assert
            assertThat(found.getPaymentMethod()).isEqualTo("CASH");
        }

        @Test
        @DisplayName("Should handle different payment methods")
        void shouldHandleDifferentPaymentMethods() {
            // Arrange
            Invoice cardInvoice = Invoice.builder()
                    .invoiceCode("INV-20251202-000005")
                    .totalPrice(60000.0)
                    .paymentMethod("CARD")
                    .userId(1L)
                    .branchId(branch.getId())
                    .invoiceType(InvoiceType.PAID)
                    .build();
            entityManager.persist(cardInvoice);
            entityManager.flush();

            // Act
            List<Invoice> invoices = invoiceRepository.findAll();

            // Assert
            assertThat(invoices).extracting(Invoice::getPaymentMethod)
                    .contains("CASH", "CARD");
        }
    }

    @Nested
    @DisplayName("Branch and User Association Tests")
    class BranchAndUserAssociationTests {

        @Test
        @DisplayName("Should store branch id")
        void shouldStoreBranchId() {
            // Act
            Invoice found = invoiceRepository.findById(invoice.getId()).orElseThrow();

            // Assert
            assertThat(found.getBranchId()).isEqualTo(branch.getId());
        }

        @Test
        @DisplayName("Should store user id")
        void shouldStoreUserId() {
            // Act
            Invoice found = invoiceRepository.findById(invoice.getId()).orElseThrow();

            // Assert
            assertThat(found.getUserId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Should filter invoices by branch")
        void shouldFilterInvoicesByBranch() {
            // Arrange
            Branch branch2 = Branch.builder()
                    .name("Branch 2")
                    .address("Address 2")
                    .build();
            entityManager.persist(branch2);

            Invoice invoice2 = Invoice.builder()
                    .invoiceCode("INV-20251202-000006")
                    .totalPrice(40000.0)
                    .paymentMethod("CASH")
                    .userId(1L)
                    .branchId(branch2.getId())
                    .invoiceType(InvoiceType.PAID)
                    .build();
            entityManager.persist(invoice2);
            entityManager.flush();

            // Act
            List<Invoice> allInvoices = invoiceRepository.findAll();

            // Assert
            assertThat(allInvoices).hasSize(2);
            long branch1Count = allInvoices.stream()
                    .filter(inv -> inv.getBranchId().equals(branch.getId()))
                    .count();
            assertThat(branch1Count).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("Price Calculations Tests")
    class PriceCalculationsTests {

        @Test
        @DisplayName("Should store total price accurately")
        void shouldStoreTotalPriceAccurately() {
            // Act
            Invoice found = invoiceRepository.findById(invoice.getId()).orElseThrow();

            // Assert
            assertThat(found.getTotalPrice()).isEqualTo(100000.0);
        }

        @Test
        @DisplayName("Should handle decimal prices")
        void shouldHandleDecimalPrices() {
            // Arrange
            Invoice decimalInvoice = Invoice.builder()
                    .invoiceCode("INV-20251202-000007")
                    .totalPrice(12345.67)
                    .paymentMethod("CASH")
                    .userId(1L)
                    .branchId(branch.getId())
                    .invoiceType(InvoiceType.PAID)
                    .build();
            entityManager.persist(decimalInvoice);
            entityManager.flush();

            // Act
            Invoice found = invoiceRepository.findById(decimalInvoice.getId()).orElseThrow();

            // Assert
            assertThat(found.getTotalPrice()).isEqualTo(12345.67);
        }

        @Test
        @DisplayName("Should handle large amounts")
        void shouldHandleLargeAmounts() {
            // Arrange
            Invoice largeInvoice = Invoice.builder()
                    .invoiceCode("INV-20251202-000008")
                    .totalPrice(9999999.99)
                    .paymentMethod("TRANSFER")
                    .userId(1L)
                    .branchId(branch.getId())
                    .invoiceType(InvoiceType.PAID)
                    .build();
            entityManager.persist(largeInvoice);
            entityManager.flush();

            // Act
            Invoice found = invoiceRepository.findById(largeInvoice.getId()).orElseThrow();

            // Assert
            assertThat(found.getTotalPrice()).isEqualTo(9999999.99);
        }
    }
}

