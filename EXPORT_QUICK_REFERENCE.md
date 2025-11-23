# Quick Reference: Export Creation Feature

## 🚀 How to Use

### From Request Detail Page:

1. **Navigate to Request Detail:**
   ```
   GET /warehouse/request/detail?id={requestId}
   ```

2. **Click "Tạo Phiếu Xuất" Button:**
   - JavaScript redirects to: `/warehouse/export/create?requestId={id}`

3. **Fill Export Form:**
   - Chi nhánh nhận: Auto-filled (readonly)
   - Ngày tạo: Auto-filled (editable)
   - Ghi chú: From request (editable)
   - Danh sách thuốc: Auto-loaded with batches

4. **Enter Quantities:**
   - Input số lượng cho từng lô
   - Real-time validation
   - Auto-calculate total

5. **Submit:**
   - Click "Tạo phiếu xuất"
   - (TODO: API call to save)

---

## 🔗 API Endpoints

### Load Export Form
```
GET /warehouse/export/create?requestId={id}
```

**Response:** Renders JTE template with ExportCreateDTO

**Example:**
```
/warehouse/export/create?requestId=123
→ Shows form with branch "Hoàng Mai", medicines, batches
```

---

## 📝 Data Structure

### ExportCreateDTO
```java
{
  requestId: Long,           // ID của request gốc
  branchId: Long,           // ID chi nhánh nhận
  branchName: String,       // Tên chi nhánh (hiển thị)
  createdDate: LocalDate,   // Ngày tạo phiếu
  note: String,             // Ghi chú
  medicines: [
    {
      variantId: Long,
      medicineName: String,
      unit: String,
      concentration: String,
      requestedQuantity: Long,
      batches: [
        {
          inventoryId: Long,    // ID inventory record
          batchId: Long,        // ID batch
          batchCode: String,    // Mã lô
          availableQuantity: Long,  // SL tồn kho
          branchPrice: Double,  // Giá bán cho chi nhánh
          quantityToSend: Long  // SL xuất (user input)
        }
      ]
    }
  ]
}
```

---

## 🎯 Business Logic

### Price Selection
```java
1. Branch-specific price (exact branchId + valid date range)
   ↓ if not found
2. Global price (branchId = null + valid date range)
   ↓ if not found
3. Default: 0.0
```

### Batch Sorting (FEFO)
```java
// First Expiry First Out
batches.sort(by: expiryDate ASC)
```

### Inventory Source
```java
// Find warehouse branch
Branch warehouse = branches.find(type == HEAD_QUARTER)

// Get inventory for variant in warehouse
inventory.filter(
  variantId = {variantId},
  branchId = warehouse.id,
  quantity > 0
)
```

---

## ✅ Validation

### Client-side (JavaScript)

```javascript
// 1. Quantity >= 0
if (quantity < 0) {
  error("Số lượng không được âm")
}

// 2. Quantity <= Available
if (quantity > batch.availableQuantity) {
  error("Vượt quá số lượng tồn kho")
}

// 3. Total <= Requested
totalSent = sum(batches.quantityToSend)
if (totalSent > medicine.requestedQuantity) {
  error("Vượt quá số lượng yêu cầu")
}

// 4. At least one batch has quantity > 0
if (all batches have quantityToSend == 0) {
  error("Vui lòng nhập số lượng")
}
```

---

## 🗂️ File Locations

```
Backend:
├── dto/warehouse/ExportCreateDTO.java          (DTO)
├── service/RequestFormService.java             (Interface)
├── service/impl/RequestFormServiceImpl.java    (Implementation)
└── controller/wareHouse/WarehouseController.java (Controller)

Frontend:
├── jte/pages/warehouse/export_create.jte       (Template)
├── static/assets/js/warehouse/export_create.js (JavaScript)
├── static/assets/js/warehouse/request_detail.js (Modified)
└── static/assets/css/warehouse/export_create.css (Styles)
```

---

## 🧪 Manual Testing Steps

### Prerequisites:
1. Database has:
   - Request form with status CONFIRMED
   - Request details (thuốc + số lượng)
   - Inventory in warehouse (HEAD_QUARTER branch)
   - Price records for variants

### Test Case 1: Navigate from Request Detail
```
1. Go to /warehouse/request/detail?id=1
2. Click "Tạo Phiếu Xuất"
3. ✅ Should redirect to /warehouse/export/create?requestId=1
4. ✅ Form should show branch name
5. ✅ Table should show medicines and batches
```

### Test Case 2: Quantity Validation
```
1. Enter -10 → ❌ Error
2. Enter 0 → ✅ OK
3. Enter 99999 (> available) → ⚠️ Warning, auto-correct to max
4. Enter valid number → ✅ OK
```

### Test Case 3: Total Calculation
```
1. Batch 1: qty=100, price=2000 → subtotal=200,000
2. Batch 2: qty=50, price=2100 → subtotal=105,000
3. ✅ Total should show: 305,000
```

### Test Case 4: Form Submission
```
1. Fill all fields
2. Enter quantities for at least one batch
3. Click "Tạo phiếu xuất"
4. ✅ Should show loading toast
5. ⏳ TODO: Actually create inventory movement
```

---

## 🐛 Known Issues

1. **Port 8080 already in use:**
   - Stop existing Spring Boot instance
   - Or change port in application.yaml

2. **Package name warning:**
   - Package: `vn.edu.fpt.pharma.controller.warehouse`
   - Folder: `vn/edu/fpt/pharma/controller/wareHouse`
   - Non-critical, build still succeeds

3. **View resolution warnings:**
   - IDE cannot find some JTE files
   - Actual runtime should work fine

---

## 🔜 Next Steps

### 1. Create POST Endpoint
```java
@PostMapping("/warehouse/export/create")
public ResponseEntity<?> submitExport(@RequestBody ExportSubmitDTO dto) {
    // Create InventoryMovement
    // Create InventoryMovementDetails
    // Update inventory
    return ResponseEntity.ok(movementId);
}
```

### 2. Update Frontend
```javascript
function createExport() {
    fetch('/warehouse/export/create', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(exportData)
    })
    .then(response => {
        if (response.ok) {
            showToast('Tạo phiếu xuất thành công!', 'success');
            redirectToMovementList();
        }
    });
}
```

### 3. Add Draft Feature
```java
@PostMapping("/warehouse/export/draft")
public ResponseEntity<?> saveDraft(@RequestBody ExportSubmitDTO dto) {
    // Save with status = DRAFT
}
```

---

## 📞 Support

**Documentation:**
- EXPORT_CREATION_SUMMARY.md
- PhieuXuat.txt (original requirements)
- flow.txt (business logic)

**Key Concepts:**
- FEFO (First Expiry First Out)
- Branch Price vs Sale Price
- Inventory layers (cost tracking)

---

**Last Updated:** 23/11/2024  
**Status:** ✅ UI Complete, ⏳ API Pending

