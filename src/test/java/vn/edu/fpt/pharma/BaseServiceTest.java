package vn.edu.fpt.pharma;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

/**
 * Base test class for service layer tests
 * Provides common setup and utilities for all service tests
 */
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public abstract class BaseServiceTest {

    /**
     * Utility method to compare strings ignoring case
     */
    protected boolean equalsIgnoreCase(String str1, String str2) {
        if (str1 == null && str2 == null) return true;
        if (str1 == null || str2 == null) return false;
        return str1.equalsIgnoreCase(str2);
    }

    /**
     * Utility method to compare objects
     */
    protected boolean equals(Object obj1, Object obj2) {
        if (obj1 == null && obj2 == null) return true;
        if (obj1 == null || obj2 == null) return false;
        return obj1.equals(obj2);
    }
}

