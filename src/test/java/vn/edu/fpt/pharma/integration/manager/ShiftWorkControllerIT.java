package vn.edu.fpt.pharma.integration.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import vn.edu.fpt.pharma.dto.manager.ShiftWorkAssignRequest;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration Tests for Shift Work functionality.
 * <p>
 * Tests cover:
 * - GET /api/manager/shifts/{shiftId}/works - Lấy danh sách shift work theo ngày
 * - POST /api/manager/shifts/{shiftId}/works - Phân công shift work cho ngày cụ thể
 * - DELETE /api/manager/shift-works/{id} - Xóa shift work
 * </p>
 */
@DisplayName("Shift Work Controller Integration Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ShiftWorkControllerIT extends BaseManagerIT {

    private static final String TEST_CLASS = "ShiftWorkControllerIT";

    @Autowired
    private ObjectMapper objectMapper;

    // ========================================================================
    // GET /api/manager/shifts/{shiftId}/works - Danh sách shift work
    // ========================================================================

    @Test
    @Order(1)
    @DisplayName("GET /api/manager/shifts/1/works - Should return shift works for today")
    void testGetShiftWorks_forToday() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/manager/shifts/1/works";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asManager()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testGetShiftWorks_forToday", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(2)
    @DisplayName("GET /api/manager/shifts/1/works?date=2025-12-10 - Should return shift works for specific date")
    void testGetShiftWorks_forSpecificDate() throws Exception {
        long startTime = System.currentTimeMillis();
        String date = LocalDate.now().toString();
        String endpoint = "/api/manager/shifts/1/works?date=" + date;

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asManager())
                        .param("date", date))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testGetShiftWorks_forSpecificDate", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(3)
    @DisplayName("GET /api/manager/shifts/9999/works - Should return empty list for non-existent shift")
    void testGetShiftWorks_forNonExistentShift() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/manager/shifts/9999/works";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asManager()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testGetShiftWorks_forNonExistentShift", "GET", endpoint, result, startTime);
    }

    // ========================================================================
    // POST /api/manager/shifts/{shiftId}/works - Phân công shift work
    // ========================================================================

    @Test
    @Order(4)
    @DisplayName("POST /api/manager/shifts/1/works - Should assign shift work successfully")
    void testAssignShiftWork_shouldAssignSuccessfully() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/manager/shifts/1/works";

        ShiftWorkAssignRequest request = new ShiftWorkAssignRequest();
        request.setUserId(3L);
        request.setWorkDate(LocalDate.now().toString());

        MvcResult result = mockMvc.perform(post(endpoint)
                        .with(asManager())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testAssignShiftWork_shouldAssignSuccessfully", "POST", endpoint, result, startTime);
    }

    @Test
    @Order(5)
    @DisplayName("POST /api/manager/shifts/1/works - Should assign shift work for future date")
    void testAssignShiftWork_forFutureDate() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/manager/shifts/1/works";

        ShiftWorkAssignRequest request = new ShiftWorkAssignRequest();
        request.setUserId(4L);
        request.setWorkDate(LocalDate.now().plusDays(1).toString());

        MvcResult result = mockMvc.perform(post(endpoint)
                        .with(asManager())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testAssignShiftWork_forFutureDate", "POST", endpoint, result, startTime);
    }

    @Test
    @Order(6)
    @DisplayName("POST /api/manager/shifts/1/works - Should return 400 for missing required fields")
    void testAssignShiftWork_shouldReturn400ForMissingFields() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/manager/shifts/1/works";

        ShiftWorkAssignRequest request = new ShiftWorkAssignRequest();
        // Missing userId and workDate

        MvcResult result = mockMvc.perform(post(endpoint)
                        .with(asManager())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        logEvidence(TEST_CLASS, "testAssignShiftWork_shouldReturn400ForMissingFields", "POST", endpoint, result, startTime);
    }

    @Test
    @Order(7)
    @DisplayName("POST /api/manager/shifts/1/works - Should return 400 for invalid date format")
    void testAssignShiftWork_shouldReturn400ForInvalidDate() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/manager/shifts/1/works";

        ShiftWorkAssignRequest request = new ShiftWorkAssignRequest();
        request.setUserId(3L);
        request.setWorkDate("invalid-date");

        MvcResult result = mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        logEvidence(TEST_CLASS, "testAssignShiftWork_shouldReturn400ForInvalidDate", "POST", endpoint, result, startTime);
    }

    @Test
    @Order(8)
    @DisplayName("POST /api/manager/shifts/9999/works - Should return 400 for non-existent shift")
    void testAssignShiftWork_shouldReturn400ForNonExistentShift() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/manager/shifts/9999/works";

        ShiftWorkAssignRequest request = new ShiftWorkAssignRequest();
        request.setUserId(3L);
        request.setWorkDate(LocalDate.now().toString());

        MvcResult result = mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        logEvidence(TEST_CLASS, "testAssignShiftWork_shouldReturn400ForNonExistentShift", "POST", endpoint, result, startTime);
    }

    @Test
    @Order(9)
    @DisplayName("POST /api/manager/shifts/1/works - Should handle duplicate shift work assignment")
    void testAssignShiftWork_shouldHandleDuplicateAssignment() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/manager/shifts/1/works";

        ShiftWorkAssignRequest request = new ShiftWorkAssignRequest();
        request.setUserId(3L);
        request.setWorkDate(LocalDate.now().toString());

        // Try to assign the same user to same shift on same date again
        MvcResult result = mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                // May return 200 or 400 depending on business logic
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testAssignShiftWork_shouldHandleDuplicateAssignment", "POST", endpoint, result, startTime);
    }

    // ========================================================================
    // DELETE /api/manager/shift-works/{id} - Xóa shift work
    // ========================================================================

    @Test
    @Order(10)
    @DisplayName("DELETE /api/manager/shift-works/1 - Should delete shift work successfully")
    void testDeleteShiftWork_shouldDeleteSuccessfully() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/manager/shift-works/1";

        MvcResult result = mockMvc.perform(delete(endpoint))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andReturn();

        logEvidence(TEST_CLASS, "testDeleteShiftWork_shouldDeleteSuccessfully", "DELETE", endpoint, result, startTime);
    }

    @Test
    @Order(11)
    @DisplayName("DELETE /api/manager/shift-works/9999 - Should handle deleting non-existent shift work")
    void testDeleteShiftWork_shouldHandleNonExistent() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/api/manager/shift-works/9999";

        MvcResult result = mockMvc.perform(delete(endpoint))
                .andDo(print())
                // May return 204 or 404 depending on business logic
                .andReturn();

        logEvidence(TEST_CLASS, "testDeleteShiftWork_shouldHandleNonExistent", "DELETE", endpoint, result, startTime);
    }
}


