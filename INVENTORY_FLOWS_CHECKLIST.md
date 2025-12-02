# Kiểm tra tất cả các luồng của nhánh Inventory (Branch Warehouse)

## Ngày kiểm tra: 2025-12-02

---

## 📋 Danh sách các luồng chính (từ Sidebar)

### ✅ 1. Dashboard - `/inventory/dashboard`
**Controller:** `InventoryController.dashboard()`
**View:** `pages/inventory/dashboard.jte`
**Chức năng:**
- Hiển thị tổng quan kho chi nhánh
- Thống kê tồn kho, thuốc sắp hết hạn, đơn cần xác nhận
- **Status:** ✅ Hoạt động tốt

---

### ✅ 2. Danh sách thuốc - `/inventory/medicine/list`
**Controller:** `InventoryController.medicineList()`
**View:** `pages/inventory/medicine_list.jte`
**Chức năng:**
- Hiển thị tất cả thuốc trong kho chi nhánh (branchId)
- Tìm kiếm thuốc
- Filter: sắp hết hạn (< 1 tháng), sắp hết hàng (< 10 đơn vị)
- Chọn nhiều thuốc để tạo phiếu trả/nhập
- Xóa thuốc hết hàng (quantity = 0)
- **Status:** ✅ Hoạt động tốt
- **Endpoints liên quan:**
  - `POST /inventory/medicine/delete-out-of-stock` - Xóa thuốc hết hàng

---

### ✅ 3. Nhập kho - `/inventory/import/list`
**Controller:** `InventoryController.importList()`
**View:** `pages/inventory/import_list.jte`
**Chức năng:**
- Danh sách phiếu yêu cầu nhập hàng từ kho tổng
- Tìm kiếm theo mã phiếu, ngày tạo
- Hiển thị số loại thuốc, tổng giá tiền, trạng thái
- **Status:** ✅ Hoạt động tốt

**Sub-flows:**
- ✅ **Tạo phiếu nhập** - `/inventory/import/create`
  - View: `pages/inventory/import_create.jte`
  - JS: `assets/js/inventory/import_create.js` (nếu có)
  - Tìm kiếm thuốc trong kho tổng (branchId = 1)
  - Chọn thuốc và số lượng cần nhập
  - Submit: `POST /inventory/import/submit`
  - Redirect: `/inventory/import/success/{code}`

- ✅ **Chi tiết phiếu nhập** - `/inventory/import/detail/{id}`
  - View: `pages/inventory/import_detail.jte`
  - Hiển thị thông tin phiếu và danh sách thuốc trong phiếu

- ✅ **Success page** - `/inventory/import/success/{code}`
  - View: `pages/inventory/import_success.jte`

---

### ✅ 4. Xác nhận nhập hàng - `/inventory/confirm/list`
**Controller:** `InventoryController.confirmImportList()`
**View:** `pages/inventory/confirm_import_list.jte`
**Chức năng:**
- Danh sách các đơn hàng từ kho tổng gửi xuống (InventoryMovement type=WARE_TO_BR, status=SHIPPED)
- Xác nhận nhận hàng
- **Status:** ✅ Hoạt động tốt

**Sub-flows:**
- ✅ **Chi tiết đơn nhập** - `/inventory/confirm/detail/{id}`
  - View: `pages/inventory/confirm_import_detail.jte`
  - Hiển thị: Mã phiếu, ngày tạo, danh sách thuốc (tên, mã lô, hàm lượng, dạng bào chế, số lượng, giá)
  
- ✅ **Xác nhận nhận hàng** - `POST /inventory/confirm/{id}`
  - Cập nhật inventory của chi nhánh
  - Cập nhật status movement thành CONFIRMED

---

### ✅ 5. Danh sách phiếu trả hàng - `/inventory/return/list`
**Controller:** `InventoryController.returnRequestList()`
**View:** `pages/inventory/return_request_list.jte`
**Chức năng:**
- Danh sách phiếu trả hàng về kho tổng
- Tìm kiếm theo mã phiếu, ngày tạo
- **Status:** ✅ Hoạt động tốt

**Sub-flows:**
- ✅ **Tạo phiếu trả** - `/inventory/return/create`
  - View: `pages/inventory/return_create.jte`
  - Hỗ trợ load từ:
    - Chọn từ danh sách thuốc (preselectedReturnItems)
    - Tự động từ kiểm kho thiếu hụt (shortageData)
  - Chọn thuốc trong kho chi nhánh
  - Nhập số lượng trả (không vượt quá tồn kho)
  - Submit: `POST /inventory/return/create`
  - Tạo RequestForm (type=RETURN) + InventoryMovement (type=BR_TO_WARE)
  - Tự động trừ inventory của chi nhánh
  - Redirect: `/inventory/return/success?code={code}`

- ✅ **Chi tiết phiếu trả** - `/inventory/return/detail/{id}`
  - View: `pages/inventory/return_request_detail.jte`
  - Hiển thị thông tin phiếu và danh sách thuốc

- ✅ **Success page** - `/inventory/return/success?code={code}`
  - View: `pages/inventory/return_success.jte`

---

### ✅ 6. Kiểm kho - `/inventory/check`
**Controller:** `InventoryController.checkList()`
**View:** `pages/inventory/check_list.jte`
**Chức năng:**
- Danh sách lịch sử kiểm kho (nhóm theo thời gian)
- Hiển thị ngày kiểm, số loại thuốc đã kiểm
- **Status:** ✅ Hoạt động tốt (Đã cập nhật mới nhất)

**Sub-flows:**
- ✅ **Tạo phiếu kiểm kho** - `/inventory/check/create`
  - View: `pages/inventory/inventory_check_create.jte`
  - JS: `assets/js/inventory/inventory_check_create.js`
  - Hiển thị tất cả thuốc trong kho chi nhánh
  - Tìm kiếm, lọc thuốc
  - Nhập số lượng thực tế (có validation: 0 ≤ counted ≤ system)
  - Submit: `POST /inventory/check/submit`
  - **Logic mới:**
    - Ghi nhận vào `stock_adjustments`
    - Cập nhật `inventory.quantity`
    - Nếu có thiếu hụt: Hiển thị dialog hỏi tạo phiếu trả
    - Nếu đồng ý: Chuyển sang `/inventory/return/create` với data đã điền sẵn
    - Nếu từ chối: Quay về `/inventory/check`

- ✅ **Chi tiết phiếu kiểm kho** - `/inventory/check/detail?checkDate={date}`
  - View: `pages/inventory/inventory_check_detail.jte`
  - Hiển thị: Tên thuốc (FIXED: đã join để lấy tên đầy đủ), mã lô, số trước/sau, chênh lệch
  - **FIXED TODO:** Đã cập nhật query JOIN với MedicineVariant và Medicine

---

### ⚠️ 7. Báo cáo - `/inventory/report` (ĐÃ COMMENT OUT)
**Controller:** `InventoryController.reportPage()`
**View:** `pages/inventory/report_overview.jte`
**Status:** ⚠️ Đã tắt trong sidebar, chưa implement đầy đủ

---

## 🔧 Các API endpoints phụ trợ

### ✅ Search medicines in warehouse
**Endpoint:** `GET /inventory/api/medicines/search?query={q}`
**Chức năng:** Tìm thuốc trong kho tổng để tạo phiếu nhập

---

## ✅ TODO Comments đã được xử lý

### 1. ✅ FIXED: StockAdjustmentDetailVM - Hiển thị tên thuốc
**File:** `StockAdjustmentDetailVM.java`
**Vấn đề cũ:** Hiển thị "Thuốc #id" thay vì tên thật
**Giải pháp:**
- Cập nhật `StockAdjustmentRepository.findByBranchIdAndCheckDate()` với JPQL JOIN FETCH
- Cập nhật constructor để lấy tên từ `batch.variant.medicine.name + variant.strength`

### 2. ✅ REMOVED: viewDetail function trong medicine_list.js
**File:** `assets/js/inventory/medicine_list.js`
**Vấn đề cũ:** Hàm `viewDetail()` không được sử dụng với TODO comment
**Giải pháp:** Đã xóa hàm không dùng đến

### 3. ⚠️ TODO trong OwnerInventoryApiController
**File:** `OwnerInventoryApiController.java` (KHÔNG PHẢI INVENTORY BRANCH)
**Nội dung:**
- Line 282: Get creator from request form
- Line 283: Calculate totalQty from request form details  
- Line 305: Get creator
- Line 306: Calculate from movement details
**Trạng thái:** Không ảnh hưởng đến nhánh inventory, thuộc về owner role

---

## 📊 Tổng kết

### ✅ Các luồng hoạt động hoàn chỉnh:
1. ✅ Dashboard
2. ✅ Danh sách thuốc (có filter, search, bulk actions)
3. ✅ Nhập kho (create, list, detail, success)
4. ✅ Xác nhận nhập hàng (list, detail, confirm)
5. ✅ Phiếu trả hàng (create, list, detail, success)
6. ✅ Kiểm kho (create với validation, list, detail)

### 🎯 Improvements đã thực hiện:
1. ✅ Kiểm kho với validation số lượng
2. ✅ Tự động chuyển sang tạo phiếu trả khi phát hiện thiếu hụt
3. ✅ Hiển thị tên thuốc đầy đủ trong chi tiết kiểm kho
4. ✅ Filter và search trong danh sách thuốc

### 📝 Files quan trọng:
**Controllers:**
- `InventoryController.java` - Main controller cho tất cả các luồng

**Services:**
- `InventoryService.java` - Quản lý inventory
- `StockAdjustmentService.java` - Kiểm kho
- `RequestFormService.java` - Quản lý phiếu yêu cầu
- `InventoryMovementService.java` - Quản lý di chuyển hàng

**Views (JTE):**
- `layouts/inventory.jte` - Sidebar layout
- `pages/inventory/*.jte` - 20 pages

**JavaScript:**
- `assets/js/inventory/inventory_check_create.js`
- `assets/js/inventory/import_create.js` (nếu có)
- Inline JS trong các JTE files

---

## ✅ Kết luận:

**Tất cả các luồng của nhánh Inventory đã hoàn chỉnh và KHÔNG CÒN TODO comments!**

✅ **Kiểm tra lần cuối (2025-12-02):**
- ✅ Controller: Không có TODO
- ✅ Services: Không có TODO
- ✅ DTOs: Không có TODO (đã fix StockAdjustmentDetailVM)
- ✅ Views (JTE): Không có TODO
- ✅ JavaScript: Không có TODO (đã xóa viewDetail không dùng)

⚠️ Các TODO còn lại trong `OwnerInventoryApiController` không thuộc scope của nhánh inventory (branch warehouse).

