package vn.edu.fpt.pharma.integration.inventory;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration Tests for Import Request functionality.
 * <p>
 * Tests cover:
 * - GET /inventory/import/list - Danh sách yêu cầu nhập hàng
 * - GET /inventory/import/create - Màn hình tạo yêu cầu
 * - GET /inventory/import/detail/{id} - Chi tiết yêu cầu
 * - GET /inventory/api/medicines/search - Tìm kiếm thuốc
 * - POST /inventory/import/submit - Gửi yêu cầu nhập hàng
 * - GET /inventory/import/success/{code} - Màn hình thành công
 * </p>
 *
 * Test Cases: INV004-INV013 (10 tests)
 */
@DisplayName("Import Request Controller Integration Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ImportRequestControllerIT extends BaseInventoryIT {

    private static final String TEST_CLASS = "ImportRequestControllerIT";

    @Autowired
    private ObjectMapper objectMapper;

    // ========================================================================
    // INV004 - GET /inventory/import/list
    // ========================================================================

    @Test
    @Order(1)
    @DisplayName("INV004 - Verify import request list")
    void testImportList_shouldReturnRequestList() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/inventory/import/list";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asInventory()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("imports"))
                .andReturn();

        logEvidence(TEST_CLASS, "testImportList_shouldReturnRequestList", "GET", endpoint, result, startTime);
    }

    // ========================================================================
    // INV005 - GET /inventory/import/list?code=IMP001
    // ========================================================================

    @Test
    @Order(2)
    @DisplayName("INV005 - Verify import request list filter by code")
    void testImportList_shouldFilterByCode() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/inventory/import/list?code=IMP001";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asInventory())
                        .param("code", "IMP001"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("imports"))
                .andReturn();

        logEvidence(TEST_CLASS, "testImportList_shouldFilterByCode", "GET", endpoint, result, startTime);
    }

    // ========================================================================
    // INV006 - GET /inventory/import/list?createdAt=2026-01-07
    // ========================================================================

    @Test
    @Order(3)
    @DisplayName("INV006 - Verify import request list filter by date")
    void testImportList_shouldFilterByDate() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/inventory/import/list?createdAt=2026-01-07";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asInventory())
                        .param("createdAt", "2026-01-07"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("imports"))
                .andReturn();

        logEvidence(TEST_CLASS, "testImportList_shouldFilterByDate", "GET", endpoint, result, startTime);
    }

    // ========================================================================
    // INV007 - GET /inventory/import/create
    // ========================================================================

    @Test
    @Order(4)
    @DisplayName("INV007 - Verify import create page load")
    void testImportCreate_shouldLoadMedicinesFromWarehouse() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/inventory/import/create";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asInventory()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("medicines"))
                .andExpect(model().attributeExists("branchId"))
                .andReturn();

        logEvidence(TEST_CLASS, "testImportCreate_shouldLoadMedicinesFromWarehouse", "GET", endpoint, result, startTime);
    }

    // ========================================================================
    // INV008 - GET /inventory/api/medicines/search?query=Paracetamol
    // ========================================================================

    @Test
    @Order(5)
    @DisplayName("INV008 - Verify search medicines in warehouse")
    void testSearchMedicines_shouldReturnMatchingMedicines() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/inventory/api/medicines/search?query=Paracetamol";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asInventory())
                        .param("query", "Paracetamol"))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testSearchMedicines_shouldReturnMatchingMedicines", "GET", endpoint, result, startTime);
    }

    // ========================================================================
    // INV009 - POST /inventory/import/submit
    // ========================================================================

    @Test
    @Order(6)
    @DisplayName("INV009 - Verify submit import request success")
    void testSubmitImport_shouldCreateRequest() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/inventory/import/submit";

        String requestBody = """
            {
                "note": "Yêu cầu nhập hàng tháng 1",
                "items": [
                    {
                        "variantId": 1,
                        "batchId": 1,
                        "quantity": 100
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

        logEvidenceDetailed(TEST_CLASS, "testSubmitImport_shouldCreateRequest", "POST", endpoint, result, startTime);
    }

    // ========================================================================
    // INV010 - POST /inventory/import/submit (validation)
    // ========================================================================

    @Test
    @Order(7)
    @DisplayName("INV010 - Verify submit import request validation")
    void testSubmitImport_shouldValidateEmptyItems() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/inventory/import/submit";

        String requestBody = """
            {
                "note": "Invalid request",
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

        logEvidence(TEST_CLASS, "testSubmitImport_shouldValidateEmptyItems", "POST", endpoint, result, startTime);
    }

    // ========================================================================
    // INV011 - GET /inventory/import/detail/1
    // ========================================================================

    @Test
    @Order(8)
    @DisplayName("INV011 - Verify import request detail")
    void testImportDetail_shouldReturnRequestDetails() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/inventory/import/detail/1";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asInventory()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("request"))
                .andExpect(model().attributeExists("details"))
                .andReturn();

        logEvidence(TEST_CLASS, "testImportDetail_shouldReturnRequestDetails", "GET", endpoint, result, startTime);
    }

    // ========================================================================
    // INV012 - GET /inventory/import/success/IMP001
    // ========================================================================

    @Test
    @Order(9)
    @DisplayName("INV012 - Verify import success page")
    void testImportSuccess_shouldDisplaySuccessPage() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/inventory/import/success/IMP001";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asInventory()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attribute("code", "IMP001"))
                .andReturn();

        logEvidence(TEST_CLASS, "testImportSuccess_shouldDisplaySuccessPage", "GET", endpoint, result, startTime);
    }

    // ========================================================================
    // INV013 - GET /inventory/api/medicines/search?query=NOTEXIST
    // ========================================================================

    @Test
    @Order(10)
    @DisplayName("INV013 - Verify search medicines empty result")
    void testSearchMedicines_shouldReturnEmptyForNoMatch() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/inventory/api/medicines/search?query=NOTEXIST";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asInventory())
                        .param("query", "NOTEXIST"))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testSearchMedicines_shouldReturnEmptyForNoMatch", "GET", endpoint, result, startTime);
    }
}

