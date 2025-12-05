# 🎯 Migration Endpoint - Quick Reference

## 📍 Vị trí Endpoint

**File:** `AdminAccountApiController.java`

**Endpoint:** 
```
POST /api/admin/accounts/unit-conversion-migration
```

## 🚀 Cách Gọi

### Option 1: cURL
```bash
curl -X POST http://localhost:8080/api/admin/accounts/unit-conversion-migration
```

### Option 2: Postman
- **Method:** POST
- **URL:** `http://localhost:8080/api/admin/accounts/unit-conversion-migration`
- **Body:** (không cần)

### Option 3: JavaScript/Fetch
```javascript
fetch('/api/admin/accounts/unit-conversion-migration', {
    method: 'POST'
})
.then(response => response.json())
.then(data => console.log(data));
```

## 📦 Response

### Thành công (200 OK):
```json
{
  "success": true,
  "message": "✅ Migration hoàn tất! Tất cả MedicineVariant đã được xử lý. Xem console để biết chi tiết."
}
```

### Lỗi (500 Internal Server Error):
```json
{
  "success": false,
  "message": "❌ Lỗi trong quá trình migration: [chi tiết lỗi]"
}
```

## 📋 Chức năng

Endpoint này sẽ:
1. Lấy tất cả `MedicineVariant` từ database
2. Với mỗi variant, tạo 2 `UnitConversion`:
   - `(variant_id, baseUnitId, 1.0)`
   - `(variant_id, packageUnitId, quantityPerPackage)`
3. Chỉ tạo nếu chưa tồn tại (tránh duplicate)
4. In chi tiết ra console

## ⚠️ Lưu ý

- **Chỉ chạy MỘT LẦN** khi deploy lần đầu
- **Idempotent:** Có thể chạy lại an toàn nếu cần
- **Xem console** để biết chi tiết số lượng records được tạo
- **Không ảnh hưởng** đến unit conversions đã tồn tại

## ✅ Sau khi chạy

Các tính năng tự động sẽ hoạt động:
- ✅ Tạo variant mới → Tự động tạo unit conversions
- ✅ Update variant → Tự động cập nhật nếu unit thay đổi
- ✅ Không cần chạy migration nữa

