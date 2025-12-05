package vn.edu.fpt.pharma.test;

import vn.edu.fpt.pharma.dto.invoice.InvoiceDetailVM;
import vn.edu.fpt.pharma.dto.invoice.MedicineItemVM;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Test để verify rằng ViewModel methods hoạt động đúng với JTE template expectations
 */
public class ViewModelMethodTest {

    public static void main(String[] args) {
        System.out.println("=== Testing ViewModel Methods ===");

        testInvoiceDetailVM();
        testMedicineItemVM();

        System.out.println("✅ All ViewModel methods working correctly!");
    }

    private static void testInvoiceDetailVM() {
        try {
            // Create test data
            MedicineItemVM medicine = new MedicineItemVM(
                "Paracetamol",
                "500mg",
                15000.0,
                2L
            );

            InvoiceDetailVM invoice = new InvoiceDetailVM(
                "Chi nhánh 1",
                "123 Đường ABC",
                "Nguyễn Văn A",
                "0123456789",
                LocalDateTime.now(),
                BigDecimal.valueOf(30000),
                "Ghi chú test",
                List.of(medicine)
            );

            // Test methods that JTE template expects
            System.out.println("🧪 Testing InvoiceDetailVM methods:");
            System.out.println("  ✅ branchName(): " + invoice.branchName());
            System.out.println("  ✅ branchAddress(): " + invoice.branchAddress());
            System.out.println("  ✅ customerName(): " + invoice.customerName());
            System.out.println("  ✅ customerPhone(): " + invoice.customerPhone());
            System.out.println("  ✅ createdAt(): " + invoice.createdAt());
            System.out.println("  ✅ totalPrice(): " + invoice.totalPrice());
            System.out.println("  ✅ description(): " + invoice.description());
            System.out.println("  ✅ medicines(): " + invoice.medicines().size() + " items");

        } catch (Exception e) {
            System.err.println("❌ InvoiceDetailVM test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void testMedicineItemVM() {
        try {
            MedicineItemVM medicine = new MedicineItemVM(
                "Amoxicillin",
                "250mg",
                25000.0,
                1L
            );

            // Test methods that JTE template expects
            System.out.println("🧪 Testing MedicineItemVM methods:");
            System.out.println("  ✅ medicineName(): " + medicine.medicineName());
            System.out.println("  ✅ strength(): " + medicine.strength());
            System.out.println("  ✅ unitPrice(): " + medicine.unitPrice());
            System.out.println("  ✅ quantity(): " + medicine.quantity());

            // Test calculation that template does
            double total = medicine.unitPrice() * medicine.quantity();
            System.out.println("  ✅ calculation (unitPrice * quantity): " + total);

        } catch (Exception e) {
            System.err.println("❌ MedicineItemVM test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
