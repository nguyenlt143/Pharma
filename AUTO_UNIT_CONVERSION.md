# Tài liệu Auto-Create UnitConversion

## 📋 Tổng quan

Hệ thống đã được cấu hình để **TỰ ĐỘNG** tạo `UnitConversion` từ `MedicineVariant` trong các trường hợp sau:

## 🎯 Các Trường Hợp Tự Động Tạo

### 1️⃣ Khi Tạo Mới Variant
**Trigger:** Gọi `POST /api/.../variants` hoặc `createVariant()`

**Hành vi:**
```java
MedicineVariant variant = save(newVariant);
createUnitConversionsFromVariant(variant); // ✅ Tự động gọi
```

**Kết quả:**
- Tạo 2 bản ghi `UnitConversion`:
  - `(variant_id, baseUnitId, 1.0)`
  - `(variant_id, packageUnitId, quantityPerPackage)`

**Ví dụ:**
```json
POST /api/variants
{
  "medicineId": 1,
  "baseUnitId": 1,      // Viên
  "packageUnitId": 2,    // Hộp
  "quantityPerPackage": 100
}
```

→ Tự động tạo:
- `(variant, Viên, 1.0)`
- `(variant, Hộp, 100.0)`

---

### 2️⃣ Khi Update Variant (Nếu Unit Thay Đổi)
**Trigger:** Gọi `PUT /api/.../variants/{id}` với unit thay đổi

**Điều kiện kích hoạt:**
- `baseUnitId` thay đổi, HOẶC
- `packageUnitId` thay đổi, HOẶC
- `quantityPerPackage` thay đổi

**Hành vi:**
```java
if (unitsChanged) {
    // Xóa unit conversions cũ của base/package unit
    deleteOldUnitConversions();
    
    // Tạo lại unit conversions mới
    createUnitConversionsFromVariant(variant); // ✅ Tự động gọi
}
```

**Ví dụ:**
```json
PUT /api/variants/123
{
  "quantityPerPackage": 50  // Thay đổi từ 100 → 50
}
```

→ Tự động:
1. Xóa unit conversion cũ `(variant, Hộp, 100.0)`
2. Tạo unit conversion mới `(variant, Hộp, 50.0)`

---

### 3️⃣ Migrate DB Lần Đầu (Manual)
**Trigger:** Gọi API migration một lần duy nhất

**Endpoint:** `POST /api/admin/unit-conversion-migration/migrate-all`

**Hành vi:**
```java
List<MedicineVariant> allVariants = findAll();
for (variant : allVariants) {
    createUnitConversionsFromVariant(variant); // ✅ Gọi cho tất cả
}
```

**Khi nào cần:**
- Lần đầu deploy code này
- Khi có dữ liệu MedicineVariant cũ chưa có UnitConversion
- Chỉ chạy **MỘT LẦN**

**Cách chạy:**
```bash
curl -X POST http://localhost:8080/api/admin/unit-conversion-migration/migrate-all
```

---

## 🔄 Luồng Hoạt Động Chi Tiết

### Luồng 1: Tạo Mới Variant
```
User → POST /variants
  ↓
createVariant(request)
  ↓
save(variant)
  ↓
createUnitConversionsFromVariant(variant) ← ✅ TỰ ĐỘNG
  ↓
  ├→ Kiểm tra baseUnit tồn tại?
  │   └→ Chưa → Tạo (variant, baseUnit, 1.0)
  │
  └→ Kiểm tra packageUnit tồn tại?
      └→ Chưa → Tạo (variant, packageUnit, quantityPerPackage)
```

### Luồng 2: Update Variant
```
User → PUT /variants/{id}
  ↓
updateVariant(id, request)
  ↓
Kiểm tra unit có thay đổi?
  ↓
  ├→ Có thay đổi
  │   ↓
  │   Xóa unit conversions cũ (base/package)
  │   ↓
  │   createUnitConversionsFromVariant(variant) ← ✅ TỰ ĐỘNG
  │
  └→ Không thay đổi
      └→ Giữ nguyên unit conversions
```

### Luồng 3: Migration
```
Admin → POST /migrate-all
  ↓
migrateAllVariantsToUnitConversions()
  ↓
for each MedicineVariant:
  ↓
  createUnitConversionsFromVariant(variant) ← ✅ TỰ ĐỘNG
  ↓
Log kết quả ra console
```

---

## 🛡️ Chống Duplicate

Hàm `createUnitConversionsFromVariant()` có cơ chế chống duplicate:

```java
// Lấy unit conversions hiện có
List<UnitConversion> existing = findByVariantId(variantId);

// Chỉ tạo nếu CHƯA tồn tại
if (!exists(baseUnit)) {
    create(baseUnit);
}

if (!exists(packageUnit)) {
    create(packageUnit);
}
```

→ **An toàn** khi gọi nhiều lần!

---

## 📊 Test Cases

### Test 1: Tạo mới variant
```java
@Test
void testCreateVariant_ShouldAutoCreateUnitConversions() {
    // Given
    MedicineVariantRequest request = new MedicineVariantRequest();
    request.setBaseUnitId(1L);
    request.setPackageUnitId(2L);
    request.setQuantityPerPackage(100.0);
    
    // When
    MedicineVariantResponse response = service.createVariant(request);
    
    // Then
    List<UnitConversion> conversions = 
        unitConversionRepo.findByVariantIdId(response.getId());
    
    assertThat(conversions).hasSize(2);
    assertThat(conversions).extracting("multiplier")
        .containsExactlyInAnyOrder(1.0, 100.0);
}
```

### Test 2: Update variant với unit thay đổi
```java
@Test
void testUpdateVariant_WhenUnitsChanged_ShouldRecreateUnitConversions() {
    // Given
    Long variantId = 1L;
    MedicineVariantRequest request = new MedicineVariantRequest();
    request.setQuantityPerPackage(50.0); // Changed from 100 to 50
    
    // When
    service.updateVariant(variantId, request);
    
    // Then
    List<UnitConversion> conversions = 
        unitConversionRepo.findByVariantIdId(variantId);
    
    assertThat(conversions).anyMatch(c -> c.getMultiplier() == 50.0);
}
```

### Test 3: Migration toàn bộ
```java
@Test
void testMigrateAll_ShouldCreateUnitConversionsForAllVariants() {
    // Given
    int totalVariants = variantRepo.count();
    
    // When
    service.migrateAllVariantsToUnitConversions();
    
    // Then
    int totalConversions = unitConversionRepo.count();
    assertThat(totalConversions).isGreaterThanOrEqualTo(totalVariants);
}
```

---

## ⚙️ Configuration

Không cần cấu hình gì thêm! Hệ thống tự động hoạt động khi:

✅ Spring Boot khởi động
✅ Service được inject
✅ Transaction được quản lý bởi `@Transactional`

---

## 🚨 Lưu Ý Quan Trọng

### ⚠️ Khi nào UnitConversion KHÔNG được tạo?

1. **baseUnitId = null**: Không tạo base unit conversion
2. **packageUnitId = null**: Không tạo package unit conversion
3. **quantityPerPackage = null**: Không tạo package unit conversion
4. **Đã tồn tại**: Bỏ qua, không tạo duplicate

### ⚠️ Khi update variant

- **Chỉ xóa và tạo lại** nếu unit thay đổi
- **Giữ nguyên** nếu unit không đổi
- **Additional conversions** (nếu có) được xử lý riêng

### ⚠️ Migration

- **Chạy một lần** sau khi deploy code mới
- **Idempotent**: Có thể chạy lại an toàn
- **Không xóa** unit conversions hiện có

---

## 📝 Checklist Deploy

Khi deploy hệ thống lần đầu:

- [ ] Deploy code mới
- [ ] Khởi động ứng dụng
- [ ] Gọi API migration: `POST /api/admin/unit-conversion-migration/migrate-all`
- [ ] Kiểm tra console log
- [ ] Verify database: `SELECT COUNT(*) FROM unit_conversions`
- [ ] Test tạo variant mới
- [ ] Test update variant
- [ ] ✅ Hoàn tất!

---

## 🎉 Kết luận

Từ giờ, bạn **KHÔNG CẦN** gọi thủ công `createUnitConversionsFromVariant()` nữa!

Hệ thống tự động xử lý:
- ✅ Tạo mới → Tự động tạo conversions
- ✅ Update → Tự động cập nhật nếu cần
- ✅ Migration → Chỉ gọi API một lần

**Enjoy! 🚀**

