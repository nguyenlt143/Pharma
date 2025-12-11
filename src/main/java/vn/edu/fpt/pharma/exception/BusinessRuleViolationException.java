package vn.edu.fpt.pharma.exception;

import lombok.Getter;

/**
 * Exception được throw khi vi phạm quy tắc nghiệp vụ
 */
@Getter
public class BusinessRuleViolationException extends RuntimeException {
    private final String ruleName;

    public BusinessRuleViolationException(String message) {
        super(message);
        this.ruleName = null;
    }

    public BusinessRuleViolationException(String ruleName, String message) {
        super(message);
        this.ruleName = ruleName;
    }
}

