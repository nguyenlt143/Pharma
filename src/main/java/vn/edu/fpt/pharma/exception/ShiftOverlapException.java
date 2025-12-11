package vn.edu.fpt.pharma.exception;

import lombok.Getter;

/**
 * Exception được throw khi ca làm việc bị trùng lặp thời gian
 */
@Getter
public class ShiftOverlapException extends RuntimeException {
    private final String overlappingShifts;

    public ShiftOverlapException(String overlappingShifts) {
        super(String.format("Ca làm việc bị trùng thời gian với: %s", overlappingShifts));
        this.overlappingShifts = overlappingShifts;
    }

    public ShiftOverlapException(String overlappingShifts, String customMessage) {
        super(customMessage);
        this.overlappingShifts = overlappingShifts;
    }
}

