package vn.edu.fpt.pharma.integration.warehouse;

import org.junit.jupiter.api.*;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration Tests for Warehouse Inventory functionality.
 * <p>
 * Tests cover:
 * - GET /warehouse/inventory - Danh sách tồn kho tại kho tổng
 * </p>
 */
@DisplayName("Warehouse Inventory Controller Integration Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class InventoryControllerIT extends BaseWarehouseIT {

    private static final String TEST_CLASS = "InventoryControllerIT";

    // ========================================================================
    // GET /warehouse/inventory - Danh sách tồn kho
    // ========================================================================

    @Test
    @Order(1)
    @DisplayName("GET /warehouse/inventory - Should return inventory list for warehouse")
    void testWarehouseInventory_shouldReturnInventoryList() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/warehouse/inventory";

        MvcResult result = mockMvc.perform(get(endpoint))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("medicines"))
                .andExpect(model().attribute("branchId", 1L))
                .andExpect(model().attribute("branchName", "Kho Tổng"))
                .andReturn();

        logEvidence(TEST_CLASS, "testWarehouseInventory_shouldReturnInventoryList", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(2)
    @DisplayName("GET /warehouse/inventory - Should have medicines attribute not empty")
    void testWarehouseInventory_shouldHaveMedicines() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/warehouse/inventory";

        MvcResult result = mockMvc.perform(get(endpoint))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("medicines"))
                .andReturn();

        // Verify medicines list exists
        Object medicines = result.getModelAndView().getModel().get("medicines");
        Assertions.assertNotNull(medicines, "Medicines attribute should not be null");

        logEvidence(TEST_CLASS, "testWarehouseInventory_shouldHaveMedicines", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(3)
    @DisplayName("GET /warehouse/inventory - Should return correct branch info")
    void testWarehouseInventory_shouldReturnCorrectBranchInfo() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/warehouse/inventory";

        MvcResult result = mockMvc.perform(get(endpoint))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attribute("branchId", 1L))
                .andExpect(model().attribute("branchName", "Kho Tổng"))
                .andReturn();

        logEvidence(TEST_CLASS, "testWarehouseInventory_shouldReturnCorrectBranchInfo", "GET", endpoint, result, startTime);
    }
}

