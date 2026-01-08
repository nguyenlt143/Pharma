package vn.edu.fpt.pharma.integration.owner;

import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration Tests for Supplier Management, Reports, Inventory, and Adjustments.
 * Test Cases: OWN004-OWN020 (17 tests)
 */
@DisplayName("Owner All Features Controller Integration Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OwnerAllFeaturesControllerIT extends BaseOwnerIT {

    private static final String TEST_CLASS = "OwnerAllFeaturesControllerIT";

    // ========================================================================
    // SUPPLIER MANAGEMENT - OWN004-OWN009 (6 tests)
    // ========================================================================

    @Test
    @Order(1)
    @DisplayName("OWN004 - Verify supplier list page")
    void testSupplierListPage() throws Exception {
        long startTime = System.currentTimeMillis();
        MvcResult result = mockMvc.perform(get("/owner/supplier/list").with(asOwner())).andDo(print()).andExpect(status().isOk()).andReturn();
        logEvidence(TEST_CLASS, "testSupplierListPage", "GET", "/owner/supplier/list", result, startTime);
    }

    @Test
    @Order(2)
    @DisplayName("OWN005 - Verify get all suppliers")
    void testGetAllSuppliers() throws Exception {
        long startTime = System.currentTimeMillis();
        MvcResult result = mockMvc.perform(get("/api/owner/suppliers").with(asOwner())).andDo(print()).andExpect(status().is2xxSuccessful()).andReturn();
        logEvidenceDetailed(TEST_CLASS, "testGetAllSuppliers", "GET", "/api/owner/suppliers", result, startTime);
    }

    @Test
    @Order(3)
    @DisplayName("OWN006 - Verify get supplier by ID")
    void testGetSupplierById() throws Exception {
        long startTime = System.currentTimeMillis();
        MvcResult result = mockMvc.perform(get("/api/owner/suppliers/1").with(asOwner())).andDo(print()).andExpect(status().is2xxSuccessful()).andReturn();
        logEvidenceDetailed(TEST_CLASS, "testGetSupplierById", "GET", "/api/owner/suppliers/1", result, startTime);
    }

    @Test
    @Order(4)
    @DisplayName("OWN007 - Verify create supplier success")
    void testCreateSupplier() throws Exception {
        long startTime = System.currentTimeMillis();
        String body = "{\"name\":\"New Supplier\",\"address\":\"123 ABC\",\"phone\":\"0901234567\"}";
        MvcResult result = mockMvc.perform(post("/api/owner/suppliers").with(asOwner()).contentType(MediaType.APPLICATION_JSON).content(body)).andDo(print()).andExpect(status().is2xxSuccessful()).andReturn();
        logEvidenceDetailed(TEST_CLASS, "testCreateSupplier", "POST", "/api/owner/suppliers", result, startTime);
    }

    @Test
    @Order(5)
    @DisplayName("OWN008 - Verify create supplier validation")
    void testCreateSupplierValidation() throws Exception {
        long startTime = System.currentTimeMillis();
        String body = "{\"name\":\"\"}";
        MvcResult result = mockMvc.perform(post("/api/owner/suppliers").with(asOwner()).contentType(MediaType.APPLICATION_JSON).content(body)).andDo(print()).andExpect(status().is4xxClientError()).andReturn();
        logEvidence(TEST_CLASS, "testCreateSupplierValidation", "POST", "/api/owner/suppliers", result, startTime);
    }

    @Test
    @Order(6)
    @DisplayName("OWN009 - Verify get supplier not found")
    void testGetSupplierNotFound() throws Exception {
        long startTime = System.currentTimeMillis();
        MvcResult result = mockMvc.perform(get("/api/owner/suppliers/999").with(asOwner())).andDo(print()).andExpect(status().is4xxClientError()).andReturn();
        logEvidence(TEST_CLASS, "testGetSupplierNotFound", "GET", "/api/owner/suppliers/999", result, startTime);
    }

    // ========================================================================
    // REPORTS - OWN010-OWN014 (5 tests)
    // ========================================================================

    @Test
    @Order(7)
    @DisplayName("OWN010 - Verify revenue report page")
    void testRevenueReportPage() throws Exception {
        long startTime = System.currentTimeMillis();
        MvcResult result = mockMvc.perform(get("/owner/report/revenue").with(asOwner())).andDo(print()).andExpect(status().isOk()).andReturn();
        logEvidence(TEST_CLASS, "testRevenueReportPage", "GET", "/owner/report/revenue", result, startTime);
    }

    @Test
    @Order(8)
    @DisplayName("OWN011 - Verify profit report page")
    void testProfitReportPage() throws Exception {
        long startTime = System.currentTimeMillis();
        MvcResult result = mockMvc.perform(get("/owner/report/profit").with(asOwner())).andDo(print()).andExpect(status().isOk()).andReturn();
        logEvidence(TEST_CLASS, "testProfitReportPage", "GET", "/owner/report/profit", result, startTime);
    }

    @Test
    @Order(9)
    @DisplayName("OWN012 - Verify inventory report page")
    void testInventoryReportPage() throws Exception {
        long startTime = System.currentTimeMillis();
        MvcResult result = mockMvc.perform(get("/owner/report/inventory").with(asOwner())).andDo(print()).andExpect(status().isOk()).andReturn();
        logEvidence(TEST_CLASS, "testInventoryReportPage", "GET", "/owner/report/inventory", result, startTime);
    }

    @Test
    @Order(10)
    @DisplayName("OWN013 - Verify adjustments report page")
    void testAdjustmentsReportPage() throws Exception {
        long startTime = System.currentTimeMillis();
        MvcResult result = mockMvc.perform(get("/owner/report/adjustments").with(asOwner())).andDo(print()).andExpect(status().isOk()).andReturn();
        logEvidence(TEST_CLASS, "testAdjustmentsReportPage", "GET", "/owner/report/adjustments", result, startTime);
    }

    @Test
    @Order(11)
    @DisplayName("OWN014 - Verify revenue report API")
    void testRevenueReportAPI() throws Exception {
        long startTime = System.currentTimeMillis();
        MvcResult result = mockMvc.perform(get("/api/owner/reports/revenue").with(asOwner())).andDo(print()).andExpect(status().is2xxSuccessful()).andReturn();
        logEvidenceDetailed(TEST_CLASS, "testRevenueReportAPI", "GET", "/api/owner/reports/revenue", result, startTime);
    }

    // ========================================================================
    // INVENTORY MANAGEMENT - OWN015-OWN018 (4 tests)
    // ========================================================================

    @Test
    @Order(12)
    @DisplayName("OWN015 - Verify current inventory page")
    void testCurrentInventoryPage() throws Exception {
        long startTime = System.currentTimeMillis();
        MvcResult result = mockMvc.perform(get("/owner/inventory/current").with(asOwner())).andDo(print()).andExpect(status().isOk()).andReturn();
        logEvidence(TEST_CLASS, "testCurrentInventoryPage", "GET", "/owner/inventory/current", result, startTime);
    }

    @Test
    @Order(13)
    @DisplayName("OWN016 - Verify inventory summary API")
    void testInventorySummaryAPI() throws Exception {
        long startTime = System.currentTimeMillis();
        MvcResult result = mockMvc.perform(get("/api/owner/inventory/summary").with(asOwner())).andDo(print()).andExpect(status().is2xxSuccessful()).andReturn();
        logEvidenceDetailed(TEST_CLASS, "testInventorySummaryAPI", "GET", "/api/owner/inventory/summary", result, startTime);
    }

    @Test
    @Order(14)
    @DisplayName("OWN017 - Verify inventory movements chart")
    void testInventoryMovementsChart() throws Exception {
        long startTime = System.currentTimeMillis();
        MvcResult result = mockMvc.perform(get("/api/owner/inventory/movements?range=week").with(asOwner()).param("range", "week")).andDo(print()).andExpect(status().is2xxSuccessful()).andReturn();
        logEvidenceDetailed(TEST_CLASS, "testInventoryMovementsChart", "GET", "/api/owner/inventory/movements", result, startTime);
    }

    @Test
    @Order(15)
    @DisplayName("OWN018 - Verify inventory categories")
    void testInventoryCategories() throws Exception {
        long startTime = System.currentTimeMillis();
        MvcResult result = mockMvc.perform(get("/api/owner/inventory/categories").with(asOwner())).andDo(print()).andExpect(status().is2xxSuccessful()).andReturn();
        logEvidenceDetailed(TEST_CLASS, "testInventoryCategories", "GET", "/api/owner/inventory/categories", result, startTime);
    }

    // ========================================================================
    // ADJUSTMENTS - OWN019-OWN020 (2 tests)
    // ========================================================================

    @Test
    @Order(16)
    @DisplayName("OWN019 - Verify adjustments list")
    void testAdjustmentsList() throws Exception {
        long startTime = System.currentTimeMillis();
        MvcResult result = mockMvc.perform(get("/api/owner/adjustments").with(asOwner())).andDo(print()).andExpect(status().is2xxSuccessful()).andReturn();
        logEvidenceDetailed(TEST_CLASS, "testAdjustmentsList", "GET", "/api/owner/adjustments", result, startTime);
    }

    @Test
    @Order(17)
    @DisplayName("OWN020 - Verify adjustments by branch")
    void testAdjustmentsByBranch() throws Exception {
        long startTime = System.currentTimeMillis();
        MvcResult result = mockMvc.perform(get("/api/owner/adjustments?branchId=2").with(asOwner()).param("branchId", "2")).andDo(print()).andExpect(status().is2xxSuccessful()).andReturn();
        logEvidenceDetailed(TEST_CLASS, "testAdjustmentsByBranch", "GET", "/api/owner/adjustments", result, startTime);
    }
}

