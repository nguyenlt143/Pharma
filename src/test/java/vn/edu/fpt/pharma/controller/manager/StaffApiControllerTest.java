package vn.edu.fpt.pharma.controller.manager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import vn.edu.fpt.pharma.config.CustomUserDetails;
import vn.edu.fpt.pharma.dto.manager.UserDto;
import vn.edu.fpt.pharma.dto.manager.UserRequest;
import vn.edu.fpt.pharma.service.UserService;
import vn.edu.fpt.pharma.testutil.BaseControllerTest;
import vn.edu.fpt.pharma.testutil.MockUserDetailsHelper;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StaffApiController.class)
@Import(StaffApiControllerTest.MockConfig.class)
@DisplayName("StaffApiController Tests")
class StaffApiControllerTest extends BaseControllerTest {

    @TestConfiguration
    static class MockConfig {
        @Bean
        UserService userService() {
            return mock(UserService.class);
        }
    }

    @Autowired
    private UserService userService;

    @Nested
    @DisplayName("GET /api/manager/staffs - List all staff")
    class GetAllStaffTests {

        @Test
        @WithMockUser(roles = "BRANCH_MANAGER")
        @DisplayName("Should return active staff only when showDeleted=false")
        void getAll_activeOnly_success() throws Exception {
            // Given
            CustomUserDetails userDetails = MockUserDetailsHelper.createManagerUserDetails(1L, 1L);
            List<UserDto> staffList = Arrays.asList(
                    createUserDto(1L, "staff1", "Staff One"),
                    createUserDto(2L, "staff2", "Staff Two")
            );
            when(userService.getStaffsActive(1L)).thenReturn(staffList);

            // When & Then
            mockMvc.perform(get("/api/manager/staffs").with(user(userDetails))
                            .param("showDeleted", "false"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].userName").value("staff1"))
                    .andExpect(jsonPath("$[1].userName").value("staff2"));

            verify(userService).getStaffsActive(1L);
        }

        @Test
        @WithMockUser(roles = "BRANCH_MANAGER")
        @DisplayName("Should return all staff including deleted when showDeleted=true")
        void getAll_includingDeleted_success() throws Exception {
            // Given
            CustomUserDetails userDetails = MockUserDetailsHelper.createManagerUserDetails(1L, 1L);
            List<UserDto> staffList = Arrays.asList(
                    createUserDto(1L, "staff1", "Staff One"),
                    createUserDto(2L, "staff2", "Staff Two"),
                    createUserDto(3L, "staff3", "Staff Three (Deleted)")
            );
            when(userService.getStaffs(1L)).thenReturn(staffList);

            // When & Then
            mockMvc.perform(get("/api/manager/staffs").with(user(userDetails))
                            .param("showDeleted", "true"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(3));

            verify(userService).getStaffs(1L);
        }
    }

    @Nested
    @DisplayName("GET /api/manager/staffs/pharmacists - List pharmacists")
    class GetPharmacistsTests {

        @Test
        @WithMockUser(roles = "BRANCH_MANAGER")
        @DisplayName("Should return only pharmacists (roleId=6)")
        void getPharmacists_success() throws Exception {
            // Given
            CustomUserDetails userDetails = MockUserDetailsHelper.createManagerUserDetails(1L, 1L);
            List<UserDto> pharmacists = Arrays.asList(
                    createUserDto(10L, "pharmacist1", "Pharmacist One", "PHARMACIST"),
                    createUserDto(11L, "pharmacist2", "Pharmacist Two", "PHARMACIST")
            );
            when(userService.getPharmacists(1L)).thenReturn(pharmacists);

            // When & Then
            mockMvc.perform(get("/api/manager/staffs/pharmacists").with(user(userDetails)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].roleName").value("PHARMACIST"))
                    .andExpect(jsonPath("$[1].roleName").value("PHARMACIST"));

            verify(userService).getPharmacists(1L);
        }
    }

    @Nested
    @DisplayName("GET /api/manager/staffs/{id} - Get staff by ID")
    class GetByIdTests {

        @Test
        @WithMockUser(roles = "BRANCH_MANAGER")
        @DisplayName("Should return staff when found")
        void getById_found_success() throws Exception {
            // Given
            UserDto userDto = createUserDto(1L, "staff1", "Staff One");
            when(userService.getById(1L)).thenReturn(userDto);

            // When & Then
            mockMvc.perform(get("/api/manager/staffs/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.userName").value("staff1"))
                    .andExpect(jsonPath("$.fullName").value("Staff One"));

            verify(userService).getById(1L);
        }

        @Test
        @WithMockUser(roles = "BRANCH_MANAGER")
        @DisplayName("Should return 404 when staff not found")
        void getById_notFound_returnsError() throws Exception {
            // Given
            when(userService.getById(999L)).thenThrow(new RuntimeException("Staff not found"));

            // When & Then
            mockMvc.perform(get("/api/manager/staffs/999"))
                    .andExpect(status().isNotFound());

            verify(userService).getById(999L);
        }
    }

    @Nested
    @DisplayName("POST /api/manager/staffs - Create staff")
    class CreateStaffTests {

        @Test
        @WithMockUser(roles = "BRANCH_MANAGER")
        @DisplayName("Should create staff with valid data and auto-set branchId")
        void create_validData_success() throws Exception {
            // Given
            CustomUserDetails userDetails = MockUserDetailsHelper.createManagerUserDetails(1L, 1L);
            UserRequest request = createUserRequest("newstaff", "New Staff", "newstaff@example.com", 2L);
            UserDto createdUser = createUserDto(10L, "newstaff", "New Staff");

            when(userService.create(any(UserRequest.class))).thenReturn(createdUser);

            // When & Then
            mockMvc.perform(post("/api/manager/staffs")
                            .with(user(userDetails))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJson(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.userName").value("newstaff"))
                    .andExpect(jsonPath("$.fullName").value("New Staff"));

            verify(userService).create(any(UserRequest.class));
        }

        @Test
        @WithMockUser(roles = "BRANCH_MANAGER")
        @DisplayName("Should return 400 when creating staff with duplicate username")
        void create_duplicateUsername_returnsBadRequest() throws Exception {
            // Given
            CustomUserDetails userDetails = MockUserDetailsHelper.createManagerUserDetails(1L, 1L);
            UserRequest request = createUserRequest("existinguser", "Existing User", "test@example.com", 2L);

            when(userService.create(any(UserRequest.class)))
                    .thenThrow(new RuntimeException("Username already exists"));

            // When & Then
            mockMvc.perform(post("/api/manager/staffs")
                            .with(user(userDetails))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJson(request)))
                    .andExpect(status().isBadRequest());

            verify(userService, atLeastOnce()).create(any(UserRequest.class));
        }

        @Test
        @WithMockUser(roles = "BRANCH_MANAGER")
        @DisplayName("Should return 400 when validation fails")
        void create_validationError_returnsBadRequest() throws Exception {
            // Given
            CustomUserDetails userDetails = MockUserDetailsHelper.createManagerUserDetails(1L, 1L);
            UserRequest request = createUserRequest("", "", "", 2L); // Invalid empty fields

            // When & Then
            mockMvc.perform(post("/api/manager/staffs")
                            .with(user(userDetails))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJson(request)))
                    .andExpect(status().isBadRequest());

            verify(userService, never()).create(any(UserRequest.class));
        }
    }

    @Nested
    @DisplayName("PUT /api/manager/staffs/{id} - Update staff")
    class UpdateStaffTests {

        @Test
        @WithMockUser(roles = "BRANCH_MANAGER")
        @DisplayName("Should update staff successfully")
        void update_validData_success() throws Exception {
            // Given
            CustomUserDetails userDetails = MockUserDetailsHelper.createManagerUserDetails(1L, 1L);
            UserRequest request = createUserRequest("staff1", "Staff One Updated", "updated@example.com", 2L);
            UserDto updatedUser = createUserDto(1L, "staff1", "Staff One Updated");

            when(userService.update(eq(1L), any(UserRequest.class))).thenReturn(updatedUser);

            // When & Then
            mockMvc.perform(put("/api/manager/staffs/1")
                            .with(user(userDetails))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJson(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.fullName").value("Staff One Updated"));

            verify(userService).update(eq(1L), any(UserRequest.class));
        }

        @Test
        @WithMockUser(roles = "BRANCH_MANAGER")
        @DisplayName("Should return 400 when updating with duplicate username")
        void update_duplicateUsername_returnsBadRequest() throws Exception {
            // Given
            CustomUserDetails userDetails = MockUserDetailsHelper.createManagerUserDetails(1L, 1L);
            UserRequest request = createUserRequest("existinguser", "Staff One", "test@example.com", 2L);

            when(userService.update(eq(1L), any(UserRequest.class)))
                    .thenThrow(new RuntimeException("Username already exists"));

            // When & Then
            mockMvc.perform(put("/api/manager/staffs/1")
                            .with(user(userDetails))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJson(request)))
                    .andExpect(status().isBadRequest());

            verify(userService, atLeastOnce()).update(eq(1L), any(UserRequest.class));
        }
    }

    @Nested
    @DisplayName("DELETE /api/manager/staffs/{id} - Delete staff")
    class DeleteStaffTests {

        @Test
        @WithMockUser(roles = "BRANCH_MANAGER")
        @DisplayName("Should delete staff successfully")
        void delete_success() throws Exception {
            // Given
            CustomUserDetails userDetails = MockUserDetailsHelper.createManagerUserDetails(1L, 1L);
            doNothing().when(userService).delete(2L);

            // When & Then
            mockMvc.perform(delete("/api/manager/staffs/2")
                            .with(user(userDetails))
                            .with(csrf()))
                    .andExpect(status().isNoContent());

            verify(userService).delete(2L);
        }

        @Test
        @WithMockUser(roles = "BRANCH_MANAGER")
        @DisplayName("Should return 400 when deleting staff with active shift assignment")
        void delete_staffInActiveShift_returnsBadRequest() throws Exception {
            // Given
            CustomUserDetails userDetails = MockUserDetailsHelper.createManagerUserDetails(1L, 1L);
            doThrow(new RuntimeException("Nhân viên đang trong một ca làm việc, không thể xóa"))
                    .when(userService).delete(1L);

            // When & Then
            mockMvc.perform(delete("/api/manager/staffs/1")
                            .with(user(userDetails))
                            .with(csrf()))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Nhân viên đang trong một ca làm việc, không thể xóa"));

            verify(userService).delete(1L);
        }
    }

    @Nested
    @DisplayName("PATCH /api/manager/staffs/{id}/restore - Restore staff")
    class RestoreStaffTests {

        @Test
        @WithMockUser(roles = "BRANCH_MANAGER")
        @DisplayName("Should restore deleted staff successfully")
        void restore_success() throws Exception {
            // Given
            CustomUserDetails userDetails = MockUserDetailsHelper.createManagerUserDetails(1L, 1L);
            doNothing().when(userService).restore(3L);

            // When & Then
            mockMvc.perform(patch("/api/manager/staffs/3/restore")
                            .with(user(userDetails))
                            .with(csrf()))
                    .andExpect(status().isNoContent());

            verify(userService).restore(3L);
        }
    }

    // Helper methods
    private UserDto createUserDto(Long id, String userName, String fullName) {
        return createUserDto(id, userName, fullName, "STAFF");
    }

    private UserDto createUserDto(Long id, String userName, String fullName, String roleName) {
        UserDto dto = new UserDto();
        dto.setId(id);
        dto.setUserName(userName);
        dto.setFullName(fullName);
        dto.setRoleName(roleName);
        dto.setEmail(userName + "@example.com");
        dto.setPhoneNumber("0912345678");
        return dto;
    }

    private UserRequest createUserRequest(String userName, String fullName, String email, Long roleId) {
        UserRequest request = new UserRequest();
        request.setUserName(userName);
        request.setFullName(fullName);
        request.setEmail(email);
        request.setPhoneNumber("0912345678");
        request.setPassword("password123");
        request.setRoleId(roleId);
        return request;
    }
}
