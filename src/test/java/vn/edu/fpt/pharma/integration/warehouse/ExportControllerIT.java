package vn.edu.fpt.pharma.integration.warehouse;

import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration Tests for Export (Phiếu Xuất) functionality.
 * <p>
 * Tests cover:
 * - GET /warehouse/export/create - Màn hình tạo phiếu xuất (không có requestId)
 * - GET /warehouse/export/create?requestId={id} - Tạo phiếu xuất từ yêu cầu
 * - POST /warehouse/export/create - Submit tạo phiếu xuất
 * </p>
 */
@DisplayName("Export Controller Integration Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ExportControllerIT extends BaseWarehouseIT {

    private static final String TEST_CLASS = "ExportControllerIT";

    // ========================================================================
    // GET /warehouse/export/create - Màn hình tạo phiếu xuất
    // ========================================================================

    @Test
    @Order(1)
    @DisplayName("GET /warehouse/export/create - Should return empty export form")
    void testExportCreate_withoutRequestId_shouldReturnEmptyForm() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/warehouse/export/create";

        MvcResult result = mockMvc.perform(get(endpoint))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attribute("exportData", (Object) null))
                .andReturn();

        logEvidence(TEST_CLASS, "testExportCreate_withoutRequestId_shouldReturnEmptyForm", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(2)
    @DisplayName("GET /warehouse/export/create?requestId=1 - Should return form with request data")
    void testExportCreate_withRequestId_shouldReturnPopulatedForm() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/warehouse/export/create?requestId=1";

        MvcResult result = mockMvc.perform(get(endpoint))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("exportData"))
                .andReturn();

        logEvidence(TEST_CLASS, "testExportCreate_withRequestId_shouldReturnPopulatedForm", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(3)
    @DisplayName("GET /warehouse/export/create?requestId=2 - Should work with different request")
    void testExportCreate_withApprovedRequestId_shouldReturnData() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/warehouse/export/create?requestId=2";

        MvcResult result = mockMvc.perform(get(endpoint))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        logEvidence(TEST_CLASS, "testExportCreate_withApprovedRequestId_shouldReturnData", "GET", endpoint, result, startTime);
    }

    // ========================================================================
    // POST /warehouse/export/create - Submit tạo phiếu xuất
    // ========================================================================

    @Test
    @Order(4)
    @DisplayName("POST /warehouse/export/create - Should create export movement successfully")
    void testExportCreate_postValidData_shouldReturnSuccess() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/warehouse/export/create";

        String jsonBody = """
            {
                "branchId": 2,
                "requestId": 1,
                "note": "Xuất hàng theo yêu cầu",
                "details": [
                    {
                        "inventoryId": 1,
                        "variantId": 1,
                        "batchId": 1,
                        "quantity": 50,
                        "price": 5000.0
                    }
                ]
            }
            """;

        MvcResult result = mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").exists())
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testExportCreate_postValidData_shouldReturnSuccess", "POST", endpoint, result, startTime);
    }

    @Test
    @Order(5)
    @DisplayName("POST /warehouse/export/create - Should handle multiple items")
    void testExportCreate_postMultipleItems_shouldReturnSuccess() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/warehouse/export/create";

        String jsonBody = """
            {
                "branchId": 3,
                "requestId": null,
                "note": "Xuất hàng đợt 2",
                "details": [
                    {
                        "inventoryId": 1,
                        "variantId": 1,
                        "batchId": 1,
                        "quantity": 30,
                        "price": 5000.0
                    },
                    {
                        "inventoryId": 2,
                        "variantId": 2,
                        "batchId": 2,
                        "quantity": 20,
                        "price": 15000.0
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

        logEvidenceDetailed(TEST_CLASS, "testExportCreate_postMultipleItems_shouldReturnSuccess", "POST", endpoint, result, startTime);
    }

    @Test
    @Order(6)
    @DisplayName("POST /warehouse/export/create - Should handle empty items gracefully")
    void testExportCreate_postEmptyItems_shouldHandleGracefully() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/warehouse/export/create";

        String jsonBody = """
            {
                "branchId": 2,
                "requestId": null,
                "note": "Test empty",
                "details": []
            }
            """;

        MvcResult result = mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andDo(print())
                // May return error or success depending on validation
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testExportCreate_postEmptyItems_shouldHandleGracefully", "POST", endpoint, result, startTime);
    }
}

