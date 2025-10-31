package vn.edu.fpt.pharma.service.impl;

import org.springframework.stereotype.Service;
import vn.edu.fpt.pharma.base.BaseServiceImpl;
import vn.edu.fpt.pharma.dto.DataTableRequest;
import vn.edu.fpt.pharma.dto.DataTableResponse;
import vn.edu.fpt.pharma.entity.Invoice;
import vn.edu.fpt.pharma.repository.InvoiceRepository;
import vn.edu.fpt.pharma.service.AuditService;
import vn.edu.fpt.pharma.service.CsvService;
import vn.edu.fpt.pharma.service.InvoiceService;

import java.util.List;

@Service
public class InvoiceServiceImpl extends BaseServiceImpl<Invoice, Long, InvoiceRepository> implements InvoiceService {

    public InvoiceServiceImpl(InvoiceRepository repository, AuditService auditService) {
        super(repository, auditService);
    }

    @Override
    public DataTableResponse<Invoice> findAllInvoices(DataTableRequest request) {
        DataTableResponse<Invoice> invoices = findAllForDataTable(request, List.of("invoiceNumber", "customerName"));
        return invoices.transform(auditService::addAuditInfo);
    }

}
