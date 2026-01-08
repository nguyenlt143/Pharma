package vn.edu.fpt.pharma.integration.inventory;

import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration Tests for Inventory Check functionality.
 * <p>
 * Tests cover:
 * - GET /inventory/check - Lịch sử kiểm kho
 * - GET /inventory/check/create - Tạo phiếu kiểm kho
 * - GET /inventory/check/detail - Chi tiết kiểm kho
 * - POST /inventory/check/submit - Gửi kết quả kiểm kho
 * </p>
 *
 * Test Cases: INV020-INV029 (10 tests)
 */
@DisplayName("Inventory Check Controller Integration Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class InventoryCheckControllerIT extends BaseInventoryIT {

    private static final String TEST_CLASS = "InventoryCheckControllerIT";

    // ========================================================================
    // INV020 - GET /inventory/check
    // ========================================================================

    @Test
    @Order(1)
    @DisplayName("INV020 - Verify inventory check history")
    void testCheckHistory_shouldReturnCheckList() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/inventory/check";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asInventory()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        logEvidence(TEST_CLASS, "testCheckHistory_shouldReturnCheckList", "GET", endpoint, result, startTime);
    }

    // ========================================================================
    // INV021 - GET /inventory/check/create
    // ========================================================================

    @Test
    @Order(2)
    @DisplayName("INV021 - Verify inventory check create page")
    void testCheckCreate_shouldLoadBranchMedicines() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/inventory/check/create";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asInventory()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("medicines"))
                .andExpect(model().attributeExists("branchId"))
                .andReturn();

        logEvidence(TEST_CLASS, "testCheckCreate_shouldLoadBranchMedicines", "GET", endpoint, result, startTime);
    }

    // ========================================================================
    // INV022 - GET /inventory/check/detail?checkDate=...
    // ========================================================================

    @Test
    @Order(3)
    @DisplayName("INV022 - Verify inventory check detail")
    void testCheckDetail_shouldReturnCheckDetails() throws Exception {
        long startTime = System.currentTimeMillis();
        String checkDate = "2026-01-07 08:00:00";
        String endpoint = "/inventory/check/detail?checkDate=" + checkDate;

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asInventory())
                        .param("checkDate", checkDate))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        logEvidence(TEST_CLASS, "testCheckDetail_shouldReturnCheckDetails", "GET", endpoint, result, startTime);
    }

    // ========================================================================
    // INV023 - POST /inventory/check/submit (success)
    // ========================================================================

    @Test
    @Order(4)
    @DisplayName("INV023 - Verify submit inventory check success")
    void testCheckSubmit_shouldSaveCheckResults() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/inventory/check/submit";

        String requestBody = """
            {
                "items": [
                    {
                        "inventoryId": 1,
                        "variantId": 1,
                        "countedQuantity": 100
                    }
                ]
            }
            """;

        MvcResult result = mockMvc.perform(post(endpoint)
                        .with(asInventory())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testCheckSubmit_shouldSaveCheckResults", "POST", endpoint, result, startTime);
    }

    // ========================================================================
    // INV024 - POST /inventory/check/submit (detect shortage)
    // ========================================================================

    @Test
    @Order(5)
    @DisplayName("INV024 - Verify check detects shortage")
    void testCheckSubmit_shouldDetectShortage() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/inventory/check/submit";

        String requestBody = """
            {
                "items": [
                    {
                        "inventoryId": 1,
                        "variantId": 1,
                        "countedQuantity": 50
                    }
                ]
            }
            """;

        MvcResult result = mockMvc.perform(post(endpoint)
                        .with(asInventory())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testCheckSubmit_shouldDetectShortage", "POST", endpoint, result, startTime);
    }

    // ========================================================================
    // INV025 - POST /inventory/check/submit (detect surplus)
    // ========================================================================

    @Test
    @Order(6)
    @DisplayName("INV025 - Verify check detects surplus")
    void testCheckSubmit_shouldDetectSurplus() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/inventory/check/submit";

        String requestBody = """
            {
                "items": [
                    {
                        "inventoryId": 1,
                        "variantId": 1,
                        "countedQuantity": 200
                    }
                ]
            }
            """;

        MvcResult result = mockMvc.perform(post(endpoint)
                        .with(asInventory())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testCheckSubmit_shouldDetectSurplus", "POST", endpoint, result, startTime);
    }

    // ========================================================================
    // INV026 - POST /inventory/check/submit (exact match)
    // ========================================================================

    @Test
    @Order(7)
    @DisplayName("INV026 - Verify check exact match")
    void testCheckSubmit_exactMatch_shouldNotCreateAdjustment() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/inventory/check/submit";

        String requestBody = """
            {
                "items": [
                    {
                        "inventoryId": 1,
                        "variantId": 1,
                        "countedQuantity": 100
                    }
                ]
            }
            """;

        MvcResult result = mockMvc.perform(post(endpoint)
                        .with(asInventory())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        logEvidence(TEST_CLASS, "testCheckSubmit_exactMatch_shouldNotCreateAdjustment", "POST", endpoint, result, startTime);
    }

    // ========================================================================
    // INV027 - POST /inventory/check/submit (validation)
    // ========================================================================

    @Test
    @Order(8)
    @DisplayName("INV027 - Verify check validation")
    void testCheckSubmit_shouldValidateInput() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/inventory/check/submit";

        String requestBody = """
            {
                "items": []
            }
            """;

        MvcResult result = mockMvc.perform(post(endpoint)
                        .with(asInventory())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andReturn();

        logEvidence(TEST_CLASS, "testCheckSubmit_shouldValidateInput", "POST", endpoint, result, startTime);
    }

    // ========================================================================
    // INV028 - GET /inventory/check (empty history)
    // ========================================================================

    @Test
    @Order(9)
    @DisplayName("INV028 - Verify check history empty")
    void testCheckHistory_shouldReturnEmptyList() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/inventory/check";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asInventory()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        logEvidence(TEST_CLASS, "testCheckHistory_shouldReturnEmptyList", "GET", endpoint, result, startTime);
    }

    // ========================================================================
    // INV029 - POST /inventory/check/submit (multiple items)
    // ========================================================================

    @Test
    @Order(10)
    @DisplayName("INV029 - Verify check multiple items")
    void testCheckSubmit_multipleItems_shouldProcessAll() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/inventory/check/submit";

        String requestBody = """
            {
                "items": [
                    {
                        "inventoryId": 1,
                        "variantId": 1,
                        "countedQuantity": 100
                    },
                    {
                        "inventoryId": 2,
                        "variantId": 2,
                        "countedQuantity": 50
                    },
                    {
                        "inventoryId": 3,
                        "variantId": 3,
                        "countedQuantity": 200
                    }
                ]
            }
            """;

        MvcResult result = mockMvc.perform(post(endpoint)
                        .with(asInventory())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testCheckSubmit_multipleItems_shouldProcessAll", "POST", endpoint, result, startTime);
    }
}

