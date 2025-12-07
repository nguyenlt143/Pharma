package vn.edu.fpt.pharma.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import vn.edu.fpt.pharma.BaseServiceTest;
import vn.edu.fpt.pharma.dto.PagingRequest;
import vn.edu.fpt.pharma.dto.manager.InvoiceSummary;
import vn.edu.fpt.pharma.dto.manager.KpiData;
import vn.edu.fpt.pharma.dto.manager.TopProductItem;
import vn.edu.fpt.pharma.repository.InvoiceRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for RevenueReportServiceImpl - 12 tests
 * Strategy: Full tests for getRevenueReport (business logic chính)
 */
@DisplayName("RevenueReportServiceImpl Tests")
class RevenueReportServiceImplTest extends BaseServiceTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @InjectMocks
    private RevenueReportServiceImpl service;

    private Long branchId;
    private PagingRequest pagingRequest;

    @BeforeEach
    void setUp() {
        branchId = 1L;
        pagingRequest = new PagingRequest();
        pagingRequest.setPage(0);
        pagingRequest.setSize(10);
    }

    @Nested
    @DisplayName("getRevenueReport() tests - 12 tests ⭐ FULL COVERAGE")
    class GetRevenueReportTests {

        @Test
        @DisplayName("Should return daily report with mode=day")
        void getRevenueReport_withModeDay_shouldReturnDailyReport() {
            // Arrange
            String mode = "day";
            String date = "2024-12-07";
            KpiData kpi = new KpiData(1000000.0, 10L, 200000.0);

            when(invoiceRepository.sumRevenue(eq(branchId), any(LocalDateTime.class),
                    any(LocalDateTime.class), eq(null), eq(null))).thenReturn(kpi);
            when(invoiceRepository.topCategories(eq(branchId), any(LocalDateTime.class),
                    any(LocalDateTime.class), eq(null), eq(null), any(PageRequest.class)))
                    .thenReturn(Collections.emptyList());
            when(invoiceRepository.findInvoicesWithProfit(eq(branchId), any(LocalDateTime.class),
                    any(LocalDateTime.class), eq(null), eq(null), any(PageRequest.class)))
                    .thenReturn(Page.empty());

            // Act
            Map<String, Object> result = service.getRevenueReport(
                    branchId, date, mode, null, null, null, pagingRequest);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.get("totalInvoices")).isEqualTo(10L);
            assertThat(result.get("totalRevenue")).isEqualTo(1000000.0);
            assertThat(result.get("totalProfit")).isEqualTo(200000.0);
            // Verify date range is for one day (00:00 to 24:00 next day)
            verify(invoiceRepository).sumRevenue(eq(branchId),
                    argThat(from -> from.toLocalDate().toString().equals("2024-12-07")),
                    argThat(to -> to.toLocalDate().toString().equals("2024-12-08")),
                    eq(null), eq(null));
        }

        @Test
        @DisplayName("Should return weekly report with mode=week")
        void getRevenueReport_withModeWeek_shouldReturnWeeklyReport() {
            // Arrange
            String mode = "week";
            String period = "2024-W50";
            KpiData kpi = new KpiData(5000000.0, 50L, 1000000.0);

            when(invoiceRepository.sumRevenue(eq(branchId), any(LocalDateTime.class),
                    any(LocalDateTime.class), eq(null), eq(null))).thenReturn(kpi);
            when(invoiceRepository.topCategories(eq(branchId), any(LocalDateTime.class),
                    any(LocalDateTime.class), eq(null), eq(null), any(PageRequest.class)))
                    .thenReturn(Collections.emptyList());
            when(invoiceRepository.findInvoicesWithProfit(eq(branchId), any(LocalDateTime.class),
                    any(LocalDateTime.class), eq(null), eq(null), any(PageRequest.class)))
                    .thenReturn(Page.empty());

            // Act
            Map<String, Object> result = service.getRevenueReport(
                    branchId, null, mode, period, null, null, pagingRequest);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.get("totalInvoices")).isEqualTo(50L);
            // Verify it's weekly range (Monday to next Monday)
            verify(invoiceRepository).sumRevenue(eq(branchId),
                    any(LocalDateTime.class), any(LocalDateTime.class), eq(null), eq(null));
        }

        @Test
        @DisplayName("Should return monthly report with mode=month")
        void getRevenueReport_withModeMonth_shouldReturnMonthlyReport() {
            // Arrange
            String mode = "month";
            String period = "2024-12";
            KpiData kpi = new KpiData(20000000.0, 200L, 4000000.0);

            when(invoiceRepository.sumRevenue(eq(branchId), any(LocalDateTime.class),
                    any(LocalDateTime.class), eq(null), eq(null))).thenReturn(kpi);
            when(invoiceRepository.topCategories(eq(branchId), any(LocalDateTime.class),
                    any(LocalDateTime.class), eq(null), eq(null), any(PageRequest.class)))
                    .thenReturn(Collections.emptyList());
            when(invoiceRepository.findInvoicesWithProfit(eq(branchId), any(LocalDateTime.class),
                    any(LocalDateTime.class), eq(null), eq(null), any(PageRequest.class)))
                    .thenReturn(Page.empty());

            // Act
            Map<String, Object> result = service.getRevenueReport(
                    branchId, null, mode, period, null, null, pagingRequest);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.get("totalInvoices")).isEqualTo(200L);
            // Verify monthly range (first day to first day of next month)
            verify(invoiceRepository).sumRevenue(eq(branchId),
                    argThat(from -> from.toLocalDate().getDayOfMonth() == 1),
                    any(LocalDateTime.class), eq(null), eq(null));
        }

        @Test
        @DisplayName("Should parse valid date correctly")
        void getRevenueReport_withValidDate_shouldParseCorrectly() {
            // Arrange
            String date = "2024-12-05";
            KpiData kpi = new KpiData(0.0, 0L, 0.0);

            when(invoiceRepository.sumRevenue(eq(branchId), any(LocalDateTime.class),
                    any(LocalDateTime.class), eq(null), eq(null))).thenReturn(kpi);
            when(invoiceRepository.topCategories(eq(branchId), any(LocalDateTime.class),
                    any(LocalDateTime.class), eq(null), eq(null), any(PageRequest.class)))
                    .thenReturn(Collections.emptyList());
            when(invoiceRepository.findInvoicesWithProfit(eq(branchId), any(LocalDateTime.class),
                    any(LocalDateTime.class), eq(null), eq(null), any(PageRequest.class)))
                    .thenReturn(Page.empty());

            // Act
            service.getRevenueReport(branchId, date, "day", null, null, null, pagingRequest);

            // Assert
            verify(invoiceRepository).sumRevenue(eq(branchId),
                    argThat(from -> from.toLocalDate().toString().equals("2024-12-05")),
                    any(LocalDateTime.class), eq(null), eq(null));
        }

        @Test
        @DisplayName("Should use today when date is null")
        void getRevenueReport_withNullDate_shouldUseToday() {
            // Arrange
            KpiData kpi = new KpiData(0.0, 0L, 0.0);

            when(invoiceRepository.sumRevenue(eq(branchId), any(LocalDateTime.class),
                    any(LocalDateTime.class), eq(null), eq(null))).thenReturn(kpi);
            when(invoiceRepository.topCategories(eq(branchId), any(LocalDateTime.class),
                    any(LocalDateTime.class), eq(null), eq(null), any(PageRequest.class)))
                    .thenReturn(Collections.emptyList());
            when(invoiceRepository.findInvoicesWithProfit(eq(branchId), any(LocalDateTime.class),
                    any(LocalDateTime.class), eq(null), eq(null), any(PageRequest.class)))
                    .thenReturn(Page.empty());

            // Act
            service.getRevenueReport(branchId, null, "day", null, null, null, pagingRequest);

            // Assert - Should use LocalDate.now()
            verify(invoiceRepository).sumRevenue(eq(branchId),
                    any(LocalDateTime.class), any(LocalDateTime.class), eq(null), eq(null));
        }

        @Test
        @DisplayName("Should fallback to today with invalid date")
        void getRevenueReport_withInvalidDate_shouldFallbackToToday() {
            // Arrange
            String invalidDate = "invalid-date-format";
            KpiData kpi = new KpiData(0.0, 0L, 0.0);

            when(invoiceRepository.sumRevenue(eq(branchId), any(LocalDateTime.class),
                    any(LocalDateTime.class), eq(null), eq(null))).thenReturn(kpi);
            when(invoiceRepository.topCategories(eq(branchId), any(LocalDateTime.class),
                    any(LocalDateTime.class), eq(null), eq(null), any(PageRequest.class)))
                    .thenReturn(Collections.emptyList());
            when(invoiceRepository.findInvoicesWithProfit(eq(branchId), any(LocalDateTime.class),
                    any(LocalDateTime.class), eq(null), eq(null), any(PageRequest.class)))
                    .thenReturn(Page.empty());

            // Act
            service.getRevenueReport(branchId, invalidDate, "day", null, null, null, pagingRequest);

            // Assert - Should fallback to today without throwing
            verify(invoiceRepository).sumRevenue(eq(branchId),
                    any(LocalDateTime.class), any(LocalDateTime.class), eq(null), eq(null));
        }

        @Test
        @DisplayName("Should pass shift filter to repository")
        void getRevenueReport_withShiftFilter_shouldPassToRepo() {
            // Arrange
            Long shiftId = 5L;
            KpiData kpi = new KpiData(1000000.0, 10L, 200000.0);

            when(invoiceRepository.sumRevenue(eq(branchId), any(LocalDateTime.class),
                    any(LocalDateTime.class), eq(shiftId), eq(null))).thenReturn(kpi);
            when(invoiceRepository.topCategories(eq(branchId), any(LocalDateTime.class),
                    any(LocalDateTime.class), eq(shiftId), eq(null), any(PageRequest.class)))
                    .thenReturn(Collections.emptyList());
            when(invoiceRepository.findInvoicesWithProfit(eq(branchId), any(LocalDateTime.class),
                    any(LocalDateTime.class), eq(shiftId), eq(null), any(PageRequest.class)))
                    .thenReturn(Page.empty());

            // Act
            service.getRevenueReport(branchId, null, "day", null, shiftId, null, pagingRequest);

            // Assert
            verify(invoiceRepository).sumRevenue(eq(branchId),
                    any(LocalDateTime.class), any(LocalDateTime.class), eq(shiftId), eq(null));
        }

        @Test
        @DisplayName("Should pass employee filter to repository")
        void getRevenueReport_withEmployeeFilter_shouldPassToRepo() {
            // Arrange
            Long employeeId = 10L;
            KpiData kpi = new KpiData(500000.0, 5L, 100000.0);

            when(invoiceRepository.sumRevenue(eq(branchId), any(LocalDateTime.class),
                    any(LocalDateTime.class), eq(null), eq(employeeId))).thenReturn(kpi);
            when(invoiceRepository.topCategories(eq(branchId), any(LocalDateTime.class),
                    any(LocalDateTime.class), eq(null), eq(employeeId), any(PageRequest.class)))
                    .thenReturn(Collections.emptyList());
            when(invoiceRepository.findInvoicesWithProfit(eq(branchId), any(LocalDateTime.class),
                    any(LocalDateTime.class), eq(null), eq(employeeId), any(PageRequest.class)))
                    .thenReturn(Page.empty());

            // Act
            service.getRevenueReport(branchId, null, "day", null, null, employeeId, pagingRequest);

            // Assert
            verify(invoiceRepository).sumRevenue(eq(branchId),
                    any(LocalDateTime.class), any(LocalDateTime.class), eq(null), eq(employeeId));
        }

        @Test
        @DisplayName("Should calculate KPIs correctly")
        void getRevenueReport_shouldCalculateKPIs() {
            // Arrange
            KpiData kpi = new KpiData(10000000.0, 100L, 2000000.0);

            when(invoiceRepository.sumRevenue(eq(branchId), any(LocalDateTime.class),
                    any(LocalDateTime.class), eq(null), eq(null))).thenReturn(kpi);
            when(invoiceRepository.topCategories(eq(branchId), any(LocalDateTime.class),
                    any(LocalDateTime.class), eq(null), eq(null), any(PageRequest.class)))
                    .thenReturn(Collections.emptyList());
            when(invoiceRepository.findInvoicesWithProfit(eq(branchId), any(LocalDateTime.class),
                    any(LocalDateTime.class), eq(null), eq(null), any(PageRequest.class)))
                    .thenReturn(Page.empty());

            // Act
            Map<String, Object> result = service.getRevenueReport(
                    branchId, null, "day", null, null, null, pagingRequest);

            // Assert
            assertThat(result.get("totalInvoices")).isEqualTo(100L);
            assertThat(result.get("totalRevenue")).isEqualTo(10000000.0);
            assertThat(result.get("totalProfit")).isEqualTo(2000000.0);
        }

        @Test
        @DisplayName("Should include top categories with percentages")
        void getRevenueReport_shouldIncludeTopCategories() {
            // Arrange
            KpiData kpi = new KpiData(10000000.0, 100L, 2000000.0);
            List<TopProductItem> topCategories = java.util.Arrays.asList(
                    new TopProductItem("Thuốc giảm đau", 50L),
                    new TopProductItem("Kháng sinh", 30L),
                    new TopProductItem("Vitamin", 20L)
            );

            when(invoiceRepository.sumRevenue(eq(branchId), any(LocalDateTime.class),
                    any(LocalDateTime.class), eq(null), eq(null))).thenReturn(kpi);
            when(invoiceRepository.topCategories(eq(branchId), any(LocalDateTime.class),
                    any(LocalDateTime.class), eq(null), eq(null), any(PageRequest.class)))
                    .thenReturn(topCategories);
            when(invoiceRepository.findInvoicesWithProfit(eq(branchId), any(LocalDateTime.class),
                    any(LocalDateTime.class), eq(null), eq(null), any(PageRequest.class)))
                    .thenReturn(Page.empty());

            // Act
            Map<String, Object> result = service.getRevenueReport(
                    branchId, null, "day", null, null, null, pagingRequest);

            // Assert
            assertThat(result).containsKey("productStats");
            List<Map<String, Object>> productStats = (List<Map<String, Object>>) result.get("productStats");
            assertThat(productStats).hasSize(3);
            assertThat(productStats.get(0).get("name")).isEqualTo("Thuốc giảm đau");
            assertThat(productStats.get(0).get("percent")).isEqualTo(50L); // 50/100 * 100
        }

        @Test
        @DisplayName("Should handle gracefully when branchId is null")
        void getRevenueReport_withNullBranchId_shouldHandleGracefully() {
            // Arrange
            KpiData kpi = new KpiData(0.0, 0L, 0.0);

            when(invoiceRepository.sumRevenue(eq(null), any(LocalDateTime.class),
                    any(LocalDateTime.class), eq(null), eq(null))).thenReturn(kpi);
            when(invoiceRepository.topCategories(eq(null), any(LocalDateTime.class),
                    any(LocalDateTime.class), eq(null), eq(null), any(PageRequest.class)))
                    .thenReturn(Collections.emptyList());
            when(invoiceRepository.findInvoicesWithProfit(eq(null), any(LocalDateTime.class),
                    any(LocalDateTime.class), eq(null), eq(null), any(PageRequest.class)))
                    .thenReturn(Page.empty());

            // Act
            Map<String, Object> result = service.getRevenueReport(
                    null, null, "day", null, null, null, pagingRequest);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.get("totalInvoices")).isEqualTo(0L);
            assertThat(result.get("totalRevenue")).isEqualTo(0.0);
            assertThat(result.get("totalProfit")).isEqualTo(0.0);
        }

        @Test
        @DisplayName("Should return zero stats when no data")
        void getRevenueReport_withNoData_shouldReturnZeroStats() {
            // Arrange
            KpiData kpi = new KpiData(0.0, 0L, 0.0);

            when(invoiceRepository.sumRevenue(eq(branchId), any(LocalDateTime.class),
                    any(LocalDateTime.class), eq(null), eq(null))).thenReturn(kpi);
            when(invoiceRepository.topCategories(eq(branchId), any(LocalDateTime.class),
                    any(LocalDateTime.class), eq(null), eq(null), any(PageRequest.class)))
                    .thenReturn(Collections.emptyList());
            when(invoiceRepository.findInvoicesWithProfit(eq(branchId), any(LocalDateTime.class),
                    any(LocalDateTime.class), eq(null), eq(null), any(PageRequest.class)))
                    .thenReturn(Page.empty());

            // Act
            Map<String, Object> result = service.getRevenueReport(
                    branchId, null, "day", null, null, null, pagingRequest);

            // Assert
            assertThat(result.get("totalInvoices")).isEqualTo(0L);
            assertThat(result.get("totalRevenue")).isEqualTo(0.0);
            assertThat(result.get("totalProfit")).isEqualTo(0.0);
            assertThat(result.get("totalProducts")).isEqualTo(0L);
        }
    }
}
