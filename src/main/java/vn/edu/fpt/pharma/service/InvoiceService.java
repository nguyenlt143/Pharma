package vn.edu.fpt.pharma.service;

import vn.edu.fpt.pharma.base.BaseService;
import vn.edu.fpt.pharma.dto.DataTableRequest;
import vn.edu.fpt.pharma.dto.DataTableResponse;
import vn.edu.fpt.pharma.entity.Invoice;

public interface InvoiceService extends BaseService<Invoice, Long> {
    DataTableResponse<Invoice> findAllInvoices(DataTableRequest request, Long userId);
}
