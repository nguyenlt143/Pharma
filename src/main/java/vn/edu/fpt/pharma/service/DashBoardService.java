package vn.edu.fpt.pharma.service;

public interface DashboardService {
    int countWaitingOrders();
    String getLastInventoryCheck();
    int countNearlyExpiredMedicines();
    int countLowStockItems();
}
