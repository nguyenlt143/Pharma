package vn.edu.fpt.pharma.dto.medicine;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.annotation.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicineVariantRequest {
    @NotNull(message = "Medicine ID is required")
    private Long medicineId;

    @NotNull(message = "Dạng bào chế không được để trống")
    private Long dosageFormId; // ID of the dosage form

    @NotNull(message = "Liều dùng không được để trống")
    @NotBlank(message = "Liều dùng không được để trống")
    private String dosage;

    @NotNull(message = "Hàm lượng không được để trống")
    @NotBlank(message = "Hàm lượng không được để trống")
    private String strength;

    private String packaging;

    @NotNull(message = "Mã vạch không được để trống")
    @NotBlank(message = "Mã vạch không được để trống")
    private String barcode;

    @NotNull(message = "Số đăng ký không được để trống")
    @NotBlank(message = "Số đăng ký không được để trống")
    private String registrationNumber;

    @NotNull(message = "Điều kiện bảo quản không được để trống")
    @NotBlank(message = "Điều kiện bảo quản không được để trống")
    private String storageConditions;

    @NotNull(message = "Hướng dẫn sử dụng không được để trống")
    @NotBlank(message = "Hướng dẫn sử dụng không được để trống")
    private String instructions;

    @NotNull(message = "Cần kê đơn không được để trống")
    private Boolean prescription_require;

    private String note; // Moved from UnitConversion

    @ValidUnitConversions(message = "Giá trị quy đổi của đơn vị sau phải chia hết cho đơn vị trước")
    private List<UnitConversionDTO> unitConversions;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UnitConversionDTO {
        private Long unitId;
        private Double multiplier;
        private Boolean isSale; // Indicates if this conversion is for sale purposes
    }

    // Custom validation annotation
    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @Constraint(validatedBy = UnitConversionsValidator.class)
    @Documented
    public @interface ValidUnitConversions {
        String message() default "Giá trị quy đổi không hợp lệ";
        Class<?>[] groups() default {};
        Class<? extends Payload>[] payload() default {};
    }

    // Validator implementation
    public static class UnitConversionsValidator implements ConstraintValidator<ValidUnitConversions, List<UnitConversionDTO>> {

        @Override
        public boolean isValid(List<UnitConversionDTO> conversions, ConstraintValidatorContext context) {
            if (conversions == null || conversions.isEmpty()) {
                return true; // Allow empty list
            }

            // Check for duplicate unit IDs
            long distinctUnitCount = conversions.stream()
                    .map(UnitConversionDTO::getUnitId)
                    .distinct()
                    .count();

            if (distinctUnitCount != conversions.size()) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Không được chọn trùng đơn vị quy đổi")
                        .addConstraintViolation();
                return false;
            }

            // Check ascending order and divisibility
            for (int i = 1; i < conversions.size(); i++) {
                UnitConversionDTO prev = conversions.get(i - 1);
                UnitConversionDTO current = conversions.get(i);

                if (prev.getMultiplier() == null || current.getMultiplier() == null) {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate("Giá trị quy đổi không được để trống")
                            .addConstraintViolation();
                    return false;
                }

                // Check if values are positive
                if (prev.getMultiplier() <= 0 || current.getMultiplier() <= 0) {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate("Giá trị quy đổi phải lớn hơn 0")
                            .addConstraintViolation();
                    return false;
                }

                // Check ascending order
                if (current.getMultiplier() <= prev.getMultiplier()) {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate(
                            String.format("Giá trị quy đổi phải tăng dần! Đơn vị thứ %d (%.2f) phải lớn hơn đơn vị thứ %d (%.2f)",
                                    i + 1, current.getMultiplier(), i, prev.getMultiplier()))
                            .addConstraintViolation();
                    return false;
                }

                // Check divisibility: current must be divisible by previous
                double remainder = current.getMultiplier() % prev.getMultiplier();
                // Use epsilon for floating point comparison
                if (Math.abs(remainder) > 0.0001) {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate(
                            String.format("Đơn vị thứ %d (%.2f) phải chia hết cho đơn vị thứ %d (%.2f)",
                                    i + 1, current.getMultiplier(), i, prev.getMultiplier()))
                            .addConstraintViolation();
                    return false;
                }
            }

            return true;
        }
    }
}

