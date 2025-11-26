package vn.edu.fpt.pharma.service;

import java.util.List;
import java.util.Map;

public interface InventoryReportService {
    Map<String, Object> getInventorySummary(Long branchId);
    String generateInventoryCsv(Long branchId, String warehouse, String range, String category);
    List<Map<String, Object>> getInventoryDetails(Long branchId);
    List<Map<String, Object>> getCategoryStatistics(Long branchId);
    List<Map<String, Object>> getAllCategories(Long branchId);
    List<Map<String, Object>> searchInventory(Long branchId, String query, Long categoryId, String status);
}

