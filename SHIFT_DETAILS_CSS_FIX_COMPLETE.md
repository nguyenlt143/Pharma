# 🔍 KIỂM TRA VÀ FIX - shift_details.jte & CSS

**Date**: 2025-12-05  
**Issue**: Chỉ hiển thị 5/8 cột, thiếu: Số lô, Hãng sản xuất, Xuất xứ

---

## 🎯 PHÁT HIỆN VẤN ĐỀ

### Từ ảnh screenshot:
Chỉ thấy 5 cột:
- ✅ Tên thuốc
- ✅ Đơn vị
- ❌ **Số lô - THIẾU**
- ❌ **Hãng sản xuất - THIẾU**
- ❌ **Xuất xứ - THIẾU**
- ✅ Số lượng
- ✅ Đơn giá
- ✅ Thành tiền

### Root Cause: CONFLICT GIỮA CSS VÀ DATATABLES

#### Vấn đề 1: `table-layout: fixed`
```css
/* CSS cũ */
.table {
    width: 100% !important;
    table-layout: fixed;  /* ❌ Làm columns bị ép vào 100% */
}
```

**Problem**: 
- `table-layout: fixed` chia đều width cho columns
- Nhưng DataTables với `scrollX: true` cần `table-layout: auto`
- Conflict → Một số columns bị co về 0

#### Vấn đề 2: CSS nth-child có width cụ thể
```css
/* CSS cũ */
.table th:nth-child(2) { width: 80px; }
.table th:nth-child(3) { width: 100px; }
.table th:nth-child(5) { width: 120px; }
.table th:nth-child(6) { width: 90px; }
.table th:nth-child(7) { width: 130px; }
.table th:nth-child(8) { width: 150px; }
```

**Problem**:
- CSS width override DataTables `columnDefs` width
- CSS có ưu tiên cao → DataTables widths bị ignore
- Kết quả: Columns 3, 4, 5 (Số lô, Hãng SX, Xuất xứ) không có width trong CSS → Width = 0

#### Vấn đề 3: `width: 100% !important`
```css
.table {
    width: 100% !important;  /* ❌ Force table width = container width */
}
```

**Problem**:
- Force table width = 100% của container
- Nhưng 8 columns cần > 100% để hiển thị hết
- Kết quả: Columns bị co lại

---

## ✅ GIẢI PHÁP ĐÃ ÁP DỤNG

### Fix 1: Đổi `table-layout: fixed` → `auto`
```css
/* SAU - Đã fix */
.table {
    width: auto !important;        /* ✅ Let table grow naturally */
    table-layout: auto;            /* ✅ Auto calculate widths */
}
```

### Fix 2: Xóa width cụ thể trong CSS nth-child
```css
/* SAU - Đã fix */
/* Column 3: Số lô */
.table th:nth-child(3),
.table td:nth-child(3) {
    text-align: center;
    /* ✅ NO width - let DataTables columnDefs handle it */
}

/* Column 4: Hãng sản xuất */
.table th:nth-child(4),
.table td:nth-child(4) {
    /* ✅ NO width - let DataTables columnDefs handle it */
}

/* Column 5: Xuất xứ */
.table th:nth-child(5),
.table td:nth-child(5) {
    text-align: center;
    /* ✅ NO width - let DataTables columnDefs handle it */
}
```

**Lý do**: 
- DataTables `columnDefs` đã định nghĩa width cho tất cả columns
- CSS không nên override → Let DataTables control widths
- CSS chỉ nên định nghĩa styling (color, font, alignment)

---

## 📊 PHÂN TÍCH

### TRƯỚC fix:

```
DataTables columnDefs:
├─ targets: 0, width: '180px'  ✅
├─ targets: 1, width: '80px'   ✅
├─ targets: 2, width: '120px'  ❌ Override by CSS
├─ targets: 3, width: '150px'  ❌ NO CSS width → width = 0
├─ targets: 4, width: '100px'  ❌ NO CSS width → width = 0
├─ targets: 5, width: '90px'   ❌ Override by CSS width: 90px
├─ targets: 6, width: '130px'  ❌ Override by CSS width: 130px
└─ targets: 7, width: '150px'  ❌ Override by CSS width: 150px

CSS nth-child:
├─ :nth-child(1) → NO width (OK)
├─ :nth-child(2) → width: 80px (OVERRIDE DataTables)
├─ :nth-child(3) → width: 100px (OVERRIDE DataTables)
├─ :nth-child(4) → min-width: 150px (Conflict)
├─ :nth-child(5) → width: 120px (OVERRIDE DataTables)
├─ :nth-child(6) → width: 90px (OVERRIDE DataTables)
├─ :nth-child(7) → width: 130px (OVERRIDE DataTables)
└─ :nth-child(8) → width: 150px (OVERRIDE DataTables)

table-layout: fixed
    ↓
Table forced to 100% width
    ↓
8 columns squeezed into fixed space
    ↓
Columns 3, 4, 5 (no CSS width) → width = 0
    ↓
KHÔNG NHÌN THẤY ❌
```

### SAU fix:

```
DataTables columnDefs:
├─ targets: 0, width: '180px'  ✅
├─ targets: 1, width: '80px'   ✅
├─ targets: 2, width: '120px'  ✅ No CSS override
├─ targets: 3, width: '150px'  ✅ No CSS override
├─ targets: 4, width: '100px'  ✅ No CSS override
├─ targets: 5, width: '90px'   ✅ No CSS override
├─ targets: 6, width: '130px'  ✅ No CSS override
└─ targets: 7, width: '150px'  ✅ No CSS override

CSS nth-child:
├─ :nth-child(1) → Styling only (color, font-weight)
├─ :nth-child(2) → Styling only (text-align, color)
├─ :nth-child(3) → Styling only (text-align, font-family)
├─ :nth-child(4) → Styling only (nothing specific)
├─ :nth-child(5) → Styling only (text-align)
├─ :nth-child(6) → Styling only (text-align, color, font-weight)
├─ :nth-child(7) → Styling only (text-align, color, font-weight)
└─ :nth-child(8) → Styling only (text-align, color, font-weight, font-size)

table-layout: auto
    ↓
Table width calculated based on content
    ↓
DataTables columnDefs widths respected
    ↓
All 8 columns get their defined widths
    ↓
TẤT CẢ HIỂN THỊ ✅
```

---

## 🔧 FILES MODIFIED

### detail_pages_common.css

**Change 1**: Line ~173
```css
/* BEFORE */
.table {
    width: 100% !important;
    table-layout: fixed;
}

/* AFTER */
.table {
    width: auto !important;
    table-layout: auto;
}
```

**Change 2**: Lines ~230-280 (Column-specific styling)
```css
/* BEFORE */
.table th:nth-child(2) { width: 80px; }
.table th:nth-child(3) { width: 100px; }
.table th:nth-child(4) { min-width: 150px; }
.table th:nth-child(5) { width: 120px; }
.table th:nth-child(6) { width: 90px; }
.table th:nth-child(7) { width: 130px; }
.table th:nth-child(8) { width: 150px; }

/* AFTER */
/* All width properties REMOVED */
/* Only keep styling (color, font, alignment) */
```

---

## ✅ VERIFICATION

### Test trong Browser Console:

```javascript
// Check column widths
$('#shiftDetailTable thead th').each(function(i) {
    console.log('Column ' + i + ': ' + $(this).text() + ' = ' + $(this).width() + 'px');
});

// Expected output SAU fix:
// Column 0: Tên thuốc = 180px
// Column 1: Đơn vị = 80px
// Column 2: Số lô = 120px      ← Should be visible!
// Column 3: Hãng sản xuất = 150px  ← Should be visible!
// Column 4: Xuất xứ = 100px    ← Should be visible!
// Column 5: Số lượng = 90px
// Column 6: Đơn giá = 130px
// Column 7: Thành tiền = 150px
```

---

## 📊 EXPECTED RESULT

```
┌────────────┬────────┬────────┬──────────────┬─────────┬─────────┬─────────┬───────────┐
│ Tên thuốc │ Đơn vị │ Số lô  │ Hãng sản xuất│ Xuất xứ │ Số lượng│ Đơn giá │ Thành tiền│
│ (180px)   │ (80px) │(120px)✅│ (150px) ✅   │(100px)✅│ (90px)  │ (130px) │ (150px)   │
├────────────┼────────┼────────┼──────────────┼─────────┼─────────┼─────────┼───────────┤
│Paracetamol │ Viên   │ BATCH..│ GlaxoSmith...│ Anh     │ 1       │ 3.500₫  │ 3.500₫    │
│Paracetamol │ Vi     │ BATCH..│ GlaxoSmith...│ Anh     │ 2       │35.000₫  │ 70.000₫   │
└────────────┴────────┴────────┴──────────────┴─────────┴─────────┴─────────┴───────────┘

✅ TẤT CẢ 8 CỘT HIỂN THỊ!
```

---

## 💡 KEY LESSONS

### 1. CSS vs DataTables Priority

**Rule**: Khi dùng DataTables với `columnDefs`:
- ✅ Let DataTables control **widths**
- ✅ CSS chỉ control **styling** (color, font, alignment)
- ❌ KHÔNG dùng CSS width/min-width cho columns

### 2. table-layout

**For DataTables with scrollX**:
- ✅ Use `table-layout: auto`
- ❌ DON'T use `table-layout: fixed`

**Why?**
- `auto`: Width calculated based on content and columnDefs
- `fixed`: Width divided equally → Conflicts with custom widths

### 3. width: 100% vs auto

**For scrollable tables**:
- ✅ Use `width: auto` để table grow naturally
- ❌ DON'T use `width: 100%` để force fit container

---

## 🚀 TESTING

### Bước 1: Clear cache
```
Ctrl + Shift + F5 (Hard reload)
```

### Bước 2: Rebuild
```bash
./gradlew clean build
```

### Bước 3: Run
```bash
./gradlew bootRun
```

### Bước 4: Test
```
1. Navigate to /pharmacist/shifts
2. Click "Xem chi tiết" on "Ca full ngày"
3. Verify: ALL 8 columns visible
4. Check: Số lô, Hãng sản xuất, Xuất xứ columns showing
```

---

## ✅ STATUS

| Item | Status |
|------|--------|
| Root cause | ✅ CSS width conflict với DataTables |
| Fix applied | ✅ Removed CSS widths |
| table-layout | ✅ Changed to auto |
| width | ✅ Changed to auto |
| Compile | ✅ No errors |
| Ready to test | ✅ **YES!** |

---

**Status**: 🟢 **FIXED - READY TO TEST**

**Changes**: 
- CSS width properties removed
- table-layout: auto
- width: auto
- Let DataTables columnDefs control all widths

**Expected**: TẤT CẢ 8 COLUMNS HIỂN THỊ ĐẦY ĐỦ! ✅

---

*Fixed: 2025-12-05*  
*Root cause: CSS width override DataTables columnDefs*  
*Solution: Remove CSS widths, use table-layout: auto*

