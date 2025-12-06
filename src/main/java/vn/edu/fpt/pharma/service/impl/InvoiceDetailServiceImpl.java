package vn.edu.fpt.pharma.service.impl;

import org.springframework.stereotype.Service;
import vn.edu.fpt.pharma.base.BaseServiceImpl;
import vn.edu.fpt.pharma.dto.invoice.MedicineItemVM;
import vn.edu.fpt.pharma.dto.reveuce.RevenueDetailVM;
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
                        r[0] != null ? r[0].toString() : "",                    // medicineName
                        r[1] != null ? r[1].toString() : "",                    // strength
                        r[2] != null ? r[2].toString() : "",                    // unitName
                        r[3] != null ? ((Number) r[3]).doubleValue() : 0.0,     // unitPrice
                        r[4] != null ? ((Number) r[4]).longValue() : 0L         // quantity
                ))
                .collect(Collectors.toList());
    }
//    @Override
//    public List<RevenueDetailVM> getRevenueDetail(Long userId, Integer year, Integer month) {
//        List<Object[]> rows = repository.getMedicineRevenueByMonth(userId, year, month);
//
//        return rows.stream()
//                .map(r -> new RevenueDetailVM(
//                        (String) r[0],
//                        ((Number) r[1]).longValue(),
//                        (String) r[2],
//                        (String) r[3],
//                        (String) r[4],
//                        (String) r[5],
//                        ((Number) r[6]).doubleValue(),
//                        ((Number) r[7]).doubleValue()
//                ))
//                .collect(Collectors.toList());
//    }
}
