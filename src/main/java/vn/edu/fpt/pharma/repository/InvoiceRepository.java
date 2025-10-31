package vn.edu.fpt.pharma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.fpt.pharma.dto.manager.DailyRevenue;
import vn.edu.fpt.pharma.dto.manager.KpiData;
import vn.edu.fpt.pharma.entity.Invoice;

import java.time.LocalDateTime;
import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long>, JpaSpecificationExecutor<Invoice> {
    List<Invoice> findByBrandId(Long brandId);

    // doanh thu theo ngay
    @Query("""
    SELECT 
        DATE(i.createdAt) AS date,
        SUM(i.totalPrice) AS revenue
    FROM Invoice i
    WHERE i.createdAt >= :fromDate
      AND i.createdAt < :toDate
      AND i.branchId = :branchId
    GROUP BY DATE(i.createdAt)
    ORDER BY DATE(i.createdAt)
""")
    List<DailyRevenue> getDailyRevenueByDate(
            @Param("branchId") Long branchId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );
    // KPI total
    @Query("""
    SELECT
        SUM(i.totalPrice) AS revenue,
        COUNT(DISTINCT i.id) AS orderCount,
        SUM((id.price - id.costPrice) * id.quantity) AS profit
    FROM Invoice i
    JOIN InvoiceDetail id ON id.invoice.id = i.id
    WHERE i.createdAt >= :fromDate
      AND i.createdAt < :toDate
      AND i.branchId = :branchId
""")
    KpiData sumRevenue(
            @Param("branchId") Long branchId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );

}