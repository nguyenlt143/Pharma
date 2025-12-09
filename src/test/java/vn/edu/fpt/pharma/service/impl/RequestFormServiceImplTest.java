package vn.edu.fpt.pharma.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import vn.edu.fpt.pharma.BaseServiceTest;
import vn.edu.fpt.pharma.constant.RequestStatus;
import vn.edu.fpt.pharma.constant.RequestType;
import vn.edu.fpt.pharma.entity.RequestForm;
import vn.edu.fpt.pharma.repository.RequestFormRepository;
import vn.edu.fpt.pharma.service.AuditService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for RequestFormServiceImpl - 4 tests
 * Strategy: Coverage for inherited BaseServiceImpl methods
 */
@DisplayName("RequestFormServiceImpl Tests")
class RequestFormServiceImplTest extends BaseServiceTest {

    @Mock
    private RequestFormRepository requestFormRepository;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private RequestFormServiceImpl requestFormService;

    private RequestForm testRequestForm;

    @BeforeEach
    void setUp() {
        testRequestForm = new RequestForm();
        testRequestForm.setId(1L);
        testRequestForm.setBranchId(1L);
        testRequestForm.setRequestType(RequestType.IMPORT);
        testRequestForm.setRequestStatus(RequestStatus.REQUESTED);
    }

    @Nested
    @DisplayName("findById() tests - 2 tests")
    class FindByIdTests {

        @Test
        @DisplayName("Should return request form when found")
        void findById_whenRequestFormExists_shouldReturnRequestForm() {
            // Arrange
            when(requestFormRepository.findById(1L)).thenReturn(Optional.of(testRequestForm));

            // Act
            RequestForm result = requestFormService.findById(1L);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getBranchId()).isEqualTo(1L);
            verify(requestFormRepository).findById(1L);
        }

        @Test
        @DisplayName("Should return null when request form not found")
        void findById_whenRequestFormNotFound_shouldReturnNull() {
            // Arrange
            when(requestFormRepository.findById(999L)).thenReturn(Optional.empty());

            // Act
            RequestForm result = requestFormService.findById(999L);

            // Assert
            assertThat(result).isNull();
            verify(requestFormRepository).findById(999L);
        }
    }

    @Nested
    @DisplayName("create() tests - 2 tests")
    class CreateTests {

        @Test
        @DisplayName("Should create request form successfully")
        void create_withValidRequestForm_shouldCreateSuccessfully() {
            // Arrange
            RequestForm newRequestForm = new RequestForm();
            newRequestForm.setBranchId(2L);
            newRequestForm.setRequestType(RequestType.IMPORT);
            when(requestFormRepository.save(any(RequestForm.class))).thenReturn(testRequestForm);

            // Act
            RequestForm result = requestFormService.create(newRequestForm);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            verify(requestFormRepository).save(newRequestForm);
        }

        @Test
        @DisplayName("Should handle repository save failure")
        void create_whenRepositoryFails_shouldPropagateException() {
            // Arrange
            RequestForm newRequestForm = new RequestForm();
            newRequestForm.setBranchId(2L);
            when(requestFormRepository.save(any())).thenThrow(new RuntimeException("Database error"));

            // Act & Assert
            assertThatThrownBy(() -> requestFormService.create(newRequestForm))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Database error");
        }
    }
}

