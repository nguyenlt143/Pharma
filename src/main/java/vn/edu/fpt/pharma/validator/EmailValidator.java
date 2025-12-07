package vn.edu.fpt.pharma.validator;

import java.util.regex.Pattern;

/**
 * Validator for email addresses
 * Validates email format according to RFC 5322 simplified pattern
 */
public class EmailValidator {

    private static final String EMAIL_PATTERN =
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
                    "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    private final Pattern pattern;

    public EmailValidator() {
        this.pattern = Pattern.compile(EMAIL_PATTERN);
    }

    /**
     * Validates email format
     *
     * @param email the email to validate
     * @return true if email is valid, false otherwise
     */
    public boolean validate(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        return pattern.matcher(email.trim()).matches();
    }
}

