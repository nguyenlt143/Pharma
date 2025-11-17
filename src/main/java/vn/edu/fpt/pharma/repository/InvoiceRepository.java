package vn.edu.fpt.pharma.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import vn.edu.fpt.pharma.dto.invoice.InvoiceInfoVM;
import vn.edu.fpt.pharma.dto.manager.DailyRevenue;
import vn.edu.fpt.pharma.dto.manager.KpiData;
import vn.edu.fpt.pharma.dto.manager.TopProductItem;
import vn.edu.fpt.pharma.dto.manager.InvoiceListItem;

import vn.edu.fpt.pharma.dto.invoice.InvoiceInfoVM;
import vn.edu.fpt.pharma.dto.manager.*;
import vn.edu.fpt.pharma.entity.Invoice;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long>, JpaSpecificationExecutor<Invoice> {
//    List<Invoice> findByBrandId(Long brandId);

    // doanh thu theo ngay
    @Query(value = """
    SELECT DATE(i.created_at) AS date,
           SUM(i.total_price) AS revenue
    FROM invoices i
    WHERE i.created_at >= :fromDate
      AND i.created_at < :toDate
      AND i.deleted = false
      AND (:branchId IS NULL OR i.branch_id = :branchId)
      AND (:shiftId IS NULL OR i.shift_work_id = :shiftId)
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
    
    default List<DailyRevenue> getDailyRevenueByDate(Long branchId, LocalDateTime fromDate, LocalDateTime toDate) {
        return getDailyRevenueByDate(branchId, fromDate, toDate, null, null);
    }
    // KPI total - using native query to get cost price from inventory_movement_details
    @Query(value = """
        SELECT 
            COALESCE(SUM(i.total_price), 0) AS revenue,
            COALESCE(COUNT(DISTINCT i.id), 0) AS order_count,
            COALESCE(SUM(
                (id.price - COALESCE(
                    (SELECT imd.price 
                     FROM inventory_movement_details imd 
                     JOIN inventory_movements im ON imd.movement_id = im.id
                     WHERE imd.batch_id = id.batch_id 
                       AND im.movement_type IN ('SUP_TO_WARE', 'WARE_TO_BR')
                       AND imd.deleted = false
                     ORDER BY im.created_at DESC, imd.created_at DESC
                     LIMIT 1),
                    id.price * 0.7
                )) * id.quantity
            ), 0) AS profit
        FROM invoices i
        LEFT JOIN invoice_details id ON i.id = id.invoice_id AND id.deleted = false
        WHERE i.created_at >= :fromDate
          AND i.created_at < :toDate
          AND i.deleted = false
          AND (:branchId IS NULL OR i.branch_id = :branchId)
          AND (:shiftId IS NULL OR i.shift_work_id = :shiftId)
          AND (:employeeId IS NULL OR i.user_id = :employeeId)
        """, nativeQuery = true)
    List<Object[]> sumRevenueNative(
            @Param("branchId") Long branchId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            @Param("shiftId") Long shiftId,
            @Param("employeeId") Long employeeId
    );
    
    default KpiData sumRevenue(Long branchId, LocalDateTime fromDate, LocalDateTime toDate, Long shiftId, Long employeeId) {
        List<Object[]> results = sumRevenueNative(branchId, fromDate, toDate, shiftId, employeeId);
        if (results == null || results.isEmpty() || results.get(0) == null) {
            return new KpiData(0.0, 0L, 0.0);
        }
        Object[] result = results.get(0);
        if (result.length < 3) {
            return new KpiData(0.0, 0L, 0.0);
        }
        
        // Handle BigDecimal/Double conversion
        Double revenue = result[0] != null ? ((Number) result[0]).doubleValue() : 0.0;
        Long orderCount = result[1] != null ? ((Number) result[1]).longValue() : 0L;
        Double profit = result[2] != null ? ((Number) result[2]).doubleValue() : 0.0;
        
        return new KpiData(revenue, orderCount, profit);
    }

    default KpiData sumRevenue(Long branchId, LocalDateTime fromDate, LocalDateTime toDate) {
        return sumRevenue(branchId, fromDate, toDate, null, null);
    }


    // top 5 item ????

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
      AND (:branchId IS NULL OR i.branchId = :branchId)
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
        c.name AS categoryName,
        SUM(COALESCE(id.price, 0) * COALESCE(id.quantity, 0)) AS revenue
    FROM Invoice i
    JOIN i.details id
    JOIN id.variant mv
    JOIN mv.medicine m
    JOIN m.category c
    WHERE i.createdAt >= :fromDate
      AND i.createdAt < :toDate
      AND (:branchId IS NULL OR i.branchId = :branchId)
      AND (:shiftId IS NULL OR i.shiftWorkId = :shiftId)
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
    JOIN shift_assignments sa ON sw.assignment_id = sa.id
    WHERE i.deleted = 0
      AND i.invoice_type = 'PAID'
      AND sa.user_id = :userId
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
    JOIN shift_assignments sa ON sw.assignment_id = sa.id
    JOIN shifts s ON sa.shift_id = s.id
    WHERE sa.user_id = :userId
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
    InvoiceInfoVM findInvoiceInfoById(@Param("id") long id);

    @Query(value = """
        SELECT 
            i.id,
            i.invoice_code,
            COALESCE(u.full_name, '') AS full_name,
            COALESCE(s.name, '') AS shift_name,
            i.created_at,
            i.total_price,
            COALESCE(SUM(
                (id.price - COALESCE(
                    (SELECT imd.price 
                     FROM inventory_movement_details imd 
                     JOIN inventory_movements im ON imd.movement_id = im.id
                     WHERE imd.batch_id = id.batch_id 
                       AND im.movement_type IN ('SUP_TO_WARE', 'WARE_TO_BR')
                       AND imd.deleted = false
                     ORDER BY im.created_at DESC, imd.created_at DESC
                     LIMIT 1),
                    id.price * 0.7
                )) * id.quantity
            ), 0) AS profit
        FROM invoices i
        LEFT JOIN invoice_details id ON i.id = id.invoice_id AND id.deleted = false
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
        GROUP BY i.id, i.invoice_code, u.full_name, s.name, i.created_at, i.total_price
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
            Long branchId,
            LocalDateTime fromDate,
            LocalDateTime toDate,
            Long shift,
            Long employeeId) {
        List<Object[]> results = findInvoicesForReportNative(branchId, fromDate, toDate, shift, employeeId);
        return results.stream()
                .map(row -> new InvoiceSummary(
                        ((Number) row[0]).longValue(), // id
                        (String) row[1], // invoice_code
                        (String) row[2], // full_name
                        (String) row[3], // shift_name
                        ((java.sql.Timestamp) row[4]).toLocalDateTime(), // created_at
                        ((Number) row[5]).doubleValue(), // total_price
                        ((Number) row[6]).doubleValue() // profit
                ))
                .toList();
    }


}
