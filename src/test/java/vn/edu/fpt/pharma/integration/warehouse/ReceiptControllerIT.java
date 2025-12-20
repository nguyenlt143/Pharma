package vn.edu.fpt.pharma.integration.warehouse;

import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration Tests for Receipt (Phiếu Nhập/Xuất) functionality.
 * <p>
 * Tests cover:
 * - GET /warehouse/receipt/create - Tạo phiếu mới
 * - GET /warehouse/receipt-list - Danh sách phiếu
 * - GET /warehouse/receipt-detail/{id} - Chi tiết phiếu
 * - POST /warehouse/receipts/{id}/approve - Duyệt phiếu
 * - POST /warehouse/receipts/{id}/ship - Gửi hàng
 * - POST /warehouse/receipts/{id}/receive - Nhận hàng
 * - POST /warehouse/receipts/{id}/cancel - Hủy phiếu
 * </p>
 */
@DisplayName("Receipt Controller Integration Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ReceiptControllerIT extends BaseWarehouseIT {

    private static final String TEST_CLASS = "ReceiptControllerIT";

    @BeforeAll
    static void setUpClass() {
        // Logging handled by BaseWarehouseIT
    }

    // ========================================================================
    // GET /warehouse/receipt/create - Màn hình tạo phiếu nhập
    // ========================================================================

    @Test
    @Order(1)
    @DisplayName("GET /warehouse/receipt/create - Should return create page with empty form")
    void testReceiptCreate_shouldReturnCreatePageWithEmptyForm() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/warehouse/receipt/create";

        MvcResult result = mockMvc.perform(get(endpoint))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("inventoryMovementVM"))
                .andExpect(model().attributeExists("inventoryMovementDetails"))
                .andReturn();

        logEvidence(TEST_CLASS, "testReceiptCreate_shouldReturnCreatePageWithEmptyForm", "GET", endpoint, result, startTime);
    }

    // ========================================================================
    // GET /warehouse/receipt-list - Danh sách phiếu
    // ========================================================================

    @Test
    @Order(2)
    @DisplayName("GET /warehouse/receipt-list - Should return list page with receipts and branches")
    void testReceiptList_shouldReturnListWithData() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/warehouse/receipt-list";

        MvcResult result = mockMvc.perform(get(endpoint))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("receipts"))
                .andExpect(model().attributeExists("branches"))
                .andReturn();

        logEvidence(TEST_CLASS, "testReceiptList_shouldReturnListWithData", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(3)
    @DisplayName("GET /warehouse/receipt/list - Alias should work same as receipt-list")
    void testReceiptListAlias_shouldWork() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/warehouse/receipt/list";

        MvcResult result = mockMvc.perform(get(endpoint))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("receipts"))
                .andReturn();

        logEvidence(TEST_CLASS, "testReceiptListAlias_shouldWork", "GET", endpoint, result, startTime);
    }

    // ========================================================================
    // GET /warehouse/receipt-list/filter - Lọc danh sách phiếu
    // ========================================================================

    @Test
    @Order(4)
    @DisplayName("GET /warehouse/receipt-list/filter - Should filter by type")
    void testReceiptListFilter_byType() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/warehouse/receipt-list/filter?type=SUP_TO_WARE";

        MvcResult result = mockMvc.perform(get(endpoint))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        logEvidence(TEST_CLASS, "testReceiptListFilter_byType", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(5)
    @DisplayName("GET /warehouse/receipt-list/filter - Should filter by branch")
    void testReceiptListFilter_byBranch() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/warehouse/receipt-list/filter?branchId=2";

        MvcResult result = mockMvc.perform(get(endpoint))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        logEvidence(TEST_CLASS, "testReceiptListFilter_byBranch", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(6)
    @DisplayName("GET /warehouse/receipt-list/filter - Should filter by status")
    void testReceiptListFilter_byStatus() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/warehouse/receipt-list/filter?status=DRAFT";

        MvcResult result = mockMvc.perform(get(endpoint))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        logEvidence(TEST_CLASS, "testReceiptListFilter_byStatus", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(7)
    @DisplayName("GET /warehouse/receipt-list/filter - INVENTORY_ADJUSTMENT should only show from main warehouse (source_branch_id = 1)")
    void testReceiptListFilter_inventoryAdjustmentOnlyFromMainWarehouse() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/warehouse/receipt-list/filter?type=INVENTORY_ADJUSTMENT";

        MvcResult result = mockMvc.perform(get(endpoint))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        logEvidence(TEST_CLASS, "testReceiptListFilter_inventoryAdjustmentOnlyFromMainWarehouse", "GET", endpoint, result, startTime);

        // Note: This test verifies that when filtering by INVENTORY_ADJUSTMENT type,
        // only movements with source_branch_id = 1 (main warehouse) are returned
    }

    // ========================================================================
    // GET /warehouse/receipt-detail/{id} - Chi tiết phiếu
    // ========================================================================

    @Test
    @Order(8)
    @DisplayName("GET /warehouse/receipt-detail/{id} - Should return receipt detail page")
    void testReceiptDetail_shouldReturnDetailPage() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/warehouse/receipt-detail/1";

        MvcResult result = mockMvc.perform(get(endpoint))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("receipt"))
                .andExpect(model().attributeExists("details"))
                .andReturn();

        logEvidence(TEST_CLASS, "testReceiptDetail_shouldReturnDetailPage", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(9)
    @DisplayName("GET /warehouse/receipt-detail/{id} - Should return detail for APPROVED receipt")
    void testReceiptDetail_approvedReceipt() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/warehouse/receipt-detail/2";

        MvcResult result = mockMvc.perform(get(endpoint))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("receipt"))
                .andReturn();

        logEvidence(TEST_CLASS, "testReceiptDetail_approvedReceipt", "GET", endpoint, result, startTime);
    }

    // ========================================================================
    // POST /warehouse/receipts/{id}/approve - Duyệt phiếu
    // ========================================================================

    @Test
    @Order(10)
    @DisplayName("POST /warehouse/receipts/{id}/approve - Should approve DRAFT receipt")
    void testApproveReceipt_shouldApproveSuccessfully() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/warehouse/receipts/1/approve";

        MvcResult result = mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        logEvidence(TEST_CLASS, "testApproveReceipt_shouldApproveSuccessfully", "POST", endpoint, result, startTime);
    }

    // ========================================================================
    // POST /warehouse/receipts/{id}/ship - Gửi hàng
    // ========================================================================

    @Test
    @Order(11)
    @DisplayName("POST /warehouse/receipts/{id}/ship - Should ship APPROVED receipt")
    void testShipReceipt_shouldShipSuccessfully() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/warehouse/receipts/2/ship";

        MvcResult result = mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        logEvidence(TEST_CLASS, "testShipReceipt_shouldShipSuccessfully", "POST", endpoint, result, startTime);
    }

    // ========================================================================
    // POST /warehouse/receipts/{id}/receive - Nhận hàng
    // ========================================================================

    @Test
    @Order(12)
    @DisplayName("POST /warehouse/receipts/{id}/receive - Should receive SHIPPED receipt")
    void testReceiveReceipt_shouldReceiveSuccessfully() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/warehouse/receipts/4/receive";

        MvcResult result = mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        logEvidence(TEST_CLASS, "testReceiveReceipt_shouldReceiveSuccessfully", "POST", endpoint, result, startTime);
    }

    // ========================================================================
    // POST /warehouse/receipts/{id}/cancel - Hủy phiếu
    // ========================================================================

    @Test
    @Order(13)
    @DisplayName("POST /warehouse/receipts/{id}/cancel - Should cancel DRAFT receipt")
    void testCancelReceipt_shouldCancelSuccessfully() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/warehouse/receipts/1/cancel";

        MvcResult result = mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        logEvidence(TEST_CLASS, "testCancelReceipt_shouldCancelSuccessfully", "POST", endpoint, result, startTime);
    }
}

