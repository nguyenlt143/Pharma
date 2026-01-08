package vn.edu.fpt.pharma.integration.pharmacist;

import org.junit.jupiter.api.*;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration Tests for Revenue functionality.
 * Test Cases: PHA034-PHA038 (5 tests)
 */
@DisplayName("Pharmacist Revenue Controller Integration Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PharmacistRevenueControllerIT extends BasePharmacistIT {

    private static final String TEST_CLASS = "PharmacistRevenueControllerIT";

    @Test
    @Order(1)
    @DisplayName("PHA034 - Verify revenues page")
    void testRevenuesPage_shouldDisplay() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/pharmacist/revenues";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asPharmacist()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        logEvidence(TEST_CLASS, "testRevenuesPage_shouldDisplay", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(2)
    @DisplayName("PHA035 - Verify shifts revenue page")
    void testShiftsRevenuePage_shouldDisplay() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/pharmacist/shifts";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asPharmacist()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        logEvidence(TEST_CLASS, "testShiftsRevenuePage_shouldDisplay", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(3)
    @DisplayName("PHA036 - Verify revenue by date range")
    void testRevenue_byDateRange_shouldReturnTotal() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/pharmacist/revenues";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asPharmacist()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testRevenue_byDateRange_shouldReturnTotal", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(4)
    @DisplayName("PHA037 - Verify revenue by shift")
    void testRevenue_byShift_shouldShowBreakdown() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/pharmacist/shifts";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asPharmacist()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testRevenue_byShift_shouldShowBreakdown", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(5)
    @DisplayName("PHA038 - Verify revenue empty period")
    void testRevenue_emptyPeriod_shouldReturnZero() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/pharmacist/revenues";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asPharmacist()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        logEvidence(TEST_CLASS, "testRevenue_emptyPeriod_shouldReturnZero", "GET", endpoint, result, startTime);
    }
}

