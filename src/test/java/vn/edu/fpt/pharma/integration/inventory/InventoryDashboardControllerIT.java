package vn.edu.fpt.pharma.integration.inventory;

import org.junit.jupiter.api.*;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration Tests for Inventory Dashboard and Overview functionality.
 * <p>
 * Tests cover:
 * - GET /inventory/dashboard - Dashboard tổng quan kho chi nhánh
 * - GET /inventory/medicine/list - Danh sách thuốc tại chi nhánh
 * - POST /inventory/api/inventory/{id}/min-stock - Cập nhật min stock
 * </p>
 *
 * Test Cases: INV001, INV002, INV003
 */
@DisplayName("Inventory Dashboard Integration Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class InventoryDashboardControllerIT extends BaseInventoryIT {

    private static final String TEST_CLASS = "InventoryDashboardControllerIT";

    // ========================================================================
    // INV001 - GET /inventory/dashboard
    // ========================================================================

    @Test
    @Order(1)
    @DisplayName("INV001 - Verify inventory dashboard display")
    void testDashboard_shouldReturnDashboardData() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/inventory/dashboard";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asInventory()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        logEvidence(TEST_CLASS, "testDashboard_shouldReturnDashboardData", "GET", endpoint, result, startTime);
    }

    // ========================================================================
    // INV002 - GET /inventory/medicine/list
    // ========================================================================

    @Test
    @Order(2)
    @DisplayName("INV002 - Verify medicine list at branch")
    void testMedicineList_shouldReturnBranchMedicines() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/inventory/medicine/list";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asInventory()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("medicines"))
                .andExpect(model().attributeExists("branchId"))
                .andReturn();

        logEvidence(TEST_CLASS, "testMedicineList_shouldReturnBranchMedicines", "GET", endpoint, result, startTime);
    }

    // ========================================================================
    // INV003 - POST /inventory/api/inventory/{id}/min-stock
    // ========================================================================

    @Test
    @Order(3)
    @DisplayName("INV003 - Verify min stock update")
    void testUpdateMinStock_shouldUpdateSuccessfully() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/inventory/api/inventory/1/min-stock";

        String requestBody = """
            {
                "minStock": 10
            }
            """;

        MvcResult result = mockMvc.perform(post(endpoint)
                        .with(asInventory())
                        .contentType("application/json")
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testUpdateMinStock_shouldUpdateSuccessfully", "POST", endpoint, result, startTime);
    }
}

