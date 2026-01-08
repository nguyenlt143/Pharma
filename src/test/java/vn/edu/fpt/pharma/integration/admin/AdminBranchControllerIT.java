package vn.edu.fpt.pharma.integration.admin;

import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration Tests for Admin Branch Management functionality.
 * Test Cases: ADM014-ADM020 (7 tests)
 */
@DisplayName("Admin Branch Controller Integration Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AdminBranchControllerIT extends BaseAdminIT {

    private static final String TEST_CLASS = "AdminBranchControllerIT";

    @Test
    @Order(1)
    @DisplayName("ADM014 - Verify branches page")
    void testBranchesPage_shouldDisplay() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/admin/branches";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asAdmin()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        logEvidence(TEST_CLASS, "testBranchesPage_shouldDisplay", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(2)
    @DisplayName("ADM015 - Verify get all branches")
    void testGetAllBranches_shouldReturnAllBranches() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/admin/branches";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asAdmin()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testGetAllBranches_shouldReturnAllBranches", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(3)
    @DisplayName("ADM016 - Verify get branches including deleted")
    void testGetAllBranches_withDeleted_shouldIncludeDeletedBranches() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/admin/branches?showDeleted=true";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asAdmin())
                        .param("showDeleted", "true"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testGetAllBranches_withDeleted_shouldIncludeDeletedBranches", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(4)
    @DisplayName("ADM017 - Verify create branch success")
    void testCreateBranch_validData_shouldCreateSuccessfully() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/admin/branches";

        String requestBody = """
            {
                "name": "Chi nhánh mới",
                "address": "123 Đường ABC, Quận 1, TP.HCM",
                "branchType": "BRANCH"
            }
            """;

        MvcResult result = mockMvc.perform(post(endpoint)
                        .with(asAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Chi nhánh mới"))
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testCreateBranch_validData_shouldCreateSuccessfully", "POST", endpoint, result, startTime);
    }

    @Test
    @Order(5)
    @DisplayName("ADM018 - Verify create branch duplicate name")
    void testCreateBranch_duplicateName_shouldReturn400() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/admin/branches";

        String requestBody = """
            {
                "name": "Kho Tổng",
                "address": "Duplicate address",
                "branchType": "BRANCH"
            }
            """;

        MvcResult result = mockMvc.perform(post(endpoint)
                        .with(asAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andReturn();

        logEvidence(TEST_CLASS, "testCreateBranch_duplicateName_shouldReturn400", "POST", endpoint, result, startTime);
    }

    @Test
    @Order(6)
    @DisplayName("ADM019 - Verify delete branch success")
    void testDeleteBranch_shouldSoftDelete() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/admin/branches/3";

        MvcResult result = mockMvc.perform(delete(endpoint)
                        .with(asAdmin()))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andReturn();

        logEvidence(TEST_CLASS, "testDeleteBranch_shouldSoftDelete", "DELETE", endpoint, result, startTime);
    }

    @Test
    @Order(7)
    @DisplayName("ADM020 - Verify restore branch success")
    void testRestoreBranch_shouldRestoreDeletedBranch() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/admin/branches/3/restore";

        MvcResult result = mockMvc.perform(patch(endpoint)
                        .with(asAdmin()))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andReturn();

        logEvidence(TEST_CLASS, "testRestoreBranch_shouldRestoreDeletedBranch", "PATCH", endpoint, result, startTime);
    }
}

