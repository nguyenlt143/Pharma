package vn.edu.fpt.pharma.integration.inventory;

import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration Tests for Return Request functionality.
 * Test Cases: INV030-INV037 (8 tests)
 */
@DisplayName("Return Request Controller Integration Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ReturnRequestControllerIT extends BaseInventoryIT {

    private static final String TEST_CLASS = "ReturnRequestControllerIT";

    @Test
    @Order(1)
    @DisplayName("INV030 - Verify return request list")
    void testReturnList_shouldReturnRequestList() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/inventory/return/list";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asInventory()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("returnRequests"))
                .andReturn();

        logEvidence(TEST_CLASS, "testReturnList_shouldReturnRequestList", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(2)
    @DisplayName("INV031 - Verify return create page")
    void testReturnCreate_shouldLoadBranchMedicines() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/inventory/return/create";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asInventory()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("medicines"))
                .andExpect(model().attributeExists("branchId"))
                .andReturn();

        logEvidence(TEST_CLASS, "testReturnCreate_shouldLoadBranchMedicines", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(3)
    @DisplayName("INV032 - Verify submit return request success")
    void testReturnSubmit_shouldCreateReturnRequest() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/inventory/return/create";

        String requestBody = """
            {
                "note": "Trả hàng hết hạn",
                "items": [
                    {
                        "variantId": 1,
                        "batchId": 1,
                        "quantity": 10,
                        "reason": "Expiring soon"
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

        logEvidenceDetailed(TEST_CLASS, "testReturnSubmit_shouldCreateReturnRequest", "POST", endpoint, result, startTime);
    }

    @Test
    @Order(4)
    @DisplayName("INV033 - Verify return request validation")
    void testReturnSubmit_shouldValidateQuantity() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/inventory/return/create";

        String requestBody = """
            {
                "note": "Invalid quantity",
                "items": [
                    {
                        "variantId": 1,
                        "batchId": 1,
                        "quantity": 99999
                    }
                ]
            }
            """;

        MvcResult result = mockMvc.perform(post(endpoint)
                        .with(asInventory())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andReturn();

        logEvidence(TEST_CLASS, "testReturnSubmit_shouldValidateQuantity", "POST", endpoint, result, startTime);
    }

    @Test
    @Order(5)
    @DisplayName("INV034 - Verify return success page")
    void testReturnSuccess_shouldDisplaySuccessPage() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/inventory/return/success?code=RET001";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asInventory())
                        .param("code", "RET001"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        logEvidence(TEST_CLASS, "testReturnSuccess_shouldDisplaySuccessPage", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(6)
    @DisplayName("INV035 - Verify return request detail")
    void testReturnDetail_shouldReturnRequestDetails() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/inventory/return/detail/1";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asInventory()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        logEvidence(TEST_CLASS, "testReturnDetail_shouldReturnRequestDetails", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(7)
    @DisplayName("INV036 - Verify return list empty")
    void testReturnList_shouldReturnEmptyWhenNoReturns() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/inventory/return/list";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asInventory()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("returnRequests"))
                .andReturn();

        logEvidence(TEST_CLASS, "testReturnList_shouldReturnEmptyWhenNoReturns", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(8)
    @DisplayName("INV037 - Verify return create validation")
    void testReturnCreate_shouldValidateEmptyItems() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/inventory/return/create";

        String requestBody = """
            {
                "note": "Empty items",
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

        logEvidence(TEST_CLASS, "testReturnCreate_shouldValidateEmptyItems", "POST", endpoint, result, startTime);
    }
}

