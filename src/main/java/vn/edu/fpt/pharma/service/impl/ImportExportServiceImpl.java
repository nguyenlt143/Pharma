package vn.edu.fpt.pharma.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.fpt.pharma.constant.MovementType;
import vn.edu.fpt.pharma.repository.InventoryRepository;
import vn.edu.fpt.pharma.repository.RequestFormRepository;
import vn.edu.fpt.pharma.service.ImportExportService;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ImportExportServiceImpl implements ImportExportService {

    private final InventoryRepository inventoryRepository;
    private final RequestFormRepository requestFormRepository;

    @Override
    public Double calculateTotalInventoryValue(Long branchId) {
        try {
            Double total = inventoryRepository.calculateTotalValue(branchId);
            return total != null ? total : 0.0;
        } catch (Exception e) {
            System.err.println("Error calculating total inventory value: " + e.getMessage());
            return 0.0;
        }
    }

    @Override
    public int countLowStock(Long branchId) {
        return inventoryRepository.countLowStockItems(branchId);
    }

    @Override
    public int countPendingInbound(Long branchId) {
        return requestFormRepository.countPendingInboundForBranch(branchId);
    }

    @Override
    public int countPendingOutbound(Long branchId) {
        return requestFormRepository.countPendingOutboundForBranch(branchId);
    }

    @Override
    public String formatCurrencyReadable(Double value) {
        if (value == null || value == 0) return "0 đ";
        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.forLanguageTag("vi-VN"));
        if (value >= 1_000_000_000) {
            return formatter.format(value / 1_000_000_000) + " Tỷ";
        } else if (value >= 1_000_000) {
            return formatter.format(value / 1_000_000) + " Triệu";
        } else if (value >= 1_000) {
            return formatter.format(value / 1_000) + " K";
        }
        return formatter.format(value) + " đ";
    }

    @Override
    public String formatTimeAgo(LocalDateTime dateTime) {
        if (dateTime == null) return "-";
        LocalDateTime now = LocalDateTime.now();
        long minutes = java.time.Duration.between(dateTime, now).toMinutes();

        if (minutes < 1) return "Vừa xong";
        if (minutes < 60) return minutes + " phút trước";

        long hours = minutes / 60;
        if (hours < 24) return hours + " giờ trước";

        long days = hours / 24;
        if (days < 30) return days + " ngày trước";

        long months = days / 30;
        if (months < 12) return months + " tháng trước";

        long years = months / 12;
        return years + " năm trước";
    }

    @Override
    public void medicineVariantOptionalPut(Map<String, Object> detail, Long variantId) {
        try {
            detail.put("variantId", variantId);
            detail.put("variantName", "-");
            detail.put("medicineName", "-");
            detail.put("unit", "-");
        } catch (Exception ignored) {
            detail.put("variantId", variantId);
            detail.put("variantName", "-");
            detail.put("medicineName", "-");
            detail.put("unit", "-");
        }
    }

    @Override
    public String movementTypeLabel(MovementType type) {
        if (type == null) return "-";
        switch (type) {
            case BR_TO_WARE:
                return "Xuất kho";
            case WARE_TO_BR:
                return "Nhập kho";
            case SUP_TO_WARE:
                return "Nhập từ NCC";
            case WARE_TO_SUP:
                return "Trả NCC";
            case DISPOSAL:
                return "Hủy";
            default:
                return type.name();
        }
    }

    @Override
    public String movementTypeClass(MovementType type) {
        if (type == null) return "";
        switch (type) {
            case BR_TO_WARE:
                return "export";
            case WARE_TO_BR:
                return "import";
            case SUP_TO_WARE:
                return "import";
            case WARE_TO_SUP:
                return "export";
            case DISPOSAL:
                return "disposal";
            default:
                return type.name().toLowerCase();
        }
    }
}
