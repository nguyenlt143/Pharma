package vn.edu.fpt.pharma.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.pharma.base.BaseServiceImpl;
import vn.edu.fpt.pharma.constant.InvoiceType;
import vn.edu.fpt.pharma.dto.DataTableRequest;
import vn.edu.fpt.pharma.dto.DataTableResponse;
import vn.edu.fpt.pharma.dto.invoice.InvoiceDetailVM;
import vn.edu.fpt.pharma.dto.invoice.InvoiceInfoVM;
import vn.edu.fpt.pharma.dto.invoice.MedicineItemVM;
import vn.edu.fpt.pharma.dto.invoice.InvoiceCreateRequest;
import vn.edu.fpt.pharma.dto.invoice.InvoiceItemRequest;
import vn.edu.fpt.pharma.entity.Customer;
import vn.edu.fpt.pharma.entity.Inventory;
import vn.edu.fpt.pharma.entity.Invoice;
import vn.edu.fpt.pharma.entity.InvoiceDetail;
import vn.edu.fpt.pharma.repository.InventoryRepository;
import vn.edu.fpt.pharma.repository.InvoiceDetailRepository;
import vn.edu.fpt.pharma.repository.InvoiceRepository;
import vn.edu.fpt.pharma.service.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


@Service
public class InvoiceServiceImpl extends BaseServiceImpl<Invoice, Long, InvoiceRepository> implements InvoiceService {

    private final InvoiceDetailService invoiceDetailService;
    private final CustomerService customerService;
    private final UserContext userContext;
    private final InventoryService inventoryService;
    private final InventoryRepository inventoryRepository;
    private final InvoiceDetailRepository invoiceDetailRepository;

    public InvoiceServiceImpl(InvoiceRepository repository, AuditService auditService, InvoiceDetailService invoiceDetailService, CustomerService customerService, UserContext userContext, InventoryService inventoryService,
                              InventoryRepository inventoryRepository, InvoiceDetailRepository invoiceDetailRepository) {
        super(repository, auditService);
        this.invoiceDetailService = invoiceDetailService;
        this.customerService = customerService;
        this.userContext = userContext;
        this.inventoryService = inventoryService;
        this.inventoryRepository = inventoryRepository;
        this.invoiceDetailRepository = invoiceDetailRepository;
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
        // Check if invoice exists first
        if (!repository.existsById(invoiceId)) {
            throw new RuntimeException("Không tìm thấy hóa đơn với ID: " + invoiceId);
        }

        InvoiceInfoVM info = repository.findInvoiceInfoById(invoiceId);

        // Double check if query returned result
        if (info == null) {
            throw new RuntimeException("Không thể truy xuất thông tin hóa đơn ID: " + invoiceId);
        }

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

    @Override
    @Transactional
    public Invoice createInvoice(InvoiceCreateRequest req) {
        // Set default customer name if empty
        String customerName = req.getCustomerName();
        if (customerName == null || customerName.trim().isEmpty()) {
            customerName = "Khách lẻ";
        }

        Customer customer = null;
        // Only create customer if phone number is provided and not the default "Không có"
        if(req.getPhoneNumber() != null &&
           !req.getPhoneNumber().isEmpty() &&
           !req.getPhoneNumber().equals("Không có")){
            customer = customerService.getOrCreate(customerName, req.getPhoneNumber());
        }
        Invoice invoice = new Invoice();
        invoice.setInvoiceCode(generateInvoiceCode());
        invoice.setCustomer(customer);
        invoice.setPaymentMethod(req.getPaymentMethod());
        invoice.setTotalPrice(req.getTotalAmount());
        invoice.setDescription(req.getNote());
        invoice.setUserId(userContext.getUserId());
        invoice.setBranchId(userContext.getBranchId());
        invoice.setShiftWorkId(userContext.getShiftWorkId());
        invoice.setInvoiceType(InvoiceType.PAID);

        invoice = repository.save(invoice);
        List<InvoiceDetail> details = new ArrayList<>();
        for (InvoiceItemRequest itemReq : req.getItems()){
            Inventory inventory = inventoryService.findById(itemReq.getInventoryId());
            Long realQty = (long) (itemReq.getQuantity() * itemReq.getSelectedMultiplier());
            if(inventory.getQuantity() < realQty){
                throw new RuntimeException("Tồn kho không đủ");
            }
            inventory.setQuantity(inventory.getQuantity() - realQty);
            inventoryRepository.save(inventory);

            InvoiceDetail detail = new InvoiceDetail();
            detail.setInvoice(invoice);
            detail.setInventory(inventory);
            detail.setQuantity(itemReq.getQuantity());
            detail.setPrice(itemReq.getUnitPrice() * itemReq.getSelectedMultiplier());

            details.add(detail);
            invoiceDetailRepository.save(detail);
        }
        invoice.setDetails(details);

        return invoice;
    }

    @Override
    public String generateInvoiceCode() {
        String date = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        Long maxId = repository.findMaxInvoiceId();
        long next = maxId + 1;
        // Format: INV-20250201-000123
        return "INV-" + date + "-" + String.format("%06d", next);
    }
}
