package vn.edu.fpt.pharma.integration.admin;

import org.junit.jupiter.api.*;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration Tests for Admin Dashboard functionality.
 * Test Cases: ADM001 (1 test)
 */
@DisplayName("Admin Dashboard Controller Integration Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AdminDashboardControllerIT extends BaseAdminIT {

    private static final String TEST_CLASS = "AdminDashboardControllerIT";

    @Test
    @Order(1)
    @DisplayName("ADM001 - Verify admin dashboard")
    void testAdminDashboard_shouldDisplaySystemOverview() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/admin/dashboard";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asAdmin()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        logEvidence(TEST_CLASS, "testAdminDashboard_shouldDisplaySystemOverview", "GET", endpoint, result, startTime);
    }
}

