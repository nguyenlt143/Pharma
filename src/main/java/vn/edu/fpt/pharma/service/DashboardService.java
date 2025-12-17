package vn.edu.fpt.pharma.service;

import java.util.Map;

public interface DashboardService {
    Map<String, Object> getDashboardData(Long branchId);
    Map<String, Object> getWarehouseDashboardData();
}