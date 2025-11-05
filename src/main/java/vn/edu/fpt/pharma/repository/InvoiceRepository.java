package vn.edu.fpt.pharma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.fpt.pharma.entity.Invoice;

import java.util.Collection;
import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long>, JpaSpecificationExecutor<Invoice> {


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
        MIN(s.start_time) AS startTime,
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
    List<Object[]> findRevenueShift(@Param("userId") Long userId);

}