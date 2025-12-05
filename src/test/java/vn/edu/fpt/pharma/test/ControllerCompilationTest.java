package vn.edu.fpt.pharma.test;

/**
 * Quick compilation test for Pharmacist controllers after JTE template fix
 */
public class ControllerCompilationTest {

    public void testControllerImports() {
        try {
            // Test that controller classes can be loaded
            Class<?> pharmacistController = vn.edu.fpt.pharma.controller.pharmacist.PharmacistController.class;
            Class<?> invoiceController = vn.edu.fpt.pharma.controller.pharmacist.InvoiceController.class;

            System.out.println("✅ PharmacistController loaded: " + pharmacistController.getName());
            System.out.println("✅ InvoiceController loaded: " + invoiceController.getName());

            // Test InvoiceService
            Class<?> invoiceService = vn.edu.fpt.pharma.service.InvoiceService.class;
            System.out.println("✅ InvoiceService loaded: " + invoiceService.getName());

            System.out.println("🎉 All controller classes compile successfully!");

        } catch (Exception e) {
            System.err.println("❌ Controller compilation error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ControllerCompilationTest test = new ControllerCompilationTest();
        test.testControllerImports();
    }
}
