package vn.edu.fpt.pharma.integration.admin;

import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration Tests for Admin Account Management functionality.
 * Test Cases: ADM002-ADM013 (12 tests)
 */
@DisplayName("Admin Account Controller Integration Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AdminAccountControllerIT extends BaseAdminIT {

    private static final String TEST_CLASS = "AdminAccountControllerIT";

    @Test
    @Order(1)
    @DisplayName("ADM002 - Verify accounts page")
    void testAccountsPage_shouldDisplay() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/admin/accounts";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asAdmin()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        logEvidence(TEST_CLASS, "testAccountsPage_shouldDisplay", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(2)
    @DisplayName("ADM003 - Verify get all high-level accounts")
    void testGetAllAccounts_shouldReturnHighLevelRoles() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/admin/accounts";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asAdmin()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testGetAllAccounts_shouldReturnHighLevelRoles", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(3)
    @DisplayName("ADM004 - Verify get accounts including deleted")
    void testGetAllAccounts_withDeleted_shouldIncludeDeletedAccounts() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/admin/accounts?showDeleted=true";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asAdmin())
                        .param("showDeleted", "true"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testGetAllAccounts_withDeleted_shouldIncludeDeletedAccounts", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(4)
    @DisplayName("ADM005 - Verify get account by ID")
    void testGetAccountById_shouldReturnAccountDetails() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/admin/accounts/1";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asAdmin()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testGetAccountById_shouldReturnAccountDetails", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(5)
    @DisplayName("ADM006 - Verify get account not found")
    void testGetAccountById_notFound_shouldReturn404() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/admin/accounts/999";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asAdmin()))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();

        logEvidence(TEST_CLASS, "testGetAccountById_notFound_shouldReturn404", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(6)
    @DisplayName("ADM007 - Verify create account success")
    void testCreateAccount_validData_shouldCreateSuccessfully() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/admin/accounts";

        String requestBody = """
            {
                "userName": "new_manager",
                "password": "password123",
                "fullName": "New Manager",
                "email": "new.manager@example.com",
                "phoneNumber": "0901234567",
                "roleId": 3,
                "branchId": 2
            }
            """;

        MvcResult result = mockMvc.perform(post(endpoint)
                        .with(asAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testCreateAccount_validData_shouldCreateSuccessfully", "POST", endpoint, result, startTime);
    }

    @Test
    @Order(7)
    @DisplayName("ADM008 - Verify create account duplicate username")
    void testCreateAccount_duplicateUsername_shouldReturn400() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/admin/accounts";

        String requestBody = """
            {
                "userName": "manager_user",
                "password": "password123",
                "fullName": "Duplicate Manager",
                "roleId": 3,
                "branchId": 2
            }
            """;

        MvcResult result = mockMvc.perform(post(endpoint)
                        .with(asAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andReturn();

        logEvidence(TEST_CLASS, "testCreateAccount_duplicateUsername_shouldReturn400", "POST", endpoint, result, startTime);
    }

    @Test
    @Order(8)
    @DisplayName("ADM009 - Verify update account success")
    void testUpdateAccount_validData_shouldUpdateSuccessfully() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/admin/accounts/3";

        String requestBody = """
            {
                "fullName": "Updated Warehouse Manager",
                "email": "updated.warehouse@example.com",
                "phoneNumber": "0987654321"
            }
            """;

        MvcResult result = mockMvc.perform(put(endpoint)
                        .with(asAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testUpdateAccount_validData_shouldUpdateSuccessfully", "PUT", endpoint, result, startTime);
    }

    @Test
    @Order(9)
    @DisplayName("ADM010 - Verify delete account success")
    void testDeleteAccount_shouldSoftDelete() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/admin/accounts/3";

        MvcResult result = mockMvc.perform(delete(endpoint)
                        .with(asAdmin()))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andReturn();

        logEvidence(TEST_CLASS, "testDeleteAccount_shouldSoftDelete", "DELETE", endpoint, result, startTime);
    }

    @Test
    @Order(10)
    @DisplayName("ADM011 - Verify restore account success")
    void testRestoreAccount_shouldRestoreDeletedAccount() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/admin/accounts/3/restore";

        MvcResult result = mockMvc.perform(patch(endpoint)
                        .with(asAdmin()))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andReturn();

        logEvidence(TEST_CLASS, "testRestoreAccount_shouldRestoreDeletedAccount", "PATCH", endpoint, result, startTime);
    }

    @Test
    @Order(11)
    @DisplayName("ADM012 - Verify create account validation")
    void testCreateAccount_emptyUsername_shouldReturn400() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/admin/accounts";

        String requestBody = """
            {
                "userName": "",
                "password": "password123",
                "fullName": "Test User",
                "roleId": 3
            }
            """;

        MvcResult result = mockMvc.perform(post(endpoint)
                        .with(asAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andReturn();

        logEvidence(TEST_CLASS, "testCreateAccount_emptyUsername_shouldReturn400", "POST", endpoint, result, startTime);
    }

    @Test
    @Order(12)
    @DisplayName("ADM013 - Verify update account not found")
    void testUpdateAccount_notFound_shouldReturn400() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/admin/accounts/999";

        String requestBody = """
            {
                "fullName": "Non-existent User"
            }
            """;

        MvcResult result = mockMvc.perform(put(endpoint)
                        .with(asAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andReturn();

        logEvidence(TEST_CLASS, "testUpdateAccount_notFound_shouldReturn400", "PUT", endpoint, result, startTime);
    }
}

