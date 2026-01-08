package vn.edu.fpt.pharma.integration.inventory;

import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration Tests for Medicine Management functionality.
 * Test Cases: INV038-INV040 (3 tests)
 */
@DisplayName("Medicine Management Controller Integration Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MedicineManagementControllerIT extends BaseInventoryIT {

    private static final String TEST_CLASS = "MedicineManagementControllerIT";

    @Test
    @Order(1)
    @DisplayName("INV038 - Verify delete out of stock medicines")
    void testDeleteOutOfStock_shouldRemoveZeroQuantityItems() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/inventory/medicine/delete-out-of-stock";

        MvcResult result = mockMvc.perform(post(endpoint)
                        .with(asInventory()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.deletedCount").exists())
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testDeleteOutOfStock_shouldRemoveZeroQuantityItems", "POST", endpoint, result, startTime);
    }

    @Test
    @Order(2)
    @DisplayName("INV039 - Verify update min stock success")
    void testUpdateMinStock_shouldUpdateSuccessfully() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/inventory/api/inventory/1/min-stock";

        String requestBody = """
            {
                "minStock": 50
            }
            """;

        MvcResult result = mockMvc.perform(post(endpoint)
                        .with(asInventory())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testUpdateMinStock_shouldUpdateSuccessfully", "POST", endpoint, result, startTime);
    }

    @Test
    @Order(3)
    @DisplayName("INV040 - Verify update min stock validation")
    void testUpdateMinStock_shouldReturn404WhenNotFound() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/inventory/api/inventory/999/min-stock";

        String requestBody = """
            {
                "minStock": 50
            }
            """;

        MvcResult result = mockMvc.perform(post(endpoint)
                        .with(asInventory())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andReturn();

        logEvidence(TEST_CLASS, "testUpdateMinStock_shouldReturn404WhenNotFound", "POST", endpoint, result, startTime);
    }
}

