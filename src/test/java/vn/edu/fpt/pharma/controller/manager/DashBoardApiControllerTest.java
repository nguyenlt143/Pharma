package vn.edu.fpt.pharma.controller.manager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import vn.edu.fpt.pharma.config.CustomUserDetails;
import vn.edu.fpt.pharma.dto.manager.DashboardData;
import vn.edu.fpt.pharma.dto.manager.KpiData;
import vn.edu.fpt.pharma.service.ManagerDashBoardService;
import vn.edu.fpt.pharma.testutil.BaseControllerTest;
import vn.edu.fpt.pharma.testutil.MockUserDetailsHelper;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DashBoardApiController.class)
@Import(DashBoardApiControllerTest.MockConfig.class)
@DisplayName("DashBoardApiController Tests")
class DashBoardApiControllerTest extends BaseControllerTest {

    @TestConfiguration
    static class MockConfig {
        @Bean
        ManagerDashBoardService managerDashBoardService() {
            return mock(ManagerDashBoardService.class);
        }
    }

    @Autowired
    ManagerDashBoardService managerDashBoardService;

    @Nested
    @DisplayName("GET /api/manager/dashboard - Get dashboard data")
    class GetDashboardTests {
        @Test
        @WithMockUser(roles = "BRANCH_MANAGER")
        void getDashboard_today_success() throws Exception {
            CustomUserDetails userDetails = MockUserDetailsHelper.createManagerUserDetails(1L, 1L);
            DashboardData dashboardData = createDashboardData(0);
            when(managerDashBoardService.getDashboardDataByPeriod(0, 1L)).thenReturn(dashboardData);

            mockMvc.perform(get("/api/manager/dashboard").with(user(userDetails)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.days").value(0));

            verify(managerDashBoardService).getDashboardDataByPeriod(0, 1L);
        }

        @Test
        @WithMockUser(roles = "BRANCH_MANAGER")
        void getDashboard_last7Days_success() throws Exception {
            CustomUserDetails userDetails = MockUserDetailsHelper.createManagerUserDetails(1L, 1L);
            DashboardData dashboardData = createDashboardData(7);
            when(managerDashBoardService.getDashboardDataByPeriod(7, 1L)).thenReturn(dashboardData);

            mockMvc.perform(get("/api/manager/dashboard")
                            .with(user(userDetails))
                            .param("days", "7"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.days").value(7));

            verify(managerDashBoardService).getDashboardDataByPeriod(7, 1L);
        }

        @Test
        @WithMockUser(roles = "BRANCH_MANAGER")
        void getDashboard_last30Days_success() throws Exception {
            CustomUserDetails userDetails = MockUserDetailsHelper.createManagerUserDetails(1L, 1L);
            DashboardData dashboardData = createDashboardData(30);
            when(managerDashBoardService.getDashboardDataByPeriod(30, 1L)).thenReturn(dashboardData);

            mockMvc.perform(get("/api/manager/dashboard")
                            .with(user(userDetails))
                            .param("days", "30"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.days").value(30));

            verify(managerDashBoardService).getDashboardDataByPeriod(30, 1L);
        }
    }

    private DashboardData createDashboardData(int days) {
        DashboardData data = new DashboardData();
        data.setDays(days);
        KpiData kpi = new KpiData();
        kpi.setRevenue(10000.0);
        kpi.setOrderCount(50L);
        kpi.setProfit(3000.0);
        data.setKpis(kpi);
        data.setDailyRevenues(new ArrayList<>());
        data.setProductStats(new ArrayList<>());
        return data;
    }
}

