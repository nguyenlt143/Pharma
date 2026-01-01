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
        m.name AS medicineName,
        mv.strength AS strength,
        COALESCE(
            (SELECT u.name 
             FROM unit_conversions uc 
             JOIN units u ON uc.unit_id = u.id 
             WHERE uc.variant_id = inv.variant_id 
               AND uc.multiplier = idt.multiplier 
               AND uc.is_sale = TRUE 
             LIMIT 1),
            'Đơn vị'
        ) AS unitName,
        (idt.price * idt.multiplier) AS unitPrice,
        (idt.quantity / idt.multiplier) AS quantity
    FROM invoice_details idt
    JOIN inventory inv ON idt.inventory_id = inv.id
    JOIN medicine_variant mv ON inv.variant_id = mv.id
    JOIN medicines m ON mv.medicine_id = m.id
    WHERE idt.invoice_id = :id
    ORDER BY idt.id
""", nativeQuery = true)
    List<Object[]> findByInvoiceId(@Param("id") long id);

    @Query(value = """
    SELECT
        COALESCE(CONCAT(m.name, ' ', mv.strength), 'unknow') AS name,
        COALESCE(u.name, 'unknow') AS unit,
        COALESCE(b.batch_code, 'unknow') AS batch,
        COALESCE(m.manufacturer, 'unknow') AS manufacturer,
        COALESCE(m.country, 'unknow') AS country,
        COALESCE(SUM(idt.quantity) / idt.multiplier, 0) AS quantity,
        COALESCE(idt.price * idt.multiplier, 0) AS unitPrice,
        COALESCE(SUM(idt.quantity * idt.price), 0) AS total_amount
    FROM invoice_details idt
    JOIN invoices iv ON idt.invoice_id = iv.id
    LEFT JOIN inventory inv ON idt.inventory_id = inv.id
    LEFT JOIN prices p ON inv.variant_id = p.variant_id
    LEFT JOIN medicine_variant mv ON inv.variant_id = mv.id
    LEFT JOIN medicines m ON mv.medicine_id = m.id
    LEFT JOIN batches b ON inv.batch_id = b.id
    LEFT JOIN unit_conversions uc
        ON inv.variant_id = uc.variant_id
        AND uc.multiplier = idt.multiplier
        AND uc.is_sale = TRUE
    LEFT JOIN units u ON uc.unit_id = u.id
    LEFT JOIN shift_works sw ON iv.shift_work_id = sw.id
    LEFT JOIN shift_assignments sa ON sw.assignment_id = sa.id
    WHERE
        sa.user_id = :userId
        AND YEAR(iv.created_at) = :year
        AND MONTH(iv.created_at) = :month
    GROUP BY
        name,
        unit,
        batch,
        manufacturer,
        country,
        idt.price,
        idt.multiplier
    ORDER BY
        name, unitPrice;
    """, nativeQuery = true)
    List<Object[]> getMedicineRevenueByMonth(
            @Param("userId") Long userId,
            @Param("year") Integer year,
            @Param("month") Integer month
    );

    @Query(value = """
        SELECT
            COALESCE(CONCAT(m.name, ' ', mv.strength), 'unknow') AS name,
            COALESCE(u.name, 'unknow') AS unit,
            COALESCE(b.batch_code, 'unknow') AS batch,
            COALESCE(m.manufacturer, 'unknow') AS manufacturer,
            COALESCE(m.country, 'unknow') AS country,
            COALESCE(SUM(idt.quantity) / idt.multiplier, 0) AS quantity,
            COALESCE(idt.price * idt.multiplier, 0) AS unitPrice,
            COALESCE(SUM(idt.quantity * idt.price), 0) AS total_amount
        FROM invoice_details idt
        JOIN invoices iv ON idt.invoice_id = iv.id
        LEFT JOIN inventory inv ON idt.inventory_id = inv.id
        LEFT JOIN prices p ON inv.variant_id = p.variant_id
        LEFT JOIN medicine_variant mv ON inv.variant_id = mv.id
        LEFT JOIN medicines m ON mv.medicine_id = m.id
        LEFT JOIN batches b ON inv.batch_id = b.id
        LEFT JOIN unit_conversions uc
            ON inv.variant_id = uc.variant_id
            AND uc.multiplier = idt.multiplier
            AND uc.is_sale = TRUE
        LEFT JOIN units u ON uc.unit_id = u.id
        LEFT JOIN shift_works sw ON iv.shift_work_id = sw.id
        LEFT JOIN shift_assignments sa ON sw.assignment_id = sa.id
        LEFT JOIN shifts s ON sa.shift_id = s.id
        WHERE
            sa.user_id = :userId
            AND s.name = :shiftName
            AND DATE(CONVERT_TZ(sw.work_date, '+00:00', '+07:00')) = :workDate
        GROUP BY
            name,
            unit,
            batch,
            manufacturer,
            country,
            idt.price,
            idt.multiplier
        ORDER BY
            name, unitPrice;
    """, nativeQuery = true)
    List<Object[]> getMedicineRevenueByShift(
            @Param("userId") Long userId,
            @Param("shiftName") String shiftName,
            @Param("workDate") String workDate
    );
}