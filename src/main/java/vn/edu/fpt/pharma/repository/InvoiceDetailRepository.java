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
        JOIN units u ON mv.package_unit_id = u.id
        JOIN medicines m ON mv.medicine_id = m.id
        WHERE idt.invoice_id = :id
""", nativeQuery = true)
    List<Object[]> findByInvoiceId(@Param("id") long id);
}