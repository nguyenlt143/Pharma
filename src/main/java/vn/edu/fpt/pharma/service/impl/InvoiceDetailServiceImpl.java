package vn.edu.fpt.pharma.service.impl;

import org.springframework.stereotype.Service;
import vn.edu.fpt.pharma.base.BaseServiceImpl;
import vn.edu.fpt.pharma.dto.invoice.MedicineItemVM;
import vn.edu.fpt.pharma.dto.reveuce.RevenueVM;
import vn.edu.fpt.pharma.entity.InvoiceDetail;
import vn.edu.fpt.pharma.repository.InvoiceDetailRepository;
import vn.edu.fpt.pharma.service.AuditService;
import vn.edu.fpt.pharma.service.InvoiceDetailService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvoiceDetailServiceImpl extends BaseServiceImpl<InvoiceDetail, Long, InvoiceDetailRepository> implements InvoiceDetailService {

    public InvoiceDetailServiceImpl(InvoiceDetailRepository repository, AuditService auditService) {
        super(repository, auditService);
    }

    @Override
    public List<MedicineItemVM> getListMedicine(Long invoiceId) {
        List<Object[]> rows = repository.findByInvoiceId(invoiceId);

        return rows.stream()
                .map(r -> new MedicineItemVM(
                        r[0] != null ? r[0].toString() : "",
                        r[1] != null ? r[1].toString() : "",
                        r[2] != null ? ((Number) r[2]).doubleValue() : 0.0,
                        r[3] != null ? ((Number) r[3]).longValue() : 0L
                ))
                .collect(Collectors.toList());
    }

}
