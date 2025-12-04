package vn.edu.fpt.pharma.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.pharma.dto.manager.UserRequest;
import vn.edu.fpt.pharma.repository.RoleRepository;
import vn.edu.fpt.pharma.repository.UserRepository;
import vn.edu.fpt.pharma.entity.Role;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Staff Management Integration Tests")
class StaffManagementIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private Role staffRole;

    @BeforeEach
    void setUp() {
        // Create a staff role if not exists
        staffRole = roleRepository.findById(1L).orElseGet(() -> {
            Role role = new Role();
            role.setName("STAFF");
            return roleRepository.save(role);
        });
    }

    @Test
    @WithMockUser(username = "manager", roles = "BRANCH_MANAGER")
    @DisplayName("Complete staff lifecycle: create -> list -> update -> delete -> restore")
    void staffLifecycle_completeFlow_success() throws Exception {
        // 1. Create staff
        UserRequest createRequest = new UserRequest();
        createRequest.setUserName("newstaff");
        createRequest.setFullName("New Staff Member");
        createRequest.setEmail("newstaff@test.com");
        createRequest.setPhoneNumber("1234567890");
        createRequest.setPassword("password123");
        createRequest.setRoleId(staffRole.getId());
        createRequest.setBranchId(1L);

        String createResponse = mockMvc.perform(post("/api/manager/staffs")
                        .with(user("manager"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userName").value("newstaff"))
                .andExpect(jsonPath("$.fullName").value("New Staff Member"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long staffId = objectMapper.readTree(createResponse).get("id").asLong();

        // 2. List staff and verify new staff appears
        mockMvc.perform(get("/api/manager/staffs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == " + staffId + ")].userName").value("newstaff"));

        // 3. Update staff
        UserRequest updateRequest = new UserRequest();
        updateRequest.setUserName("newstaff");
        updateRequest.setFullName("Updated Staff Name");
        updateRequest.setEmail("newstaff@test.com");
        updateRequest.setPhoneNumber("0987654321");
        updateRequest.setRoleId(staffRole.getId());
        updateRequest.setBranchId(1L);

        mockMvc.perform(put("/api/manager/staffs/" + staffId)
                        .with(user("manager"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Updated Staff Name"))
                .andExpect(jsonPath("$.phoneNumber").value("0987654321"));

        // 4. Delete staff
        mockMvc.perform(delete("/api/manager/staffs/" + staffId)
                        .with(user("manager"))
                        .with(csrf()))
                .andExpect(status().isNoContent());

        // 5. Verify staff not in active list
        mockMvc.perform(get("/api/manager/staffs")
                        .with(user("manager")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == " + staffId + ")]").doesNotExist());

        // 6. Verify staff appears in deleted list
        mockMvc.perform(get("/api/manager/staffs")
                        .with(user("manager"))
                        .param("showDeleted", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == " + staffId + ")].deleted").value(true));

        // 7. Restore staff
        mockMvc.perform(patch("/api/manager/staffs/" + staffId + "/restore")
                        .with(user("manager"))
                        .with(csrf()))
                .andExpect(status().isNoContent());

        // 8. Verify staff back in active list
        mockMvc.perform(get("/api/manager/staffs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == " + staffId + ")].userName").value("newstaff"));
    }

    @Test
    @WithMockUser(username = "manager", roles = "BRANCH_MANAGER")
    @DisplayName("Cannot create staff with duplicate username")
    void createStaff_duplicateUsername_fails() throws Exception {
        // Create first staff
        UserRequest request1 = new UserRequest();
        request1.setUserName("duplicate");
        request1.setFullName("First Staff");
        request1.setEmail("first@test.com");
        request1.setPhoneNumber("1111111111");
        request1.setPassword("password123");
        request1.setRoleId(staffRole.getId());
        request1.setBranchId(1L);

        mockMvc.perform(post("/api/manager/staffs")
                        .with(user("manager"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        // Try to create second staff with same username
        UserRequest request2 = new UserRequest();
        request2.setUserName("duplicate");
        request2.setFullName("Second Staff");
        request2.setEmail("second@test.com");
        request2.setPhoneNumber("2222222222");
        request2.setPassword("password123");
        request2.setRoleId(staffRole.getId());
        request2.setBranchId(1L);

        mockMvc.perform(post("/api/manager/staffs")
                        .with(user("manager"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Tên đăng nhập đã tồn tại"));
    }
}

