package vn.edu.fpt.pharma.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.fpt.pharma.dto.manager.DailyRevenue;
import vn.edu.fpt.pharma.dto.manager.KpiData;
import vn.edu.fpt.pharma.dto.manager.TopProductItem;
import vn.edu.fpt.pharma.entity.Invoice;

import java.time.LocalDateTime;
import java.util.List;
import jakarta.persistence.Tuple;
public interface InvoiceRepository extends JpaRepository<Invoice, Long>, JpaSpecificationExecutor<Invoice> {
//    List<Invoice> findByBrandId(Long brandId);
    List<Invoice> findAllByInvoiceCodeIn(Collection<String> invoiceCodes);

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
    SUM(i.totalPrice),
    COUNT(DISTINCT i.id),
    SUM((id.price - id.costPrice) * id.quantity)
    )
    FROM Invoice i
    JOIN i.details id 
    WHERE i.createdAt >= :fromDate
      AND i.createdAt < :toDate
      AND i.branchId = :branchId
""")
    KpiData sumRevenue(
            @Param("branchId") Long branchId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );

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
      AND i.branchId = :branchId
    GROUP BY c.id, c.name
    ORDER BY SUM(id.quantity) DESC
""")
    List<TopProductItem> topCategories(
            @Param("branchId") Long branchId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable
    );







}