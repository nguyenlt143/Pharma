// filepath: c:\Users\duy\Desktop\PRM\Pharma\src\test\java\vn\edu\fpt\pharma\controller\manager\RevenueApiControllerTest.java
package vn.edu.fpt.pharma.controller.manager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import vn.edu.fpt.pharma.config.CustomUserDetails;
import vn.edu.fpt.pharma.service.RevenueReportService;
import vn.edu.fpt.pharma.testutil.BaseControllerTest;
import vn.edu.fpt.pharma.testutil.MockUserDetailsHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RevenueApiController.class)
@Import(RevenueApiControllerTest.MockConfig.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("RevenueApiController Tests")
class RevenueApiControllerTest extends BaseControllerTest {

    @TestConfiguration
    static class MockConfig {
        @Bean
        RevenueReportService revenueReportService() {
            return mock(RevenueReportService.class);
        }
    }

    @Autowired
    RevenueReportService revenueReportService;

    @Nested
    @DisplayName("GET /api/manager/report/revenue - Day mode")
    class DayModeTests {
        @Test
        @WithMockUser(roles = "BRANCH_MANAGER")
        void getRevenue_defaultDay_success() throws Exception {
            Page<Map<String, Object>> reportPage = new PageImpl<>(List.of(
                    Map.of("code", "INV001"),
                    Map.of("code", "INV002")
            ));

            Map<String, Object> report = new HashMap<>();
            report.put("totalInvoices", 2);
            report.put("totalRevenue", 200.0);
            report.put("totalProfit", 50.0);
            report.put("invoices", reportPage);

            when(revenueReportService.getRevenueReport(anyLong(), isNull(), eq("day"), isNull(), isNull(), isNull(), any())).thenReturn(report);

            CustomUserDetails userDetails = MockUserDetailsHelper.createManagerUserDetails(1L, 1L);
            mockMvc.perform(get("/api/manager/report/revenue").with(user(userDetails)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalInvoices").value(2))
                    .andExpect(jsonPath("$.invoices").exists());

            verify(revenueReportService).getRevenueReport(anyLong(), isNull(), eq("day"), isNull(), isNull(), isNull(), any());
        }

        @Test
        @WithMockUser(roles = "BRANCH_MANAGER")
        void getRevenue_filterByShift_success() throws Exception {
            Map<String, Object> report = Map.of("totalInvoices", 1, "totalRevenue", 100.0, "totalProfit", 20.0);
            when(revenueReportService.getRevenueReport(anyLong(), isNull(), eq("day"), isNull(), eq(1L), isNull(), any())).thenReturn(report);

            CustomUserDetails userDetails = MockUserDetailsHelper.createManagerUserDetails(1L, 1L);
            mockMvc.perform(get("/api/manager/report/revenue").with(user(userDetails)).param("shift", "1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalInvoices").value(1));

            verify(revenueReportService).getRevenueReport(anyLong(), isNull(), eq("day"), isNull(), eq(1L), isNull(), any());
        }
    }
}
