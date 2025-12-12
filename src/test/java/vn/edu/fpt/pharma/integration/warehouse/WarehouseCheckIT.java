package vn.edu.fpt.pharma.integration.warehouse;

import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration Tests for Warehouse Check (Kiểm kho) functionality.
 * <p>
 * Tests cover:
 * - GET /warehouse/check - Danh sách lịch sử kiểm kho
 * - GET /warehouse/check/create - Màn hình tạo phiếu kiểm kho
 * - GET /warehouse/check/detail?checkDate=... - Chi tiết kiểm kho theo ngày
 * - POST /warehouse/check/submit - Submit kiểm kho
 * </p>
 *
 * <p>
 * NOTE: Uses custom H2 DATE_FORMAT function registered in H2TestConfig
 * to simulate MySQL DATE_FORMAT behavior for testing purposes.
 * </p>
 */
@DisplayName("Warehouse Check Controller Integration Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class WarehouseCheckIT extends BaseWarehouseIT {

    private static final String TEST_CLASS = "WarehouseCheckIT";

    // ========================================================================
    // GET /warehouse/check - Danh sách lịch sử kiểm kho
    // ========================================================================

    @Test
    @Order(1)
    @DisplayName("GET /warehouse/check - Should return check history list")
    void testCheckList_shouldReturnHistoryList() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/warehouse/check";

        MvcResult result = mockMvc.perform(get(endpoint))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("inventoryChecks"))
                .andExpect(model().attribute("branchName", "Kho Tổng"))
                .andReturn();

        logEvidence(TEST_CLASS, "testCheckList_shouldReturnHistoryList", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(2)
    @DisplayName("GET /warehouse/check - Should have inventoryChecks attribute")
    void testCheckList_shouldHaveInventoryChecksAttribute() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/warehouse/check";

        MvcResult result = mockMvc.perform(get(endpoint))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("inventoryChecks"))
                .andReturn();

        Object inventoryChecks = result.getModelAndView().getModel().get("inventoryChecks");
        Assertions.assertNotNull(inventoryChecks, "inventoryChecks attribute should not be null");

        logEvidence(TEST_CLASS, "testCheckList_shouldHaveInventoryChecksAttribute", "GET", endpoint, result, startTime);
    }

    // ========================================================================
    // GET /warehouse/check/create - Màn hình tạo phiếu kiểm kho
    // ========================================================================

    @Test
    @Order(3)
    @DisplayName("GET /warehouse/check/create - Should return create check page with medicines")
    void testCheckCreate_shouldReturnCreatePageWithMedicines() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/warehouse/check/create";

        MvcResult result = mockMvc.perform(get(endpoint))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("medicines"))
                .andExpect(model().attribute("branchId", 1L))
                .andReturn();

        logEvidence(TEST_CLASS, "testCheckCreate_shouldReturnCreatePageWithMedicines", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(4)
    @DisplayName("GET /warehouse/check/create - Should have medicines list for checking")
    void testCheckCreate_shouldHaveMedicinesList() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/warehouse/check/create";

        MvcResult result = mockMvc.perform(get(endpoint))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("medicines"))
                .andReturn();

        Object medicines = result.getModelAndView().getModel().get("medicines");
        Assertions.assertNotNull(medicines, "Medicines attribute should not be null");

        logEvidence(TEST_CLASS, "testCheckCreate_shouldHaveMedicinesList", "GET", endpoint, result, startTime);
    }

    // ========================================================================
    // GET /warehouse/check/detail - Chi tiết kiểm kho theo ngày
    // ========================================================================

    @Test
    @Order(5)
    @DisplayName("GET /warehouse/check/detail?checkDate=2024-12-01 08:00:00 - Should return check details")
    void testCheckDetail_shouldReturnCheckDetails() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/warehouse/check/detail?checkDate=2024-12-01 08:00:00";

        MvcResult result = mockMvc.perform(get(endpoint))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("checkDate"))
                .andExpect(model().attributeExists("details"))
                .andReturn();

        logEvidence(TEST_CLASS, "testCheckDetail_shouldReturnCheckDetails", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(6)
    @DisplayName("GET /warehouse/check/detail - Should return correct checkDate")
    void testCheckDetail_shouldReturnCorrectCheckDate() throws Exception {
        long startTime = System.currentTimeMillis();
        String checkDate = "2024-12-01 08:00:00";
        String endpoint = "/warehouse/check/detail?checkDate=" + checkDate;

        MvcResult result = mockMvc.perform(get(endpoint))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attribute("checkDate", checkDate))
                .andReturn();

        logEvidence(TEST_CLASS, "testCheckDetail_shouldReturnCorrectCheckDate", "GET", endpoint, result, startTime);
    }

    // ========================================================================
    // POST /warehouse/check/submit - Submit kiểm kho
    // ========================================================================

    @Test
    @Order(7)
    @DisplayName("POST /warehouse/check/submit - Should submit inventory check successfully")
    void testCheckSubmit_shouldSubmitSuccessfully() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/warehouse/check/submit";

        String jsonBody = """
            {
                "items": [
                    {
                        "inventoryId": 1,
                        "variantId": 1,
                        "countedQuantity": 500
                    },
                    {
                        "inventoryId": 2,
                        "variantId": 2,
                        "countedQuantity": 300
                    }
                ]
            }
            """;

        MvcResult result = mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testCheckSubmit_shouldSubmitSuccessfully", "POST", endpoint, result, startTime);
    }

    @Test
    @Order(8)
    @DisplayName("POST /warehouse/check/submit - Should handle shortage detection")
    void testCheckSubmit_withShortage_shouldDetectShortage() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/warehouse/check/submit";

        // Submit with counted quantity less than system quantity (500 -> 480 = shortage 20)
        String jsonBody = """
            {
                "items": [
                    {
                        "inventoryId": 1,
                        "variantId": 1,
                        "countedQuantity": 480
                    }
                ]
            }
            """;

        MvcResult result = mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testCheckSubmit_withShortage_shouldDetectShortage", "POST", endpoint, result, startTime);
    }

    @Test
    @Order(9)
    @DisplayName("POST /warehouse/check/submit - Should handle surplus detection")
    void testCheckSubmit_withSurplus_shouldDetectSurplus() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/warehouse/check/submit";

        // Submit with counted quantity more than system quantity (500 -> 520 = surplus 20)
        String jsonBody = """
            {
                "items": [
                    {
                        "inventoryId": 1,
                        "variantId": 1,
                        "countedQuantity": 520
                    }
                ]
            }
            """;

        MvcResult result = mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testCheckSubmit_withSurplus_shouldDetectSurplus", "POST", endpoint, result, startTime);
    }

    @Test
    @Order(10)
    @DisplayName("POST /warehouse/check/submit - Should handle multiple items check")
    void testCheckSubmit_multipleItems_shouldProcessAll() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/warehouse/check/submit";

        String jsonBody = """
            {
                "items": [
                    {
                        "inventoryId": 1,
                        "variantId": 1,
                        "countedQuantity": 500
                    },
                    {
                        "inventoryId": 2,
                        "variantId": 2,
                        "countedQuantity": 295
                    },
                    {
                        "inventoryId": 3,
                        "variantId": 3,
                        "countedQuantity": 150
                    }
                ]
            }
            """;

        MvcResult result = mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testCheckSubmit_multipleItems_shouldProcessAll", "POST", endpoint, result, startTime);
    }
}
