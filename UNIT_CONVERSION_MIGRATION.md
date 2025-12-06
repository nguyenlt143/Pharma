# Hướng dẫn Migration UnitConversion

## 📋 Mô tả

Hệ thống đã được tạo hàm tự động để migrate tất cả dữ liệu từ bảng `medicine_variant` sang bảng `unit_conversions`.

## 🎯 Chức năng

Hàm `migrateAllVariantsToUnitConversions()` sẽ:

1. **Lấy tất cả MedicineVariant** từ database
2. **Với mỗi variant**, tạo 2 bản ghi `UnitConversion`:
   - `(variant_id, baseUnitId, 1.0)` - Đơn vị cơ bản với multiplier = 1
   - `(variant_id, packageUnitId, quantityPerPackage)` - Đơn vị đóng gói với multiplier = số lượng mỗi gói

3. **Kiểm tra trùng lặp**: Chỉ tạo nếu chưa tồn tại (tránh duplicate data)
4. **Báo cáo chi tiết**: In ra console số lượng records đã xử lý

## 🚀 Cách sử dụng

### Phương pháp 1: Qua API (Khuyến nghị)

**Endpoint:** `POST /api/admin/accounts/unit-conversion-migration`

**Sử dụng Postman/cURL:**

```bash
curl -X POST http://localhost:8080/api/admin/accounts/unit-conversion-migration
```

**Response thành công:**
```json
{
  "success": true,
  "message": "✅ Migration hoàn tất! Tất cả MedicineVariant đã được xử lý. Xem console để biết chi tiết."
}
```

**Response lỗi:**
```json
{
  "success": false,
  "message": "❌ Lỗi trong quá trình migration: [chi tiết lỗi]"
}
```

### Phương pháp 2: Gọi trực tiếp từ code

```java
@Autowired
private MedicineVariantService medicineVariantService;

public void runMigration() {
    medicineVariantService.migrateAllVariantsToUnitConversions();
}
```

### Phương pháp 3: Chạy từ @PostConstruct hoặc CommandLineRunner (nếu cần tự động khi khởi động)

```java
@Component
public class DataMigrationRunner implements CommandLineRunner {
    
    @Autowired
    private MedicineVariantService medicineVariantService;
    
    @Override
    public void run(String... args) throws Exception {
        // Uncomment dòng dưới để tự động migrate khi khởi động ứng dụng
        // medicineVariantService.migrateAllVariantsToUnitConversions();
    }
}
```

## 📊 Output Console

Khi chạy migration, console sẽ hiển thị:

```
========================================
Bắt đầu migrate 150 MedicineVariant sang UnitConversion...
========================================
✓ Variant ID 1 [Paracetamol 500mg]: 2 unit conversion(s) được tạo
✓ Variant ID 2 [Ibuprofen 200mg]: 2 unit conversion(s) được tạo
✓ Variant ID 5 [Amoxicillin 500mg]: 1 unit conversion(s) được tạo
...
========================================
Hoàn thành! Đã xử lý: 150/150 variants
Tổng số unit conversions được tạo: 285
========================================
```

## ⚠️ Lưu ý quan trọng

1. **Kiểm tra dữ liệu trước**: Migration sẽ tự động bỏ qua các bản ghi đã tồn tại
2. **Transaction safety**: Hàm sử dụng `@Transactional` để đảm bảo tính toàn vẹn dữ liệu
3. **Idempotent**: Có thể chạy nhiều lần mà không gây duplicate
4. **Log chi tiết**: Kiểm tra console để xem chi tiết quá trình migration

## 🔍 Kiểm tra kết quả

Sau khi migration, kiểm tra database:

```sql
-- Xem tổng số unit conversions
SELECT COUNT(*) FROM unit_conversions WHERE deleted = 0;

-- Xem chi tiết unit conversions của một variant
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

## 📁 Files liên quan

- `MedicineVariantService.java` - Interface định nghĩa method
- `MedicineVariantServiceImpl.java` - Implementation logic
- `UnitConversionMigrationController.java` - REST API endpoint
- `UnitConversion.java` - Entity model
- `MedicineVariant.java` - Source entity

## 🛠️ Troubleshooting

**Lỗi: "MedicineVariant không được để trống"**
- Nguyên nhân: Variant bị null
- Giải pháp: Kiểm tra dữ liệu trong bảng medicine_variant

**Lỗi: Cannot find Unit**
- Nguyên nhân: baseUnitId hoặc packageUnitId không tồn tại trong bảng units
- Giải pháp: Đảm bảo tất cả units được referenced đều tồn tại

**Một số variant không được tạo unit conversion**
- Nguyên nhân: Variant thiếu baseUnitId hoặc packageUnitId
- Hành vi: Hệ thống sẽ bỏ qua và tiếp tục với variant tiếp theo

## ✅ Hoàn tất

Migration này chỉ cần chạy **MỘT LẦN** sau khi deploy code mới. Sau đó, các unit conversions sẽ tự động được tạo khi tạo/cập nhật MedicineVariant mới.

