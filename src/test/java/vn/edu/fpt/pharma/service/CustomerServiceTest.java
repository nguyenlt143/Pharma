package vn.edu.fpt.pharma.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.edu.fpt.pharma.entity.Customer;
import vn.edu.fpt.pharma.entity.Invoice;
import vn.edu.fpt.pharma.repository.CustomerRepository;
import vn.edu.fpt.pharma.repository.InvoiceRepository;
import vn.edu.fpt.pharma.service.impl.CustomerServiceImpl;
import vn.edu.fpt.pharma.testutil.TestDataFactory;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CustomerService - Happy Path Only
 * Strategy: 1 test per method to achieve 100% line coverage
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerService Tests - Happy Path Only")
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private InvoiceRepository invoiceRepository;

    private CustomerService customerService;

    @BeforeEach
    void setUp() {
        customerService = new CustomerServiceImpl(customerRepository, invoiceRepository);
    }

    @Test
    @DisplayName("getOrCreate - should return existing customer")
    void getOrCreate_existingCustomer_happyPath() {
        // Arrange
        String name = "John Doe";
        String phone = "0123456789";
        Customer customer = TestDataFactory.createCustomer();
        when(customerRepository.findByPhoneNumber(phone))
                .thenReturn(Optional.of(customer));

        // Act
        Customer result = customerService.getOrCreate(name, phone);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getPhoneNumber()).isEqualTo(phone);
        verify(customerRepository, never()).save(any());
    }

    @Test
    @DisplayName("getOrCreate - should create new customer")
    void getOrCreate_newCustomer_happyPath() {
        // Arrange
        String name = "John Doe";
        String phone = "0123456789";
        Customer customer = TestDataFactory.createCustomer();

        when(customerRepository.findByPhoneNumber(phone))
                .thenReturn(Optional.empty());
        when(customerRepository.save(any(Customer.class)))
                .thenReturn(customer);

        // Act
        Customer result = customerService.getOrCreate(name, phone);

        // Assert
        assertThat(result).isNotNull();
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    @DisplayName("findByPhone - should return customer")
    void findByPhone_happyPath() {
        // Arrange
        String phone = "0123456789";
        Customer customer = TestDataFactory.createCustomer();
        when(customerRepository.findByPhoneNumber(phone))
                .thenReturn(Optional.of(customer));

        // Act
        Customer result = customerService.findByPhone(phone);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getPhoneNumber()).isEqualTo(phone);
    }

    @Test
    @DisplayName("createCustomer - should create new customer")
    void createCustomer_happyPath() {
        // Arrange
        Customer customer = TestDataFactory.createCustomer();
        when(customerRepository.save(any(Customer.class)))
                .thenReturn(customer);

        // Act
        Customer result = customerService.createCustomer(customer);

        // Assert
        assertThat(result).isNotNull();
        verify(customerRepository).save(customer);
    }

    @Test
    @DisplayName("updateCustomer - should update customer")
    void updateCustomer_happyPath() {
        // Arrange
        Long id = 1L;
        Customer existingCustomer = TestDataFactory.createCustomer();
        Customer updatedData = TestDataFactory.createCustomer();
        updatedData.setName("Updated Name");

        when(customerRepository.findById(id))
                .thenReturn(Optional.of(existingCustomer));
        when(customerRepository.save(any(Customer.class)))
                .thenReturn(updatedData);

        // Act
        Customer result = customerService.updateCustomer(id, updatedData);

        // Assert
        assertThat(result).isNotNull();
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    @DisplayName("searchCustomers - should return search results")
    void searchCustomers_happyPath() {
        // Arrange
        String keyword = "test";
        List<Customer> customers = List.of(TestDataFactory.createCustomer());
        when(customerRepository.searchByKeyword(keyword))
                .thenReturn(customers);

        // Act
        List<Customer> result = customerService.searchCustomers(keyword);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("getCustomerPurchaseHistory - should return purchase history")
    void getCustomerPurchaseHistory_happyPath() {
        // Arrange
        Long customerId = 1L;
        List<Invoice> invoices = List.of(TestDataFactory.createInvoice());
        when(invoiceRepository.findByCustomerId(customerId))
                .thenReturn(invoices);

        // Act
        List<Invoice> result = customerService.getCustomerPurchaseHistory(customerId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
    }
}

