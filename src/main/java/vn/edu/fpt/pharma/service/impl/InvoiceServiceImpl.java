package vn.edu.fpt.pharma.service.impl;

import jakarta.persistence.criteria.Join;

import org.springframework.stereotype.Service;
import vn.edu.fpt.pharma.base.BaseServiceImpl;
import vn.edu.fpt.pharma.dto.DataTableRequest;
import vn.edu.fpt.pharma.dto.DataTableResponse;
import vn.edu.fpt.pharma.dto.invoice.InvoiceDetailVM;
import vn.edu.fpt.pharma.dto.invoice.InvoiceInfoVM;
import vn.edu.fpt.pharma.dto.invoice.MedicineItemVM;
import vn.edu.fpt.pharma.entity.Invoice;
import vn.edu.fpt.pharma.repository.InvoiceDetailRepository;
import vn.edu.fpt.pharma.repository.InvoiceRepository;
import vn.edu.fpt.pharma.service.AuditService;
import vn.edu.fpt.pharma.service.InvoiceDetailService;
import vn.edu.fpt.pharma.service.InvoiceService;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvoiceServiceImpl extends BaseServiceImpl<Invoice, Long, InvoiceRepository> implements InvoiceService {

    private InvoiceDetailService invoiceDetailService;

    public InvoiceServiceImpl(InvoiceRepository repository, AuditService auditService, InvoiceDetailService invoiceDetailService) {
        super(repository, auditService);
        this.invoiceDetailService = invoiceDetailService;
    }


    @Override
    public DataTableResponse<Invoice> findAllInvoices(DataTableRequest request) {
        return null;
    }

    public DataTableResponse<Invoice> findAllInvoices(DataTableRequest request, Long userId) {
        DataTableResponse<Invoice> invoices = findAllForDataTable(request, List.of("invoiceCode", "customer.name"),  userId);
        return invoices.transform(auditService::addAuditInfo);
    }

    @Override
    public InvoiceDetailVM getInvoiceDetail(Long invoiceId) {
        InvoiceInfoVM info = repository.findInvoiceInfoById(invoiceId);
        List<MedicineItemVM> listMedicine = invoiceDetailService.getListMedicine(invoiceId);

        return new InvoiceDetailVM(
                info.getBranchName(),
                info.getBranchAddress(),
                info.getCustomerName(),
                info.getCustomerPhone(),
                info.getCreatedAt(),
                info.getTotalPrice(),
                info.getDescription(),
                listMedicine
        );
    }
}
