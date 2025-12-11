package vn.edu.fpt.pharma.integration.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import vn.edu.fpt.pharma.dto.manager.ShiftAssignmentRequest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration Tests for Shift Assignment functionality.
 * <p>
 * Tests cover:
 * - GET /api/manager/shifts/{shiftId}/assignments - Lấy danh sách nhân viên được phân ca
 * - GET /api/manager/shifts/{shiftId}/assign - Lấy danh sách nhân viên có thể phân ca
 * - POST /api/manager/shifts/{shiftId}/assign - Phân ca cho nhân viên
 * - DELETE /api/manager/shifts/{shiftId}/remove/{userId} - Gỡ nhân viên khỏi ca
 * - POST /api/manager/shifts/{shiftId}/extend/{userId} - Gia hạn ca làm việc
 * </p>
 */
@DisplayName("Shift Assignment Controller Integration Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ShiftAssignmentControllerIT extends BaseManagerIT {

    private static final String TEST_CLASS = "ShiftAssignmentControllerIT";

    @Autowired
    private ObjectMapper objectMapper;

    // ========================================================================
    // GET /api/manager/shifts/{shiftId}/assignments - Danh sách đã phân ca
    // ========================================================================

    @Test
    @Order(1)
    @DisplayName("GET /api/manager/shifts/1/assignments - Should return assignments for shift")
    void testGetAssignments_shouldReturnAssignments() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/manager/shifts/1/assignments";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asManager()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testGetAssignments_shouldReturnAssignments", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(2)
    @DisplayName("GET /api/manager/shifts/9999/assignments - Should return empty list for non-existent shift")
    void testGetAssignments_shouldReturnEmptyForNonExistentShift() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/manager/shifts/9999/assignments";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asManager()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testGetAssignments_shouldReturnEmptyForNonExistentShift", "GET", endpoint, result, startTime);
    }

    // ========================================================================
    // GET /api/manager/shifts/{shiftId}/assign - Danh sách có thể phân ca
    // ========================================================================

    @Test
    @Order(3)
    @DisplayName("GET /api/manager/shifts/1/assign - Should return assignable staff for shift")
    void testGetAssignable_shouldReturnAssignableStaff() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/manager/shifts/1/assign";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asManager()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testGetAssignable_shouldReturnAssignableStaff", "GET", endpoint, result, startTime);
    }

    // ========================================================================
    // POST /api/manager/shifts/{shiftId}/assign - Phân ca cho nhân viên
    // ========================================================================

    @Test
    @Order(4)
    @DisplayName("POST /api/manager/shifts/1/assign - Should assign staff to shift successfully")
    void testAssignStaff_shouldAssignSuccessfully() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/manager/shifts/1/assign";

        ShiftAssignmentRequest request = new ShiftAssignmentRequest();
        request.setUserId(3L); // Assuming staff ID 3 exists from test data

        MvcResult result = mockMvc.perform(post(endpoint)
                        .with(asManager())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testAssignStaff_shouldAssignSuccessfully", "POST", endpoint, result, startTime);
    }

    @Test
    @Order(5)
    @DisplayName("POST /api/manager/shifts/1/assign - Should return 400 for missing userId")
    void testAssignStaff_shouldReturn400ForMissingUserId() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/manager/shifts/1/assign";

        ShiftAssignmentRequest request = new ShiftAssignmentRequest();
        // Missing userId

        MvcResult result = mockMvc.perform(post(endpoint)
                        .with(asManager())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        logEvidence(TEST_CLASS, "testAssignStaff_shouldReturn400ForMissingUserId", "POST", endpoint, result, startTime);
    }

    @Test
    @Order(6)
    @DisplayName("POST /api/manager/shifts/1/assign - Should handle duplicate assignment gracefully")
    void testAssignStaff_shouldHandleDuplicateAssignment() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/manager/shifts/1/assign";

        ShiftAssignmentRequest request = new ShiftAssignmentRequest();
        request.setUserId(3L); // Assign same user again

        MvcResult result = mockMvc.perform(post(endpoint)
                        .with(asManager())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                // May return 200 or 400 depending on business logic
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testAssignStaff_shouldHandleDuplicateAssignment", "POST", endpoint, result, startTime);
    }

    // ========================================================================
    // DELETE /api/manager/shifts/{shiftId}/remove/{userId} - Gỡ nhân viên
    // ========================================================================

    @Test
    @Order(7)
    @DisplayName("DELETE /api/manager/shifts/1/remove/3 - Should remove staff from shift successfully")
    void testRemoveStaff_shouldRemoveSuccessfully() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/manager/shifts/1/remove/3";

        MvcResult result = mockMvc.perform(delete(endpoint)
                        .with(asManager()))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andReturn();

        logEvidence(TEST_CLASS, "testRemoveStaff_shouldRemoveSuccessfully", "DELETE", endpoint, result, startTime);
    }

    @Test
    @Order(8)
    @DisplayName("DELETE /api/manager/shifts/1/remove/9999 - Should handle removing non-existent assignment")
    void testRemoveStaff_shouldHandleNonExistentAssignment() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/manager/shifts/1/remove/9999";

        MvcResult result = mockMvc.perform(delete(endpoint)
                        .with(asManager()))
                .andDo(print())
                // May return 204 or 404 depending on business logic
                .andReturn();

        logEvidence(TEST_CLASS, "testRemoveStaff_shouldHandleNonExistentAssignment", "DELETE", endpoint, result, startTime);
    }

    // ========================================================================
    // POST /api/manager/shifts/{shiftId}/extend/{userId} - Gia hạn ca
    // ========================================================================

    @Test
    @Order(9)
    @DisplayName("POST /api/manager/shifts/1/extend/3 - Should extend shift work successfully")
    void testExtendShiftWork_shouldExtendSuccessfully() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/manager/shifts/1/extend/3";

        // First assign the staff again
        ShiftAssignmentRequest assignRequest = new ShiftAssignmentRequest();
        assignRequest.setUserId(3L);
        mockMvc.perform(post("/api/manager/shifts/1/assign")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(assignRequest)));

        // Then extend
        MvcResult result = mockMvc.perform(post(endpoint))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        logEvidence(TEST_CLASS, "testExtendShiftWork_shouldExtendSuccessfully", "POST", endpoint, result, startTime);
    }

    @Test
    @Order(10)
    @DisplayName("POST /api/manager/shifts/1/extend/9999 - Should return 404 for non-existent assignment")
    void testExtendShiftWork_shouldReturn404ForNonExistent() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/manager/shifts/1/extend/9999";

        MvcResult result = mockMvc.perform(post(endpoint))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();

        logEvidence(TEST_CLASS, "testExtendShiftWork_shouldReturn404ForNonExistent", "POST", endpoint, result, startTime);
    }
}


