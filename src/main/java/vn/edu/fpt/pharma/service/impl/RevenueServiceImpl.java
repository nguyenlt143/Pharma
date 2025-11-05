package vn.edu.fpt.pharma.service.impl;

import org.springframework.stereotype.Service;
import vn.edu.fpt.pharma.base.BaseServiceImpl;
import vn.edu.fpt.pharma.dto.DataTableRequest;
import vn.edu.fpt.pharma.dto.reveuce.RevenueVM;
import vn.edu.fpt.pharma.entity.Invoice;
import vn.edu.fpt.pharma.repository.InvoiceRepository;
import vn.edu.fpt.pharma.service.AuditService;
import vn.edu.fpt.pharma.service.RevenueService;

import java.util.List;

@Service
public class RevenueServiceImpl extends BaseServiceImpl<Invoice, Long, InvoiceRepository> implements RevenueService {

    public RevenueServiceImpl(InvoiceRepository repository, AuditService auditService) {
        super(repository, auditService);
    }

    @Override
    public List<RevenueVM> getRevenueSummary() {
        List<Object[]> rows = repository.findRevenue();
        return rows.stream()
                .map(r -> new RevenueVM(
                        (String) r[0],
                        ((Number) r[1]).longValue(),
                        ((Number) r[2]).longValue(),
                        ((Number) r[3]).doubleValue()
                ))
                .toList();
    }

    @Override
    public List<RevenueVM> getRevenueShiftSummary() {
        List<Object[]> rows = repository.findRevenueShift();
        return rows.stream()
                .map(r -> new RevenueVM(
                        (String) r[0],
                        ((Number) r[1]).longValue(),
                        ((Number) r[2]).longValue(),
                        ((Number) r[3]).doubleValue()
                ))
                .toList();
    }
}
