package vn.edu.fpt.pharma.integration.manager;

import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration Tests for Revenue Report functionality.
 * <p>
 * Tests cover:
 * - GET /api/manager/report/revenue - Lấy báo cáo doanh thu với các filter
 * </p>
 */
@DisplayName("Revenue Report Controller Integration Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RevenueReportControllerIT extends BaseManagerIT {

    private static final String TEST_CLASS = "RevenueReportControllerIT";

    // ========================================================================
    // GET /api/manager/report/revenue - Báo cáo doanh thu
    // ========================================================================

    @Test
    @Order(1)
    @DisplayName("GET /api/manager/report/revenue - Should return revenue report with default params")
    void testGetRevenue_defaultParams() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/manager/report/revenue";

        MvcResult result = mockMvc.perform(get(endpoint))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testGetRevenue_defaultParams", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(2)
    @DisplayName("GET /api/manager/report/revenue?date=2025-12-10 - Should return revenue for specific date")
    void testGetRevenue_byDate() throws Exception {
        long startTime = System.currentTimeMillis();
        String date = LocalDate.now().toString();
        String endpoint = "/api/manager/report/revenue?date=" + date;

        MvcResult result = mockMvc.perform(get(endpoint)
                        .param("date", date))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testGetRevenue_byDate", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(3)
    @DisplayName("GET /api/manager/report/revenue?mode=daily - Should return daily revenue mode")
    void testGetRevenue_dailyMode() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/manager/report/revenue?mode=daily";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .param("mode", "daily"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testGetRevenue_dailyMode", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(4)
    @DisplayName("GET /api/manager/report/revenue?mode=weekly - Should return weekly revenue mode")
    void testGetRevenue_weeklyMode() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/manager/report/revenue?mode=weekly";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .param("mode", "weekly"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testGetRevenue_weeklyMode", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(5)
    @DisplayName("GET /api/manager/report/revenue?mode=monthly - Should return monthly revenue mode")
    void testGetRevenue_monthlyMode() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/manager/report/revenue?mode=monthly";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .param("mode", "monthly"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testGetRevenue_monthlyMode", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(6)
    @DisplayName("GET /api/manager/report/revenue?period=thisMonth - Should return revenue for this month")
    void testGetRevenue_thisMonthPeriod() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/manager/report/revenue?period=thisMonth";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .param("period", "thisMonth"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testGetRevenue_thisMonthPeriod", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(7)
    @DisplayName("GET /api/manager/report/revenue?shift=1 - Should filter by shift")
    void testGetRevenue_byShift() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/manager/report/revenue?shift=1";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .param("shift", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testGetRevenue_byShift", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(8)
    @DisplayName("GET /api/manager/report/revenue?employeeId=2 - Should filter by employee")
    void testGetRevenue_byEmployee() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/manager/report/revenue?employeeId=2";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .param("employeeId", "2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testGetRevenue_byEmployee", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(9)
    @DisplayName("GET /api/manager/report/revenue?page=0&size=10 - Should support pagination")
    void testGetRevenue_withPagination() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/manager/report/revenue?page=0&size=10";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .param("page", "0")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testGetRevenue_withPagination", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(10)
    @DisplayName("GET /api/manager/report/revenue - Should apply multiple filters")
    void testGetRevenue_multipleFilters() throws Exception {
        long startTime = System.currentTimeMillis();
        String date = LocalDate.now().toString();
        String endpoint = "/api/manager/report/revenue?date=" + date + "&mode=daily&shift=1";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .param("date", date)
                        .param("mode", "daily")
                        .param("shift", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testGetRevenue_multipleFilters", "GET", endpoint, result, startTime);
    }
}


