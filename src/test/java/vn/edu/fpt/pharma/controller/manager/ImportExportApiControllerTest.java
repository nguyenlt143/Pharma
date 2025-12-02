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
import vn.edu.fpt.pharma.repository.BranchRepository;
import vn.edu.fpt.pharma.repository.InventoryMovementRepository;
import vn.edu.fpt.pharma.repository.InventoryRepository;
import vn.edu.fpt.pharma.service.ImportExportService;
import vn.edu.fpt.pharma.testutil.BaseControllerTest;
import vn.edu.fpt.pharma.testutil.MockUserDetailsHelper;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ImportExportApiController.class)
@Import(ImportExportApiControllerTest.MockConfig.class)
@DisplayName("ImportExportApiController Tests")
class ImportExportApiControllerTest extends BaseControllerTest {

    @TestConfiguration
    static class MockConfig {
        @Bean
        ImportExportService importExportService() {
            return mock(ImportExportService.class);
        }

        @Bean
        InventoryRepository inventoryRepository() {
            return mock(InventoryRepository.class);
        }

        @Bean
        BranchRepository branchRepository() {
            return mock(BranchRepository.class);
        }

        @Bean
        InventoryMovementRepository inventoryMovementRepository() {
            return mock(InventoryMovementRepository.class);
        }
    }

    @Autowired
    ImportExportService importExportService;

    @Autowired
    InventoryMovementRepository inventoryMovementRepository;

    @Nested
    @DisplayName("GET /api/manager/import-export/summary - Get inventory summary")
    class GetSummaryTests {
        @Test
        @WithMockUser(roles = "BRANCH_MANAGER")
        void getSummary_success() throws Exception {
            CustomUserDetails userDetails = MockUserDetailsHelper.createManagerUserDetails(1L, 1L);
            when(importExportService.calculateTotalInventoryValue(1L)).thenReturn(123456.78);
            when(importExportService.formatCurrencyReadable(123456.78)).thenReturn("123,456.78 VNĐ");
            when(importExportService.countLowStock(1L)).thenReturn(5);
            when(importExportService.countPendingInbound(1L)).thenReturn(3);
            when(importExportService.countPendingOutbound(1L)).thenReturn(2);

            mockMvc.perform(get("/api/manager/import-export/summary").with(user(userDetails)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalValue").value(123456.78))
                    .andExpect(jsonPath("$.lowStockCount").value(5));

            verify(importExportService).calculateTotalInventoryValue(1L);
            verify(importExportService).countLowStock(1L);
        }
    }

    @Nested
    @DisplayName("GET /api/manager/import-export/movements - Get inventory movements")
    class GetMovementsTests {
        @Test
        @WithMockUser(roles = "BRANCH_MANAGER")
        void getMovements_defaultWeek_success() throws Exception {
            CustomUserDetails userDetails = MockUserDetailsHelper.createManagerUserDetails(1L, 1L);
            when(inventoryMovementRepository.findMovementsSinceByBranch(any(), eq(1L))).thenReturn(new ArrayList<>());

            mockMvc.perform(get("/api/manager/import-export/movements").with(user(userDetails)))
                    .andExpect(status().isOk());

            verify(inventoryMovementRepository, atLeastOnce()).findMovementsSinceByBranch(any(), eq(1L));
        }

        @Test
        @WithMockUser(roles = "BRANCH_MANAGER")
        void getMovements_month_success() throws Exception {
            CustomUserDetails userDetails = MockUserDetailsHelper.createManagerUserDetails(1L, 1L);
            when(inventoryMovementRepository.findMovementsSinceByBranch(any(), eq(1L))).thenReturn(new ArrayList<>());

            mockMvc.perform(get("/api/manager/import-export/movements")
                            .with(user(userDetails))
                            .param("range", "month"))
                    .andExpect(status().isOk());

            verify(inventoryMovementRepository, atLeastOnce()).findMovementsSinceByBranch(any(), eq(1L));
        }

        @Test
        @WithMockUser(roles = "BRANCH_MANAGER")
        void getMovements_quarter_success() throws Exception {
            CustomUserDetails userDetails = MockUserDetailsHelper.createManagerUserDetails(1L, 1L);
            when(inventoryMovementRepository.findMovementsSinceByBranch(any(), eq(1L))).thenReturn(new ArrayList<>());

            mockMvc.perform(get("/api/manager/import-export/movements")
                            .with(user(userDetails))
                            .param("range", "quarter"))
                    .andExpect(status().isOk());

            verify(inventoryMovementRepository, atLeastOnce()).findMovementsSinceByBranch(any(), eq(1L));
        }
    }
}

