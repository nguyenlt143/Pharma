-- ====================================================================
-- SHIFTS DATA LOADING - SQL TEST QUERIES
-- ====================================================================
-- Để test query findRevenueShiftByUser
-- Replace :userId với actual user ID để test

-- ====================================================================
-- TEST 1: Kiểm tra user và branch của họ
-- ====================================================================
SELECT
    u.id AS userId,
    u.user_name,
    u.full_name,
    u.branch_id,
    b.name AS branchName,
    r.name AS roleName
FROM users u
LEFT JOIN branchs b ON u.branch_id = b.id
LEFT JOIN roles r ON u.role_id = r.id
WHERE u.deleted = 0
  AND r.name = 'PHARMACIST'
ORDER BY u.id;

-- ====================================================================
-- TEST 2: Kiểm tra shift assignments của user
-- ====================================================================
SELECT
    sa.id AS assignmentId,
    sa.user_id,
    u.full_name,
    u.branch_id AS userBranch,
    s.id AS shiftId,
    s.name AS shiftName,
    s.branch_id AS shiftBranch,
    CASE
        WHEN u.branch_id = s.branch_id THEN '✓ Match'
        ELSE '✗ Mismatch'
    END AS branchMatch
FROM shift_assignments sa
INNER JOIN users u ON sa.user_id = u.id
INNER JOIN shifts s ON sa.shift_id = s.id
WHERE sa.deleted = 0
  AND u.deleted = 0
  AND s.deleted = 0
  -- Replace with actual userId
  AND sa.user_id = 1
ORDER BY s.start_time;

-- ====================================================================
-- TEST 3: Kiểm tra shift works trong 90 ngày
-- ====================================================================
SELECT
    sw.id AS workId,
    sw.work_date,
    DATEDIFF(NOW(), sw.work_date) AS daysAgo,
    sa.user_id,
    u.full_name,
    s.name AS shiftName
FROM shift_works sw
INNER JOIN shift_assignments sa ON sw.assignment_id = sa.id
INNER JOIN users u ON sa.user_id = u.id
INNER JOIN shifts s ON sa.shift_id = s.id
WHERE sw.deleted = 0
  AND sa.deleted = 0
  AND DATE(sw.work_date) >= DATE_SUB(DATE(NOW()), INTERVAL 90 DAY)
  -- Replace with actual userId
  AND sa.user_id = 1
ORDER BY sw.work_date DESC;

-- ====================================================================
-- TEST 4: Kiểm tra invoices theo shift work
-- ====================================================================
SELECT
    i.id,
    i.invoice_code,
    i.created_at,
    i.total_price,
    i.payment_method,
    i.invoice_type,
    sw.work_date,
    s.name AS shiftName,
    u.full_name AS pharmacist
FROM invoices i
INNER JOIN shift_works sw ON i.shift_work_id = sw.id
INNER JOIN shift_assignments sa ON sw.assignment_id = sa.id
INNER JOIN shifts s ON sa.shift_id = s.id
INNER JOIN users u ON i.user_id = u.id
WHERE i.deleted = 0
  AND i.invoice_type = 'PAID'
  -- Replace with actual userId
  AND i.user_id = 1
  AND DATE(sw.work_date) >= DATE_SUB(DATE(NOW()), INTERVAL 90 DAY)
ORDER BY i.created_at DESC
LIMIT 20;

-- ====================================================================
-- TEST 5: FULL QUERY - Giống như findRevenueShiftByUser
-- ====================================================================
-- Replace :userId = 1 với actual user ID
SET @userId = 1;

SELECT
    s.name AS shiftName,
    COALESCE(COUNT(i.id), 0) AS orderCount,
    COALESCE(SUM(CASE WHEN LOWER(i.payment_method) IN ('tiền mặt', 'cash') THEN i.total_price ELSE 0 END), 0) AS cashTotal,
    COALESCE(SUM(CASE WHEN LOWER(i.payment_method) IN ('chuyển khoản', 'transfer') THEN i.total_price ELSE 0 END), 0) AS transferTotal,
    COALESCE(SUM(i.total_price), 0) AS totalRevenue
FROM shifts s
INNER JOIN shift_assignments sa ON s.id = sa.shift_id
    AND sa.deleted = 0
    AND sa.user_id = @userId
INNER JOIN users u ON sa.user_id = u.id
    AND u.deleted = 0
    AND u.branch_id = s.branch_id  -- KEY: Chỉ lấy shifts của branch hiện tại
LEFT JOIN shift_works sw ON sa.id = sw.assignment_id
    AND sw.deleted = 0
    AND DATE(sw.work_date) >= DATE_SUB(DATE(NOW()), INTERVAL 90 DAY)
LEFT JOIN invoices i ON sw.id = i.shift_work_id
    AND i.user_id = @userId
    AND i.invoice_type = 'PAID'
    AND i.deleted = 0
    AND LOWER(i.payment_method) IN ('tiền mặt', 'cash', 'chuyển khoản', 'transfer')
WHERE s.deleted = 0
GROUP BY s.id, s.name, s.start_time
ORDER BY s.start_time;

-- ====================================================================
-- TEST 6: So sánh OLD vs NEW query
-- ====================================================================
-- OLD QUERY (Lấy tất cả shifts)
SELECT 'OLD QUERY' AS type, COUNT(DISTINCT s.id) AS shiftCount
FROM shifts s
LEFT JOIN shift_assignments sa ON s.id = sa.shift_id AND sa.deleted = 0
LEFT JOIN users u ON sa.user_id = u.id AND u.deleted = 0
WHERE s.deleted = 0
  AND sa.user_id = @userId;

-- NEW QUERY (Chỉ lấy shifts của branch hiện tại)
SELECT 'NEW QUERY' AS type, COUNT(DISTINCT s.id) AS shiftCount
FROM shifts s
INNER JOIN shift_assignments sa ON s.id = sa.shift_id
    AND sa.deleted = 0
    AND sa.user_id = @userId
INNER JOIN users u ON sa.user_id = u.id
    AND u.deleted = 0
    AND u.branch_id = s.branch_id
WHERE s.deleted = 0;

-- ====================================================================
-- TEST 7: Debug - Tìm shifts không thuộc branch của user
-- ====================================================================
SELECT
    s.id AS shiftId,
    s.name AS shiftName,
    s.branch_id AS shiftBranch,
    sb.name AS shiftBranchName,
    u.id AS userId,
    u.full_name,
    u.branch_id AS userBranch,
    ub.name AS userBranchName,
    'This shift will be EXCLUDED in new query' AS note
FROM shifts s
INNER JOIN shift_assignments sa ON s.id = sa.shift_id
INNER JOIN users u ON sa.user_id = u.id
LEFT JOIN branchs sb ON s.branch_id = sb.id
LEFT JOIN branchs ub ON u.branch_id = ub.id
WHERE s.deleted = 0
  AND sa.deleted = 0
  AND u.deleted = 0
  AND sa.user_id = @userId
  AND u.branch_id != s.branch_id  -- Shifts ở branch khác
ORDER BY s.name;

-- ====================================================================
-- EXPECTED RESULTS
-- ====================================================================
-- Nếu query đúng, bạn sẽ thấy:
-- 1. Chỉ các shifts trong cùng branch với user
-- 2. orderCount > 0 nếu có invoices trong 90 ngày
-- 3. cashTotal + transferTotal = totalRevenue
-- 4. Không có shifts từ branch khác

-- ====================================================================
-- TROUBLESHOOTING
-- ====================================================================
-- Nếu không có data:
-- 1. Kiểm tra user có shift_assignments không? (TEST 2)
-- 2. Kiểm tra có shift_works trong 90 ngày không? (TEST 3)
-- 3. Kiểm tra có invoices không? (TEST 4)
-- 4. Kiểm tra branch_id của user và shift có match không? (TEST 2, TEST 7)

