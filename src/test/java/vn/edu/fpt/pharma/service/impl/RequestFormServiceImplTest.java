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
import vn.edu.fpt.pharma.dto.inventory.ImportRequestDTO;
import vn.edu.fpt.pharma.entity.RequestDetail;
import vn.edu.fpt.pharma.entity.RequestForm;
import vn.edu.fpt.pharma.repository.RequestDetailRepository;
import vn.edu.fpt.pharma.repository.RequestFormRepository;
import vn.edu.fpt.pharma.service.AuditService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for RequestFormServiceImpl - 10 tests
 * Role: Inventory
 * Functions: createImportRequest, confirmRequest
 */
@DisplayName("RequestFormServiceImpl Tests")
@org.mockito.junit.jupiter.MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
class RequestFormServiceImplTest extends BaseServiceTest {

    @Mock
    private RequestFormRepository requestFormRepository;

    @Mock
    private RequestDetailRepository requestDetailRepository;

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
        @DisplayName("TC1: Should create request form successfully - Normal")
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
        @DisplayName("TC2: Should handle repository save failure - Abnormal")
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

    @Nested
    @DisplayName("createImportRequest() tests - 4 tests")
    class CreateImportRequestTests {

        @Test
        @DisplayName("TC1: Should create import request successfully - Normal (Happy Path)")
        void createImportRequest_withValidData_shouldCreateSuccessfully() {
            // Arrange
            ImportRequestDTO.ImportItemDTO item = new ImportRequestDTO.ImportItemDTO(1L, null, 100);
            ImportRequestDTO request = new ImportRequestDTO("Test import request", List.of(item));

            when(requestFormRepository.save(any(RequestForm.class))).thenAnswer(inv -> {
                RequestForm form = inv.getArgument(0);
                form.setId(1L);
                return form;
            });
            when(requestDetailRepository.save(any(RequestDetail.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            String result = requestFormService.createImportRequest(1L, request);

            // Assert
            assertThat(result).isEqualTo("#RQ001");
            verify(requestFormRepository).save(argThat(form ->
                form.getBranchId().equals(1L) &&
                form.getRequestType() == RequestType.IMPORT &&
                form.getRequestStatus() == RequestStatus.REQUESTED
            ));
            verify(requestDetailRepository).save(any(RequestDetail.class));
        }

        @Test
        @DisplayName("TC2: Should create import request with multiple items - Normal")
        void createImportRequest_withMultipleItems_shouldCreateAllDetails() {
            // Arrange
            ImportRequestDTO.ImportItemDTO item1 = new ImportRequestDTO.ImportItemDTO(1L, null, 100);
            ImportRequestDTO.ImportItemDTO item2 = new ImportRequestDTO.ImportItemDTO(2L, null, 200);
            ImportRequestDTO request = new ImportRequestDTO("Test", List.of(item1, item2));

            when(requestFormRepository.save(any(RequestForm.class))).thenAnswer(inv -> {
                RequestForm form = inv.getArgument(0);
                form.setId(5L);
                return form;
            });
            when(requestDetailRepository.save(any(RequestDetail.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            String result = requestFormService.createImportRequest(1L, request);

            // Assert
            assertThat(result).isEqualTo("#RQ005");
            verify(requestDetailRepository, times(2)).save(any(RequestDetail.class));
        }

        @Test
        @DisplayName("TC3: Should skip invalid items with null variantId - Boundary")
        void createImportRequest_withNullVariantId_shouldSkipItem() {
            // Arrange
            ImportRequestDTO.ImportItemDTO validItem = new ImportRequestDTO.ImportItemDTO(1L, null, 100);
            ImportRequestDTO.ImportItemDTO invalidItem = new ImportRequestDTO.ImportItemDTO(null, null, 50); // null variantId
            ImportRequestDTO request = new ImportRequestDTO("Test", List.of(validItem, invalidItem));

            when(requestFormRepository.save(any(RequestForm.class))).thenAnswer(inv -> {
                RequestForm form = inv.getArgument(0);
                form.setId(1L);
                return form;
            });
            when(requestDetailRepository.save(any(RequestDetail.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            String result = requestFormService.createImportRequest(1L, request);

            // Assert
            assertThat(result).isEqualTo("#RQ001");
            // Only 1 detail should be saved (invalid item skipped)
            verify(requestDetailRepository, times(1)).save(any(RequestDetail.class));
        }

        @Test
        @DisplayName("TC4: Should skip items with zero or negative quantity - Boundary")
        void createImportRequest_withZeroQuantity_shouldSkipItem() {
            // Arrange
            ImportRequestDTO.ImportItemDTO validItem = new ImportRequestDTO.ImportItemDTO(1L, null, 100);
            ImportRequestDTO.ImportItemDTO zeroItem = new ImportRequestDTO.ImportItemDTO(2L, null, 0); // zero quantity
            ImportRequestDTO request = new ImportRequestDTO("Test", List.of(validItem, zeroItem));

            when(requestFormRepository.save(any(RequestForm.class))).thenAnswer(inv -> {
                RequestForm form = inv.getArgument(0);
                form.setId(1L);
                return form;
            });
            when(requestDetailRepository.save(any(RequestDetail.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            String result = requestFormService.createImportRequest(1L, request);

            // Assert
            assertThat(result).isEqualTo("#RQ001");
            // Only 1 detail should be saved (zero quantity item skipped)
            verify(requestDetailRepository, times(1)).save(any(RequestDetail.class));
        }
    }

    @Nested
    @DisplayName("confirmRequest() tests - 2 tests")
    class ConfirmRequestTests {

        @Test
        @DisplayName("TC1: Should confirm request successfully - Normal")
        void confirmRequest_withRequestedStatus_shouldConfirm() {
            // Arrange
            testRequestForm.setRequestStatus(RequestStatus.REQUESTED);
            when(requestFormRepository.findById(1L)).thenReturn(Optional.of(testRequestForm));
            when(requestFormRepository.save(any(RequestForm.class))).thenReturn(testRequestForm);

            // Act
            requestFormService.confirmRequest(1L);

            // Assert
            verify(requestFormRepository).save(argThat(form ->
                form.getRequestStatus() == RequestStatus.CONFIRMED
            ));
        }

        @Test
        @DisplayName("TC2: Should throw exception when request not found - Abnormal")
        void confirmRequest_whenNotFound_shouldThrowException() {
            // Arrange
            when(requestFormRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> requestFormService.confirmRequest(999L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Request not found");
        }
    }
}

