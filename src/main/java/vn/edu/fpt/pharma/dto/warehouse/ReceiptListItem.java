package vn.edu.fpt.pharma.dto.warehouse;

import vn.edu.fpt.pharma.constant.MovementStatus;
import vn.edu.fpt.pharma.constant.MovementType;
import vn.edu.fpt.pharma.entity.InventoryMovement;

import java.time.format.DateTimeFormatter;

public record ReceiptListItem(
        Long id,
        String branchName,
        String createdDate,
        String requestType,
        String status,
        String statusClass
) {
    public ReceiptListItem(InventoryMovement movement, String branchName) {
        this(
                movement.getId(),
                branchName != null ? branchName : "N/A",
                movement.getCreatedAt() != null
                    ? movement.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    : "",
                getRequestTypeLabel(movement.getMovementType()),
                getStatusLabel(movement.getMovementStatus()),
                getStatusClass(movement.getMovementStatus())
        );
    }

    private static String getRequestTypeLabel(MovementType type) {
        if (type == null) return "N/A";
        return switch (type) {
            case SUP_TO_WARE -> "Nhập Hàng";
            case WARE_TO_BR -> "Xuất Hàng";
            case WARE_TO_SUP -> "Trả Hàng";
            case BR_TO_WARE -> "Chuyển về Kho";
            case DISPOSAL -> "Hủy";
            default -> type.name();
        };
    }

    private static String getStatusLabel(MovementStatus status) {
        if (status == null) return "N/A";
        return switch (status) {
            case DRAFT -> "Đang duyệt";
            case APPROVED -> "Chấp nhận";
            case SHIPPED -> "Đang giao";
            case RECEIVED -> "Đã nhận";
            case CANCELLED -> "Từ chối";
            case CLOSED -> "Hoàn tất";
            default -> status.name();
        };
    }

    private static String getStatusClass(MovementStatus status) {
        if (status == null) return "pending";
        return switch (status) {
            case DRAFT -> "pending";
            case APPROVED, RECEIVED, CLOSED -> "approved";
            case CANCELLED -> "rejected";
            case SHIPPED -> "pending";
            default -> "pending";
        };
    }
}

