package vn.edu.fpt.pharma.service;

import org.springframework.web.bind.annotation.RequestParam;
import vn.edu.fpt.pharma.base.BaseService;
import vn.edu.fpt.pharma.dto.DataTableRequest;
import vn.edu.fpt.pharma.dto.DataTableResponse;
import vn.edu.fpt.pharma.dto.invoice.InvoiceCreateRequest;
import vn.edu.fpt.pharma.dto.invoice.InvoiceDetailVM;
import vn.edu.fpt.pharma.entity.Invoice;

public interface InvoiceService extends BaseService<Invoice, Long> {

    DataTableResponse<Invoice> findAllInvoices(DataTableRequest request);

    DataTableResponse<Invoice> findAllInvoices(DataTableRequest request, Long userId);

    InvoiceDetailVM getInvoiceDetail(Long invoiceId);

    Invoice createInvoice(InvoiceCreateRequest req);

    String generateInvoiceCode();
}
