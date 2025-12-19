package vn.edu.fpt.pharma.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import vn.edu.fpt.pharma.dto.warehouse.ReceiptDetailRequest;

import java.time.LocalDate;

public class DateRangeValidator implements ConstraintValidator<ValidDateRange, ReceiptDetailRequest> {

    @Override
    public void initialize(ValidDateRange constraintAnnotation) {
    }

    @Override
    public boolean isValid(ReceiptDetailRequest detail, ConstraintValidatorContext context) {
        if (detail == null) {
            return true;
        }

        if (detail.getManufactureDate() == null || detail.getExpiryDate() == null) {
            return true; // Let @NotBlank handle null validation
        }

        try {
            LocalDate mfgDate = LocalDate.parse(detail.getManufactureDate());
            LocalDate expDate = LocalDate.parse(detail.getExpiryDate());
            LocalDate currentDate = LocalDate.now();

            // Validation 1: NSX không được lớn hơn ngày hiện tại
            if (mfgDate.isAfter(currentDate)) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Ngày sản xuất không được lớn hơn ngày hiện tại")
                        .addPropertyNode("manufactureDate")
                        .addConstraintViolation();
                return false;
            }

            // Validation 2: HSD phải sau NSX
            if (!expDate.isAfter(mfgDate)) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Hạn sử dụng phải sau ngày sản xuất")
                        .addPropertyNode("expiryDate")
                        .addConstraintViolation();
                return false;
            }

            // Validation 3: HSD tối đa 20 năm tính từ NSX
            LocalDate maxExpiryDate = mfgDate.plusYears(20);
            if (expDate.isAfter(maxExpiryDate)) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Hạn sử dụng không được vượt quá 20 năm kể từ ngày sản xuất")
                        .addPropertyNode("expiryDate")
                        .addConstraintViolation();
                return false;
            }

            return true;

        } catch (Exception e) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Định dạng ngày không hợp lệ")
                    .addConstraintViolation();
            return false;
        }
    }
}

