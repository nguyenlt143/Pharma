package vn.edu.fpt.pharma.integration.pharmacist;

import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration Tests for Profile functionality.
 * Test Cases: PHA039-PHA040 (2 tests)
 */
@DisplayName("Pharmacist Profile Controller Integration Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PharmacistProfileControllerIT extends BasePharmacistIT {

    private static final String TEST_CLASS = "PharmacistProfileControllerIT";

    @Test
    @Order(1)
    @DisplayName("PHA039 - Verify profile page")
    void testProfilePage_shouldDisplayUserInfo() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/pharmacist/profile";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asPharmacist()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("profile"))
                .andExpect(model().attributeExists("profileUpdateRequest"))
                .andReturn();

        logEvidence(TEST_CLASS, "testProfilePage_shouldDisplayUserInfo", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(2)
    @DisplayName("PHA040 - Verify update profile success")
    void testUpdateProfile_validData_shouldUpdateSuccessfully() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/pharmacist/profile/update";

        MvcResult result = mockMvc.perform(post(endpoint)
                        .with(asPharmacist())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("fullName", "Nguyễn Văn A Updated")
                        .param("email", "updated@example.com")
                        .param("phone", "0901234567"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testUpdateProfile_validData_shouldUpdateSuccessfully", "POST", endpoint, result, startTime);
    }
}

