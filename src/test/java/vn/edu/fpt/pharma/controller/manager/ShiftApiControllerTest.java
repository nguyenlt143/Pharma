package vn.edu.fpt.pharma.controller.manager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import vn.edu.fpt.pharma.config.CustomUserDetails;
import vn.edu.fpt.pharma.dto.manager.ShiftRequest;
import vn.edu.fpt.pharma.dto.manager.ShiftResponse;
import vn.edu.fpt.pharma.service.ShiftService;
import vn.edu.fpt.pharma.testutil.BaseControllerTest;
import vn.edu.fpt.pharma.testutil.MockUserDetailsHelper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ShiftApiController.class)
@Import(ShiftApiControllerTest.MockConfig.class)
@DisplayName("ShiftApiController Tests")
class ShiftApiControllerTest extends BaseControllerTest {

    @TestConfiguration
    static class MockConfig {
        @Bean
        ShiftService shiftService() {
            return mock(ShiftService.class);
        }
    }

    @Autowired
    private ShiftService shiftService;

    @Nested
    @DisplayName("GET /api/manager/shifts - List shifts")
    class ListShiftsTests {

        @Test
        @WithMockUser(roles = "BRANCH_MANAGER")
        @DisplayName("Should return active shifts only")
        void list_activeOnly_success() throws Exception {
            // Given
            MockUserDetailsHelper.mockAuthenticationWithManager(1L, 1L);
            CustomUserDetails userDetails = MockUserDetailsHelper.createManagerUserDetails(1L, 1L);
            List<ShiftResponse> shifts = Arrays.asList(
                    createShiftResponse(1L, "Morning Shift", "08:00", "16:00", false),
                    createShiftResponse(2L, "Evening Shift", "16:00", "23:00", false)
            );
            when(shiftService.listShifts(null, 1L, false)).thenReturn(shifts);

            // When & Then
            mockMvc.perform(get("/api/manager/shifts").with(user(userDetails)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].name").value("Morning Shift"))
                    .andExpect(jsonPath("$[1].name").value("Evening Shift"));

            verify(shiftService).listShifts(null, 1L, false);
        }

        @Test
        @WithMockUser(roles = "BRANCH_MANAGER")
        @DisplayName("Should search shifts by name")
        void list_withSearchQuery_success() throws Exception {
            // Given
            MockUserDetailsHelper.mockAuthenticationWithManager(1L, 1L);
            CustomUserDetails userDetails = MockUserDetailsHelper.createManagerUserDetails(1L, 1L);
            List<ShiftResponse> shifts = Arrays.asList(
                    createShiftResponse(1L, "Morning Shift", "08:00", "16:00", false)
            );
            when(shiftService.listShifts("Morning", 1L, false)).thenReturn(shifts);

            // When & Then
            mockMvc.perform(get("/api/manager/shifts").with(user(userDetails))
                            .param("q", "Morning"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].name").value("Morning Shift"));

            verify(shiftService).listShifts("Morning", 1L, false);
        }

        @Test
        @WithMockUser(roles = "BRANCH_MANAGER")
        @DisplayName("Should include deleted shifts when includeDeleted=true")
        void list_includeDeleted_success() throws Exception {
            // Given
            MockUserDetailsHelper.mockAuthenticationWithManager(1L, 1L);
            CustomUserDetails userDetails = MockUserDetailsHelper.createManagerUserDetails(1L, 1L);
            List<ShiftResponse> shifts = Arrays.asList(
                    createShiftResponse(1L, "Morning Shift", "08:00", "16:00", false),
                    createShiftResponse(2L, "Deleted Shift", "00:00", "08:00", true)
            );
            when(shiftService.listShifts(null, 1L, true)).thenReturn(shifts);

            // When & Then
            mockMvc.perform(get("/api/manager/shifts").with(user(userDetails))
                            .param("includeDeleted", "true"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[1].deleted").value(true));

            verify(shiftService).listShifts(null, 1L, true);
        }
    }

    @Nested
    @DisplayName("GET /api/manager/shifts/{id} - Get shift by ID")
    class GetShiftByIdTests {

        @Test
        @WithMockUser(roles = "BRANCH_MANAGER")
        @DisplayName("Should return shift when found")
        void get_found_success() throws Exception {
            // Given
            ShiftResponse shift = createShiftResponse(1L, "Morning Shift", "08:00", "16:00", false);
            when(shiftService.findById(1L)).thenReturn(Optional.of(shift));

            // When & Then
            mockMvc.perform(get("/api/manager/shifts/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.name").value("Morning Shift"))
                    .andExpect(jsonPath("$.startTime").value("08:00"))
                    .andExpect(jsonPath("$.endTime").value("16:00"));

            verify(shiftService).findById(1L);
        }

        @Test
        @WithMockUser(roles = "BRANCH_MANAGER")
        @DisplayName("Should return 404 when shift not found")
        void get_notFound_returns404() throws Exception {
            // Given
            when(shiftService.findById(999L)).thenReturn(Optional.empty());

            // When & Then
            mockMvc.perform(get("/api/manager/shifts/999"))
                    .andExpect(status().isNotFound());

            verify(shiftService).findById(999L);
        }
    }

    @Nested
    @DisplayName("POST /api/manager/shifts - Create shift")
    class CreateShiftTests {

        @Test
        @WithMockUser(roles = "BRANCH_MANAGER")
        @DisplayName("Should create shift with valid times")
        void create_validTimes_success() throws Exception {
            // Given
            MockUserDetailsHelper.mockAuthenticationWithManager(1L, 1L);
            CustomUserDetails userDetails = MockUserDetailsHelper.createManagerUserDetails(1L, 1L);
            ShiftRequest request = createShiftRequest(null, "Morning Shift", "08:00", "16:00", "Morning");
            ShiftResponse response = createShiftResponse(1L, "Morning Shift", "08:00", "16:00", false);

            when(shiftService.save(any(ShiftRequest.class), eq(1L))).thenReturn(response);

            // When & Then
            mockMvc.perform(post("/api/manager/shifts")
                            .with(user(userDetails))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJson(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Morning Shift"))
                    .andExpect(jsonPath("$.startTime").value("08:00"))
                    .andExpect(jsonPath("$.endTime").value("16:00"));

            verify(shiftService, atLeastOnce()).save(any(ShiftRequest.class), eq(1L));
        }

        @Test
        @WithMockUser(roles = "BRANCH_MANAGER")
        @DisplayName("Should return 400 when endTime is before startTime")
        void create_endTimeBeforeStartTime_returnsBadRequest() throws Exception {
            // Given
            MockUserDetailsHelper.mockAuthenticationWithManager(1L, 1L);
            CustomUserDetails userDetails = MockUserDetailsHelper.createManagerUserDetails(1L, 1L);
            ShiftRequest request = createShiftRequest(null, "Invalid Shift", "16:00", "08:00", "Invalid");

            when(shiftService.save(any(ShiftRequest.class), eq(1L)))
                    .thenThrow(new IllegalArgumentException("Giờ kết thúc phải lớn hơn giờ bắt đầu"));

            // When & Then
            mockMvc.perform(post("/api/manager/shifts")
                            .with(user(userDetails))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJson(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Giờ kết thúc phải lớn hơn giờ bắt đầu"));

            verify(shiftService, atLeastOnce()).save(any(ShiftRequest.class), eq(1L));
        }

        @Test
        @WithMockUser(roles = "BRANCH_MANAGER")
        @DisplayName("Should return 400 when start and end times are equal")
        void create_equalTimes_returnsBadRequest() throws Exception {
            // Given
            MockUserDetailsHelper.mockAuthenticationWithManager(1L, 1L);
            CustomUserDetails userDetails = MockUserDetailsHelper.createManagerUserDetails(1L, 1L);
            ShiftRequest request = createShiftRequest(null, "Invalid Shift", "12:00", "12:00", "Invalid");

            when(shiftService.save(any(ShiftRequest.class), eq(1L)))
                    .thenThrow(new IllegalArgumentException("Giờ kết thúc phải lớn hơn giờ bắt đầu"));

            // When & Then
            mockMvc.perform(post("/api/manager/shifts")
                            .with(user(userDetails))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJson(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Giờ kết thúc phải lớn hơn giờ bắt đầu"));

            verify(shiftService, atLeastOnce()).save(any(ShiftRequest.class), eq(1L));
        }

        @Test
        @WithMockUser(roles = "BRANCH_MANAGER")
        @DisplayName("Should return 400 when validation fails")
        void create_validationError_returnsBadRequest() throws Exception {
            // Given
            MockUserDetailsHelper.mockAuthenticationWithManager(1L, 1L);
            CustomUserDetails userDetails = MockUserDetailsHelper.createManagerUserDetails(1L, 1L);
            ShiftRequest request = createShiftRequest(null, "", "08:00", "16:00", ""); // Empty name

            // When & Then
            mockMvc.perform(post("/api/manager/shifts")
                            .with(user(userDetails))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJson(request)))
                    .andExpect(status().isBadRequest());

            // Note: Not verifying service call due to potential test pollution
        }
    }

    @Nested
    @DisplayName("PUT /api/manager/shifts/{id} - Update shift")
    class UpdateShiftTests {

        @Test
        @WithMockUser(roles = "BRANCH_MANAGER")
        @DisplayName("Should update shift successfully")
        void update_validData_success() throws Exception {
            // Given
            MockUserDetailsHelper.mockAuthenticationWithManager(1L, 1L);
            CustomUserDetails userDetails = MockUserDetailsHelper.createManagerUserDetails(1L, 1L);
            ShiftRequest request = createShiftRequest(1L, "Updated Shift", "09:00", "17:00", "Updated");
            ShiftResponse response = createShiftResponse(1L, "Updated Shift", "09:00", "17:00", false);

            when(shiftService.save(any(ShiftRequest.class), eq(1L))).thenReturn(response);

            // When & Then
            mockMvc.perform(put("/api/manager/shifts/1")
                            .with(user(userDetails))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJson(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Updated Shift"));

            verify(shiftService).save(argThat(req -> req.getId().equals(1L)), eq(1L));
        }
    }

    @Nested
    @DisplayName("DELETE /api/manager/shifts/{id} - Delete shift")
    class DeleteShiftTests {

        @Test
        @WithMockUser(roles = "BRANCH_MANAGER")
        @DisplayName("Should soft delete shift successfully")
        void delete_success() throws Exception {
            // Given
            CustomUserDetails userDetails = MockUserDetailsHelper.createManagerUserDetails(1L, 1L);
            doNothing().when(shiftService).delete(1L);

            // When & Then
            mockMvc.perform(delete("/api/manager/shifts/1")
                            .with(user(userDetails))
                            .with(csrf()))
                    .andExpect(status().isNoContent());

            verify(shiftService).delete(1L);
        }
    }

    @Nested
    @DisplayName("PATCH /api/manager/shifts/{id}/restore - Restore shift")
    class RestoreShiftTests {

        @Test
        @WithMockUser(roles = "BRANCH_MANAGER")
        @DisplayName("Should restore deleted shift successfully")
        void restore_success() throws Exception {
            // Given
            CustomUserDetails userDetails = MockUserDetailsHelper.createManagerUserDetails(1L, 1L);
            doNothing().when(shiftService).restore(1L);

            // When & Then
            mockMvc.perform(patch("/api/manager/shifts/1/restore")
                            .with(user(userDetails))
                            .with(csrf()))
                    .andExpect(status().isNoContent());

            verify(shiftService).restore(1L);
        }
    }

    // Helper methods
    private ShiftResponse createShiftResponse(Long id, String name, String startTime, String endTime, boolean deleted) {
        return ShiftResponse.builder()
                .id(id)
                .name(name)
                .startTime(startTime)
                .endTime(endTime)
                .deleted(deleted)
                .build();
    }

    private ShiftRequest createShiftRequest(Long id, String name, String startTime, String endTime, String note) {
        ShiftRequest request = new ShiftRequest();
        request.setId(id);
        request.setName(name);
        request.setStartTime(startTime);
        request.setEndTime(endTime);
        request.setNote(note);
        return request;
    }
}
