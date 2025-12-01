package vn.edu.fpt.pharma.controller.manager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import vn.edu.fpt.pharma.config.CustomUserDetails;
import vn.edu.fpt.pharma.service.ShiftWorkService;
import vn.edu.fpt.pharma.testutil.BaseControllerTest;
import vn.edu.fpt.pharma.testutil.MockUserDetailsHelper;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ShiftWorkApiController.class)
@Import(ShiftWorkApiControllerTest.MockConfig.class)
@DisplayName("ShiftWorkApiController Tests")
class ShiftWorkApiControllerTest extends BaseControllerTest {

    @TestConfiguration
    static class MockConfig {
        @Bean
        ShiftWorkService shiftWorkService() {
            return mock(ShiftWorkService.class);
        }
    }

    @Autowired
    ShiftWorkService shiftWorkService;

    @Nested
    @DisplayName("GET /api/manager/shifts/{shiftId}/works - List shift works")
    class ListShiftWorksTests {
        @Test
        @WithMockUser(roles = "BRANCH_MANAGER")
        void listWorks_defaultToday_success() throws Exception {
            CustomUserDetails userDetails = MockUserDetailsHelper.createManagerUserDetails(1L, 1L);
            LocalDate today = LocalDate.now();

            mockMvc.perform(get("/api/manager/shifts/1/works").with(user(userDetails)))
                    .andExpect(status().isOk());

            verify(shiftWorkService).findByShiftAndDate(1L, today);
        }

        @Test
        @WithMockUser(roles = "BRANCH_MANAGER")
        void listWorks_specificDate_success() throws Exception {
            CustomUserDetails userDetails = MockUserDetailsHelper.createManagerUserDetails(1L, 1L);
            LocalDate date = LocalDate.of(2025, 11, 29);

            mockMvc.perform(get("/api/manager/shifts/1/works")
                            .with(user(userDetails))
                            .param("date", "2025-11-29"))
                    .andExpect(status().isOk());

            verify(shiftWorkService).findByShiftAndDate(1L, date);
        }
    }

    @Nested
    @DisplayName("POST /api/manager/shifts/{shiftId}/works - Assign work to shift")
    class AssignWorkTests {
        @Test
        @WithMockUser(roles = "BRANCH_MANAGER")
        void assignWork_validDate_success() throws Exception {
            CustomUserDetails userDetails = MockUserDetailsHelper.createManagerUserDetails(1L, 1L);

            mockMvc.perform(post("/api/manager/shifts/1/works")
                            .with(user(userDetails))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"userId\": 3, \"workDate\": \"2025-11-29\"}"))
                    .andExpect(status().isOk());

            verify(shiftWorkService).assignToShift(eq(1L), any());
        }

        @Test
        @WithMockUser(roles = "BRANCH_MANAGER")
        void assignWork_invalidDate_throwsException() throws Exception {
            CustomUserDetails userDetails = MockUserDetailsHelper.createManagerUserDetails(1L, 1L);
            when(shiftWorkService.assignToShift(eq(1L), any()))
                    .thenThrow(new IllegalArgumentException("Invalid date format"));

            mockMvc.perform(post("/api/manager/shifts/1/works")
                            .with(user(userDetails))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"userId\": 3, \"workDate\": \"invalid-date\"}"))
                    .andExpect(status().isBadRequest());

            verify(shiftWorkService, atLeastOnce()).assignToShift(eq(1L), any());
        }
    }

    @Nested
    @DisplayName("DELETE /api/manager/shift-works/{id} - Remove shift work")
    class RemoveShiftWorkTests {
        @Test
        @WithMockUser(roles = "BRANCH_MANAGER")
        void removeShiftWork_success() throws Exception {
            CustomUserDetails userDetails = MockUserDetailsHelper.createManagerUserDetails(1L, 1L);
            doNothing().when(shiftWorkService).removeShiftWork(5L);

            mockMvc.perform(delete("/api/manager/shift-works/5")
                            .with(user(userDetails))
                            .with(csrf()))
                    .andExpect(status().isNoContent());

            verify(shiftWorkService).removeShiftWork(5L);
        }
    }
}

