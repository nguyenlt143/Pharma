# Hướng dẫn xử lý Unit Price trong Inventory

## Tổng quan
Sau khi thêm cột `unitPrice` vào entity `Inventory`, cần đảm bảo rằng giá này được cập nhật đúng khi có các giao dịch di chuyển hàng hóa (inventory movements).

## Quy tắc cập nhật Unit Price

### 1. Kho tổng (Branch ID = 1)
- `unitPrice` = 0 (hoặc không áp dụng)
- Kho tổng chỉ là nơi trung chuyển, không bán hàng

### 2. Chi nhánh (Branch ID != 1)

#### Khi nhận hàng từ kho tổng (WARE_TO_BR - Movement Type)
```java
// Khi movement có status = RECEIVED
// Cập nhật inventory.unitPrice = inventoryMovementDetail.price
// Với điều kiện:
// - movement.movementType = WARE_TO_BR
// - movement.destinationBranchId = branchId
// - movement.movementStatus = RECEIVED
```

#### Logic cập nhật:
```java
public void updateInventoryAfterMovement(InventoryMovement movement) {
    if (movement.getMovementType() == MovementType.WARE_TO_BR 
        && movement.getMovementStatus() == MovementStatus.RECEIVED) {
        
        Long branchId = movement.getDestinationBranchId();
        
        for (InventoryMovementDetail detail : movement.getDetails()) {
            Inventory inventory = inventoryRepository
                .findByBranchIdAndVariantIdAndBatchId(
                    branchId, 
                    detail.getVariant().getId(), 
                    detail.getBatch().getId()
                );
            
            if (inventory == null) {
                // Tạo mới inventory record
                inventory = Inventory.builder()
                    .branchId(branchId)
                    .variantId(detail.getVariant().getId())
                    .batch(detail.getBatch())
                    .quantity(detail.getQuantity())
                    .unitPrice(detail.getPrice().intValue()) // Lấy price từ movement detail
                    .lastMovementId(movement.getId())
                    .build();
            } else {
                // Cập nhật inventory hiện tại
                inventory.setQuantity(inventory.getQuantity() + detail.getQuantity());
                inventory.setUnitPrice(detail.getPrice().intValue()); // Cập nhật giá mới
                inventory.setLastMovementId(movement.getId());
            }
            
            inventoryRepository.save(inventory);
        }
    }
}
```

## Các trường hợp khác

### BR_TO_WARE (Chi nhánh trả hàng về kho)
- Không cập nhật `unitPrice` của chi nhánh
- Chỉ giảm số lượng tồn kho tại chi nhánh

### SUP_TO_WARE (Nhà cung cấp giao hàng cho kho)
- Không ảnh hưởng đến `unitPrice` của chi nhánh
- Chỉ cập nhật kho tổng

### DISPOSAL (Hủy hàng)
- Không cập nhật `unitPrice`
- Chỉ giảm số lượng tồn kho

## Repository Methods cần implement

```java
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    // Tìm inventory theo branch, variant, và batch
    Optional<Inventory> findByBranchIdAndVariantIdAndBatchId(
        Long branchId, 
        Long variantId, 
        Long batchId
    );
    
    // Các method khác đã có...
}
```

## Các file đã được cập nhật

1. **Entity: Inventory.java**
   - Thêm field `unitPrice` (type: int)
   - Comment giải thích nguồn gốc giá

2. **Repository: InventoryRepository.java**
   - Cập nhật query `sumInventoryValueByBranch` để sử dụng `unit_price`
   - Cập nhật query `sumTotalInventoryValue` để sử dụng `unit_price`
   - Thêm method `countAllLowStock()` cho dashboard tổng quan

3. **Service: InventoryReportService.java**
   - Cập nhật `getInventorySummary()` để yêu cầu branchId
   - Cập nhật `generateInventoryCsv()` để bao gồm unitPrice và totalValue

4. **Service: DashboardServiceImpl.java**
   - Sử dụng `countAllLowStock()` thay vì `countLowStock()`

5. **Data: data.sql**
   - Cập nhật INSERT statements để bao gồm cột `unit_price`

## Lưu ý quan trọng

1. **Tất cả các query và method liên quan đến inventory đều phải truyền `branchId`**
   - Không có inventory chung cho tất cả chi nhánh
   - Mỗi chi nhánh quản lý inventory riêng

2. **Unit Price vs Cost Price**
   - `unitPrice` = giá bán/giá nhánh (branch_price) từ movement detail
   - `snapCost` = giá vốn tại thời điểm movement
   - Sử dụng `unitPrice` để tính giá trị tồn kho tại chi nhánh

3. **Khi tính tổng giá trị tồn kho**
   ```sql
   SELECT SUM(quantity * unit_price) FROM inventory WHERE branch_id = ?
   ```

## TODO - Cần implement

- [ ] Service xử lý inventory movements và tự động cập nhật unitPrice
- [ ] Repository method: `findByBranchIdAndVariantIdAndBatchId`
- [ ] Test cases cho việc cập nhật unitPrice
- [ ] Validation để đảm bảo unitPrice > 0 cho chi nhánh (branch_id != 1)

