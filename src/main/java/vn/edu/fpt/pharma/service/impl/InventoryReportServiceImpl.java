package vn.edu.fpt.pharma.service.impl;

import org.springframework.stereotype.Service;
import vn.edu.fpt.pharma.repository.InventoryRepository;
import vn.edu.fpt.pharma.service.InventoryReportService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InventoryReportServiceImpl implements InventoryReportService {

    private final InventoryRepository inventoryRepository;

    public InventoryReportServiceImpl(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public Map<String, Object> getInventorySummary(Long branchId) {
        Map<String, Object> summary = new HashMap<>();

        try {
            // Validate branchId
            if (branchId == null) {
                System.err.println("Error in getInventorySummary: branchId is null");
                summary.put("totalItems", 0);
                summary.put("lowStock", 0);
                summary.put("totalValue", 0.0);
                summary.put("nearExpiry", 0);
                summary.put("expired", 0);
                summary.put("lastUpdated", java.time.LocalDateTime.now());
                return summary;
            }

            // Get inventory statistics with detailed logging
            System.out.println("Fetching inventory summary for branchId: " + branchId);

            int totalItems = inventoryRepository.countTotalItems(branchId);
            System.out.println("Total items: " + totalItems);

            int lowStock = inventoryRepository.countLowStockItems(branchId);
            System.out.println("Low stock items: " + lowStock);

            Double totalValue = inventoryRepository.calculateTotalValue(branchId);
            System.out.println("Total value: " + totalValue);

            int nearExpiry = inventoryRepository.countNearExpiryItems(branchId);
            System.out.println("Near expiry items: " + nearExpiry);

            int expired = inventoryRepository.countExpiredItems(branchId);
            System.out.println("Expired items: " + expired);

            // Build summary
            summary.put("totalItems", totalItems);
            summary.put("lowStock", lowStock);
            summary.put("totalValue", totalValue != null ? totalValue : 0.0);
            summary.put("nearExpiry", nearExpiry);
            summary.put("expired", expired);
            summary.put("lastUpdated", java.time.LocalDateTime.now());

            System.out.println("Summary generated successfully: " + summary);

        } catch (Exception e) {
            // Log detailed error information
            System.err.println("Error in getInventorySummary for branchId " + branchId + ": " + e.getMessage());
            e.printStackTrace();

            // Return empty data on error
            summary.put("totalItems", 0);
            summary.put("lowStock", 0);
            summary.put("totalValue", 0.0);
            summary.put("nearExpiry", 0);
            summary.put("expired", 0);
            summary.put("lastUpdated", java.time.LocalDateTime.now());
            // Note: Not including error message in response to avoid exposing internal details to frontend
        }

        return summary;
    }

    @Override
    public String generateInventoryCsv(Long branchId, String warehouse, String range, String category) {
        StringBuilder csv = new StringBuilder();

        // CSV Header
        csv.append("Medicine Name,Active Ingredient,Strength,Dosage Form,Batch Code,");
        csv.append("Quantity,Unit,Cost Price,Total Value,Expiry Date,Manufacturer,Category,Status\n");

        try {
            // Get inventory data
            List<Object[]> inventoryData = inventoryRepository.findMedicinesByBranch(branchId);

            for (Object[] row : inventoryData) {
                // Extract data from row
                String medicineName = escapeCSV(String.valueOf(row[3]));
                String activeIngredient = escapeCSV(String.valueOf(row[4]));
                String strength = escapeCSV(String.valueOf(row[5]));
                String dosageForm = escapeCSV(String.valueOf(row[6]));
                String manufacturer = escapeCSV(String.valueOf(row[7]));
                String batchCode = escapeCSV(String.valueOf(row[8]));
                Object expiryDateObj = row[9];
                Long quantity = row[10] != null ? ((Number) row[10]).longValue() : 0L;
                String unit = escapeCSV(String.valueOf(row[11]));
                String categoryName = escapeCSV(String.valueOf(row[12]));

                // Format expiry date
                String expiryDate = "";
                if (expiryDateObj != null) {
                    expiryDate = expiryDateObj.toString();
                }

                // Determine status
                String status = determineStatus(expiryDateObj, quantity);

                // Calculate values (cost price might be null in some cases)
                String costPrice = "N/A";
                String totalValue = "N/A";

                // Build CSV row
                csv.append(medicineName).append(",");
                csv.append(activeIngredient).append(",");
                csv.append(strength).append(",");
                csv.append(dosageForm).append(",");
                csv.append(batchCode).append(",");
                csv.append(quantity).append(",");
                csv.append(unit).append(",");
                csv.append(costPrice).append(",");
                csv.append(totalValue).append(",");
                csv.append(expiryDate).append(",");
                csv.append(manufacturer).append(",");
                csv.append(categoryName).append(",");
                csv.append(status).append("\n");
            }

        } catch (Exception e) {
            csv.append("Error generating CSV: ").append(e.getMessage()).append("\n");
        }

        return csv.toString();
    }

    private String escapeCSV(String value) {
        if (value == null || value.equals("null")) {
            return "";
        }
        // Escape quotes and wrap in quotes if contains comma, quote, or newline
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    private String determineStatus(Object expiryDateObj, Long quantity) {
        if (quantity <= 0) {
            return "Out of Stock";
        }

        if (expiryDateObj == null) {
            return "Active";
        }

        try {
            java.time.LocalDate expiryDate;
            if (expiryDateObj instanceof java.time.LocalDate) {
                expiryDate = (java.time.LocalDate) expiryDateObj;
            } else if (expiryDateObj instanceof java.sql.Date) {
                expiryDate = ((java.sql.Date) expiryDateObj).toLocalDate();
            } else {
                expiryDate = java.time.LocalDate.parse(expiryDateObj.toString());
            }

            java.time.LocalDate now = java.time.LocalDate.now();
            java.time.LocalDate thirtyDaysFromNow = now.plusDays(30);

            if (expiryDate.isBefore(now)) {
                return "Expired";
            } else if (expiryDate.isBefore(thirtyDaysFromNow)) {
                return "Near Expiry";
            } else {
                return "Active";
            }
        } catch (Exception e) {
            return "Unknown";
        }
    }

    @Override
    public List<Map<String, Object>> getInventoryDetails(Long branchId) {
        List<Map<String, Object>> result = new java.util.ArrayList<>();

        try {
            List<Object[]> inventoryData = inventoryRepository.findMedicinesByBranch(branchId);

            for (Object[] row : inventoryData) {
                Map<String, Object> item = new HashMap<>();

                // Map data from row to item
                item.put("inventoryId", row[0]);
                item.put("variantId", row[1]);
                item.put("batchId", row[2]);
                item.put("medicineName", row[3] != null ? row[3].toString() : "");
                item.put("activeIngredient", row[4] != null ? row[4].toString() : "");
                item.put("strength", row[5] != null ? row[5].toString() : "");
                item.put("dosageForm", row[6] != null ? row[6].toString() : "");
                item.put("manufacturer", row[7] != null ? row[7].toString() : "");
                item.put("batchCode", row[8] != null ? row[8].toString() : "");
                item.put("expiryDate", row[9]);
                item.put("quantity", row[10] != null ? ((Number) row[10]).longValue() : 0L);
                item.put("unit", row[11] != null ? row[11].toString() : "");
                item.put("categoryName", row[12] != null ? row[12].toString() : "");
                item.put("categoryId", row[13] != null ? ((Number) row[13]).longValue() : null);
                item.put("minStock", row[14] != null ? ((Number) row[14]).longValue() : null);

                result.add(item);
            }

        } catch (Exception e) {
            System.err.println("Error getting inventory details: " + e.getMessage());
        }

        return result;
    }

    @Override
    public List<Map<String, Object>> getCategoryStatistics(Long branchId) {
        List<Map<String, Object>> result = new java.util.ArrayList<>();

        try {
            List<Object[]> stats = inventoryRepository.getCategoryStatistics(branchId);

            for (Object[] row : stats) {
                Map<String, Object> item = new HashMap<>();
                item.put("categoryName", row[0] != null ? row[0].toString() : "");
                item.put("itemCount", row[1] != null ? ((Number) row[1]).intValue() : 0);
                item.put("totalQuantity", row[2] != null ? ((Number) row[2]).longValue() : 0L);
                item.put("totalValue", row[3] != null ? ((Number) row[3]).doubleValue() : 0.0);
                item.put("categoryId", row.length > 4 && row[4] != null ? ((Number) row[4]).longValue() : null);
                result.add(item);
            }

        } catch (Exception e) {
            System.err.println("Error getting category statistics: " + e.getMessage());
        }

        return result;
    }

    @Override
    public List<Map<String, Object>> getAllCategories(Long branchId) {
        List<Map<String, Object>> result = new java.util.ArrayList<>();

        try {
            List<Object[]> categories = inventoryRepository.getAllCategories(branchId);

            for (Object[] row : categories) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", row[0] != null ? ((Number) row[0]).longValue() : 0L);
                item.put("name", row[1] != null ? row[1].toString() : "");
                result.add(item);
            }

        } catch (Exception e) {
            System.err.println("Error getting categories: " + e.getMessage());
        }

        return result;
    }

    @Override
    public List<Map<String, Object>> searchInventory(Long branchId, String query, Long categoryId, String status) {
        List<Map<String, Object>> allItems = getInventoryDetails(branchId);
        List<Map<String, Object>> result = new java.util.ArrayList<>();

        for (Map<String, Object> item : allItems) {
            // Filter by query (search in variant fields)
            if (query != null && !query.trim().isEmpty()) {
                String searchQuery = query.toLowerCase().trim();
                String medicineName = item.get("medicineName").toString().toLowerCase();
                String activeIngredient = item.get("activeIngredient").toString().toLowerCase();
                String strength = item.get("strength").toString().toLowerCase();

                if (!medicineName.contains(searchQuery) &&
                        !activeIngredient.contains(searchQuery) &&
                        !strength.contains(searchQuery)) {
                    continue;
                }
            }

            // Filter by category
            if (categoryId != null && categoryId > 0) {
                Long itemCategoryId = (Long) item.get("categoryId");
                if (itemCategoryId == null || !itemCategoryId.equals(categoryId)) {
                    continue;
                }
            }

            // Filter by status
            if (status != null && !status.trim().isEmpty()) {
                String itemStatus = determineItemStatus(item);
                if (!itemStatus.equalsIgnoreCase(status)) {
                    continue;
                }
            }

            result.add(item);
        }

        return result;
    }

    private String determineItemStatus(Map<String, Object> item) {
        Long quantity = item.get("quantity") != null ? ((Number) item.get("quantity")).longValue() : 0L;

        if (quantity <= 0) {
            return "out-of-stock";
        }

        Object expiryDateObj = item.get("expiryDate");
        if (expiryDateObj != null) {
            try {
                java.time.LocalDate expiryDate;
                if (expiryDateObj instanceof java.time.LocalDate) {
                    expiryDate = (java.time.LocalDate) expiryDateObj;
                } else if (expiryDateObj instanceof java.sql.Date) {
                    expiryDate = ((java.sql.Date) expiryDateObj).toLocalDate();
                } else {
                    expiryDate = java.time.LocalDate.parse(expiryDateObj.toString());
                }

                java.time.LocalDate now = java.time.LocalDate.now();
                java.time.LocalDate thirtyDaysFromNow = now.plusDays(30);

                if (expiryDate.isBefore(now)) {
                    return "expired";
                } else if (expiryDate.isBefore(thirtyDaysFromNow)) {
                    return "near-expiry";
                }
            } catch (Exception e) {
                // Ignore date parsing errors
            }
        }

        return "active";
    }
}
