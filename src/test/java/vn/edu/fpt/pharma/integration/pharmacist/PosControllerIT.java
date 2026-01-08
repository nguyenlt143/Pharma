package vn.edu.fpt.pharma.integration.pharmacist;

import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration Tests for POS (Point of Sale) functionality.
 * Test Cases: PHA001-PHA015 (15 tests)
 */
@DisplayName("POS Controller Integration Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PosControllerIT extends BasePharmacistIT {

    private static final String TEST_CLASS = "PosControllerIT";

    @Test
    @Order(1)
    @DisplayName("PHA001 - Verify POS page access in shift")
    void testPosPage_whenInShift_shouldDisplayWithInShiftTrue() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/pharmacist/pos";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asPharmacist()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("inShift"))
                .andReturn();

        logEvidence(TEST_CLASS, "testPosPage_whenInShift_shouldDisplayWithInShiftTrue", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(2)
    @DisplayName("PHA002 - Verify POS page access out of shift")
    void testPosPage_whenOutOfShift_shouldDisplayWithWarning() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/pharmacist/pos";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asPharmacist()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("inShift"))
                .andReturn();

        logEvidence(TEST_CLASS, "testPosPage_whenOutOfShift_shouldDisplayWithWarning", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(3)
    @DisplayName("PHA003 - Verify search medicines by name")
    void testSearchMedicines_byName_shouldReturnMatches() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/pharmacist/pos/api/search?keyword=Paracetamol";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asPharmacist())
                        .param("keyword", "Paracetamol"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testSearchMedicines_byName_shouldReturnMatches", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(4)
    @DisplayName("PHA004 - Verify search medicines by code")
    void testSearchMedicines_byCode_shouldReturnMatches() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/pharmacist/pos/api/search?keyword=MED001";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asPharmacist())
                        .param("keyword", "MED001"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testSearchMedicines_byCode_shouldReturnMatches", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(5)
    @DisplayName("PHA005 - Verify search medicines empty keyword")
    void testSearchMedicines_emptyKeyword_shouldReturnResults() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/pharmacist/pos/api/search?keyword=";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asPharmacist())
                        .param("keyword", ""))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        logEvidence(TEST_CLASS, "testSearchMedicines_emptyKeyword_shouldReturnResults", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(6)
    @DisplayName("PHA006 - Verify search medicines no result")
    void testSearchMedicines_noMatch_shouldReturnEmpty() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/pharmacist/pos/api/search?keyword=NOTEXIST";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asPharmacist())
                        .param("keyword", "NOTEXIST"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testSearchMedicines_noMatch_shouldReturnEmpty", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(7)
    @DisplayName("PHA007 - Verify get medicine variants")
    void testGetVariants_shouldReturnVariantsWithInventory() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/pharmacist/pos/api/medicine/1/variants";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asPharmacist()))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testGetVariants_shouldReturnVariantsWithInventory", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(8)
    @DisplayName("PHA008 - Verify get variants for non-existent medicine")
    void testGetVariants_nonExistent_shouldReturnEmpty() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/pharmacist/pos/api/medicine/999/variants";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asPharmacist()))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        logEvidence(TEST_CLASS, "testGetVariants_nonExistent_shouldReturnEmpty", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(9)
    @DisplayName("PHA009 - Verify create invoice success")
    void testCreateInvoice_validData_shouldCreateSuccessfully() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/pharmacist/pos/api/invoices";

        String requestBody = """
            {
                "userId": 6,
                "branchId": 2,
                "customerName": "Nguyễn Văn A",
                "customerPhone": "0901234567",
                "items": [
                    {
                        "variantId": 1,
                        "quantity": 2,
                        "price": 5000
                    }
                ],
                "discount": 0,
                "total": 10000
            }
            """;

        MvcResult result = mockMvc.perform(post(endpoint)
                        .with(asPharmacist())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testCreateInvoice_validData_shouldCreateSuccessfully", "POST", endpoint, result, startTime);
    }

    @Test
    @Order(10)
    @DisplayName("PHA010 - Verify create invoice not in shift")
    void testCreateInvoice_notInShift_shouldReturn400() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/pharmacist/pos/api/invoices";

        String requestBody = """
            {
                "userId": 6,
                "branchId": 2,
                "items": [
                    {
                        "variantId": 1,
                        "quantity": 1,
                        "price": 5000
                    }
                ]
            }
            """;

        MvcResult result = mockMvc.perform(post(endpoint)
                        .with(asPharmacist())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andReturn();

        logEvidence(TEST_CLASS, "testCreateInvoice_notInShift_shouldReturn400", "POST", endpoint, result, startTime);
    }

    @Test
    @Order(11)
    @DisplayName("PHA011 - Verify create invoice insufficient stock")
    void testCreateInvoice_insufficientStock_shouldReturn400() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/pharmacist/pos/api/invoices";

        String requestBody = """
            {
                "userId": 6,
                "branchId": 2,
                "items": [
                    {
                        "variantId": 1,
                        "quantity": 10000,
                        "price": 5000
                    }
                ]
            }
            """;

        MvcResult result = mockMvc.perform(post(endpoint)
                        .with(asPharmacist())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andReturn();

        logEvidence(TEST_CLASS, "testCreateInvoice_insufficientStock_shouldReturn400", "POST", endpoint, result, startTime);
    }

    @Test
    @Order(12)
    @DisplayName("PHA012 - Verify create invoice validation")
    void testCreateInvoice_emptyItems_shouldReturn400() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/pharmacist/pos/api/invoices";

        String requestBody = """
            {
                "userId": 6,
                "branchId": 2,
                "items": []
            }
            """;

        MvcResult result = mockMvc.perform(post(endpoint)
                        .with(asPharmacist())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andReturn();

        logEvidence(TEST_CLASS, "testCreateInvoice_emptyItems_shouldReturn400", "POST", endpoint, result, startTime);
    }

    @Test
    @Order(13)
    @DisplayName("PHA013 - Verify create invoice with discount")
    void testCreateInvoice_withDiscount_shouldCalculateCorrectly() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/pharmacist/pos/api/invoices";

        String requestBody = """
            {
                "userId": 6,
                "branchId": 2,
                "items": [
                    {
                        "variantId": 1,
                        "quantity": 2,
                        "price": 5000
                    }
                ],
                "discount": 1000,
                "total": 9000
            }
            """;

        MvcResult result = mockMvc.perform(post(endpoint)
                        .with(asPharmacist())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testCreateInvoice_withDiscount_shouldCalculateCorrectly", "POST", endpoint, result, startTime);
    }

    @Test
    @Order(14)
    @DisplayName("PHA014 - Verify create invoice with customer info")
    void testCreateInvoice_withCustomerInfo_shouldSaveDetails() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/pharmacist/pos/api/invoices";

        String requestBody = """
            {
                "userId": 6,
                "branchId": 2,
                "customerName": "Trần Thị B",
                "customerPhone": "0987654321",
                "items": [
                    {
                        "variantId": 1,
                        "quantity": 1,
                        "price": 5000
                    }
                ],
                "total": 5000
            }
            """;

        MvcResult result = mockMvc.perform(post(endpoint)
                        .with(asPharmacist())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testCreateInvoice_withCustomerInfo_shouldSaveDetails", "POST", endpoint, result, startTime);
    }

    @Test
    @Order(15)
    @DisplayName("PHA015 - Verify create invoice multiple items")
    void testCreateInvoice_multipleItems_shouldProcessAllItems() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/pharmacist/pos/api/invoices";

        String requestBody = """
            {
                "userId": 6,
                "branchId": 2,
                "items": [
                    {
                        "variantId": 1,
                        "quantity": 1,
                        "price": 5000
                    },
                    {
                        "variantId": 2,
                        "quantity": 2,
                        "price": 10000
                    },
                    {
                        "variantId": 3,
                        "quantity": 1,
                        "price": 15000
                    }
                ],
                "total": 40000
            }
            """;

        MvcResult result = mockMvc.perform(post(endpoint)
                        .with(asPharmacist())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testCreateInvoice_multipleItems_shouldProcessAllItems", "POST", endpoint, result, startTime);
    }
}

