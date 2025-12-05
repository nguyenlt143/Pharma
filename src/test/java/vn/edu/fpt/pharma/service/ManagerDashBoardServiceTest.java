package vn.edu.fpt.pharma.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import vn.edu.fpt.pharma.dto.manager.DailyRevenue;
import vn.edu.fpt.pharma.dto.manager.DashboardData;
import vn.edu.fpt.pharma.dto.manager.InvoiceSummary;
import vn.edu.fpt.pharma.dto.manager.KpiData;
import vn.edu.fpt.pharma.dto.manager.TopProductItem;
import vn.edu.fpt.pharma.repository.InvoiceRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ManagerDashBoardServiceTest {

    private InvoiceRepository invoiceRepository;
    private ManagerDashBoardService service;

    @BeforeEach
    void setUp() {
        invoiceRepository = mock(InvoiceRepository.class);
        // Ensure default methods on interface that depend on other mocked methods can execute
        // Let sumRevenue call the real default implementation which uses findInvoicesForReport
        try {
            when(invoiceRepository.sumRevenue(any(), any(), any(), any(), any())).thenCallRealMethod();
            // Also allow the 3-arg getDailyRevenueByDate default to be stubbed in tests when needed
        } catch (Throwable ignored) {
        }
        service = new ManagerDashBoardService(invoiceRepository);
    }

    @Test
    void getDashboard_noInvoices_returnsZeroKpiAndZeroDailyRevenues() {
        when(invoiceRepository.getDailyRevenueByDate(any(), any(), any())).thenReturn(new ArrayList<>());
        when(invoiceRepository.topCategories(any(), any(), any(), any(), any(), any(PageRequest.class))).thenReturn(new ArrayList<>());
        when(invoiceRepository.findInvoicesForReport(any(), any(), any(), any(), any())).thenReturn(new ArrayList<>());

        DashboardData data = service.getDashboardDataByPeriod(3, 1L);

        assertNotNull(data);
        assertEquals(3, data.getDays());
        KpiData kpi = data.getKpis();
        assertNotNull(kpi);
        assertEquals(0.0, kpi.getRevenue());
        assertEquals(0L, kpi.getOrderCount());
        assertEquals(0.0, kpi.getProfit());

        List<DailyRevenue> dr = data.getDailyRevenues();
        assertNotNull(dr);
        assertEquals(3, dr.size());
        for (DailyRevenue d : dr) {
            assertEquals(0.0, d.getRevenue());
        }

        List<Map<String, Object>> stats = data.getProductStats();
        assertNotNull(stats);
        assertTrue(stats.isEmpty());
    }

    @Test
    void getDashboard_dailyRevenue_fillsMissingDatesWithZero() {
        // Provide revenues for first and third day only
        LocalDate today = LocalDate.now();
        LocalDate from = today.minusDays(2);

        DailyRevenue dr1 = new DailyRevenue() {
            @Override
            public LocalDate getDate() {
                return from;
            }

            @Override
            public Double getRevenue() {
                return 100.0;
            }
        };

        DailyRevenue dr3 = new DailyRevenue() {
            @Override
            public LocalDate getDate() {
                return from.plusDays(2);
            }

            @Override
            public Double getRevenue() {
                return 300.0;
            }
        };

        List<DailyRevenue> queried = List.of(dr1, dr3);
        when(invoiceRepository.getDailyRevenueByDate(any(), any(), any())).thenReturn(queried);
        when(invoiceRepository.topCategories(any(), any(), any(), any(), any(), any(PageRequest.class))).thenReturn(new ArrayList<>());
        when(invoiceRepository.findInvoicesForReport(any(), any(), any(), any(), any())).thenReturn(new ArrayList<>());

        DashboardData data = service.getDashboardDataByPeriod(3, 1L);
        List<DailyRevenue> dr = data.getDailyRevenues();
        assertEquals(3, dr.size());
        assertEquals(100.0, dr.get(0).getRevenue());
        assertEquals(0.0, dr.get(1).getRevenue());
        assertEquals(300.0, dr.get(2).getRevenue());
    }

    @Test
    void getDashboard_topCategories_percentRoundingAndColors() {
        TopProductItem a = new TopProductItem("A", 30);
        TopProductItem b = new TopProductItem("B", 70);
        List<TopProductItem> tops = List.of(a, b);
        when(invoiceRepository.getDailyRevenueByDate(any(), any(), any())).thenReturn(new ArrayList<>());
        when(invoiceRepository.topCategories(any(), any(), any(), any(), any(), any(PageRequest.class))).thenReturn(tops);
        when(invoiceRepository.findInvoicesForReport(any(), any(), any(), any(), any())).thenReturn(new ArrayList<>());

        DashboardData data = service.getDashboardDataByPeriod(7, 1L);
        List<Map<String, Object>> stats = data.getProductStats();
        assertEquals(2, stats.size());

        // percentages: A = 30/(30+70)=30% -> 30, B = 70% -> 70, rounded
        assertEquals("A", stats.get(0).get("name"));
        assertEquals(Math.round(30.0), stats.get(0).get("percent"));
        assertNotNull(stats.get(0).get("color"));

        assertEquals("B", stats.get(1).get("name"));
        assertEquals(Math.round(70.0), stats.get(1).get("percent"));
        assertNotNull(stats.get(1).get("color"));
    }

    @Test
    void getDashboard_kpiSumsMatchInvoiceSummary() {
        InvoiceSummary i1 = new InvoiceSummary(1L, "C1", "u1", "s1", LocalDateTime.now(), 100.0, 10.0, "cash");
        InvoiceSummary i2 = new InvoiceSummary(2L, "C2", "u2", "s2", LocalDateTime.now(), 200.0, 20.0, "cash");
        List<InvoiceSummary> invoices = List.of(i1, i2);
        when(invoiceRepository.getDailyRevenueByDate(any(), any(), any())).thenReturn(new ArrayList<>());
        when(invoiceRepository.topCategories(any(), any(), any(), any(), any(), any(PageRequest.class))).thenReturn(new ArrayList<>());
        when(invoiceRepository.findInvoicesForReport(any(), any(), any(), any(), any())).thenReturn(invoices);

        DashboardData data = service.getDashboardDataByPeriod(7, 1L);
        KpiData kpi = data.getKpis();
        assertNotNull(kpi);
        assertEquals(300.0, kpi.getRevenue());
        assertEquals(2L, kpi.getOrderCount());
        assertEquals(30.0, kpi.getProfit());
    }

    @Test
    void negativeDays_isHandled_asTodayInRange() {
        when(invoiceRepository.getDailyRevenueByDate(any(), any(), any())).thenReturn(new ArrayList<>());
        when(invoiceRepository.topCategories(any(), any(), any(), any(), any(), any(PageRequest.class))).thenReturn(new ArrayList<>());
        when(invoiceRepository.findInvoicesForReport(any(), any(), any(), any(), any())).thenReturn(new ArrayList<>());

        DashboardData data = service.getDashboardDataByPeriod(-5, 1L);
        assertEquals(-5, data.getDays());
        List<DailyRevenue> dr = data.getDailyRevenues();
        assertEquals(1, dr.size()); // only today expected per current implementation
    }

    @Test
    void largeDays_productStats_limitedToSixAndNoException() {
        List<TopProductItem> many = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            many.add(new TopProductItem("P" + i, i + 1));
        }
        // Simulate repository honoring pageable and returning only first 6 items
        List<TopProductItem> limited = many.subList(0, 6);
        when(invoiceRepository.getDailyRevenueByDate(any(), any(), any())).thenReturn(new ArrayList<>());
        when(invoiceRepository.topCategories(any(), any(), any(), any(), any(), any(PageRequest.class))).thenReturn(limited);
        when(invoiceRepository.findInvoicesForReport(any(), any(), any(), any(), any())).thenReturn(new ArrayList<>());

        DashboardData data = service.getDashboardDataByPeriod(365, 1L);
        List<Map<String, Object>> stats = data.getProductStats();
        assertTrue(stats.size() <= 6);
    }
}
