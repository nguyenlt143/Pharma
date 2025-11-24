# Tóm Tắt Tính Năng "Tạo Phiếu Xuất Kho"

## Tổng Quan
Đã hoàn thành việc tạo trang "Tạo Phiếu Xuất Kho" cho phép kho tổng xuất hàng đến chi nhánh dựa trên yêu cầu nhập hàng từ chi nhánh.

## Các File Đã Tạo/Chỉnh Sửa

### 1. Backend

#### DTO - ExportCreateDTO.java
**Đường dẫn:** `src/main/java/vn/edu/fpt/pharma/dto/warehouse/ExportCreateDTO.java`

DTO chứa thông tin cần thiết để tạo phiếu xuất:
- `requestId`: ID yêu cầu gốc
- `branchId`: ID chi nhánh nhận
- `branchName`: Tên chi nhánh
- `createdDate`: Ngày tạo phiếu
- `note`: Ghi chú
- `medicines`: Danh sách thuốc với các lô hàng

Cấu trúc nested:
- `MedicineWithBatches`: Thông tin thuốc + danh sách lô
- `BatchInfo`: Thông tin lô hàng (mã lô, SL tồn, giá bán)

#### Service Interface
**File:** `src/main/java/vn/edu/fpt/pharma/service/RequestFormService.java`

Đã thêm method:
```java
ExportCreateDTO prepareExportCreation(Long requestId);
```

#### Service Implementation
**File:** `src/main/java/vn/edu/fpt/pharma/service/impl/RequestFormServiceImpl.java`

Đã implement `prepareExportCreation()`:
1. Lấy thông tin request form
2. Lấy thông tin chi nhánh
3. Lấy chi tiết yêu cầu (thuốc + số lượng)
4. Tìm kho tổng (HEAD_QUARTER)
5. Với mỗi thuốc:
   - Tìm inventory có sẵn trong kho
   - Sắp xếp theo ngày hết hạn (FEFO)
   - Lấy giá bán chi nhánh từ bảng prices
   - Build danh sách lô hàng

#### Controller
**File:** `src/main/java/vn/edu/fpt/pharma/controller/wareHouse/WarehouseController.java`

Đã thêm endpoint:
```java
@GetMapping("/warehouse/export/create")
public String exportCreate(@RequestParam(required = false) Long requestId, Model model)
```

- Nhận `requestId` từ query parameter
- Gọi service để chuẩn bị dữ liệu
- Truyền data vào model
- Render trang export_create.jte

### 2. Frontend

#### JTE Template
**File:** `src/main/jte/pages/warehouse/export_create.jte`

Cấu trúc theo yêu cầu trong PhieuXuat.txt:

1. **Thông tin chung:**
   - Chi nhánh nhận (readonly, từ request)
   - Ngày tạo phiếu (date picker)
   - Ghi chú (textarea)

2. **Bảng danh sách thuốc & lô hàng:**
   - Dòng cha (medicine-row): STT, Tên thuốc, ĐVT, Hàm lượng, SL yêu cầu
   - Dòng con (batch-row): Số lô, SL tồn kho, Giá bán, Input SL gửi
   - Sử dụng rowspan để merge cells của dòng cha

3. **Tổng tiền:**
   - Hiển thị tổng số tiền tự động tính

4. **Nút chức năng:**
   - Lưu nháp
   - Tạo phiếu xuất

#### JavaScript
**File:** `src/main/resources/static/assets/js/warehouse/export_create.js`

Chức năng chính:
- `initializeForm()`: Khởi tạo form, set ngày hiện tại
- `initializeTable()`: Khởi tạo event listeners cho input số lượng
- `validateQuantity()`: Validate số lượng (không âm, không vượt quá tồn kho)
- `calculateTotals()`: Tính tổng tiền = Σ(SL gửi × Giá bán)
- `createExport()`: Thu thập dữ liệu và gửi lên server
- `validateRequestedQuantities()`: Kiểm tra tổng SL xuất không vượt SL yêu cầu

#### CSS
**File:** `src/main/resources/static/assets/css/warehouse/export_create.css`

Styling:
- Layout responsive với grid system
- Phân biệt medicine-row và batch-row bằng màu sắc
- batch-row thụt lề để thể hiện cấp bậc
- Input số lượng có border highlight khi focus
- Tổng tiền hiển thị nổi bật với màu xanh
- Toast notification cho thông báo

### 3. Integration

#### request_detail.js
**File:** `src/main/resources/static/assets/js/warehouse/request_detail.js`

Đã cập nhật function `createExportSlip()`:
```javascript
function createExportSlip(id) {
    window.location.href = `/warehouse/export/create?requestId=${id}`;
}
```

Khi click nút "Tạo Phiếu Xuất" ở trang request_detail:
1. Chuyển đến `/warehouse/export/create?requestId={id}`
2. Controller nhận requestId
3. Service load dữ liệu từ request
4. Hiển thị form với thông tin đã điền sẵn

## Flow Hoạt Động

```
Request Detail Page
    ↓ (Click "Tạo Phiếu Xuất")
    ↓
/warehouse/export/create?requestId=123
    ↓
WarehouseController.exportCreate()
    ↓
RequestFormService.prepareExportCreation()
    ↓ (Query DB)
    ├─ RequestForm (branch info, note)
    ├─ RequestDetails (medicines, quantities)
    ├─ Inventory (available batches in warehouse)
    └─ Prices (branch prices)
    ↓
Build ExportCreateDTO
    ↓
Render export_create.jte
    ↓
User nhập số lượng cho từng lô
    ↓
JavaScript validate & calculate totals
    ↓
Click "Tạo phiếu xuất"
    ↓
(TODO: API call to create InventoryMovement)
```

## Cấu Trúc Dữ Liệu

### ExportCreateDTO
```json
{
  "requestId": 123,
  "branchId": 2,
  "branchName": "Hoàng Mai",
  "createdDate": "2024-05-21",
  "note": "Yêu cầu gấp",
  "medicines": [
    {
      "variantId": 1,
      "medicineName": "Amoxicillin",
      "unit": "Hộp",
      "concentration": "250mg",
      "requestedQuantity": 1000,
      "batches": [
        {
          "inventoryId": 10,
          "batchId": 5,
          "batchCode": "23qw1",
          "availableQuantity": 200,
          "branchPrice": 2000.0,
          "quantityToSend": 0
        }
      ]
    }
  ]
}
```

## Validation Rules

1. **Số lượng gửi:**
   - Không được âm
   - Không vượt quá số lượng tồn kho của lô
   - Tổng SL gửi của một thuốc không vượt SL yêu cầu

2. **Form:**
   - Phải có chi nhánh nhận
   - Ít nhất một lô có SL gửi > 0

## Tính Năng Chưa Hoàn Thành

1. **API Endpoint để lưu phiếu xuất:**
   - POST /warehouse/export/create
   - Tạo InventoryMovement (type: WARE_TO_BR)
   - Tạo InventoryMovementDetails
   - Cập nhật inventory

2. **Lưu nháp:**
   - Lưu trạng thái DRAFT
   - Cho phép chỉnh sửa sau

3. **Print phiếu xuất:**
   - Generate PDF
   - In phiếu xuất kho

## Testing

### Build Status
✅ Build successful (gradlew build -x test)

### Các trường hợp cần test:
1. Mở trang từ request detail với requestId hợp lệ
2. Hiển thị đúng thông tin chi nhánh, thuốc, lô hàng
3. Nhập số lượng và tính tổng tiền đúng
4. Validate số lượng không vượt tồn kho
5. Validate tổng SL không vượt SL yêu cầu
6. Submit form thành công

## Lưu Ý Kỹ Thuật

1. **JTE Template Syntax:**
   - Phải dùng `""` thay vì `''` cho empty string
   - Convert Long/LocalDate sang String: `String.valueOf()`, `.toString()`
   - Dùng `!{var x = ...}` để khai báo biến local

2. **Hibernate Lazy Loading:**
   - Branch, MedicineVariant fetch EAGER cho các association cần thiết
   - Tránh N+1 query problem

3. **Price Logic:**
   - Ưu tiên: Branch-specific price → Global price → Default 0
   - Check valid date range (startDate, endDate)

4. **FEFO (First Expiry First Out):**
   - Sort batches theo expiryDate tăng dần
   - Gợi ý user xuất lô sắp hết hạn trước

## Tài Liệu Tham Khảo

- PhieuXuat.txt: Mô tả UI/UX
- flow.txt: Business logic, pricing rules
- Entity diagrams: Inventory, Batch, Price, InventoryMovement

---
**Ngày tạo:** 23/11/2024
**Phiên bản:** 1.0
**Trạng thái:** Đã hoàn thành UI + Backend service, chưa hoàn thành API submission

