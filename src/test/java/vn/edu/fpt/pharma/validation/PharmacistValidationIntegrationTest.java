package vn.edu.fpt.pharma.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import vn.edu.fpt.pharma.dto.invoice.InvoiceCreateRequest;
import vn.edu.fpt.pharma.dto.invoice.InvoiceItemRequest;
import vn.edu.fpt.pharma.dto.user.ProfileUpdateRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test để đảm bảo validation hoạt động đúng cho tất cả DTOs của Pharmacist
 */
public class PharmacistValidationIntegrationTest { // RE-ENABLED

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testInvoiceCreateRequest_AllValid() {
        // Given - Tạo request hợp lệ hoàn toàn
        InvoiceCreateRequest request = createValidInvoiceRequest();

        // When - Validate
        Set<ConstraintViolation<InvoiceCreateRequest>> violations = validator.validate(request);

        // Then - Không có lỗi
        assertThat(violations).isEmpty();
    }

    @Test
    void testInvoiceCreateRequest_MultipleErrors() {
        // Given - Request với nhiều lỗi
        InvoiceCreateRequest request = new InvoiceCreateRequest();
        request.setCustomerName(""); // Lỗi: trống
        request.setPhoneNumber("123"); // Lỗi: không đúng format
        request.setTotalAmount(0.0); // Lỗi: <= 0
        request.setPaymentMethod(""); // Lỗi: trống
        request.setItems(new ArrayList<>()); // Lỗi: danh sách rỗng

        // When
        Set<ConstraintViolation<InvoiceCreateRequest>> violations = validator.validate(request);

        // Then - Có nhiều lỗi
        assertThat(violations).hasSizeGreaterThan(3);
        
        // Kiểm tra có các lỗi mong đợi
        List<String> errorMessages = violations.stream()
            .map(ConstraintViolation::getMessage)
            .toList();
        
        assertThat(errorMessages).contains("Tên khách hàng không được để trống");
        assertThat(errorMessages).contains("Số điện thoại không hợp lệ");
        assertThat(errorMessages).contains("Tổng tiền phải lớn hơn 0");
        assertThat(errorMessages).contains("Phương thức thanh toán không được để trống");
        assertThat(errorMessages).contains("Danh sách sản phẩm không được để trống");
    }

    @Test
    void testProfileUpdateRequest_AllValid() {
        // Given
        ProfileUpdateRequest request = createValidProfileRequest();

        // When
        Set<ConstraintViolation<ProfileUpdateRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    void testProfileUpdateRequest_PasswordMismatch() {
        // Given
        ProfileUpdateRequest request = createValidProfileRequest();
        request.setPassword("password123");
        request.setConfirmPassword("differentpassword");

        // When
        Set<ConstraintViolation<ProfileUpdateRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
            .isEqualTo("Mật khẩu xác nhận không khớp");
    }

    @Test
    void testInvoiceItem_InvalidData() {
        // Given - Item với dữ liệu không hợp lệ
        InvoiceItemRequest item = new InvoiceItemRequest();
        item.setInventoryId(null); // Lỗi
        item.setQuantity(0L); // Lỗi: phải >= 1
        item.setUnitPrice(-100.0); // Lỗi: phải > 0
        item.setSelectedMultiplier(0.0); // Lỗi: phải > 0

        // When
        Set<ConstraintViolation<InvoiceItemRequest>> violations = validator.validate(item);

        // Then - Có 4 lỗi
        assertThat(violations).hasSize(4);
        
        List<String> errorMessages = violations.stream()
            .map(ConstraintViolation::getMessage)
            .toList();
            
        assertThat(errorMessages).contains("ID kho không được để trống");
        assertThat(errorMessages).contains("Số lượng phải ít nhất là 1");
        assertThat(errorMessages).contains("Đơn giá phải lớn hơn 0");
        assertThat(errorMessages).contains("Hệ số nhân phải lớn hơn 0");
    }

    @Test
    void testValidPhoneNumbers() {
        // Test các format số điện thoại hợp lệ
        String[] validPhones = {
            "0123456789",
            "0987654321", 
            "+84123456789",
            "+84987654321"
        };

        for (String phone : validPhones) {
            InvoiceCreateRequest request = createValidInvoiceRequest();
            request.setPhoneNumber(phone);
            
            Set<ConstraintViolation<InvoiceCreateRequest>> violations = validator.validate(request);
            
            assertThat(violations)
                .as("Phone number %s should be valid", phone)
                .isEmpty();
        }
    }

    @Test
    void testInvalidPhoneNumbers() {
        // Test các format số điện thoại không hợp lệ
        String[] invalidPhones = {
            "123456789",      // Thiếu số 0 đầu
            "01234567890",    // Quá dài
            "012345",         // Quá ngắn
            "+841234567",     // Quá ngắn với +84
            "abcdefghij",     // Không phải số
            "0123-456-789"    // Có ký tự đặc biệt
        };

        for (String phone : invalidPhones) {
            InvoiceCreateRequest request = createValidInvoiceRequest();
            request.setPhoneNumber(phone);
            
            Set<ConstraintViolation<InvoiceCreateRequest>> violations = validator.validate(request);
            
            assertThat(violations)
                .as("Phone number %s should be invalid", phone)
                .isNotEmpty();
        }
    }

    // Helper methods
    private InvoiceCreateRequest createValidInvoiceRequest() {
        InvoiceCreateRequest request = new InvoiceCreateRequest();
        request.setCustomerName("Nguyen Van A");
        request.setPhoneNumber("0123456789");
        request.setTotalAmount(100000.0);
        request.setPaymentMethod("cash");
        request.setNote("Test note");
        
        List<InvoiceItemRequest> items = new ArrayList<>();
        InvoiceItemRequest item = new InvoiceItemRequest();
        item.setInventoryId(1L);
        item.setQuantity(2L);
        item.setUnitPrice(50000.0);
        item.setSelectedMultiplier(1.0);
        items.add(item);
        request.setItems(items);
        
        return request;
    }

    private ProfileUpdateRequest createValidProfileRequest() {
        ProfileUpdateRequest request = new ProfileUpdateRequest();
        request.setFullName("Nguyen Van A");
        request.setPhone("0123456789");
        request.setEmail("test@example.com");
        return request;
    }
}
