package vn.edu.fpt.pharma.integration.manager;

import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration Tests for Manager Dashboard functionality.
 * <p>
 * Tests cover:
 * - GET /manager/dashboard - Trang dashboard chính
 * - GET /api/manager/dashboard - API lấy dữ liệu dashboard
 * </p>
 */
@DisplayName("Dashboard Controller Integration Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DashboardControllerIT extends BaseManagerIT {

    private static final String TEST_CLASS = "DashboardControllerIT";

    @BeforeAll
    static void setUpClass() {
        // Logging handled by BaseManagerIT
    }

    // ========================================================================
    // GET /manager/dashboard - Trang Dashboard chính
    // ========================================================================

    @Test
    @Order(1)
    @DisplayName("GET /manager/dashboard - Should return dashboard page")
    void testDashboardPage_shouldReturnDashboardPage() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/manager/dashboard";

        MvcResult result = mockMvc.perform(get(endpoint))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        logEvidence(TEST_CLASS, "testDashboardPage_shouldReturnDashboardPage", "GET", endpoint, result, startTime);
    }

    // ========================================================================
    // GET /api/manager/dashboard - API Dashboard Data
    // ========================================================================

    @Test
    @Order(2)
    @DisplayName("GET /api/manager/dashboard - Should return dashboard data with default days=0")
    void testDashboardApi_shouldReturnDashboardData() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/manager/dashboard";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asManager()))  // ← Inject CustomUserDetails
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testDashboardApi_shouldReturnDashboardData", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(3)
    @DisplayName("GET /api/manager/dashboard?days=7 - Should return dashboard data for last 7 days")
    void testDashboardApi_shouldReturnDataFor7Days() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/manager/dashboard?days=7";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .param("days", "7")
                        .with(asManager()))  // ← Inject CustomUserDetails
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testDashboardApi_shouldReturnDataFor7Days", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(4)
    @DisplayName("GET /api/manager/dashboard?days=30 - Should return dashboard data for last 30 days")
    void testDashboardApi_shouldReturnDataFor30Days() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/manager/dashboard?days=30";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .param("days", "30")
                        .with(asManager()))  // ← Inject CustomUserDetails
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testDashboardApi_shouldReturnDataFor30Days", "GET", endpoint, result, startTime);
    }

    // ========================================================================
    // Manager View Pages
    // ========================================================================

    @Test
    @Order(5)
    @DisplayName("GET /manager/staff - Should return staff management page")
    void testStaffPage_shouldReturnStaffPage() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/manager/staff";

        MvcResult result = mockMvc.perform(get(endpoint))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        logEvidence(TEST_CLASS, "testStaffPage_shouldReturnStaffPage", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(6)
    @DisplayName("GET /manager/report/revenue - Should return revenue report page")
    void testRevenueReportPage_shouldReturnPage() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/manager/report/revenue";

        MvcResult result = mockMvc.perform(get(endpoint))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        logEvidence(TEST_CLASS, "testRevenueReportPage_shouldReturnPage", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(7)
    @DisplayName("GET /manager/report/inventory - Should return inventory report page")
    void testInventoryReportPage_shouldReturnPage() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/manager/report/inventory";

        MvcResult result = mockMvc.perform(get(endpoint))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        logEvidence(TEST_CLASS, "testInventoryReportPage_shouldReturnPage", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(8)
    @DisplayName("GET /manager/report/import - Should return import report page")
    void testImportReportPage_shouldReturnPage() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/manager/report/import";

        MvcResult result = mockMvc.perform(get(endpoint))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("branchName"))
                .andExpect(model().attributeExists("range"))
                .andReturn();

        logEvidence(TEST_CLASS, "testImportReportPage_shouldReturnPage", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(9)
    @DisplayName("GET /manager/shift - Should return shift management page")
    void testShiftPage_shouldReturnShiftPage() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/manager/shift";

        MvcResult result = mockMvc.perform(get(endpoint))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        logEvidence(TEST_CLASS, "testShiftPage_shouldReturnShiftPage", "GET", endpoint, result, startTime);
    }
}

