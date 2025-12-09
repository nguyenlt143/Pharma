package vn.edu.fpt.pharma.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import vn.edu.fpt.pharma.BaseServiceTest;
import vn.edu.fpt.pharma.dto.manager.ShiftWorkResponse;
import vn.edu.fpt.pharma.entity.ShiftWork;
import vn.edu.fpt.pharma.entity.ShiftAssignment;
import vn.edu.fpt.pharma.entity.Shift;
import vn.edu.fpt.pharma.repository.ShiftAssignmentRepository;
import vn.edu.fpt.pharma.repository.ShiftRepository;
import vn.edu.fpt.pharma.repository.ShiftWorkRepository;
import vn.edu.fpt.pharma.repository.UserRepository;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ShiftWorkServiceImpl - 4 tests
 * Strategy: Coverage for main methods
 */
@DisplayName("ShiftWorkServiceImpl Tests")
class ShiftWorkServiceImplTest extends BaseServiceTest {

    @Mock
    private ShiftWorkRepository shiftWorkRepository;

    @Mock
    private ShiftRepository shiftRepository;

    @Mock
    private ShiftAssignmentRepository assignmentRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ShiftWorkServiceImpl shiftWorkService;

    private ShiftWork testShiftWork;
    private Shift testShift;
    private ShiftAssignment testAssignment;

    @BeforeEach
    void setUp() {
        testShift = new Shift();
        testShift.setId(1L);
        testShift.setName("Ca sáng");

        testAssignment = new ShiftAssignment();
        testAssignment.setId(1L);
        testAssignment.setShift(testShift);

        testShiftWork = new ShiftWork();
        testShiftWork.setId(1L);
        testShiftWork.setAssignment(testAssignment);
        testShiftWork.setWorkDate(LocalDate.now());
    }

    @Nested
    @DisplayName("findByShiftAndDate() tests - 3 tests")
    class FindByShiftAndDateTests {

        @Test
        @DisplayName("Should return shift works when data exists")
        void findByShiftAndDate_whenDataExists_shouldReturnList() {
            // Arrange
            when(shiftWorkRepository.findByShiftIdAndWorkDate(1L, LocalDate.now()))
                    .thenReturn(Collections.singletonList(testShiftWork));

            // Act
            List<ShiftWorkResponse> result = shiftWorkService.findByShiftAndDate(1L, LocalDate.now());

            // Assert
            assertThat(result).hasSize(1);
            verify(shiftWorkRepository).findByShiftIdAndWorkDate(1L, LocalDate.now());
        }

        @Test
        @DisplayName("Should return empty list when no data")
        void findByShiftAndDate_whenNoData_shouldReturnEmptyList() {
            // Arrange
            when(shiftWorkRepository.findByShiftIdAndWorkDate(1L, LocalDate.now()))
                    .thenReturn(Collections.emptyList());

            // Act
            List<ShiftWorkResponse> result = shiftWorkService.findByShiftAndDate(1L, LocalDate.now());

            // Assert
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should handle repository exception")
        void findByShiftAndDate_whenRepositoryFails_shouldPropagateException() {
            // Arrange
            when(shiftWorkRepository.findByShiftIdAndWorkDate(any(), any()))
                    .thenThrow(new RuntimeException("Database error"));

            // Act & Assert
            assertThatThrownBy(() -> shiftWorkService.findByShiftAndDate(1L, LocalDate.now()))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Database error");
        }
    }
}

