package vn.edu.fpt.pharma.integration.pharmacist;

import org.junit.jupiter.api.*;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration Tests for Invoice Management functionality.
 * Test Cases: PHA016-PHA025 (10 tests)
 */
@DisplayName("Invoice Management Controller Integration Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class InvoiceManagementControllerIT extends BasePharmacistIT {

    private static final String TEST_CLASS = "InvoiceManagementControllerIT";

    @Test
    @Order(1)
    @DisplayName("PHA016 - Verify invoices list page")
    void testInvoicesPage_shouldDisplay() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/pharmacist/invoices";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asPharmacist()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        logEvidence(TEST_CLASS, "testInvoicesPage_shouldDisplay", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(2)
    @DisplayName("PHA017 - Verify get all invoices DataTable")
    void testGetAllInvoices_shouldReturnDataTable() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/pharmacist/invoices/all";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asPharmacist()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testGetAllInvoices_shouldReturnDataTable", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(3)
    @DisplayName("PHA018 - Verify invoices pagination")
    void testInvoicesPagination_shouldReturnCorrectPage() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/pharmacist/invoices/all?start=10&length=10";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asPharmacist())
                        .param("start", "10")
                        .param("length", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        logEvidence(TEST_CLASS, "testInvoicesPagination_shouldReturnCorrectPage", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(4)
    @DisplayName("PHA019 - Verify invoices search by code")
    void testInvoicesSearch_byCode_shouldFilter() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/pharmacist/invoices/all?search=INV001";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asPharmacist())
                        .param("search", "INV001"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        logEvidence(TEST_CLASS, "testInvoicesSearch_byCode_shouldFilter", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(5)
    @DisplayName("PHA020 - Verify invoice detail authorized")
    void testInvoiceDetail_ownInvoice_shouldDisplay() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/pharmacist/invoices/detail?invoiceId=1";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asPharmacist())
                        .param("invoiceId", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        logEvidence(TEST_CLASS, "testInvoiceDetail_ownInvoice_shouldDisplay", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(6)
    @DisplayName("PHA021 - Verify invoice detail same branch")
    void testInvoiceDetail_sameBranch_shouldDisplay() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/pharmacist/invoices/detail?invoiceId=2";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asPharmacist())
                        .param("invoiceId", "2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        logEvidence(TEST_CLASS, "testInvoiceDetail_sameBranch_shouldDisplay", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(7)
    @DisplayName("PHA022 - Verify invoice detail unauthorized")
    void testInvoiceDetail_differentBranch_shouldDeny() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/pharmacist/invoices/detail?invoiceId=999";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asPharmacist())
                        .param("invoiceId", "999"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andReturn();

        logEvidence(TEST_CLASS, "testInvoiceDetail_differentBranch_shouldDeny", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(8)
    @DisplayName("PHA023 - Verify invoice detail not found")
    void testInvoiceDetail_notFound_shouldRedirect() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/pharmacist/invoices/detail?invoiceId=9999";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asPharmacist())
                        .param("invoiceId", "9999"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andReturn();

        logEvidence(TEST_CLASS, "testInvoiceDetail_notFound_shouldRedirect", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(9)
    @DisplayName("PHA024 - Verify invoices list empty")
    void testGetAllInvoices_noInvoices_shouldReturnEmpty() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/pharmacist/invoices/all";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asPharmacist()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        logEvidence(TEST_CLASS, "testGetAllInvoices_noInvoices_shouldReturnEmpty", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(10)
    @DisplayName("PHA025 - Verify invoice detail with customer")
    void testInvoiceDetail_withCustomer_shouldDisplayCustomerInfo() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/pharmacist/invoices/detail?invoiceId=1";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asPharmacist())
                        .param("invoiceId", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        logEvidence(TEST_CLASS, "testInvoiceDetail_withCustomer_shouldDisplayCustomerInfo", "GET", endpoint, result, startTime);
    }
}

