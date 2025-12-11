package vn.edu.fpt.pharma.integration.manager;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Comprehensive validation tests for Manager module
 * Tests ALL @Valid validation rules on UserRequest and ShiftRequest
 */
public class ManagerValidationIT extends BaseManagerIT {

    // =====================================================================
    // UserRequest Validation Tests (StaffApiController)
    // =====================================================================

    @Test
    @DisplayName("POST /api/manager/staffs - Missing required fields should return 400")
    public void testStaffCreate_MissingRequiredFields() throws Exception {
        String payload = "{" +
                "\"userName\": \"\"," +        // ❌ @NotBlank violation
                "\"fullName\": \"\"," +        // ❌ @NotBlank violation
                "\"roleId\": null" +           // ❌ @NotNull violation
                "}";

        long start = System.currentTimeMillis();
        MvcResult res = mockMvc.perform(post("/api/manager/staffs")
                .with(asManager())
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors.userName").exists())
                .andExpect(jsonPath("$.errors.fullName").exists())
                .andExpect(jsonPath("$.errors.roleId").exists())
                .andReturn();

        logEvidenceDetailed(this.getClass().getSimpleName(),
            "testStaffCreate_MissingRequiredFields", "POST", "/api/manager/staffs", res, start);
    }

    @Test
    @DisplayName("POST /api/manager/staffs - Short username should return 400")
    public void testStaffCreate_ShortUsername() throws Exception {
        String payload = "{" +
                "\"userName\": \"abc\"," +      // ❌ @Size(min=4) violation (only 3 chars)
                "\"fullName\": \"Test User\"," +
                "\"roleId\": 5," +
                "\"password\": \"test123\"" +
                "}";

        mockMvc.perform(post("/api/manager/staffs")
                .with(asManager())
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.userName").exists())
                .andExpect(jsonPath("$.errors.userName",
                    Matchers.containsString("4-30")));
    }

    @Test
    @DisplayName("POST /api/manager/staffs - Short password should return 400")
    public void testStaffCreate_ShortPassword() throws Exception {
        String payload = "{" +
                "\"userName\": \"testuser\"," +
                "\"fullName\": \"Test User\"," +
                "\"roleId\": 5," +
                "\"password\": \"12345\"" +     // ❌ @Size(min=6) violation (only 5 chars)
                "}";

        mockMvc.perform(post("/api/manager/staffs")
                .with(asManager())
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.password").exists())
                .andExpect(jsonPath("$.errors.password",
                    Matchers.containsString("6 ký tự")));
    }

    @Test
    @DisplayName("POST /api/manager/staffs - Invalid phone should return 400")
    public void testStaffCreate_InvalidPhone() throws Exception {
        String payload = "{" +
                "\"userName\": \"testuser\"," +
                "\"fullName\": \"Test User\"," +
                "\"roleId\": 5," +
                "\"password\": \"test123\"," +
                "\"phoneNumber\": \"123\"" +    // ❌ @Pattern violation (not valid VN phone)
                "}";

        mockMvc.perform(post("/api/manager/staffs")
                .with(asManager())
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.phoneNumber").exists())
                .andExpect(jsonPath("$.errors.phoneNumber",
                    Matchers.containsString("không hợp lệ")));
    }

    @Test
    @DisplayName("POST /api/manager/staffs - Invalid email should return 400")
    public void testStaffCreate_InvalidEmail() throws Exception {
        String payload = "{" +
                "\"userName\": \"testuser\"," +
                "\"fullName\": \"Test User\"," +
                "\"roleId\": 5," +
                "\"password\": \"test123\"," +
                "\"email\": \"not-an-email\"" + // ❌ @Email violation
                "}";

        mockMvc.perform(post("/api/manager/staffs")
                .with(asManager())
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.email").exists())
                .andExpect(jsonPath("$.errors.email",
                    Matchers.containsString("không hợp lệ")));
    }

    @Test
    @DisplayName("POST /api/manager/staffs - Valid phone formats should pass validation")
    public void testStaffCreate_ValidPhoneFormats() throws Exception {
        // Test valid phone format: 0912345678
        String payload1 = "{" +
                "\"userName\": \"testuser1\"," +
                "\"fullName\": \"Test User 1\"," +
                "\"roleId\": 5," +
                "\"password\": \"test123\"," +
                "\"phoneNumber\": \"0912345678\"" + // ✅ Valid VN mobile
                "}";

        // This should pass validation (might fail on duplicate or other business logic)
        // We only care that it doesn't fail on phoneNumber validation
        MvcResult result = mockMvc.perform(post("/api/manager/staffs")
                .with(asManager())
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload1))
                .andReturn();

        // Should NOT have phoneNumber validation error
        String responseBody = result.getResponse().getContentAsString();
        if (result.getResponse().getStatus() == 400 && responseBody.contains("errors")) {
            mockMvc.perform(post("/api/manager/staffs")
                    .with(asManager())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(payload1))
                    .andExpect(jsonPath("$.errors.phoneNumber").doesNotExist());
        }
    }

    // =====================================================================
    // Business Logic Tests (not just validation)
    // =====================================================================

    @Test
    @DisplayName("POST /api/manager/staffs - Duplicate username should return 409")
    public void testStaffCreate_DuplicateUsername() throws Exception {
        // manager_user exists in test-data.sql
        String payload = "{" +
                "\"userName\": \"manager_user\"," +
                "\"fullName\": \"Test Duplicate\"," +
                "\"roleId\": 5," +
                "\"password\": \"test123456\"" + // Valid password (10 chars)
                "}";

        long start = System.currentTimeMillis();
        MvcResult res = mockMvc.perform(post("/api/manager/staffs")
                .with(asManager())
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isConflict())  // Expect 409, not 400
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message",
                    Matchers.containsString("manager_user")))
                .andReturn();

        logEvidenceDetailed(this.getClass().getSimpleName(),
            "testStaffCreate_DuplicateUsername", "POST", "/api/manager/staffs", res, start);
    }

    // =====================================================================
    // ShiftRequest Validation Tests (ShiftApiController)
    // =====================================================================

    @Test
    @DisplayName("POST /api/manager/shifts - Missing required fields should return 400")
    public void testShiftCreate_MissingRequiredFields() throws Exception {
        String payload = "{" +
                "\"name\": \"\"," +          // ❌ @NotBlank violation
                "\"startTime\": \"\"," +     // ❌ @NotBlank violation
                "\"endTime\": \"\"" +        // ❌ @NotBlank violation
                "}";

        mockMvc.perform(post("/api/manager/shifts")
                .with(asManager())
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors.name").exists())
                .andExpect(jsonPath("$.errors.startTime").exists())
                .andExpect(jsonPath("$.errors.endTime").exists());
    }

    @Test
    @DisplayName("POST /api/manager/shifts - Null fields should return 400")
    public void testShiftCreate_NullFields() throws Exception {
        String payload = "{" +
                "\"name\": null," +      // ❌ @NotBlank violation
                "\"startTime\": null," + // ❌ @NotBlank violation
                "\"endTime\": null" +    // ❌ @NotBlank violation
                "}";

        mockMvc.perform(post("/api/manager/shifts")
                .with(asManager())
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    @DisplayName("POST /api/manager/shifts - Invalid time range should return 400")
    public void testShiftCreate_InvalidTimeRange() throws Exception {
        // Valid format, but business logic error (end = start)
        String payload = "{" +
                "\"name\": \"Ca Test\"," +
                "\"startTime\": \"08:00:00\"," +
                "\"endTime\": \"08:00:00\"" +  // Same as start
                "}";

        long start = System.currentTimeMillis();
        MvcResult res = mockMvc.perform(post("/api/manager/shifts")
                .with(asManager())
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message",
                    Matchers.containsString("Giờ kết thúc phải lớn hơn")))
                .andReturn();

        logEvidenceDetailed(this.getClass().getSimpleName(),
            "testShiftCreate_InvalidTimeRange", "POST", "/api/manager/shifts", res, start);
    }

    @Test
    @DisplayName("POST /api/manager/shifts - End before start should return 400")
    public void testShiftCreate_EndBeforeStart() throws Exception {
        String payload = "{" +
                "\"name\": \"Ca Lỗi\"," +
                "\"startTime\": \"14:00:00\"," +
                "\"endTime\": \"10:00:00\"" +  // Before start
                "}";

        mockMvc.perform(post("/api/manager/shifts")
                .with(asManager())
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",
                    Matchers.containsString("Giờ kết thúc phải lớn hơn")));
    }

    // =====================================================================
    // Edge Case Tests
    // =====================================================================

    @Test
    @DisplayName("POST /api/manager/staffs - Username exactly 4 chars should pass")
    public void testStaffCreate_UsernameExactly4Chars() throws Exception {
        String payload = "{" +
                "\"userName\": \"test\"," +    // Exactly 4 chars (minimum)
                "\"fullName\": \"Test User\"," +
                "\"roleId\": 5," +
                "\"password\": \"test123\"" +
                "}";

        MvcResult result = mockMvc.perform(post("/api/manager/staffs")
                .with(asManager())
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andReturn();

        // Should NOT have userName validation error
        if (result.getResponse().getStatus() == 400) {
            mockMvc.perform(post("/api/manager/staffs")
                    .with(asManager())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(payload))
                    .andExpect(jsonPath("$.errors.userName").doesNotExist());
        }
    }

    @Test
    @DisplayName("POST /api/manager/staffs - Password exactly 6 chars should pass")
    public void testStaffCreate_PasswordExactly6Chars() throws Exception {
        String payload = "{" +
                "\"userName\": \"testuser\"," +
                "\"fullName\": \"Test User\"," +
                "\"roleId\": 5," +
                "\"password\": \"123456\"" +   // Exactly 6 chars (minimum)
                "}";

        MvcResult result = mockMvc.perform(post("/api/manager/staffs")
                .with(asManager())
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andReturn();

        // Should NOT have password validation error
        if (result.getResponse().getStatus() == 400) {
            mockMvc.perform(post("/api/manager/staffs")
                    .with(asManager())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(payload))
                    .andExpect(jsonPath("$.errors.password").doesNotExist());
        }
    }
}

