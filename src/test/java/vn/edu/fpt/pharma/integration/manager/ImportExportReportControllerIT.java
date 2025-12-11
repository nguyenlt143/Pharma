package vn.edu.fpt.pharma.integration.manager;

import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration Tests for Import/Export Report functionality.
 * <p>
 * Tests cover:
 * - GET /api/manager/import-export/summary - Lấy thống kê tổng quan
 * - GET /api/manager/import-export/movements - Lấy dữ liệu biểu đồ xuất nhập
 * </p>
 */
@DisplayName("Import/Export Report Controller Integration Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ImportExportReportControllerIT extends BaseManagerIT {

    private static final String TEST_CLASS = "ImportExportReportControllerIT";

    // ========================================================================
    // GET /api/manager/import-export/summary - Thống kê tổng quan
    // ========================================================================

    @Test
    @Order(1)
    @DisplayName("GET /api/manager/import-export/summary - Should return inventory summary")
    void testGetSummary_shouldReturnSummary() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/manager/import-export/summary";

        MvcResult result = mockMvc.perform(get(endpoint))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalValue").exists())
                .andExpect(jsonPath("$.totalValueFormatted").exists())
                .andExpect(jsonPath("$.lowStockCount").exists())
                .andExpect(jsonPath("$.pendingInbound").exists())
                .andExpect(jsonPath("$.pendingOutbound").exists())
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testGetSummary_shouldReturnSummary", "GET", endpoint, result, startTime);
    }

    // ========================================================================
    // GET /api/manager/import-export/movements - Biểu đồ xuất nhập
    // ========================================================================

    @Test
    @Order(2)
    @DisplayName("GET /api/manager/import-export/movements - Should return movements for default range (week)")
    void testGetMovements_defaultRange() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/manager/import-export/movements";

        MvcResult result = mockMvc.perform(get(endpoint))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testGetMovements_defaultRange", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(3)
    @DisplayName("GET /api/manager/import-export/movements?range=week - Should return weekly movements")
    void testGetMovements_weekRange() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/manager/import-export/movements?range=week";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .param("range", "week"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testGetMovements_weekRange", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(4)
    @DisplayName("GET /api/manager/import-export/movements?range=month - Should return monthly movements")
    void testGetMovements_monthRange() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/manager/import-export/movements?range=month";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .param("range", "month"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testGetMovements_monthRange", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(5)
    @DisplayName("GET /api/manager/import-export/movements?range=year - Should return yearly movements")
    void testGetMovements_yearRange() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/manager/import-export/movements?range=year";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .param("range", "year"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testGetMovements_yearRange", "GET", endpoint, result, startTime);
    }
}


