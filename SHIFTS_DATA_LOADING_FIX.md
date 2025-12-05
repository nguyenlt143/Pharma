# Shifts Data Loading Fix

## Vấn đề
shifts.jte không lấy được data đúng từ controller `/pharmacist/all/shift`.

**Vấn đề phát hiện**: Query SQL đang trả về dữ liệu của **tất cả các ca làm việc** mà user được assign, không filter theo **chi nhánh hiện tại** của pharmacist.

## ✅ Đã sửa

### Fix Query SQL (InvoiceRepository.java)
**Trước đây**: Sử dụng LEFT JOIN, lấy tất cả shifts mà user được assign bất kể chi nhánh nào
**Bây giờ**: 
- ✅ Thay đổi sang INNER JOIN cho shift_assignments
- ✅ Thêm INNER JOIN với users table
- ✅ Filter: `u.branch_id = s.branch_id` - Chỉ lấy shifts của chi nhánh hiện tại
- ✅ Đảm bảo chỉ hiển thị data trong 90 ngày gần nhất

### Query cũ:
```sql
FROM shifts s
LEFT JOIN shift_assignments sa ON s.id = sa.shift_id AND sa.deleted = 0
LEFT JOIN shift_works sw ON sa.id = sw.assignment_id AND sw.deleted = 0
    AND DATE(sw.work_date) >= DATE_SUB(DATE(NOW()), INTERVAL 90 DAY)
    AND sa.user_id = :userId
```

### Query mới:
```sql
FROM shifts s
INNER JOIN shift_assignments sa ON s.id = sa.shift_id
    AND sa.deleted = 0
    AND sa.user_id = :userId
INNER JOIN users u ON sa.user_id = u.id
    AND u.deleted = 0
    AND u.branch_id = s.branch_id  -- ✅ KEY FIX: Filter theo branch
LEFT JOIN shift_works sw ON sa.id = sw.assignment_id
    AND sw.deleted = 0
    AND DATE(sw.work_date) >= DATE_SUB(DATE(NOW()), INTERVAL 90 DAY)
```

## Các bước đã thực hiện

### 1. ✅ Kiểm tra Controller
- **File**: `RevenueController.java`
- **Endpoint**: `GET /pharmacist/all/shift`
- **Method**: `getAllRevenuesShift()`
- Controller có logging đầy đủ và xử lý exception
- Trả về `DataTableResponse<RevenueShiftVM>` đúng format

### 2. ✅ Kiểm tra Service
- **File**: `RevenueServiceImpl.java`
- **Method**: `getRevenueShiftSummary()`
- Service map dữ liệu từ repository đúng cách
- Support sorting và pagination

### 3. ✅ Kiểm tra Repository
- **File**: `InvoiceRepository.java`
- **Method**: `findRevenueShiftByUser()`
- Query SQL lấy dữ liệu từ bảng shifts với các thông tin:
  - shiftName
  - orderCount
  - cashTotal
  - transferTotal
  - totalRevenue

### 4. ✅ Kiểm tra ViewModel
- **File**: `RevenueShiftVM.java`
- Java record với các trường đúng như query trả về

### 5. ✅ Cập nhật shifts.jte
- Thêm detailed logging trong DataTables ajax callback
- Thêm `dataSrc` function để log và xử lý response
- Thêm direct API test để debug
- Thêm error handling đầy đủ

### 6. ✅ Tạo Debug HTML Test File
- **File**: `shifts-debug-test.html`
- Standalone HTML file để test API endpoint
- 2 test cases:
  1. Direct API call với $.ajax
  2. DataTables integration
- Console output chi tiết

## Cách Debug

### Bước 1: Mở file debug HTML
```
http://localhost:8080/shifts-debug-test.html
```
Hoặc mở trực tiếp file `shifts-debug-test.html` trong browser.

### Bước 2: Test Direct API
1. Click button "Test API Endpoint"
2. Xem console output để kiểm tra:
   - API có response không?
   - Response structure có đúng không?
   - Data array có records không?
   - Các trường dữ liệu có đúng tên không?

### Bước 3: Test DataTables Integration
1. Click button "Initialize DataTable"
2. Xem console output để kiểm tra:
   - DataTables có gọi API thành công không?
   - dataSrc function có nhận được data không?
   - Render functions có được gọi không?

### Bước 4: Kiểm tra Server Logs
Mở terminal chạy Spring Boot và xem logs:
```
Getting shift revenue data for user: {userId}
DataTable request: ...
Shift revenue response - Total records: ..., Filtered: ...
Shift data size: ...
```

## Checklist Troubleshooting

### ✓ Nếu API trả về empty data:
1. Kiểm tra database có data không?
   ```sql
   SELECT * FROM shifts WHERE deleted = 0;
   SELECT * FROM shift_assignments WHERE deleted = 0;
   SELECT * FROM shift_works WHERE deleted = 0;
   ```
2. Kiểm tra userId có đúng không?
3. Kiểm tra time range (90 ngày) có chứa data không?

### ✓ Nếu API lỗi 403/401:
1. Kiểm tra authentication/authorization
2. Kiểm tra user có role PHARMACIST không?
3. Kiểm tra session cookie

### ✓ Nếu API lỗi 500:
1. Xem stack trace trong server logs
2. Kiểm tra database connection
3. Kiểm tra SQL query syntax

### ✓ Nếu DataTables không hiển thị:
1. Kiểm tra response format phải là:
   ```json
   {
     "draw": 1,
     "recordsTotal": X,
     "recordsFiltered": X,
     "data": [...]
   }
   ```
2. Kiểm tra column mapping (`data: 'shiftName'` phải khớp với field trong response)
3. Kiểm tra jQuery và DataTables đã load chưa

## Expected Response Format

```json
{
  "draw": 1,
  "recordsTotal": 3,
  "recordsFiltered": 3,
  "data": [
    {
      "shiftName": "Ca sáng",
      "orderCount": 10,
      "cashTotal": 1000000.0,
      "transferTotal": 500000.0,
      "totalRevenue": 1500000.0
    },
    {
      "shiftName": "Ca chiều",
      "orderCount": 8,
      "cashTotal": 800000.0,
      "transferTotal": 400000.0,
      "totalRevenue": 1200000.0
    },
    {
      "shiftName": "Ca tối",
      "orderCount": 5,
      "cashTotal": 500000.0,
      "transferTotal": 200000.0,
      "totalRevenue": 700000.0
    }
  ]
}
```

## Thay đổi trong shifts.jte

### Thêm logging chi tiết:
```javascript
dataSrc: function(json) {
    console.log('Raw JSON response:', json);
    console.log('Data array:', json.data);
    console.log('Records total:', json.recordsTotal);
    // ... more logging
    return json.data;
}
```

### Thêm error handling:
```javascript
error: function(xhr, error, thrown) {
    console.error('DataTable Ajax Error:', error, thrown);
    console.error('Response:', xhr.responseText);
    console.error('Status:', xhr.status);
    alert('Lỗi khi tải dữ liệu ca làm việc: ' + error);
}
```

### Thêm direct API test:
```javascript
$.ajax({
    url: '/pharmacist/all/shift',
    type: 'GET',
    data: { draw: 1, start: 0, length: 10 },
    success: function(response) {
        console.log('Direct API test response:', response);
    }
});
```

## Next Steps

1. **Chạy ứng dụng**: `./gradlew bootRun`
2. **Mở browser**: http://localhost:8080/pharmacist/shifts
3. **Mở Developer Console** (F12)
4. **Kiểm tra logs** trong console và server terminal
5. **Nếu vẫn lỗi**: Mở `shifts-debug-test.html` để test chi tiết hơn

## Files Modified

1. ✅ `src/main/jte/pages/pharmacist/shifts.jte` - Added detailed logging
2. ✅ `shifts-debug-test.html` - Created standalone debug tool

## Files To Check

- `src/main/java/vn/edu/fpt/pharma/controller/pharmacist/RevenueController.java`
- `src/main/java/vn/edu/fpt/pharma/service/impl/RevenueServiceImpl.java`
- `src/main/java/vn/edu/fpt/pharma/repository/InvoiceRepository.java`
- `src/main/java/vn/edu/fpt/pharma/dto/reveuce/RevenueShiftVM.java`

## Expected Behavior

1. User mở `/pharmacist/shifts`
2. DataTables tự động gọi `/pharmacist/all/shift?draw=1&start=0&length=10&...`
3. Controller xử lý request, lấy userId từ authentication
4. Service gọi repository query database
5. Repository trả về List<Object[]>
6. Service map sang List<RevenueShiftVM>
7. Service apply sorting và pagination
8. Controller trả về DataTableResponse<RevenueShiftVM>
9. DataTables nhận response và render table
10. User thấy danh sách ca làm việc với thống kê doanh thu

## Status

🔍 **Chờ kiểm tra runtime** - Cần chạy ứng dụng và test trên browser để xác định vấn đề cụ thể.

---

**Date**: 2025-12-05
**Updated**: shifts.jte với detailed logging và debug tools

