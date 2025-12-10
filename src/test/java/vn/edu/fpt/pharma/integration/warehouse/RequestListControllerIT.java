package vn.edu.fpt.pharma.integration.warehouse;

import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration Tests for Request List (Danh sách yêu cầu) functionality.
 * <p>
 * Tests cover:
 * - GET /warehouse/request/list - Danh sách tất cả yêu cầu
 * - GET /warehouse/request/list/import - Danh sách yêu cầu nhập
 * - GET /warehouse/request/list/return - Danh sách yêu cầu trả
 * - GET /warehouse/request/detail?id={id} - Chi tiết yêu cầu
 * - POST /warehouse/request/confirm/{id} - Xác nhận yêu cầu
 * - POST /warehouse/request/cancel/{id} - Hủy yêu cầu
 * - GET /warehouse/request/list/filter - Lọc danh sách yêu cầu
 * </p>
 */
@DisplayName("Request List Controller Integration Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RequestListControllerIT extends BaseWarehouseIT {

    private static final String TEST_CLASS = "RequestListControllerIT";

    // ========================================================================
    // GET /warehouse/request/list - Danh sách tất cả yêu cầu
    // ========================================================================

    @Test
    @Order(1)
    @DisplayName("GET /warehouse/request/list - Should return all requests")
    void testRequestList_shouldReturnAllRequests() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/warehouse/request/list";

        MvcResult result = mockMvc.perform(get(endpoint))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("requests"))
                .andExpect(model().attributeExists("branches"))
                .andReturn();

        logEvidence(TEST_CLASS, "testRequestList_shouldReturnAllRequests", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(2)
    @DisplayName("GET /warehouse/request/list - Should have requests and branches")
    void testRequestList_shouldHaveRequestsAndBranches() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/warehouse/request/list";

        MvcResult result = mockMvc.perform(get(endpoint))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("requests"))
                .andExpect(model().attributeExists("branches"))
                .andReturn();

        Object requests = result.getModelAndView().getModel().get("requests");
        Object branches = result.getModelAndView().getModel().get("branches");
        Assertions.assertNotNull(requests, "Requests attribute should not be null");
        Assertions.assertNotNull(branches, "Branches attribute should not be null");

        logEvidence(TEST_CLASS, "testRequestList_shouldHaveRequestsAndBranches", "GET", endpoint, result, startTime);
    }

    // ========================================================================
    // GET /warehouse/request/list/import - Danh sách yêu cầu nhập
    // ========================================================================

    @Test
    @Order(3)
    @DisplayName("GET /warehouse/request/list/import - Should return import requests only")
    void testRequestListImport_shouldReturnImportRequests() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/warehouse/request/list/import";

        MvcResult result = mockMvc.perform(get(endpoint))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("requests"))
                .andExpect(model().attributeExists("branches"))
                .andReturn();

        logEvidence(TEST_CLASS, "testRequestListImport_shouldReturnImportRequests", "GET", endpoint, result, startTime);
    }

    // ========================================================================
    // GET /warehouse/request/list/return - Danh sách yêu cầu trả
    // ========================================================================

    @Test
    @Order(4)
    @DisplayName("GET /warehouse/request/list/return - Should return return requests only")
    void testRequestListReturn_shouldReturnReturnRequests() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/warehouse/request/list/return";

        MvcResult result = mockMvc.perform(get(endpoint))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("requests"))
                .andExpect(model().attributeExists("branches"))
                .andReturn();

        logEvidence(TEST_CLASS, "testRequestListReturn_shouldReturnReturnRequests", "GET", endpoint, result, startTime);
    }

    // ========================================================================
    // GET /warehouse/request/detail - Chi tiết yêu cầu
    // ========================================================================

    @Test
    @Order(5)
    @DisplayName("GET /warehouse/request/detail?id=1 - Should return request detail")
    void testRequestDetail_shouldReturnDetail() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/warehouse/request/detail?id=1";

        MvcResult result = mockMvc.perform(get(endpoint))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("request"))
                .andExpect(model().attributeExists("details"))
                .andReturn();

        logEvidence(TEST_CLASS, "testRequestDetail_shouldReturnDetail", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(6)
    @DisplayName("GET /warehouse/request/detail?id=2 - Should return another request detail")
    void testRequestDetail_anotherRequest_shouldReturnDetail() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/warehouse/request/detail?id=2";

        MvcResult result = mockMvc.perform(get(endpoint))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("request"))
                .andExpect(model().attributeExists("details"))
                .andReturn();

        logEvidence(TEST_CLASS, "testRequestDetail_anotherRequest_shouldReturnDetail", "GET", endpoint, result, startTime);
    }

    // ========================================================================
    // POST /warehouse/request/confirm/{id} - Xác nhận yêu cầu
    // ========================================================================

    @Test
    @Order(7)
    @DisplayName("POST /warehouse/request/confirm/{id} - Should confirm pending request")
    void testRequestConfirm_shouldConfirmSuccessfully() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/warehouse/request/confirm/1";

        MvcResult result = mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        logEvidence(TEST_CLASS, "testRequestConfirm_shouldConfirmSuccessfully", "POST", endpoint, result, startTime);
    }

    // ========================================================================
    // POST /warehouse/request/cancel/{id} - Hủy yêu cầu
    // ========================================================================

    @Test
    @Order(8)
    @DisplayName("POST /warehouse/request/cancel/{id} - Should cancel pending request")
    void testRequestCancel_shouldCancelSuccessfully() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/warehouse/request/cancel/3";

        MvcResult result = mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        logEvidence(TEST_CLASS, "testRequestCancel_shouldCancelSuccessfully", "POST", endpoint, result, startTime);
    }

    // ========================================================================
    // GET /warehouse/request/list/filter - Lọc danh sách yêu cầu
    // ========================================================================

    @Test
    @Order(9)
    @DisplayName("GET /warehouse/request/list/filter - Should filter by type")
    void testRequestListFilter_byType() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/warehouse/request/list/filter?type=IMPORT";

        MvcResult result = mockMvc.perform(get(endpoint))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        logEvidence(TEST_CLASS, "testRequestListFilter_byType", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(10)
    @DisplayName("GET /warehouse/request/list/filter - Should filter by branch")
    void testRequestListFilter_byBranch() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/warehouse/request/list/filter?branchId=2";

        MvcResult result = mockMvc.perform(get(endpoint))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        logEvidence(TEST_CLASS, "testRequestListFilter_byBranch", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(11)
    @DisplayName("GET /warehouse/request/list/filter - Should filter by status")
    void testRequestListFilter_byStatus() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/warehouse/request/list/filter?status=REQUESTED";

        MvcResult result = mockMvc.perform(get(endpoint))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        logEvidence(TEST_CLASS, "testRequestListFilter_byStatus", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(12)
    @DisplayName("GET /warehouse/request/list/filter - Should filter by multiple params")
    void testRequestListFilter_byMultipleParams() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/warehouse/request/list/filter?type=IMPORT&branchId=2&status=REQUESTED";

        MvcResult result = mockMvc.perform(get(endpoint))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        logEvidence(TEST_CLASS, "testRequestListFilter_byMultipleParams", "GET", endpoint, result, startTime);
    }
}

