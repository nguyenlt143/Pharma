// filepath: c:\Users\duy\Desktop\PRM\Pharma\src\test\java\vn\edu\fpt\pharma\controller\manager\InventoryApiControllerTest.java
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
import vn.edu.fpt.pharma.service.InventoryReportService;
import vn.edu.fpt.pharma.testutil.BaseControllerTest;
import vn.edu.fpt.pharma.testutil.MockUserDetailsHelper;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InventoryApiController.class)
@Import(InventoryApiControllerTest.MockConfig.class)
@DisplayName("InventoryApiController Tests")
class InventoryApiControllerTest extends BaseControllerTest {

    @TestConfiguration
    static class MockConfig {
        @Bean
        InventoryReportService inventoryReportService() {
            return mock(InventoryReportService.class);
        }
    }

    @Autowired
    InventoryReportService inventoryReportService;

    @Nested
    @DisplayName("GET /api/manager/report/inventory - Summary")
    class SummaryTests {
        @Test
        @WithMockUser(roles = "BRANCH_MANAGER")
        void getSummary_success() throws Exception {
            Map<String, Object> summary = Map.of(
                    "totalValue", 123456.78,
                    "lowStockCount", 3
            );
            when(inventoryReportService.getInventorySummary(anyLong())).thenReturn(summary);

            CustomUserDetails userDetails = MockUserDetailsHelper.createManagerUserDetails(1L, 1L);
            mockMvc.perform(get("/api/manager/report/inventory").with(user(userDetails)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalValue").value(123456.78))
                    .andExpect(jsonPath("$.lowStockCount").value(3));

            verify(inventoryReportService).getInventorySummary(anyLong());
        }
    }

    @Nested
    @DisplayName("GET /api/manager/report/inventory/details - Details")
    class DetailsTests {
        @Test
        @WithMockUser(roles = "BRANCH_MANAGER")
        void getDetails_success() throws Exception {
            List<Map<String, Object>> details = Collections.singletonList(Map.of(
                    "medicine", "Paracetamol",
                    "quantity", 50
            ));
            when(inventoryReportService.getInventoryDetails(anyLong())).thenReturn(details);

            CustomUserDetails userDetails = MockUserDetailsHelper.createManagerUserDetails(1L, 1L);
            mockMvc.perform(get("/api/manager/report/inventory/details").with(user(userDetails)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].medicine").value("Paracetamol"))
                    .andExpect(jsonPath("$[0].quantity").value(50));

            verify(inventoryReportService).getInventoryDetails(anyLong());
        }
    }

    @Nested
    @DisplayName("GET /api/manager/report/inventory/search - Search")
    class SearchTests {
        @Test
        @WithMockUser(roles = "BRANCH_MANAGER")
        void search_withoutParams_returnsAll() throws Exception {
            List<Map<String, Object>> results = Collections.singletonList(Map.of(
                    "medicine", "Aspirin",
                    "quantity", 100
            ));
            when(inventoryReportService.searchInventory(anyLong(), isNull(), isNull(), isNull())).thenReturn(results);

            CustomUserDetails userDetails = MockUserDetailsHelper.createManagerUserDetails(1L, 1L);
            mockMvc.perform(get("/api/manager/report/inventory/search").with(user(userDetails)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].medicine").value("Aspirin"))
                    .andExpect(jsonPath("$[0].quantity").value(100));

            verify(inventoryReportService).searchInventory(anyLong(), isNull(), isNull(), isNull());
        }

        @Test
        @WithMockUser(roles = "BRANCH_MANAGER")
        void search_withQuery_returnsFiltered() throws Exception {
            List<Map<String, Object>> results = Collections.singletonList(Map.of(
                    "medicine", "Paracetamol",
                    "quantity", 80
            ));
            when(inventoryReportService.searchInventory(anyLong(), eq("paracetamol"), isNull(), isNull())).thenReturn(results);

            CustomUserDetails userDetails = MockUserDetailsHelper.createManagerUserDetails(1L, 1L);
            mockMvc.perform(get("/api/manager/report/inventory/search").with(user(userDetails)).param("query", "paracetamol"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].medicine").value("Paracetamol"))
                    .andExpect(jsonPath("$[0].quantity").value(80));

            verify(inventoryReportService).searchInventory(anyLong(), eq("paracetamol"), isNull(), isNull());
        }
    }
}
