# Cập nhật chức năng kiểm kho (Inventory Check)

## Ngày: 2025-12-02

## Tóm tắt thay đổi

### 1. Validation số lượng kiểm kho

#### Backend (`StockAdjustmentServiceImpl.java`)
- **Validate số lượng không được âm**: Kiểm tra `countedQuantity >= 0`
- **Validate số lượng không được vượt quá tồn kho**: Kiểm tra `countedQuantity <= systemQuantity`
- Ném exception với thông báo rõ ràng nếu vi phạm

#### Frontend (`inventory_check_create.js` & `inventory_check_create.jte`)
- Thêm thuộc tính `max="${it.system}"` cho input số lượng
- JavaScript validate realtime khi người dùng nhập
- Hiển thị cảnh báo nếu nhập quá số tồn kho

### 2. Tự động tạo phiếu trả hàng cho số thiếu

#### Luồng hoạt động mới:

```
Kiểm kho → Phát hiện thiếu → Xác nhận → Chuyển sang tạo phiếu trả
```

#### Controller (`InventoryController.java`)
- Endpoint `/inventory/check/submit` trả về thông tin shortage
- Response bao gồm:
  ```json
  {
    "success": true,
    "hasShortage": true/false,
    "shortageItems": [
      {
        "inventoryId": 123,
        "variantId": 456,
        "shortage": 10
      }
    ]
  }
  ```

#### Frontend (`inventory_check_create.js`)
- Xử lý response từ backend
- Nếu có thiếu hụt: Hiển thị confirm dialog
- Nếu người dùng đồng ý: Chuyển sang `/inventory/return/create` với dữ liệu shortage
- Lưu shortage data vào `sessionStorage`

#### Trang tạo phiếu trả (`return_create.jte`)
- Tự động load shortage data từ `sessionStorage`
- Tự động điền các thuốc bị thiếu vào danh sách
- Hiển thị thông báo cho người dùng biết
- Người dùng có thể điều chỉnh trước khi submit

### 3. Luồng kiểm kho không thay đổi

- Vẫn ghi nhận vào bảng `stock_adjustments`
- Vẫn cập nhật `inventory.quantity` với số thực tế
- Không tự động tạo RequestForm, chỉ chuyển hướng để người dùng xác nhận

## Files đã thay đổi

1. **StockAdjustmentServiceImpl.java**
   - Thêm validation
   - Giữ nguyên logic cập nhật inventory

2. **InventoryController.java**
   - Cập nhật endpoint `/check/submit` để trả về shortage info

3. **inventory_check_create.js**
   - Thêm validation frontend
   - Xử lý response và chuyển hướng

4. **return_create.jte**
   - Hỗ trợ load shortage data từ sessionStorage
   - Tự động điền thuốc bị thiếu

## Testing Checklist

- [ ] Kiểm kho với số lượng âm → Hiển thị lỗi
- [ ] Kiểm kho với số lượng > tồn kho → Hiển thị lỗi
- [ ] Kiểm kho không có thiếu → Hoàn thành bình thường
- [ ] Kiểm kho có thiếu → Hiển thị confirm dialog
- [ ] Đồng ý tạo phiếu trả → Chuyển sang trang tạo phiếu với dữ liệu đã điền
- [ ] Từ chối tạo phiếu trả → Quay về danh sách kiểm kho
- [ ] Dữ liệu shortage được load đúng vào form tạo phiếu trả

## Lưu ý

- Frontend validation chỉ là layer đầu tiên, backend vẫn validate đầy đủ
- SessionStorage được clear sau khi load để tránh duplicate
- Người dùng vẫn có thể điều chỉnh số lượng trả trước khi submit

