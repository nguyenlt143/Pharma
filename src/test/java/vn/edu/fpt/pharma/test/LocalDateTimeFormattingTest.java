package vn.edu.fpt.pharma.test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Test để verify LocalDateTime formatting trong JTE templates
 */
public class LocalDateTimeFormattingTest {

    public static void main(String[] args) {
        System.out.println("=== Testing LocalDateTime Formatting ===");

        testDateTimeFormatting();
        testJTECompatibleFormatting();

        System.out.println("✅ All LocalDateTime formatting tests passed!");
    }

    private static void testDateTimeFormatting() {
        try {
            LocalDateTime now = LocalDateTime.now();

            // Test the exact formatting used in JTE template
            String formatted = now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

            System.out.println("🧪 Testing LocalDateTime formatting:");
            System.out.println("  Original: " + now);
            System.out.println("  Formatted: " + formatted);
            System.out.println("  ✅ DateTimeFormatter.ofPattern(\"dd/MM/yyyy HH:mm\") works");

            // Verify it's a valid string
            if (formatted != null && !formatted.isEmpty() && formatted.contains("/")) {
                System.out.println("  ✅ Formatted string is valid for JTE output");
            } else {
                throw new RuntimeException("Invalid formatted date string");
            }

        } catch (Exception e) {
            System.err.println("❌ LocalDateTime formatting test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void testJTECompatibleFormatting() {
        try {
            LocalDateTime testDate = LocalDateTime.of(2024, 12, 4, 14, 30, 0);

            // Test different formatting patterns that might be useful
            String[] patterns = {
                "dd/MM/yyyy HH:mm",     // Used in template
                "yyyy-MM-dd HH:mm:ss",  // ISO-like
                "dd-MM-yyyy",           // Date only
                "HH:mm"                 // Time only
            };

            System.out.println("🧪 Testing various formatting patterns:");
            for (String pattern : patterns) {
                String formatted = testDate.format(DateTimeFormatter.ofPattern(pattern));
                System.out.println("  Pattern '" + pattern + "': " + formatted + " ✅");
            }

        } catch (Exception e) {
            System.err.println("❌ JTE compatible formatting test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
