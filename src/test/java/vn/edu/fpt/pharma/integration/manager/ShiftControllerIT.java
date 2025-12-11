package vn.edu.fpt.pharma.integration.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import vn.edu.fpt.pharma.dto.manager.ShiftRequest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration Tests for Shift Management functionality.
 * <p>
 * Tests cover:
 * - GET /api/manager/shifts - Lấy danh sách ca làm việc
 * - GET /api/manager/shifts/{id} - Lấy chi tiết ca làm việc
 * - POST /api/manager/shifts - Tạo mới ca làm việc
 * - PUT /api/manager/shifts/{id} - Cập nhật ca làm việc
 * - DELETE /api/manager/shifts/{id} - Xóa ca làm việc (soft delete)
 * - PATCH /api/manager/shifts/{id}/restore - Khôi phục ca làm việc đã xóa
 * </p>
 */
@DisplayName("Shift Controller Integration Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ShiftControllerIT extends BaseManagerIT {

    private static final String TEST_CLASS = "ShiftControllerIT";

    @Autowired
    private ObjectMapper objectMapper;

    // ========================================================================
    // GET /api/manager/shifts - Lấy danh sách ca làm việc
    // ========================================================================

    @Test
    @Order(1)
    @DisplayName("GET /api/manager/shifts - Should return all active shifts")
    void testGetAllShifts_shouldReturnActiveShifts() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/manager/shifts";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asManager()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testGetAllShifts_shouldReturnActiveShifts", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(2)
    @DisplayName("GET /api/manager/shifts?includeDeleted=true - Should return all shifts including deleted")
    void testGetAllShifts_shouldReturnIncludingDeleted() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/manager/shifts?includeDeleted=true";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asManager())
                        .param("includeDeleted", "true"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testGetAllShifts_shouldReturnIncludingDeleted", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(3)
    @DisplayName("GET /api/manager/shifts?q=Sáng - Should search shifts by name")
    void testGetAllShifts_shouldSearchByName() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/manager/shifts?q=Sáng";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asManager())
                        .param("q", "Sáng"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testGetAllShifts_shouldSearchByName", "GET", endpoint, result, startTime);
    }

    // ========================================================================
    // GET /api/manager/shifts/{id} - Lấy chi tiết ca làm việc
    // ========================================================================

    @Test
    @Order(4)
    @DisplayName("GET /api/manager/shifts/1 - Should return shift by ID")
    void testGetShiftById_shouldReturnShift() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/manager/shifts/1";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asManager()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testGetShiftById_shouldReturnShift", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(5)
    @DisplayName("GET /api/manager/shifts/9999 - Should return 404 for non-existent shift")
    void testGetShiftById_shouldReturn404() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/manager/shifts/9999";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asManager()))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();

        logEvidence(TEST_CLASS, "testGetShiftById_shouldReturn404", "GET", endpoint, result, startTime);
    }

    // ========================================================================
    // POST /api/manager/shifts - Tạo mới ca làm việc
    // ========================================================================

    @Test
    @Order(6)
    @DisplayName("POST /api/manager/shifts - Should create new shift successfully")
    void testCreateShift_shouldCreateSuccessfully() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/manager/shifts";

        ShiftRequest request = new ShiftRequest();
        request.setName("Ca Bổ Sung");
        request.setStartTime("06:00:00");
        request.setEndTime("08:00:00");

        MvcResult result = mockMvc.perform(post(endpoint)
                        .with(asManager())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testCreateShift_shouldCreateSuccessfully", "POST", endpoint, result, startTime);
    }

    @Test
    @Order(7)
    @DisplayName("POST /api/manager/shifts - Should return 400 for invalid time range")
    void testCreateShift_shouldReturn400ForInvalidTime() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/manager/shifts";

        ShiftRequest request = new ShiftRequest();
        request.setName("Ca Lỗi");
        request.setStartTime("14:00:00");
        request.setEndTime("10:00:00"); // End before start

        MvcResult result = mockMvc.perform(post(endpoint)
                        .with(asManager())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        logEvidence(TEST_CLASS, "testCreateShift_shouldReturn400ForInvalidTime", "POST", endpoint, result, startTime);
    }

    @Test
    @Order(8)
    @DisplayName("POST /api/manager/shifts - Should return 400 for missing required fields")
    void testCreateShift_shouldReturn400ForMissingFields() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/manager/shifts";

        ShiftRequest request = new ShiftRequest();
        // Missing name, startTime, endTime

        MvcResult result = mockMvc.perform(post(endpoint)
                        .with(asManager())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        logEvidence(TEST_CLASS, "testCreateShift_shouldReturn400ForMissingFields", "POST", endpoint, result, startTime);
    }

    // ========================================================================
    // PUT /api/manager/shifts/{id} - Cập nhật ca làm việc
    // ========================================================================

    @Test
    @Order(9)
    @DisplayName("PUT /api/manager/shifts/1 - Should update shift successfully")
    void testUpdateShift_shouldUpdateSuccessfully() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/manager/shifts/1";

        ShiftRequest request = new ShiftRequest();
        request.setName("Ca Sáng - Updated");
        request.setStartTime("08:00:00");
        request.setEndTime("13:00:00");

        MvcResult result = mockMvc.perform(put(endpoint)
                        .with(asManager())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testUpdateShift_shouldUpdateSuccessfully", "PUT", endpoint, result, startTime);
    }

    @Test
    @Order(10)
    @DisplayName("PUT /api/manager/shifts/9999 - Should handle non-existent shift with non-overlapping time")
    void testUpdateShift_shouldHandleNonExistent() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/manager/shifts/9999";

        ShiftRequest request = new ShiftRequest();
        request.setName("Non-existent Shift");
        request.setStartTime("04:00:00");
        request.setEndTime("06:00:00");

        MvcResult result = mockMvc.perform(put(endpoint)
                        .with(asManager())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                // Should return appropriate status for non-existent entity
                .andReturn();

        logEvidence(TEST_CLASS, "testUpdateShift_shouldHandleNonExistent", "PUT", endpoint, result, startTime);
    }

    // ========================================================================
    // DELETE /api/manager/shifts/{id} - Xóa ca làm việc (soft delete)
    // ========================================================================

    @Test
    @Order(11)
    @DisplayName("DELETE /api/manager/shifts/3 - Should soft delete shift successfully")
    void testDeleteShift_shouldDeleteSuccessfully() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/manager/shifts/3";

        MvcResult result = mockMvc.perform(delete(endpoint)
                        .with(asManager()))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andReturn();

        logEvidence(TEST_CLASS, "testDeleteShift_shouldDeleteSuccessfully", "DELETE", endpoint, result, startTime);
    }

    // ========================================================================
    // PATCH /api/manager/shifts/{id}/restore - Khôi phục ca làm việc đã xóa
    // ========================================================================

    @Test
    @Order(12)
    @DisplayName("PATCH /api/manager/shifts/3/restore - Should restore deleted shift successfully")
    void testRestoreShift_shouldRestoreSuccessfully() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/manager/shifts/3/restore";

        MvcResult result = mockMvc.perform(patch(endpoint)
                        .with(asManager()))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andReturn();

        logEvidence(TEST_CLASS, "testRestoreShift_shouldRestoreSuccessfully", "PATCH", endpoint, result, startTime);
    }
}

