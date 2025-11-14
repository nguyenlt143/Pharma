package vn.edu.fpt.pharma.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.fpt.pharma.dto.manager.*;
import vn.edu.fpt.pharma.entity.Invoice;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.Tuple;
public interface InvoiceRepository extends JpaRepository<Invoice, Long>, JpaSpecificationExecutor<Invoice> {
//    List<Invoice> findByBrandId(Long brandId);

    // doanh thu theo ngay
    @Query(value = """
    SELECT DATE(i.created_at) AS date,
           SUM(i.total_price) AS revenue
    FROM invoices i
    WHERE i.created_at >= :fromDate
      AND i.created_at < :toDate
      AND i.branch_id = :branchId
    GROUP BY DATE(i.created_at)
    ORDER BY DATE(i.created_at)
""", nativeQuery = true)
    List<DailyRevenue> getDailyRevenueByDate(
            @Param("branchId") Long branchId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );
    // KPI total
@Query("""
    SELECT new vn.edu.fpt.pharma.dto.manager.KpiData(
    CAST(COALESCE(SUM(i.totalPrice), 0) AS double),
    CAST(COALESCE(COUNT(DISTINCT i.id), 0) AS long),
    CAST(COALESCE(SUM((id.price - id.costPrice) * id.quantity), 0) AS double)
            )
    FROM Invoice i
    LEFT JOIN i.details id
    WHERE i.createdAt >= :fromDate
    AND i.createdAt < :toDate
    AND i.branchId = :branchId
    AND (:shiftId IS NULL OR i.shiftWorkId = :shiftId)
    AND (:employeeId IS NULL OR i.userId = :employeeId)
""")
    KpiData sumRevenue(
            @Param("branchId") Long branchId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            @Param("shiftId") Long shiftId,
            @Param("employeeId") Long employeeId
    );

    default KpiData sumRevenue(Long branchId, LocalDateTime fromDate, LocalDateTime toDate) {
        return sumRevenue(branchId, fromDate, toDate, null, null);
    }

    // top5 item
    @Query("""
    SELECT new vn.edu.fpt.pharma.dto.manager.TopProductItem(
        c.name, SUM(id.quantity)
    )
    FROM Invoice i
    JOIN i.details id
    JOIN id.variant mv
    JOIN mv.medicine m
    JOIN m.category c
    WHERE i.createdAt >= :fromDate
      AND i.createdAt < :toDate
      AND i.branchId = :branchId
      AND (:shiftId IS NULL OR i.shiftWorkId = :shiftId)
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

    @Query("""
    SELECT 
      i.createdAt AS createdAt,
      i.invoiceCode AS invoiceCode,
      COALESCE(c.name, '') AS customerName,
      i.paymentMethod AS paymentMethod,
      COALESCE(i.totalPrice, 0) AS totalPrice,
      COALESCE(SUM((d.price - d.costPrice) * d.quantity), 0) AS profit
    FROM Invoice i
    LEFT JOIN i.customer c
    LEFT JOIN i.details d
    WHERE i.createdAt >= :fromDate
      AND i.createdAt < :toDate
      AND i.branchId = :branchId
      AND (:shiftId IS NULL OR i.shiftWorkId = :shiftId)
      AND (:employeeId IS NULL OR i.userId = :employeeId)
    GROUP BY i.id, i.createdAt, i.invoiceCode, c.name, i.paymentMethod, i.totalPrice
    ORDER BY i.createdAt DESC
    """)
    List<InvoiceListItem> findInvoiceItems(
            @Param("branchId") Long branchId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            @Param("shiftId") Long shiftId,
            @Param("employeeId") Long employeeId
    );

    List<Invoice> findAllByInvoiceCodeIn(Collection<String> invoiceCodes);

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

    @Query(value = """
    SELECT 
        DATE_FORMAT(i.created_at, '%m/%Y') AS period,
        COUNT(i.id) AS total_invoice,
        COUNT(DISTINCT i.customer_id) AS total_customer,
        COALESCE(SUM(i.total_price), 0) AS total_revenue
    FROM invoices i
    JOIN shift_works sw ON i.shift_work_id = sw.id
    WHERE i.deleted = 0
      AND i.invoice_type = 'PAID'
      AND sw.user_id = :userId
    GROUP BY DATE_FORMAT(i.created_at, '%m/%Y')
    ORDER BY MIN(i.created_at) DESC
""", nativeQuery = true)
    List<Object[]> findRevenueByUser(@Param("userId") Long userId);


    @Query(value = """
    SELECT
        s.name AS shiftName,
        COUNT(i.id) AS orderCount,
        SUM(CASE WHEN LOWER(i.payment_method) = 'cash' THEN i.total_price ELSE 0 END) AS cashTotal,
        SUM(CASE WHEN LOWER(i.payment_method) = 'transfer' THEN i.total_price ELSE 0 END) AS transferTotal,
        SUM(i.total_price) AS totalRevenue
    FROM invoices i
    JOIN shift_works sw ON i.shift_work_id = sw.id
    JOIN shifts s ON sw.shift_id = s.id
    WHERE sw.user_id = :userId
      AND i.invoice_type = 'PAID'
      AND i.deleted = 0
    GROUP BY s.name
    ORDER BY MIN(s.start_time)
""", nativeQuery = true)
    List<Object[]> findRevenueShiftByUser(@Param("userId") Long userId);



    @Query(value = "SELECT b.name, b.address, c.name, c.phone, i.created_at, i.total_price, i.description " +
            "FROM invoices i " +
            "JOIN customers c ON i.customer_id = c.id " +
            "JOIN branchs b ON i.branch_id = b.id " +
            "WHERE i.id = :id",
            nativeQuery = true)
    Optional<Object[]> findInvoiceInfoById(@Param("id") long id);

    @Query("""
SELECT new vn.edu.fpt.pharma.dto.manager.InvoiceSummary(
    i.id,
    i.invoiceCode,
    COALESCE(u.fullName, '') AS employeeName,
    COALESCE(s.name, '') AS shiftName,
    i.createdAt,
    i.totalPrice,
    COALESCE(CAST(SUM((d.price - d.costPrice) * d.quantity) AS double), 0)
)
FROM Invoice i
LEFT JOIN i.details d
LEFT JOIN ShiftWork sw ON i.shiftWorkId = sw.id
LEFT JOIN Shift s ON sw.id = s.id
LEFT JOIN User u ON i.userId = u.id
WHERE i.createdAt BETWEEN :fromDate AND :toDate
  AND (:branchId IS NULL OR i.branchId = :branchId)
  AND (:shift IS NULL OR s.id = :shift)
  AND (:employeeId IS NULL OR u.id = :employeeId)
GROUP BY i.id, i.invoiceCode, u.fullName, s.name, i.createdAt, i.totalPrice
ORDER BY i.createdAt DESC
""")
    List<InvoiceSummary> findInvoicesForReport(
            @Param("branchId") Long branchId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            @Param("shift") Long shift,
            @Param("employeeId") Long employeeId
    );


}
