package vn.edu.fpt.pharma.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityInUseException.class)
    public ResponseEntity<String> handleEntityInUseException(EntityInUseException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    /**
     * Xử lý validation errors từ @Valid annotation
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fe -> fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "Invalid value",
                        (a, b) -> a + "; " + b,
                        LinkedHashMap::new
                ));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(fieldErrors);
    }

    /**
     * Xử lý các RuntimeException chung
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage() != null ? ex.getMessage() : "Có lỗi xảy ra");
    }

    /**
     * Xử lý InsufficientInventoryException
     */
    @ExceptionHandler(InsufficientInventoryException.class)
    public ResponseEntity<String> handleInsufficientInventoryException(InsufficientInventoryException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage() != null ? ex.getMessage() : "Tồn kho không đủ");
    }

    /**
     * Xử lý DuplicateEntityException
     */
    @ExceptionHandler(DuplicateEntityException.class)
    public ResponseEntity<String> handleDuplicateEntityException(DuplicateEntityException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    /**
     * Xử lý EntityNotFoundException
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFoundException(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage() != null ? ex.getMessage() : "Không tìm thấy dữ liệu");
    }

    /**
     * Xử lý BusinessRuleViolationException
     */
    @ExceptionHandler(BusinessRuleViolationException.class)
    public ResponseEntity<String> handleBusinessRuleViolationException(BusinessRuleViolationException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(ex.getMessage());
    }

    /**
     * Xử lý InvalidTimeRangeException
     */
    @ExceptionHandler(InvalidTimeRangeException.class)
    public ResponseEntity<String> handleInvalidTimeRangeException(InvalidTimeRangeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    /**
     * Xử lý ShiftOverlapException
     */
    @ExceptionHandler(ShiftOverlapException.class)
    public ResponseEntity<String> handleShiftOverlapException(ShiftOverlapException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    /**
     * Xử lý các Exception chung khác
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Có lỗi xảy ra, vui lòng thử lại sau");
    }
}

