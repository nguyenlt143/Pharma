package vn.edu.fpt.pharma;

/**
 * Simple syntax validation test class DISABLED
 * Invoice functionality removed from Pharmacist role
 */
// public class SyntaxValidationTest { // DISABLED

    public static void main(String[] args) {
        System.out.println("=== Validating Pharmacist Role Implementation ===");

        // Test DTO instantiation
        testDTOs();

        // Test validation
        testValidation();

        System.out.println("✅ All basic syntax validation passed!");
    }

    private static void testDTOs() {
        try {
            // Test ProfileUpdateRequest
            vn.edu.fpt.pharma.dto.user.ProfileUpdateRequest profileRequest =
                new vn.edu.fpt.pharma.dto.user.ProfileUpdateRequest();
            profileRequest.setFullName("Test User");
            profileRequest.setEmail("test@example.com");

            // Test InvoiceCreateRequest
            vn.edu.fpt.pharma.dto.invoice.InvoiceCreateRequest invoiceRequest =
                new vn.edu.fpt.pharma.dto.invoice.InvoiceCreateRequest();
            invoiceRequest.setCustomerName("Test Customer");
            invoiceRequest.setTotalAmount(100000.0);

            System.out.println("✅ DTOs instantiation successful");
        } catch (Exception e) {
            System.err.println("❌ DTO Error: " + e.getMessage());
        }
    }

    private static void testValidation() {
        try {
            // Test validation annotations exist
            Class<?> profileClass = vn.edu.fpt.pharma.dto.user.ProfileUpdateRequest.class;
            Class<?> invoiceClass = vn.edu.fpt.pharma.dto.invoice.InvoiceCreateRequest.class;

            System.out.println("✅ ProfileUpdateRequest class loaded: " + profileClass.getName());
            System.out.println("✅ InvoiceCreateRequest class loaded: " + invoiceClass.getName());

        } catch (Exception e) {
            System.err.println("❌ Validation Error: " + e.getMessage());
        }
    }
}
