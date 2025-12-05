# 🚀 Hướng Dẫn Chạy Migration - Chi Tiết

## ❓ Tại sao PowerShell không chạy?

Có 3 nguyên nhân chính:

### 1. **Ứng dụng chưa khởi động**
   - Migration endpoint chỉ hoạt động khi server đang chạy
   - Kiểm tra: Mở browser vào `http://localhost:8080`

### 2. **Cần đăng nhập (Authentication)**
   - Endpoint yêu cầu quyền ADMIN
   - PowerShell không có cookie/session

### 3. **CORS hoặc Security Policy**
   - Spring Security có thể chặn request không có authentication

---

## ✅ GIẢI PHÁP - 3 Cách Chạy Migration

### 🥇 **Cách 1: Dùng Browser (ĐỀ XUẤT)**

**Bước 1:** Đăng nhập vào hệ thống với tài khoản ADMIN

**Bước 2:** Mở trang migration:
```
http://localhost:8080/migration.html
```

**Bước 3:** Nhấn nút "Chạy Migration"

✅ **Ưu điểm:**
- Đơn giản, trực quan
- Tự động có authentication
- Hiển thị kết quả rõ ràng

---

### 🥈 **Cách 2: Dùng Browser Console**

**Bước 1:** Đăng nhập vào hệ thống với tài khoản ADMIN

**Bước 2:** Mở Developer Tools (F12)

**Bước 3:** Vào tab **Console**

**Bước 4:** Chạy lệnh:
```javascript
fetch('/api/admin/accounts/unit-conversion-migration', {
    method: 'POST'
})
.then(response => response.json())
.then(data => {
    console.log('Kết quả:', data);
    alert(data.message);
});
```

**Bước 5:** Xem kết quả trong console và alert

✅ **Ưu điểm:**
- Có authentication từ browser
- Dễ debug
- Xem response ngay lập tức

---

### 🥉 **Cách 3: Dùng PowerShell Script**

**Bước 1:** Mở PowerShell

**Bước 2:** Di chuyển đến thư mục project:
```powershell
cd D:\Pharma\Pharma
```

**Bước 3:** Chạy script:
```powershell
.\run-migration.ps1
```

Script sẽ:
- ✅ Kiểm tra server có đang chạy không
- ✅ Gọi migration endpoint
- ✅ Hiển thị kết quả
- ✅ Hướng dẫn nếu có lỗi

⚠️ **Lưu ý:** 
- Nếu gặp lỗi "execution policy", chạy:
  ```powershell
  Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
  ```

---

## 🔍 Cách Kiểm Tra Ứng Dụng Đã Chạy Chưa

### Test 1: Mở Browser
```
http://localhost:8080
```
→ Nếu thấy trang login/dashboard → ✅ Đang chạy

### Test 2: PowerShell
```powershell
Invoke-WebRequest -Uri "http://localhost:8080" -Method GET
```
→ Nếu có response → ✅ Đang chạy
→ Nếu lỗi "Unable to connect" → ❌ Chưa chạy

---

## 🚀 Khởi Động Ứng Dụng

Nếu ứng dụng chưa chạy:

### Cách 1: Gradle
```powershell
cd D:\Pharma\Pharma
.\gradlew bootRun
```

### Cách 2: IDE (IntelliJ/Eclipse)
- Mở project
- Chạy `PharmaApplication.java`

### Cách 3: JAR file
```powershell
cd D:\Pharma\Pharma
.\gradlew build
java -jar build\libs\pharma-0.0.1-SNAPSHOT.jar
```

Đợi đến khi thấy:
```
Started PharmaApplication in X seconds
```

---

## 📊 Xem Kết Quả Migration

### 1. Console Log
Khi migration chạy, bạn sẽ thấy trong console:

```
========================================
Bắt đầu migrate 150 MedicineVariant sang UnitConversion...
========================================
✓ Variant ID 1 [Paracetamol 500mg]: 2 unit conversion(s) được tạo
✓ Variant ID 2 [Ibuprofen 200mg]: 2 unit conversion(s) được tạo
...
========================================
Hoàn thành! Đã xử lý: 150/150 variants
Tổng số unit conversions được tạo: 285
========================================
```

### 2. Kiểm tra Database
```sql
-- Xem tổng số unit conversions
SELECT COUNT(*) FROM unit_conversions WHERE deleted = 0;

-- Xem chi tiết
SELECT 
    uc.id,
    mv.id as variant_id,
    m.name as medicine_name,
    u.name as unit_name,
    uc.multiplier
FROM unit_conversions uc
JOIN medicine_variant mv ON uc.variant_id = mv.id
JOIN medicines m ON mv.medicine_id = m.id
JOIN units u ON uc.unit_id = u.id
WHERE uc.deleted = 0
ORDER BY mv.id, uc.multiplier;
```

---

## ⚠️ Troubleshooting

### Lỗi: "Connection refused"
**Nguyên nhân:** Server chưa chạy
**Giải pháp:** Khởi động ứng dụng (xem phần trên)

### Lỗi: "401 Unauthorized"
**Nguyên nhân:** Chưa đăng nhập hoặc không có quyền
**Giải pháp:** 
1. Đăng nhập với tài khoản ADMIN
2. Dùng Browser Console (Cách 2)

### Lỗi: "403 Forbidden"
**Nguyên nhân:** Tài khoản không có quyền ADMIN
**Giải pháp:** Sử dụng tài khoản có role ADMIN

### Không có output nào
**Nguyên nhân:** Có thể endpoint đang chạy nhưng không có response
**Giải pháp:**
1. Xem console log của server
2. Kiểm tra database xem có records mới không
3. Dùng Browser để thấy response rõ hơn

---

## 📝 Checklist

Trước khi chạy migration:
- [ ] Ứng dụng đã khởi động (http://localhost:8080 hoạt động)
- [ ] Đã đăng nhập với tài khoản ADMIN
- [ ] Database có dữ liệu MedicineVariant
- [ ] Backup database (recommended)

Sau khi chạy migration:
- [ ] Kiểm tra console log
- [ ] Kiểm tra database
- [ ] Test tạo variant mới
- [ ] Test update variant

---

## 🎉 Tóm Tắt

**CÁCH NHANH NHẤT:**

1. Mở browser
2. Đăng nhập ADMIN
3. Vào: `http://localhost:8080/migration.html`
4. Nhấn nút "Chạy Migration"
5. Xong! ✅

**Chỉ cần làm MỘT LẦN!**

