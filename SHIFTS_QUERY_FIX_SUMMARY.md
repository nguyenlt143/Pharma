# 🔧 Shifts Query Fix - Complete Summary

**Date**: 2025-12-05  
**Issue**: shifts.jte displaying incorrect data - showing shifts from all branches instead of current branch only

---

## 🎯 Root Cause

The SQL query `findRevenueShiftByUser` was using `LEFT JOIN` which returned:
- ❌ All shifts assigned to the user across all branches
- ❌ Revenue data mixed from multiple branches
- ❌ Incorrect shift statistics for pharmacist view

**Business Logic**: A pharmacist should only see shifts data from their current branch.

---

## ✅ Solution

### Changed Query Logic

#### Before (Incorrect):
```sql
FROM shifts s
LEFT JOIN shift_assignments sa ON s.id = sa.shift_id AND sa.deleted = 0
LEFT JOIN shift_works sw ON sa.id = sw.assignment_id AND sw.deleted = 0
    AND DATE(sw.work_date) >= DATE_SUB(DATE(NOW()), INTERVAL 90 DAY)
    AND sa.user_id = :userId
```
**Problem**: LEFT JOIN gets ALL shifts, then filters by user_id in nested condition

#### After (Correct):
```sql
FROM shifts s
INNER JOIN shift_assignments sa ON s.id = sa.shift_id
    AND sa.deleted = 0
    AND sa.user_id = :userId
INNER JOIN users u ON sa.user_id = u.id
    AND u.deleted = 0
    AND u.branch_id = s.branch_id  -- ⭐ KEY FIX
LEFT JOIN shift_works sw ON sa.id = sw.assignment_id
    AND sw.deleted = 0
    AND DATE(sw.work_date) >= DATE_SUB(DATE(NOW()), INTERVAL 90 DAY)
```
**Solution**: 
1. INNER JOIN ensures only assigned shifts
2. Join with users table
3. **Filter by branch**: `u.branch_id = s.branch_id`

---

## 📋 What Changed

### File: `InvoiceRepository.java`

**Location**: `src/main/java/vn/edu/fpt/pharma/repository/InvoiceRepository.java`

**Method**: `findRevenueShiftByUser(Long userId)`

**Changes**:
1. ✅ Changed `LEFT JOIN shift_assignments` → `INNER JOIN shift_assignments`
2. ✅ Added `INNER JOIN users u` with branch filter
3. ✅ Added condition: `u.branch_id = s.branch_id`
4. ✅ Removed trailing whitespaces

**Impact**:
- ✅ Pharmacist only sees shifts from their current branch
- ✅ Revenue data is accurate per branch
- ✅ No data leakage from other branches
- ✅ Performance improved (INNER JOIN faster than LEFT JOIN with filters)

---

## 🧪 Testing

### SQL Test File
**File**: `test-shifts-query.sql`

Contains 7 test scenarios:
1. ✅ Check user and their branch
2. ✅ Check shift assignments
3. ✅ Check shift works in last 90 days
4. ✅ Check invoices by shift work
5. ✅ Full query test
6. ✅ Compare OLD vs NEW query results
7. ✅ Debug: Find cross-branch shifts

### How to Test:
```sql
-- Set your test user ID
SET @userId = 1;

-- Run the full query (TEST 5)
-- Should only show shifts from user's current branch
```

### Debug HTML Test File
**File**: `shifts-debug-test.html`

- Test API endpoint directly
- Test DataTables integration
- Console logging for debugging

---

## 📊 Expected Behavior

### Scenario 1: User in Branch A
- User ID: 1
- Branch: "Chi nhánh A" (branch_id = 1)
- Assigned shifts: Ca sáng, Ca chiều, Ca tối (all in Branch A)

**Result**: Shows data for Ca sáng, Ca chiều, Ca tối only

### Scenario 2: User assigned to multiple branches (Historical)
- User ID: 2
- Current Branch: "Chi nhánh B" (branch_id = 2)
- Previously assigned shifts in Branch A

**Before fix**: Shows shifts from both Branch A and B ❌  
**After fix**: Shows only shifts from Branch B ✅

---

## 🔍 Verification Checklist

- [x] Query syntax correct
- [x] No SQL compilation errors
- [x] Branch filter applied correctly
- [x] INNER JOIN logic correct
- [x] 90-day time window preserved
- [x] Payment method filter preserved
- [x] Deleted records excluded
- [x] Grouping and ordering correct
- [x] COALESCE for NULL handling
- [x] Documentation updated

---

## 📝 Related Files

### Modified:
1. ✅ `InvoiceRepository.java` - Query fixed
2. ✅ `SHIFTS_DATA_LOADING_FIX.md` - Documentation updated

### Created:
1. ✅ `test-shifts-query.sql` - SQL test queries
2. ✅ `shifts-debug-test.html` - Debug test page
3. ✅ `debug-shifts.ps1` - PowerShell debug script
4. ✅ `SHIFTS_QUERY_FIX_SUMMARY.md` - This file

### Unchanged (Already correct):
- ✅ `RevenueController.java` - Controller logic correct
- ✅ `RevenueService.java` - Service interface correct
- ✅ `RevenueServiceImpl.java` - Service implementation correct
- ✅ `RevenueShiftVM.java` - ViewModel correct
- ✅ `shifts.jte` - Template with debug logging

---

## 🚀 Deployment Steps

1. **Build the project**:
   ```bash
   ./gradlew clean build
   ```

2. **Run application**:
   ```bash
   ./gradlew bootRun
   ```

3. **Test in browser**:
   - Login as pharmacist
   - Navigate to: `/pharmacist/shifts`
   - Verify only current branch shifts appear

4. **SQL Verification** (Optional):
   - Run `test-shifts-query.sql` with actual user IDs
   - Verify TEST 6 shows difference between OLD and NEW query
   - Verify TEST 7 shows which shifts are now excluded

5. **Debug if needed**:
   - Open `shifts-debug-test.html`
   - Check console logs
   - Verify API response structure

---

## 💡 Key Insights

### Why LEFT JOIN was wrong?
- LEFT JOIN returns ALL shifts even if user not assigned
- Filter `sa.user_id = :userId` was nested in ON clause
- This caused performance issues and incorrect data

### Why INNER JOIN is correct?
- INNER JOIN ensures row must exist in both tables
- Only returns shifts where user IS assigned
- Branch filter ensures current branch only
- Better performance and data integrity

### Business Rule Clarification:
- ✅ Pharmacist works in ONE branch at a time
- ✅ Only see shifts from their CURRENT branch
- ✅ Historical data from other branches should NOT appear
- ✅ Time window: Last 90 days only

---

## 📞 Support

If issues persist:
1. Check server logs for SQL errors
2. Run SQL test queries to verify data
3. Use debug HTML page to inspect API response
4. Check user's branch_id matches shift's branch_id
5. Verify shift_assignments are not deleted

---

## ✨ Final Result

After this fix:
- ✅ Shifts page shows correct data per branch
- ✅ No cross-branch data leakage
- ✅ Better performance with INNER JOIN
- ✅ Accurate revenue statistics
- ✅ Consistent with business logic

**Status**: 🟢 **FIXED AND READY FOR TESTING**

---

**Fixed by**: AI Assistant  
**Reviewed**: Pending  
**Tested**: Pending  
**Deployed**: Pending

