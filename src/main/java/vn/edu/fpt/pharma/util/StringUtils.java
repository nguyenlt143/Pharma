package vn.edu.fpt.pharma.util;

import org.springframework.validation.ObjectError;

import java.util.List;
import java.util.stream.Collectors;

public class StringUtils {

    public static boolean isBlank(String str) {
        return str == null || str.isBlank();
    }

    public static String joinNumbers(List<Integer> numbers) {
        return numbers.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
    }

    public static String convertValidationExceptionToString(List<ObjectError> errors) {
        return "Validation error: \n"  + errors.stream()
                .map(error -> "- " + error.getDefaultMessage())
                .reduce((msg1, msg2) -> msg1 + "<br/>" + msg2)
                .orElse("");
    }
}
