package vn.edu.fpt.pharma.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.fpt.pharma.dto.manager.DailyRevenue;
import vn.edu.fpt.pharma.dto.invoice.InvoiceInfoVM;
import vn.edu.fpt.pharma.dto.manager.InvoiceSummary;
import vn.edu.fpt.pharma.dto.manager.KpiData;
import vn.edu.fpt.pharma.dto.manager.TopProductItem;
import vn.edu.fpt.pharma.entity.Invoice;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long>, JpaSpecificationExecutor<Invoice> {

    // -----------------------------
    // DAILY REVENUE
    // -----------------------------
    @Query(value = """
        SELECT DATE(i.created_at) AS date,
               SUM(i.total_price) AS revenue
        FROM invoices i
        LEFT JOIN shift_works sw ON i.shift_work_id = sw.id AND sw.deleted = false
        LEFT JOIN shift_assignments sa ON sw.assignment_id = sa.id AND sa.deleted = false
        WHERE i.created_at >= :fromDate
          AND i.created_at < :toDate
          AND i.deleted = false
          AND (:branchId IS NULL OR i.branch_id = :branchId)
          AND (:shiftId IS NULL OR sa.shift_id = :shiftId)
          AND (:employeeId IS NULL OR i.user_id = :employeeId)
        GROUP BY DATE(i.created_at)
        ORDER BY DATE(i.created_at)
        """, nativeQuery = true)
    List<DailyRevenue> getDailyRevenueByDate(
            @Param("branchId") Long branchId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            @Param("shiftId") Long shiftId,
            @Param("employeeId") Long employeeId
    );

    default List<DailyRevenue> getDailyRevenueByDate(
            Long branchId, LocalDateTime fromDate, LocalDateTime toDate
    ) {
        return getDailyRevenueByDate(branchId, fromDate, toDate, null, null);
    }

    // -----------------------------
    // KPI — SUM REVENUE / PROFIT (REFACTORED)
    // -----------------------------
    default KpiData sumRevenue(
            Long branchId, LocalDateTime fromDate, LocalDateTime toDate,
            Long shiftId, Long employeeId
    ) {
        List<InvoiceSummary> invoices = findInvoicesForReport(branchId, fromDate, toDate, shiftId, employeeId);
        double totalRevenue = invoices.stream().mapToDouble(InvoiceSummary::getTotalAmount).sum();
        double totalProfit = invoices.stream().mapToDouble(InvoiceSummary::getProfit).sum();
        long orderCount = invoices.size();
        return new KpiData(totalRevenue, orderCount, totalProfit);
    }

    // PAGE INVOICE WITH PROFIT (Refactored to use findInvoicesForReport)
    // -----------------------------
    default Page<InvoiceSummary> findInvoicesWithProfit(
            Long branchId, LocalDateTime fromDate, LocalDateTime toDate,
            Long shiftId, Long employeeId, Pageable pageable
    ) {
        // 1. Fetch the full list using the existing report query
        List<InvoiceSummary> fullList = findInvoicesForReport(branchId, fromDate, toDate, shiftId, employeeId);

        // 2. Apply pagination manually
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), fullList.size());

        List<InvoiceSummary> pageContent = (start > fullList.size())
                ? Collections.emptyList()
                : fullList.subList(start, end);

        // 3. Return a Page object
        return new PageImpl<>(pageContent, pageable, fullList.size());
    }

    // -----------------------------
    // TOP PRODUCT CATEGORY
    // -----------------------------
    @Query("""
        SELECT new vn.edu.fpt.pharma.dto.manager.TopProductItem(
            c.name, SUM(id.quantity)
        )
        FROM Invoice i
        JOIN i.details id
        JOIN id.inventory inv
        JOIN inv.variant mv
        JOIN mv.medicine m
        JOIN m.category c
        LEFT JOIN ShiftWork sw ON i.shiftWorkId = sw.id
        LEFT JOIN ShiftAssignment sa ON sw.assignment.id = sa.id
        WHERE i.createdAt >= :fromDate
          AND i.createdAt < :toDate
          AND (:branchId IS NULL OR i.branchId = :branchId)
          AND (:shiftId IS NULL OR sa.shift.id = :shiftId)
          AND (:employeeId IS NULL OR i.userId = :employeeId)
        GROUP BY c.id, c.name
        ORDER BY SUM(id.quantity) DESC
        """)
    List<TopProductItem> topCategories(
            @Param("branchId") Long branchId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            @Param("shiftId") Long shiftId,
            @Param("employeeId") Long employeeId,
            Pageable pageable
    );

    // -----------------------------
    // CATEGORY REVENUE
    // -----------------------------
    @Query("""
        SELECT
            c.name AS categoryName,
            SUM(COALESCE(id.price, 0) * COALESCE(id.quantity, 0)) AS revenue
        FROM Invoice i
        JOIN i.details id
        JOIN id.inventory inv
        JOIN inv.variant mv
        JOIN mv.medicine m
        JOIN m.category c
        LEFT JOIN ShiftWork sw ON i.shiftWorkId = sw.id
        LEFT JOIN ShiftAssignment sa ON sw.assignment.id = sa.id
        WHERE i.createdAt >= :fromDate
          AND i.createdAt < :toDate
          AND (:branchId IS NULL OR i.branchId = :branchId)
          AND (:shiftId IS NULL OR sa.shift.id = :shiftId)
          AND (:employeeId IS NULL OR i.userId = :employeeId)
        GROUP BY c.id, c.name
        ORDER BY SUM(COALESCE(id.price, 0) * COALESCE(id.quantity, 0)) DESC
        """)
    List<Object[]> categoryRevenue(
            @Param("branchId") Long branchId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            @Param("shiftId") Long shiftId,
            @Param("employeeId") Long employeeId,
            Pageable pageable
    );

    // LIST INVOICE ITEMS (REMOVED AND MERGED INTO findInvoicesForReport)
    // -----------------------------

    // -----------------------------
    // BULK FIND BY INVOICE CODE
    // -----------------------------
    List<Invoice> findAllByInvoiceCodeIn(Collection<String> invoiceCodes);

    // -----------------------------
    // MONTHLY REVENUE (BRANCH)
    // -----------------------------
    @Query(value = """
        SELECT
            DATE_FORMAT(created_at, '%m/%Y') AS period,
            COUNT(*) AS total_invoice,
            COUNT(DISTINCT customer_id) AS total_customer,
            COALESCE(SUM(total_price), 0) AS total_revenue
        FROM invoices
        WHERE deleted = 0
        GROUP BY DATE_FORMAT(created_at, '%m/%Y')
        ORDER BY MIN(created_at) DESC
        """, nativeQuery = true)
    List<Object[]> findRevenue();

    // -----------------------------
    // MONTHLY REVENUE BY USER
    // -----------------------------
    @Query(value = """
        SELECT
            DATE_FORMAT(i.created_at, '%m/%Y') AS period,
            COUNT(i.id) AS total_invoice,
            COUNT(DISTINCT i.customer_id) AS total_customer,
            COALESCE(SUM(i.total_price), 0) AS total_revenue
        FROM invoices i
        JOIN shift_works sw ON i.shift_work_id = sw.id
        JOIN shift_assignments sa ON sw.assignment_id = sa.id
        WHERE i.deleted = 0
          AND i.invoice_type = 'PAID'
          AND sa.user_id = :userId
        GROUP BY DATE_FORMAT(i.created_at, '%m/%Y')
        ORDER BY MIN(i.created_at) DESC
        """, nativeQuery = true)
    List<Object[]> findRevenueByUser(@Param("userId") Long userId);

    // -----------------------------
    // REVENUE BY USER — PER SHIFT
    // -----------------------------
    @Query(value = """
    SELECT
        s.name AS shiftName,
        COALESCE(COUNT(i.id), 0) AS orderCount,
        COALESCE(SUM(CASE WHEN LOWER(i.payment_method) IN ('tiền mặt', 'cash') THEN i.total_price ELSE 0 END), 0) AS cashTotal,
        COALESCE(SUM(CASE WHEN LOWER(i.payment_method) IN ('chuyển khoản', 'transfer') THEN i.total_price ELSE 0 END), 0) AS transferTotal,
        COALESCE(SUM(i.total_price), 0) AS totalRevenue
    FROM shifts s
    LEFT JOIN shift_assignments sa ON s.id = sa.shift_id AND sa.deleted = 0
    LEFT JOIN shift_works sw ON sa.id = sw.assignment_id AND sw.deleted = 0 
        AND DATE(sw.work_date) >= DATE_SUB(DATE(NOW()), INTERVAL 90 DAY)
        AND sa.user_id = :userId
    LEFT JOIN invoices i ON sw.id = i.shift_work_id 
        AND i.user_id = :userId
        AND i.invoice_type = 'PAID'
        AND i.deleted = 0
        AND LOWER(i.payment_method) IN ('tiền mặt', 'cash', 'chuyển khoản', 'transfer')
    WHERE s.deleted = 0
    GROUP BY s.id, s.name, s.start_time
    ORDER BY s.start_time;
    """, nativeQuery = true)
    List<Object[]> findRevenueShiftByUser(@Param("userId") Long userId);

    // -----------------------------
    // INVOICE INFO PRINTING
    // -----------------------------
    @Query(value = """
            SELECT\s
                b.name AS branch_name,
                b.address AS branch_address,
                COALESCE(c.name, 'Khách lẻ') AS customer_name,
                COALESCE(c.phone, 'Không có') AS customer_phone,
                i.created_at,
                i.total_price,
                i.description
            FROM invoices i
            LEFT JOIN customers c ON i.customer_id = c.id
            JOIN branchs b ON i.branch_id = b.id
            WHERE i.id = ?;
        """, nativeQuery = true)
    InvoiceInfoVM findInvoiceInfoById(@Param("id") long id);

    // -----------------------------
    // INVOICE REPORT DETAIL
    // -----------------------------
    @Query(value = """
        SELECT
            i.id,
            i.invoice_code,
            COALESCE(u.full_name, '') AS full_name,
            COALESCE(s.name, '') AS shift_name,
            i.created_at,
            i.total_price,
            COALESCE(SUM(
                (COALESCE(id.price, 0) - COALESCE(inv.cost_price, 0))
                * COALESCE(id.quantity, 0)
            ), 0) AS profit,
            COALESCE(i.payment_method, '') AS payment_method
        FROM invoices i
        LEFT JOIN invoice_details id ON i.id = id.invoice_id AND id.deleted = false
        LEFT JOIN inventory inv ON inv.id = id.inventory_id AND inv.deleted = false
        LEFT JOIN shift_works sw ON i.shift_work_id = sw.id
        LEFT JOIN shift_assignments sa ON sw.assignment_id = sa.id
        LEFT JOIN shifts s ON sa.shift_id = s.id
        LEFT JOIN users u ON i.user_id = u.id
        WHERE i.created_at >= :fromDate
          AND i.created_at < :toDate
          AND i.deleted = false
          AND (:branchId IS NULL OR i.branch_id = :branchId)
          AND (:shift IS NULL OR s.id = :shift)
          AND (:employeeId IS NULL OR u.id = :employeeId)
        GROUP BY i.id, i.invoice_code, u.full_name, s.name, i.created_at, i.total_price, i.payment_method
        ORDER BY i.created_at DESC
        """, nativeQuery = true)
    List<Object[]> findInvoicesForReportNative(
            @Param("branchId") Long branchId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            @Param("shift") Long shift,
            @Param("employeeId") Long employeeId
    );

    default List<InvoiceSummary> findInvoicesForReport(
            Long branchId, LocalDateTime fromDate, LocalDateTime toDate,
            Long shift, Long employeeId
    ) {
        List<Object[]> rows = findInvoicesForReportNative(branchId, fromDate, toDate, shift, employeeId);

        return rows.stream().map(r -> new InvoiceSummary(
                ((Number) r[0]).longValue(),
                (String) r[1],
                (String) r[2],
                (String) r[3],
                ((java.sql.Timestamp) r[4]).toLocalDateTime(),
                ((Number) r[5]).doubleValue(),
                ((Number) r[6]).doubleValue(),
                (String) r[7]
        )).toList();
    }

    @Query("SELECT COALESCE(MAX(i.id), 0) FROM Invoice i")
    Long findMaxInvoiceId();
}
