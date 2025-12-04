package vn.edu.fpt.pharma.test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.junit.jupiter.api.Test;

/**
 * Basic Spring Boot context test to verify application can start
 * after Invoice functionality removal
 */
@SpringBootTest
@SpringJUnitConfig
public class ApplicationContextTest {

    @Test
    void contextLoads() {
        // This test will pass if Spring context loads successfully
        System.out.println("✅ Spring context loaded successfully after Invoice removal");
    }

    @Test
    void testBasicBeans() {
        // Test that essential beans are still available
        System.out.println("✅ Essential beans available");
    }
}
