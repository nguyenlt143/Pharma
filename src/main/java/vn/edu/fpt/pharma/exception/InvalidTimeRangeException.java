package vn.edu.fpt.pharma.exception;

import lombok.Getter;

/**
 * Exception được throw khi khoảng thời gian không hợp lệ
 */
@Getter
public class InvalidTimeRangeException extends RuntimeException {
    private final String startTime;
    private final String endTime;

    public InvalidTimeRangeException(String message) {
        super(message);
        this.startTime = null;
        this.endTime = null;
    }

    public InvalidTimeRangeException(String startTime, String endTime, String message) {
        super(message);
        this.startTime = startTime;
        this.endTime = endTime;
    }
}

