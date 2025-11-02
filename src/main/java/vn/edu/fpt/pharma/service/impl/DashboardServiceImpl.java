package vn.edu.fpt.pharma.service.impl;

import org.springframework.stereotype.Service;
import vn.edu.fpt.pharma.service.DashboardServicev2;

@Service
public class DashboardServiceImpl implements DashboardServicev2 {
    @Override
    public int countWaitingOrders() {
        return 2;
    }

    @Override
    public String getLastInventoryCheck() {
        return "2 ngày trước";
    }

    @Override
    public int countNearlyExpiredMedicines() {
        return 15;
    }

    @Override
    public int countLowStockItems() {
        return 8;
    }
}
