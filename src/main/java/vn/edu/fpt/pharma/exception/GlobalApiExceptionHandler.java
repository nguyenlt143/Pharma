package vn.edu.fpt.pharma.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalApiExceptionHandler {

    private ResponseEntity<Map<String, Object>> buildError(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }

    /**
     * Handle validation errors from @Valid DTOs
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(err -> err.getDefaultMessage())
                .orElse("Dữ liệu không hợp lệ");
        return buildError(HttpStatus.BAD_REQUEST, msg);
    }

    /**
     * Handle manual validation and business rule errors
     */
    @ExceptionHandler({
            IllegalArgumentException.class,
            EntityInUseException.class,
            ConstraintViolationException.class
    })
    public ResponseEntity<Map<String, Object>> handleBadRequest(RuntimeException ex) {
        String msg = ex.getMessage() != null ? ex.getMessage() : "Yêu cầu không hợp lệ";
        return buildError(HttpStatus.BAD_REQUEST, msg);
    }

    /**
     * Fallback for other unhandled exceptions in API layer
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleOtherExceptions(Exception ex) {
        String msg = ex.getMessage() != null ? ex.getMessage() : "Có lỗi xảy ra trên hệ thống";
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, msg);
    }
}


