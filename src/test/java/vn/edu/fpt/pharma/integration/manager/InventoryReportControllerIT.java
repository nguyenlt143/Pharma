package vn.edu.fpt.pharma.integration.manager;

import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration Tests for Inventory Report functionality.
 * <p>
 * Tests cover:
 * - GET /api/manager/report/inventory - Lấy tổng quan inventory
 * - GET /api/manager/report/inventory/details - Lấy chi tiết inventory
 * - GET /api/manager/report/inventory/categories - Lấy danh sách categories
 * - GET /api/manager/report/inventory/statistics - Lấy thống kê theo category
 * - GET /api/manager/report/inventory/search - Tìm kiếm inventory
 * </p>
 */
@DisplayName("Inventory Report Controller Integration Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class InventoryReportControllerIT extends BaseManagerIT {

    private static final String TEST_CLASS = "InventoryReportControllerIT";

    // ========================================================================
    // GET /api/manager/report/inventory - Tổng quan
    // ========================================================================

    @Test
    @Order(1)
    @DisplayName("GET /api/manager/report/inventory - Should return inventory summary")
    void testGetInventorySummary_shouldReturnSummary() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/manager/report/inventory";

        MvcResult result = mockMvc.perform(get(endpoint))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testGetInventorySummary_shouldReturnSummary", "GET", endpoint, result, startTime);
    }

    // ========================================================================
    // GET /api/manager/report/inventory/details - Chi tiết
    // ========================================================================

    @Test
    @Order(2)
    @DisplayName("GET /api/manager/report/inventory/details - Should return inventory details")
    void testGetInventoryDetails_shouldReturnDetails() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/manager/report/inventory/details";

        MvcResult result = mockMvc.perform(get(endpoint))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testGetInventoryDetails_shouldReturnDetails", "GET", endpoint, result, startTime);
    }

    // ========================================================================
    // GET /api/manager/report/inventory/categories - Danh sách categories
    // ========================================================================

    @Test
    @Order(3)
    @DisplayName("GET /api/manager/report/inventory/categories - Should return all categories")
    void testGetCategories_shouldReturnCategories() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/manager/report/inventory/categories";

        MvcResult result = mockMvc.perform(get(endpoint))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testGetCategories_shouldReturnCategories", "GET", endpoint, result, startTime);
    }

    // ========================================================================
    // GET /api/manager/report/inventory/statistics - Thống kê theo category
    // ========================================================================

    @Test
    @Order(4)
    @DisplayName("GET /api/manager/report/inventory/statistics - Should return category statistics")
    void testGetStatistics_shouldReturnStatistics() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/manager/report/inventory/statistics";

        MvcResult result = mockMvc.perform(get(endpoint))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testGetStatistics_shouldReturnStatistics", "GET", endpoint, result, startTime);
    }

    // ========================================================================
    // GET /api/manager/report/inventory/search - Tìm kiếm
    // ========================================================================

    @Test
    @Order(5)
    @DisplayName("GET /api/manager/report/inventory/search - Should return all inventory (no filter)")
    void testSearchInventory_noFilter() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/manager/report/inventory/search";

        MvcResult result = mockMvc.perform(get(endpoint))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testSearchInventory_noFilter", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(6)
    @DisplayName("GET /api/manager/report/inventory/search?query=Paracetamol - Should search by product name")
    void testSearchInventory_byQuery() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/manager/report/inventory/search?query=Paracetamol";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .param("query", "Paracetamol"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testSearchInventory_byQuery", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(7)
    @DisplayName("GET /api/manager/report/inventory/search?categoryId=1 - Should filter by category")
    void testSearchInventory_byCategory() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/manager/report/inventory/search?categoryId=1";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .param("categoryId", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testSearchInventory_byCategory", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(8)
    @DisplayName("GET /api/manager/report/inventory/search?status=low - Should filter by low stock status")
    void testSearchInventory_byStatus() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/manager/report/inventory/search?status=low";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .param("status", "low"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testSearchInventory_byStatus", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(9)
    @DisplayName("GET /api/manager/report/inventory/search - Should apply multiple filters")
    void testSearchInventory_multipleFilters() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/manager/report/inventory/search?query=Thuốc&categoryId=1&status=normal";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .param("query", "Thuốc")
                        .param("categoryId", "1")
                        .param("status", "normal"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testSearchInventory_multipleFilters", "GET", endpoint, result, startTime);
    }
}


