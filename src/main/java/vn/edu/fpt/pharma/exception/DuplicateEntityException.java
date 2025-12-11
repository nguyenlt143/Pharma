package vn.edu.fpt.pharma.exception;

import lombok.Getter;

/**
 * Exception được throw khi cố gắng tạo/cập nhật entity với giá trị trùng lặp
 */
@Getter
public class DuplicateEntityException extends RuntimeException {
    private final String entityName;
    private final String fieldName;
    private final Object fieldValue;

    public DuplicateEntityException(String entityName, String fieldName, Object fieldValue) {
        super(String.format("%s với %s '%s' đã tồn tại", entityName, fieldName, fieldValue));
        this.entityName = entityName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public DuplicateEntityException(String entityName, String fieldName, Object fieldValue, String customMessage) {
        super(customMessage);
        this.entityName = entityName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
}

