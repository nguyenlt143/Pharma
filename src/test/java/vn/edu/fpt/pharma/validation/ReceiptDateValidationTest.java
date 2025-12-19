package vn.edu.fpt.pharma.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import vn.edu.fpt.pharma.dto.warehouse.ReceiptDetailRequest;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for date validation in ReceiptDetailRequest
 */
@DisplayName("Receipt Date Validation Tests")
class ReceiptDateValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Valid receipt detail with proper dates should pass validation")
    void testValidReceiptDetail() {
        ReceiptDetailRequest detail = new ReceiptDetailRequest();
        detail.setVariantId(1L);
        detail.setBatchCode("BATCH001");
        detail.setManufactureDate(LocalDate.now().minusMonths(1).toString());
        detail.setExpiryDate(LocalDate.now().plusYears(2).toString());
        detail.setQuantity(100L);
        detail.setPrice(50000.0);

        Set<ConstraintViolation<ReceiptDetailRequest>> violations = validator.validate(detail);
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("NSX in future should fail validation")
    void testManufactureDateInFuture() {
        ReceiptDetailRequest detail = new ReceiptDetailRequest();
        detail.setVariantId(1L);
        detail.setBatchCode("BATCH001");
        detail.setManufactureDate(LocalDate.now().plusDays(1).toString()); // Future date
        detail.setExpiryDate(LocalDate.now().plusYears(2).toString());
        detail.setQuantity(100L);
        detail.setPrice(50000.0);

        Set<ConstraintViolation<ReceiptDetailRequest>> violations = validator.validate(detail);
        assertThat(violations).isNotEmpty();
        assertThat(violations.iterator().next().getMessage())
                .contains("Ngày sản xuất không được lớn hơn ngày hiện tại");
    }

    @Test
    @DisplayName("HSD before NSX should fail validation")
    void testExpiryDateBeforeManufactureDate() {
        ReceiptDetailRequest detail = new ReceiptDetailRequest();
        detail.setVariantId(1L);
        detail.setBatchCode("BATCH001");
        detail.setManufactureDate(LocalDate.now().minusMonths(1).toString());
        detail.setExpiryDate(LocalDate.now().minusMonths(2).toString()); // Before NSX
        detail.setQuantity(100L);
        detail.setPrice(50000.0);

        Set<ConstraintViolation<ReceiptDetailRequest>> violations = validator.validate(detail);
        assertThat(violations).isNotEmpty();
        assertThat(violations.iterator().next().getMessage())
                .contains("Hạn sử dụng phải sau ngày sản xuất");
    }

    @Test
    @DisplayName("HSD more than 20 years from NSX should fail validation")
    void testExpiryDateMoreThan20Years() {
        ReceiptDetailRequest detail = new ReceiptDetailRequest();
        detail.setVariantId(1L);
        detail.setBatchCode("BATCH001");
        detail.setManufactureDate(LocalDate.now().minusMonths(1).toString());
        detail.setExpiryDate(LocalDate.now().plusYears(21).toString()); // More than 20 years
        detail.setQuantity(100L);
        detail.setPrice(50000.0);

        Set<ConstraintViolation<ReceiptDetailRequest>> violations = validator.validate(detail);
        assertThat(violations).isNotEmpty();
        assertThat(violations.iterator().next().getMessage())
                .contains("Hạn sử dụng không được vượt quá 20 năm kể từ ngày sản xuất");
    }

    @Test
    @DisplayName("HSD exactly 20 years from NSX should pass validation")
    void testExpiryDateExactly20Years() {
        LocalDate mfgDate = LocalDate.now().minusMonths(1);
        LocalDate expDate = mfgDate.plusYears(20);

        ReceiptDetailRequest detail = new ReceiptDetailRequest();
        detail.setVariantId(1L);
        detail.setBatchCode("BATCH001");
        detail.setManufactureDate(mfgDate.toString());
        detail.setExpiryDate(expDate.toString());
        detail.setQuantity(100L);
        detail.setPrice(50000.0);

        Set<ConstraintViolation<ReceiptDetailRequest>> violations = validator.validate(detail);
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("NSX today should pass validation")
    void testManufactureDateToday() {
        ReceiptDetailRequest detail = new ReceiptDetailRequest();
        detail.setVariantId(1L);
        detail.setBatchCode("BATCH001");
        detail.setManufactureDate(LocalDate.now().toString());
        detail.setExpiryDate(LocalDate.now().plusYears(2).toString());
        detail.setQuantity(100L);
        detail.setPrice(50000.0);

        Set<ConstraintViolation<ReceiptDetailRequest>> violations = validator.validate(detail);
        assertThat(violations).isEmpty();
    }
}

