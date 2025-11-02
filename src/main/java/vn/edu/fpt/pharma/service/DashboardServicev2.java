package vn.edu.fpt.pharma.service;

public interface DashboardServicev2 {
    int countWaitingOrders();
    String getLastInventoryCheck();
    int countNearlyExpiredMedicines();
    int countLowStockItems();
}
