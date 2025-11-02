package vn.edu.fpt.pharma.service;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import vn.edu.fpt.pharma.config.CustomUserDetails;
import vn.edu.fpt.pharma.dto.manager.DailyRevenue;
import vn.edu.fpt.pharma.dto.manager.DashboardData;
import vn.edu.fpt.pharma.dto.manager.KpiData;
import vn.edu.fpt.pharma.entity.User;
import vn.edu.fpt.pharma.repository.InvoiceRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DashBoardService {

    // Giả sử bạn đã inject các Repository (ví dụ: OrderRepository, ProductRepository...)
    final private InvoiceRepository invoiceRepository;

    public DashBoardService(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    public DashboardData getDashboardData(int days,Long branchId) {
        DashboardData data = new DashboardData();
        data.setDays(days);
        data.setKpis(getKpi(branchId,days));
        data.setDailyRevenues(getDailyRevenue(branchId,days));
        return data;
    }


    private KpiData getKpi(Long branchId, int days) {
        LocalDate today = LocalDate.now();
        LocalDate from = today.minusDays(days);
        LocalDateTime fromDate = from.atStartOfDay();
        LocalDateTime toDate = today.plusDays(1).atStartOfDay();

        return invoiceRepository.sumRevenue(branchId, fromDate, toDate);
    }

    private List<DailyRevenue> getDailyRevenue(Long branchId, int days) {
        LocalDate today = LocalDate.now();
        LocalDate fromDate = today.minusDays(days);

        return invoiceRepository.getDailyRevenueByDate(
                branchId,
                fromDate.atStartOfDay(),
                today.plusDays(1).atStartOfDay()
        );
    }


}
