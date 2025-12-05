# 📊 Shifts Query Fix - Visual Explanation

## Database Structure
```
┌─────────────┐
│   users     │
├─────────────┤
│ id          │
│ user_name   │
│ branch_id   │◄─────────┐
│ deleted     │          │
└─────────────┘          │
                         │
┌─────────────┐          │
│   shifts    │          │
├─────────────┤          │
│ id          │          │
│ name        │          │
│ branch_id   │◄─────────┤ Must match!
│ start_time  │          │
│ deleted     │          │
└─────────────┘          │
       │                 │
       │                 │
       ▼                 │
┌──────────────────┐     │
│shift_assignments │     │
├──────────────────┤     │
│ id               │     │
│ shift_id         │     │
│ user_id          │─────┘
│ deleted          │
└──────────────────┘
       │
       │
       ▼
┌──────────────┐
│ shift_works  │
├──────────────┤
│ id           │
│ assignment_id│
│ work_date    │
│ deleted      │
└──────────────┘
       │
       │
       ▼
┌──────────────┐
│  invoices    │
├──────────────┤
│ id           │
│ shift_work_id│
│ user_id      │
│ total_price  │
│ payment_method│
│ invoice_type │
│ deleted      │
└──────────────┘
```

## Problem: OLD Query Logic

```
┌────────────────────────────────────────────────────────┐
│                    ALL SHIFTS                          │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐            │
│  │ Ca sáng  │  │ Ca chiều │  │ Ca tối   │            │
│  │ Branch A │  │ Branch A │  │ Branch B │  ❌        │
│  └──────────┘  └──────────┘  └──────────┘            │
│       │              │              │                  │
│       └──────────────┴──────────────┘                 │
│                      │                                 │
│         LEFT JOIN shift_assignments                   │
│         (gets ALL shifts, filters later)              │
│                      │                                 │
│                      ▼                                 │
│              ┌──────────────┐                          │
│              │ User (ID=1)  │                          │
│              │ Branch A     │                          │
│              └──────────────┘                          │
│                                                         │
│  RESULT: Shows Ca sáng, Ca chiều, Ca tối ❌           │
│          (Including shifts from Branch B!)             │
└────────────────────────────────────────────────────────┘
```

## Solution: NEW Query Logic

```
┌────────────────────────────────────────────────────────┐
│                    STEP 1: Filter User                 │
│              ┌──────────────┐                          │
│              │ User (ID=1)  │                          │
│              │ Branch A     │◄───── Get user's branch  │
│              └──────────────┘                          │
│                      │                                 │
│                      ▼                                 │
│              INNER JOIN shifts                         │
│         WHERE u.branch_id = s.branch_id ⭐            │
│                      │                                 │
│                      ▼                                 │
│  ┌──────────────────────────────────────┐             │
│  │    FILTERED SHIFTS (Branch A only)   │             │
│  │  ┌──────────┐  ┌──────────┐         │             │
│  │  │ Ca sáng  │  │ Ca chiều │   ✅    │             │
│  │  │ Branch A │  │ Branch A │         │             │
│  │  └──────────┘  └──────────┘         │             │
│  └──────────────────────────────────────┘             │
│                                                         │
│  RESULT: Shows Ca sáng, Ca chiều ONLY ✅              │
│          (Excludes Ca tối from Branch B)               │
└────────────────────────────────────────────────────────┘
```

## Comparison Table

| Aspect | OLD Query (LEFT JOIN) | NEW Query (INNER JOIN) |
|--------|----------------------|------------------------|
| **Join Type** | LEFT JOIN | INNER JOIN ✅ |
| **Branch Filter** | ❌ None | ✅ `u.branch_id = s.branch_id` |
| **Shifts Returned** | All shifts user ever assigned | Only current branch shifts ✅ |
| **Cross-branch Data** | ✅ Included ❌ | ❌ Excluded ✅ |
| **Performance** | Slower (filter after join) | Faster (filter during join) ✅ |
| **Data Accuracy** | ❌ Incorrect | ✅ Correct |

## Example Scenario

### User Profile:
```
User ID: 1
Name: "Nguyễn Văn A"
Current Branch: "Chi nhánh Quận 1" (branch_id = 1)
Role: PHARMACIST
```

### Shift Assignments:
```
Assignment 1: Ca sáng  → Branch: Quận 1 (branch_id = 1)
Assignment 2: Ca chiều → Branch: Quận 1 (branch_id = 1)
Assignment 3: Ca tối   → Branch: Quận 2 (branch_id = 2) [OLD assignment]
```

### OLD Query Result:
```
┌────────────┬────────────┬───────────┬──────────────┬──────────────┐
│ Shift Name │ Order Count│ Cash Total│ Transfer Tot │ Total Revenue│
├────────────┼────────────┼───────────┼──────────────┼──────────────┤
│ Ca sáng    │ 15         │ 3,000,000 │ 2,000,000    │ 5,000,000    │
│ Ca chiều   │ 12         │ 2,500,000 │ 1,500,000    │ 4,000,000    │
│ Ca tối     │ 8          │ 1,800,000 │ 1,200,000    │ 3,000,000    │ ❌
└────────────┴────────────┴───────────┴──────────────┴──────────────┘
                          Total: 12,000,000 ❌ (Includes Branch 2 data!)
```

### NEW Query Result:
```
┌────────────┬────────────┬───────────┬──────────────┬──────────────┐
│ Shift Name │ Order Count│ Cash Total│ Transfer Tot │ Total Revenue│
├────────────┼────────────┼───────────┼──────────────┼──────────────┤
│ Ca sáng    │ 15         │ 3,000,000 │ 2,000,000    │ 5,000,000    │
│ Ca chiều   │ 12         │ 2,500,000 │ 1,500,000    │ 4,000,000    │
└────────────┴────────────┴───────────┴──────────────┴──────────────┘
                          Total: 9,000,000 ✅ (Only Branch 1 data!)
```

## SQL Query Breakdown

### Key Changes:

#### 1️⃣ FROM shifts s
```sql
-- Same in both queries
```

#### 2️⃣ shift_assignments Join
```sql
-- OLD:
LEFT JOIN shift_assignments sa 
    ON s.id = sa.shift_id AND sa.deleted = 0
-- ❌ Gets ALL shifts, filters later

-- NEW:
INNER JOIN shift_assignments sa 
    ON s.id = sa.shift_id
    AND sa.deleted = 0
    AND sa.user_id = :userId
-- ✅ Only gets assigned shifts immediately
```

#### 3️⃣ users Join (NEW - Added!)
```sql
-- OLD: Not present ❌

-- NEW:
INNER JOIN users u 
    ON sa.user_id = u.id
    AND u.deleted = 0
    AND u.branch_id = s.branch_id  -- ⭐ KEY FIX!
-- ✅ Ensures shift belongs to user's current branch
```

#### 4️⃣ shift_works Join
```sql
-- OLD:
LEFT JOIN shift_works sw 
    ON sa.id = sw.assignment_id AND sw.deleted = 0
    AND DATE(sw.work_date) >= DATE_SUB(DATE(NOW()), INTERVAL 90 DAY)
    AND sa.user_id = :userId  -- ❌ Filter in wrong place

-- NEW:
LEFT JOIN shift_works sw 
    ON sa.id = sw.assignment_id
    AND sw.deleted = 0
    AND DATE(sw.work_date) >= DATE_SUB(DATE(NOW()), INTERVAL 90 DAY)
-- ✅ Cleaner, user_id already filtered above
```

#### 5️⃣ invoices Join
```sql
-- Similar in both, but NEW gets correct data due to upstream filtering
```

## Data Flow Diagram

```
User Request: "Show me my shifts revenue"
         │
         ▼
┌────────────────────────────────────────┐
│  Controller: getAllRevenuesShift()     │
│  - Gets authenticated user ID          │
│  - Passes to service                   │
└────────────────────────────────────────┘
         │
         ▼
┌────────────────────────────────────────┐
│  Service: getRevenueShiftSummary()     │
│  - Calls repository                    │
│  - Applies sorting & pagination        │
└────────────────────────────────────────┘
         │
         ▼
┌────────────────────────────────────────┐
│  Repository: findRevenueShiftByUser()  │
│                                        │
│  OLD Logic:                            │
│  1. Get ALL shifts ❌                  │
│  2. Try to filter by user              │
│  3. Returns cross-branch data ❌       │
│                                        │
│  NEW Logic:                            │
│  1. Get user's branch_id ✅            │
│  2. Get shifts WHERE branch matches ✅ │
│  3. Filter by user assignment ✅       │
│  4. Returns current branch data ✅     │
└────────────────────────────────────────┘
         │
         ▼
┌────────────────────────────────────────┐
│  DataTables: Displays in shifts.jte    │
│  - Shows only current branch shifts ✅ │
│  - Revenue data is accurate ✅         │
└────────────────────────────────────────┘
```

## Why This Fix Matters

### Business Impact:
1. **Data Accuracy**: Pharmacists see only relevant shifts
2. **Privacy**: No cross-branch data exposure
3. **User Experience**: Clear, focused information
4. **Performance**: Faster queries with INNER JOIN
5. **Compliance**: Proper data segregation by branch

### Technical Impact:
1. **Database**: Fewer rows scanned
2. **Network**: Less data transferred
3. **Memory**: Smaller result sets
4. **CPU**: Less filtering in application layer
5. **Maintainability**: Clearer query logic

---

## Summary

| Item | Status |
|------|--------|
| Query syntax | ✅ Correct |
| Branch filter | ✅ Applied |
| Join logic | ✅ INNER JOIN |
| Performance | ✅ Improved |
| Data accuracy | ✅ Fixed |
| Cross-branch leakage | ✅ Prevented |
| 90-day window | ✅ Preserved |
| Payment filter | ✅ Preserved |

**Result**: 🎉 **Shifts data now correctly filtered by current branch!**

---

*Generated: 2025-12-05*  
*Related Files: InvoiceRepository.java, SHIFTS_QUERY_FIX_SUMMARY.md*

