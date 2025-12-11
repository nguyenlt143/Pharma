package vn.edu.fpt.pharma.integration.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import vn.edu.fpt.pharma.dto.manager.UserRequest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration Tests for Staff Management functionality.
 * <p>
 * Tests cover:
 * - GET /api/manager/staffs - Lấy danh sách staff
 * - GET /api/manager/staffs/pharmacists - Lấy danh sách dược sĩ
 * - GET /api/manager/staffs/{id} - Lấy thông tin staff theo ID
 * - POST /api/manager/staffs - Tạo mới staff
 * - PUT /api/manager/staffs/{id} - Cập nhật staff
 * - DELETE /api/manager/staffs/{id} - Xóa staff (soft delete)
 * - PATCH /api/manager/staffs/{id}/restore - Khôi phục staff đã xóa
 * </p>
 */
@DisplayName("Staff Controller Integration Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StaffControllerIT extends BaseManagerIT {

    private static final String TEST_CLASS = "StaffControllerIT";

    @Autowired
    private ObjectMapper objectMapper;

    // ========================================================================
    // GET /api/manager/staffs - Lấy danh sách staff
    // ========================================================================

    @Test
    @Order(1)
    @DisplayName("GET /api/manager/staffs - Should return all active staff")
    void testGetAllStaff_shouldReturnActiveStaff() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/manager/staffs";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asManager()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testGetAllStaff_shouldReturnActiveStaff", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(2)
    @DisplayName("GET /api/manager/staffs?showDeleted=true - Should return all staff including deleted")
    void testGetAllStaff_shouldReturnIncludingDeleted() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/manager/staffs?showDeleted=true";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asManager())
                        .param("showDeleted", "true"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testGetAllStaff_shouldReturnIncludingDeleted", "GET", endpoint, result, startTime);
    }

    // ========================================================================
    // GET /api/manager/staffs/pharmacists - Lấy danh sách dược sĩ
    // ========================================================================

    @Test
    @Order(3)
    @DisplayName("GET /api/manager/staffs/pharmacists - Should return pharmacist staff only")
    void testGetPharmacists_shouldReturnPharmacists() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/manager/staffs/pharmacists";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asManager()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testGetPharmacists_shouldReturnPharmacists", "GET", endpoint, result, startTime);
    }

    // ========================================================================
    // GET /api/manager/staffs/{id} - Lấy staff theo ID
    // ========================================================================

    @Test
    @Order(4)
    @DisplayName("GET /api/manager/staffs/2 - Should return staff by ID")
    void testGetStaffById_shouldReturnStaff() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/manager/staffs/2";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asManager()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testGetStaffById_shouldReturnStaff", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(5)
    @DisplayName("GET /api/manager/staffs/9999 - Should return 404 for non-existent staff")
    void testGetStaffById_shouldReturn404() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/manager/staffs/9999";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asManager()))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();

        logEvidence(TEST_CLASS, "testGetStaffById_shouldReturn404", "GET", endpoint, result, startTime);
    }

    // ========================================================================
    // POST /api/manager/staffs - Tạo mới staff
    // ========================================================================

    @Test
    @Order(6)
    @DisplayName("POST /api/manager/staffs - Should create new staff successfully")
    void testCreateStaff_shouldCreateSuccessfully() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/manager/staffs";

        UserRequest request = new UserRequest();
        request.setUserName("newstaff");
        request.setPassword("Password123!");
        request.setFullName("Nhân viên mới");
        request.setRoleId(5L); // ROLE_INVENTORY (Staff role)
        request.setPhoneNumber("0987654321");
        request.setEmail("newstaff@pharma.vn");
        // branchId will be set from authenticated user

        MvcResult result = mockMvc.perform(post(endpoint)
                        .with(asManager())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testCreateStaff_shouldCreateSuccessfully", "POST", endpoint, result, startTime);
    }

    @Test
    @Order(7)
    @DisplayName("POST /api/manager/staffs - Should return 400 for invalid data")
    void testCreateStaff_shouldReturn400ForInvalidData() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/manager/staffs";

        UserRequest request = new UserRequest();
        request.setUserName(""); // Invalid: empty username
        request.setPassword("123"); // Invalid: weak password
        request.setFullName("");

        MvcResult result = mockMvc.perform(post(endpoint)
                        .with(asManager())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        logEvidence(TEST_CLASS, "testCreateStaff_shouldReturn400ForInvalidData", "POST", endpoint, result, startTime);
    }

    // ========================================================================
    // PUT /api/manager/staffs/{id} - Cập nhật staff
    // ========================================================================

    @Test
    @Order(8)
    @DisplayName("PUT /api/manager/staffs/2 - Should update staff successfully")
    void testUpdateStaff_shouldUpdateSuccessfully() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/manager/staffs/2";

        UserRequest request = new UserRequest();
        request.setUserName("manager_user");
        request.setFullName("Quản lý Chi nhánh - Updated");
        request.setRoleId(3L);

        MvcResult result = mockMvc.perform(put(endpoint)
                        .with(asManager())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testUpdateStaff_shouldUpdateSuccessfully", "PUT", endpoint, result, startTime);
    }

    @Test
    @Order(9)
    @DisplayName("PUT /api/manager/staffs/9999 - Should return 400 for non-existent staff")
    void testUpdateStaff_shouldReturn400ForNonExistent() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/manager/staffs/9999";

        UserRequest request = new UserRequest();
        request.setUserName("nonexistent");
        request.setFullName("Non Existent User");
        request.setRoleId(6L);

        MvcResult result = mockMvc.perform(put(endpoint)
                        .with(asManager())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        logEvidence(TEST_CLASS, "testUpdateStaff_shouldReturn400ForNonExistent", "PUT", endpoint, result, startTime);
    }

    // ========================================================================
    // DELETE /api/manager/staffs/{id} - Xóa staff (soft delete)
    // ========================================================================

    @Test
    @Order(10)
    @DisplayName("DELETE /api/manager/staffs/3 - Should soft delete staff successfully")
    void testDeleteStaff_shouldDeleteSuccessfully() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/manager/staffs/3";

        MvcResult result = mockMvc.perform(delete(endpoint)
                        .with(asManager()))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andReturn();

        logEvidence(TEST_CLASS, "testDeleteStaff_shouldDeleteSuccessfully", "DELETE", endpoint, result, startTime);
    }

    // ========================================================================
    // PATCH /api/manager/staffs/{id}/restore - Khôi phục staff đã xóa
    // ========================================================================

    @Test
    @Order(11)
    @DisplayName("PATCH /api/manager/staffs/3/restore - Should restore deleted staff successfully")
    void testRestoreStaff_shouldRestoreSuccessfully() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/manager/staffs/3/restore";

        MvcResult result = mockMvc.perform(patch(endpoint)
                        .with(asManager()))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andReturn();

        logEvidence(TEST_CLASS, "testRestoreStaff_shouldRestoreSuccessfully", "PATCH", endpoint, result, startTime);
    }
}


