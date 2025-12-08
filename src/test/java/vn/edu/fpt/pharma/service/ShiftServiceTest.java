package vn.edu.fpt.pharma.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.edu.fpt.pharma.dto.shifts.ShiftSummaryVM;
import vn.edu.fpt.pharma.entity.Shift;
import vn.edu.fpt.pharma.entity.ShiftWork;
import vn.edu.fpt.pharma.repository.ShiftRepository;
import vn.edu.fpt.pharma.repository.ShiftWorkRepository;
import vn.edu.fpt.pharma.service.impl.ShiftServiceImpl;
import vn.edu.fpt.pharma.testutil.TestDataFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for ShiftService - Happy Path Only
 * Strategy: 1 test per method to achieve 100% line coverage
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ShiftService Tests - Happy Path Only")
class ShiftServiceTest {

    @Mock
    private ShiftRepository shiftRepository;

    @Mock
    private ShiftWorkRepository shiftWorkRepository;

    private ShiftService shiftService;

    @BeforeEach
    void setUp() {
        shiftService = new ShiftServiceImpl(shiftRepository, shiftWorkRepository);
    }

    @Test
    @DisplayName("getCurrentShift - should return current shift")
    void getCurrentShift_happyPath() {
        // Arrange
        Long userId = 1L;
        Long branchId = 1L;
        ShiftWork shiftWork = TestDataFactory.createShiftWork();
        when(shiftWorkRepository.findCurrentShift(userId, branchId))
                .thenReturn(Optional.of(shiftWork));

        // Act
        Optional<ShiftWork> result = shiftService.getCurrentShift(userId, branchId);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("getCurrentUserShift - should return user's shift")
    void getCurrentUserShift_happyPath() {
        // Arrange
        Long userId = 1L;
        ShiftWork shiftWork = TestDataFactory.createShiftWork();
        when(shiftWorkRepository.findByUserId(userId))
                .thenReturn(Optional.of(shiftWork));

        // Act
        ShiftWork result = shiftService.getCurrentUserShift(userId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("validateShiftWork - should validate successfully")
    void validateShiftWork_happyPath() {
        // Arrange
        Long userId = 1L;
        LocalDateTime time = LocalDateTime.now();
        when(shiftWorkRepository.existsByUserIdAndTime(userId, time))
                .thenReturn(true);

        // Act
        boolean valid = shiftService.validateShiftWork(userId, time);

        // Assert
        assertThat(valid).isTrue();
    }

    @Test
    @DisplayName("startShift - should start shift successfully")
    void startShift_happyPath() {
        // Arrange
        Long userId = 1L;
        Long shiftId = 1L;
        Shift shift = TestDataFactory.createShift();
        ShiftWork shiftWork = TestDataFactory.createShiftWork();

        when(shiftRepository.findById(shiftId)).thenReturn(Optional.of(shift));
        when(shiftWorkRepository.save(any(ShiftWork.class)))
                .thenReturn(shiftWork);

        // Act
        ShiftWork result = shiftService.startShift(userId, shiftId);

        // Assert
        assertThat(result).isNotNull();
        verify(shiftWorkRepository).save(any(ShiftWork.class));
    }

    @Test
    @DisplayName("endShift - should end shift successfully")
    void endShift_happyPath() {
        // Arrange
        Long userId = 1L;
        ShiftWork shiftWork = TestDataFactory.createShiftWork();
        when(shiftWorkRepository.findActiveByUserId(userId))
                .thenReturn(Optional.of(shiftWork));
        when(shiftWorkRepository.save(any(ShiftWork.class)))
                .thenReturn(shiftWork);

        // Act
        shiftService.endShift(userId);

        // Assert
        verify(shiftWorkRepository).save(any(ShiftWork.class));
    }

    @Test
    @DisplayName("isUserInActiveShift - should return true when in shift")
    void isUserInActiveShift_happyPath() {
        // Arrange
        Long userId = 1L;
        when(shiftWorkRepository.existsActiveShift(userId)).thenReturn(true);

        // Act
        boolean inShift = shiftService.isUserInActiveShift(userId);

        // Assert
        assertThat(inShift).isTrue();
    }

    @Test
    @DisplayName("getShiftWorkHistory - should return history")
    void getShiftWorkHistory_happyPath() {
        // Arrange
        Long userId = 1L;
        LocalDate from = LocalDate.now().minusDays(30);
        LocalDate to = LocalDate.now();
        List<ShiftWork> history = List.of(TestDataFactory.createShiftWork());
        when(shiftWorkRepository.findByUserIdAndDateRange(userId, from, to))
                .thenReturn(history);

        // Act
        List<ShiftWork> result = shiftService.getShiftWorkHistory(userId, from, to);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("getShiftSummary - should return summary")
    void getShiftSummary_happyPath() {
        // Arrange
        Long shiftWorkId = 1L;
        Object[] summaryData = new Object[]{1L, "Ca sáng", 10L, 1000000.0};
        when(shiftWorkRepository.findSummary(shiftWorkId))
                .thenReturn(summaryData);

        // Act
        ShiftSummaryVM result = shiftService.getShiftSummary(shiftWorkId);

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("validateShiftTransition - should allow transition")
    void validateShiftTransition_happyPath() {
        // Arrange
        Long userId = 1L;
        Long newShiftId = 2L;
        when(shiftWorkRepository.canTransition(userId, newShiftId))
                .thenReturn(true);

        // Act
        boolean canTransition = shiftService.validateShiftTransition(userId, newShiftId);

        // Assert
        assertThat(canTransition).isTrue();
    }

    @Test
    @DisplayName("getAllShiftsByBranch - should return all shifts")
    void getAllShiftsByBranch_happyPath() {
        // Arrange
        Long branchId = 1L;
        List<Shift> shifts = List.of(TestDataFactory.createShift());
        when(shiftRepository.findByBranchId(branchId)).thenReturn(shifts);

        // Act
        List<Shift> result = shiftService.getAllShiftsByBranch(branchId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
    }
}

