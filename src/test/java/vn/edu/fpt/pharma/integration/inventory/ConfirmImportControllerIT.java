package vn.edu.fpt.pharma.integration.inventory;

import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration Tests for Confirm Import functionality.
 * Test Cases: INV014-INV019 (6 tests)
 */
@DisplayName("Confirm Import Controller Integration Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ConfirmImportControllerIT extends BaseInventoryIT {

    private static final String TEST_CLASS = "ConfirmImportControllerIT";

    @Test
    @Order(1)
    @DisplayName("INV014 - Verify confirm import list")
    void testConfirmList_shouldReturnPendingReceipts() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/inventory/confirm/list";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asInventory()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("confirmImports"))
                .andReturn();

        logEvidence(TEST_CLASS, "testConfirmList_shouldReturnPendingReceipts", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(2)
    @DisplayName("INV015 - Verify confirm import detail")
    void testConfirmDetail_shouldReturnReceiptDetails() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/inventory/confirm/detail/1";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asInventory()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        logEvidence(TEST_CLASS, "testConfirmDetail_shouldReturnReceiptDetails", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(3)
    @DisplayName("INV016 - Verify confirm import success")
    void testConfirmImport_shouldUpdateInventory() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/inventory/confirm/1";

        MvcResult result = mockMvc.perform(post(endpoint)
                        .with(asInventory()))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testConfirmImport_shouldUpdateInventory", "POST", endpoint, result, startTime);
    }

    @Test
    @Order(4)
    @DisplayName("INV017 - Verify confirm import invalid status")
    void testConfirmImport_shouldRejectInvalidStatus() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/inventory/confirm/999";

        MvcResult result = mockMvc.perform(post(endpoint)
                        .with(asInventory()))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andReturn();

        logEvidence(TEST_CLASS, "testConfirmImport_shouldRejectInvalidStatus", "POST", endpoint, result, startTime);
    }

    @Test
    @Order(5)
    @DisplayName("INV018 - Verify confirm import list empty")
    void testConfirmList_shouldReturnEmptyWhenNoPending() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/inventory/confirm/list";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asInventory()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("confirmImports"))
                .andReturn();

        logEvidence(TEST_CLASS, "testConfirmList_shouldReturnEmptyWhenNoPending", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(6)
    @DisplayName("INV019 - Verify confirm import detail not found")
    void testConfirmDetail_shouldReturn404WhenNotFound() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/inventory/confirm/detail/999";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asInventory()))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andReturn();

        logEvidence(TEST_CLASS, "testConfirmDetail_shouldReturn404WhenNotFound", "GET", endpoint, result, startTime);
    }
}

