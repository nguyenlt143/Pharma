package vn.edu.fpt.pharma.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import vn.edu.fpt.pharma.BaseServiceTest;
import vn.edu.fpt.pharma.entity.Customer;
import vn.edu.fpt.pharma.repository.CustomerRepository;
import vn.edu.fpt.pharma.service.AuditService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CustomerServiceImpl - 8 tests
 * Strategy: Full coverage for getOrCreate with race condition handling
 */
@DisplayName("CustomerServiceImpl Tests")
class CustomerServiceImplTest extends BaseServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setName("Nguyễn Văn A");
        testCustomer.setPhone("0901234567");
    }

    @Nested
    @DisplayName("getOrCreate() tests - 6 tests")
    class GetOrCreateTests {

        @Test
        @DisplayName("Should return existing customer when phone exists")
        void getOrCreate_whenPhoneExists_shouldReturnExistingCustomer() {
            // Arrange
            when(customerRepository.findByPhone("0901234567")).thenReturn(Optional.of(testCustomer));

            // Act
            Customer result = customerService.getOrCreate("Nguyễn Văn A", "0901234567");

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getName()).isEqualTo("Nguyễn Văn A");
            verify(customerRepository).findByPhone("0901234567");
            verify(customerRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should create new customer when phone not exists")
        void getOrCreate_whenPhoneNotExists_shouldCreateNewCustomer() {
            // Arrange
            when(customerRepository.findByPhone("0909999999")).thenReturn(Optional.empty());
            when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
                Customer c = invocation.getArgument(0);
                c.setId(2L);
                return c;
            });

            // Act
            Customer result = customerService.getOrCreate("Trần Văn B", "0909999999");

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(2L);
            assertThat(result.getName()).isEqualTo("Trần Văn B");
            assertThat(result.getPhone()).isEqualTo("0909999999");
            verify(customerRepository).findByPhone("0909999999");
            verify(customerRepository).save(any(Customer.class));
        }

        @Test
        @DisplayName("Should handle race condition - find on retry")
        void getOrCreate_whenRaceConditionOccurs_shouldRetryAndFind() {
            // Arrange - first find returns empty, save throws, second find returns customer
            when(customerRepository.findByPhone("0909999999"))
                    .thenReturn(Optional.empty())
                    .thenReturn(Optional.of(testCustomer));
            when(customerRepository.save(any(Customer.class)))
                    .thenThrow(new RuntimeException("Duplicate key"));

            // Act
            Customer result = customerService.getOrCreate("Nguyễn Văn A", "0909999999");

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            verify(customerRepository, times(2)).findByPhone("0909999999");
        }

        @Test
        @DisplayName("Should throw exception when save fails and retry also fails")
        void getOrCreate_whenSaveFailsAndRetryFails_shouldThrowException() {
            // Arrange
            when(customerRepository.findByPhone("0909999999")).thenReturn(Optional.empty());
            when(customerRepository.save(any(Customer.class)))
                    .thenThrow(new RuntimeException("Database error"));

            // Act & Assert
            assertThatThrownBy(() -> customerService.getOrCreate("Test", "0909999999"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Không thể tạo customer");

            verify(customerRepository, times(2)).findByPhone("0909999999");
        }

        @Test
        @DisplayName("Should set correct name and phone on new customer")
        void getOrCreate_withNewCustomer_shouldSetCorrectFields() {
            // Arrange
            when(customerRepository.findByPhone("0908888888")).thenReturn(Optional.empty());
            when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
                Customer c = invocation.getArgument(0);
                c.setId(3L);
                return c;
            });

            // Act
            Customer result = customerService.getOrCreate("Lê Thị C", "0908888888");

            // Assert
            assertThat(result.getName()).isEqualTo("Lê Thị C");
            assertThat(result.getPhone()).isEqualTo("0908888888");
            verify(customerRepository).save(argThat(c ->
                c.getName().equals("Lê Thị C") && c.getPhone().equals("0908888888")
            ));
        }

        @Test
        @DisplayName("Should handle empty name")
        void getOrCreate_withEmptyName_shouldCreateWithEmptyName() {
            // Arrange
            when(customerRepository.findByPhone("0907777777")).thenReturn(Optional.empty());
            when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
                Customer c = invocation.getArgument(0);
                c.setId(4L);
                return c;
            });

            // Act
            Customer result = customerService.getOrCreate("", "0907777777");

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("");
            verify(customerRepository).save(any());
        }
    }

    @Nested
    @DisplayName("findById() tests - 2 tests")
    class FindByIdTests {

        @Test
        @DisplayName("Should return customer when found")
        void findById_whenCustomerExists_shouldReturnCustomer() {
            // Arrange
            when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));

            // Act
            Customer result = customerService.findById(1L);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            verify(customerRepository).findById(1L);
        }

        @Test
        @DisplayName("Should return null when customer not found")
        void findById_whenCustomerNotFound_shouldReturnNull() {
            // Arrange
            when(customerRepository.findById(999L)).thenReturn(Optional.empty());

            // Act
            Customer result = customerService.findById(999L);

            // Assert
            assertThat(result).isNull();
            verify(customerRepository).findById(999L);
        }
    }
}

