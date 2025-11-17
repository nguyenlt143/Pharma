package vn.edu.fpt.pharma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.fpt.pharma.dto.invoice.MedicineItemVM;
import vn.edu.fpt.pharma.entity.InvoiceDetail;

import java.util.List;
import java.util.Optional;

public interface InvoiceDetailRepository extends JpaRepository<InvoiceDetail, Long>, JpaSpecificationExecutor<InvoiceDetail> {

    @Query(value = """
        SELECT
            CASE
                WHEN mv.strength IS NOT NULL AND mv.strength != ''
                    THEN CONCAT(m.active_ingredient, ' ', mv.strength)
                ELSE m.name
            END AS name,
            u.name AS unit,
            idt.price AS price,
            idt.quantity AS quantity
        FROM invoice_details idt
        JOIN medicine_variant mv ON idt.variant_id = mv.id
        JOIN units u ON mv.base_unit_id = u.id
        JOIN medicines m ON mv.medicine_id = m.id
        WHERE idt.invoice_id = :id
""", nativeQuery = true)
    List<Object[]> findByInvoiceId(@Param("id") long id);

    @Query(value = """
    SELECT 
        m.name AS drug_name,
        SUM(idt.quantity) AS quantity,
        u.name AS unit,
        m.active_ingredient AS active_ingredient,
        m.manufacturer AS manufacturer,
        m.country AS country,
        idt.price AS price,
        SUM(idt.quantity * idt.price) AS total_amount
    FROM invoice_details idt
    JOIN invoices i ON idt.invoice_id = i.id
    JOIN medicine_variant mv ON idt.variant_id = mv.id
    JOIN medicines m ON mv.medicine_id = m.id
    JOIN units u ON mv.base_unit_id = u.id
    JOIN shift_works sw ON i.shift_work_id = sw.id
    JOIN shift_assignments sa ON sw.assignment_id = sa.id
    JOIN shifts s ON sa.shift_id = s.id
    WHERE 
        sa.user_id = :userId
        AND YEAR(i.created_at) = :year
        AND MONTH(i.created_at) = :month
    GROUP BY 
        m.name, u.name, m.active_ingredient, m.manufacturer, m.country, idt.price
    ORDER BY m.name
    """, nativeQuery = true)
    List<Object[]> getMedicineRevenueByMonth(
            @Param("userId") Long userId,
            @Param("year") Integer year,
            @Param("month") Integer month
    );

}