package vn.edu.fpt.pharma.integration.owner;

import org.junit.jupiter.api.*;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration Tests for Owner Dashboard functionality.
 * Test Cases: OWN001-OWN003 (3 tests)
 */
@DisplayName("Owner Dashboard Controller Integration Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OwnerDashboardControllerIT extends BaseOwnerIT {

    private static final String TEST_CLASS = "OwnerDashboardControllerIT";

    @Test
    @Order(1)
    @DisplayName("OWN001 - Verify owner dashboard")
    void testOwnerDashboard_shouldDisplayOverview() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/owner/dashboard";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asOwner()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        logEvidence(TEST_CLASS, "testOwnerDashboard_shouldDisplayOverview", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(2)
    @DisplayName("OWN002 - Verify dashboard revenue API")
    void testDashboardRevenueAPI_shouldReturnTotalRevenue() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/owner/dashboard/revenue";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asOwner()))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testDashboardRevenueAPI_shouldReturnTotalRevenue", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(3)
    @DisplayName("OWN003 - Verify dashboard profit API")
    void testDashboardProfitAPI_shouldReturnTotalProfit() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/owner/dashboard/profit";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asOwner()))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testDashboardProfitAPI_shouldReturnTotalProfit", "GET", endpoint, result, startTime);
    }
}

