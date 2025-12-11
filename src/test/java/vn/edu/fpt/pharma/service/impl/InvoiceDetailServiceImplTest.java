package vn.edu.fpt.pharma.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import vn.edu.fpt.pharma.BaseServiceTest;
import vn.edu.fpt.pharma.entity.InvoiceDetail;
import vn.edu.fpt.pharma.repository.InvoiceDetailRepository;
import vn.edu.fpt.pharma.service.AuditService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for InvoiceDetailServiceImpl - 4 tests
 * Strategy: Coverage for basic methods
 */
@DisplayName("InvoiceDetailServiceImpl Tests")
class InvoiceDetailServiceImplTest extends BaseServiceTest {

    @Mock
    private InvoiceDetailRepository invoiceDetailRepository;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private InvoiceDetailServiceImpl invoiceDetailService;

    private InvoiceDetail testDetail;

    @BeforeEach
    void setUp() {
        testDetail = new InvoiceDetail();
        testDetail.setId(1L);
        testDetail.setQuantity(10L);
        testDetail.setPrice(10000.0);
    }

    @Nested
    @DisplayName("findById() tests - 2 tests")
    class FindByIdTests {

        @Test
        @DisplayName("Should return detail when found")
        void findById_whenDetailExists_shouldReturnDetail() {
            // Arrange
            when(invoiceDetailRepository.findById(1L)).thenReturn(Optional.of(testDetail));

            // Act
            InvoiceDetail result = invoiceDetailService.findById(1L);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            verify(invoiceDetailRepository).findById(1L);
        }

        @Test
        @DisplayName("Should return null when detail not found")
        void findById_whenDetailNotFound_shouldReturnNull() {
            // Arrange
            when(invoiceDetailRepository.findById(999L)).thenReturn(Optional.empty());

            // Act
            InvoiceDetail result = invoiceDetailService.findById(999L);

            // Assert
            assertThat(result).isNull();
            verify(invoiceDetailRepository).findById(999L);
        }
    }

    @Nested
    @DisplayName("create() tests - 2 tests")
    class CreateTests {

        @Test
        @DisplayName("Should create detail successfully")
        void create_withValidDetail_shouldCreateSuccessfully() {
            // Arrange
            InvoiceDetail newDetail = new InvoiceDetail();
            newDetail.setQuantity(5L);
            when(invoiceDetailRepository.save(any(InvoiceDetail.class))).thenReturn(testDetail);

            // Act
            InvoiceDetail result = invoiceDetailService.create(newDetail);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            verify(invoiceDetailRepository).save(newDetail);
        }

        @Test
        @DisplayName("Should handle repository save failure")
        void create_whenRepositoryFails_shouldPropagateException() {
            // Arrange
            InvoiceDetail newDetail = new InvoiceDetail();
            newDetail.setQuantity(5L);
            when(invoiceDetailRepository.save(any())).thenThrow(new RuntimeException("Database error"));

            // Act & Assert
            assertThatThrownBy(() -> invoiceDetailService.create(newDetail))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Database error");
        }
    }
}

