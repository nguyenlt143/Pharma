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
import vn.edu.fpt.pharma.exception.InsufficientInventoryException;
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
        // Xử lý tìm kiếm "Khách lẻ" - nếu search value khớp với bất kỳ phần nào của "khách lẻ"
        String searchValue = request.searchValue();
        if (searchValue != null && !searchValue.trim().isEmpty()) {
            String searchLower = searchValue.trim().toLowerCase()
                .replace("á", "a")
                .replace("ă", "a")
                .replace("â", "a")
                .replace("é", "e")
                .replace("ê", "e")
                .replace("ế", "e")
                .replace("ề", "e")
                .replace("ệ", "e")
                .replace("ỉ", "i")
                .replace("í", "i")
                .replace("ó", "o")
                .replace("ô", "o")
                .replace("ơ", "o")
                .replace("ú", "u")
                .replace("ư", "u")
                .replace("ý", "y");

            String khachLe = "khach le";

            // Kiểm tra nếu "khach le" chứa search value (tìm từng phần)
            // Ví dụ: "kh" -> có trong "khach", "ach" -> có trong "khach", "le" -> có trong "le"
            if (khachLe.contains(searchLower)) {
                // Tìm các invoice không có customer hoặc customer name rỗng
                DataTableResponse<Invoice> invoices = findInvoicesWithoutCustomer(request, userId);
                return invoices.transform(auditService::addAuditInfo);
            }
        }

        DataTableResponse<Invoice> invoices = findAllForDataTable(request, List.of("invoiceCode", "customer.name"),  userId);
        return invoices.transform(auditService::addAuditInfo);
    }

    private DataTableResponse<Invoice> findInvoicesWithoutCustomer(DataTableRequest request, Long userId) {
        // Tạo một request mới với search value rỗng
        DataTableRequest modifiedRequest = new DataTableRequest(
            request.draw(),
            request.start(),
            request.length(),
            "", // Xóa search value
            request.orderColumn(),
            request.orderDir()
        );

        // Lấy tất cả invoices của user
        DataTableResponse<Invoice> response = findAllForDataTable(modifiedRequest, List.of("invoiceCode"),  userId);

        // Filter chỉ lấy các invoice có customer null hoặc name rỗng
        List<Invoice> filteredData = response.data().stream()
            .filter(invoice -> invoice.getCustomer() == null ||
                              invoice.getCustomer().getName() == null ||
                              invoice.getCustomer().getName().trim().isEmpty())
            .toList();

        return new DataTableResponse<>(
            request.draw(),
            response.recordsTotal(),
            filteredData.size(),
            filteredData
        );
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
                info.getInvoiceCode(),
                info.getBranchName(),
                info.getBranchAddress(),
                info.getCustomerName(),
                info.getCustomerPhone(),
                info.getCreatedAt(),
                info.getTotalPrice(),
                info.getPaymentMethod(),
                info.getDescription(),
                listMedicine
        );
    }

    @Override
    public Invoice createInvoice(InvoiceCreateRequest req) {
        // Set default customer name if empty
        String customerName = req.getCustomerName();
        if (customerName == null || customerName.trim().isEmpty()) {
            customerName = "Khách lẻ";
        }

        // Validate and set default phone number if empty
        String phoneNumber = req.getPhoneNumber();
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            phoneNumber = "Không có";
        }

        // Validate phone number format if not empty and not default
        if (!phoneNumber.equals("Không có") && !phoneNumber.trim().isEmpty()) {
            if (!phoneNumber.matches("^(0|\\+84)[0-9]{9,10}$")) {
                throw new RuntimeException("Số điện thoại không đúng định dạng");
            }
        }

        // Create customer BEFORE transaction to avoid rollback conflicts
        Customer customer = null;
        if (!phoneNumber.equals("Không có")) {
            try {
                customer = customerService.getOrCreate(customerName, phoneNumber);
            } catch (Exception e) {
                // Log and continue without customer if creation fails
                System.err.println("Warning: Could not create customer: " + e.getMessage());
                customer = null;
            }
        }

        // Now start the main transaction for invoice creation
        return createInvoiceTransaction(req, customer);

    }

    @Transactional(rollbackFor = Exception.class)
    private Invoice createInvoiceTransaction(InvoiceCreateRequest req, Customer customer) {
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

        // Validate inventory first before starting transaction
        List<Inventory> inventories = new ArrayList<>();
        List<Long> realQuantities = new ArrayList<>();

        for (InvoiceItemRequest itemReq : req.getItems()) {
            Inventory inventory = inventoryService.findById(itemReq.getInventoryId());
            Long realQty = (long) (itemReq.getQuantity() * itemReq.getSelectedMultiplier());

            if (inventory.getQuantity() < realQty) {
                throw new InsufficientInventoryException(
                    String.format("Tồn kho không đủ cho thuốc '%s' (Số lô: %s). Còn lại: %d, yêu cầu: %d",
                        inventory.getVariant().getMedicine().getName(),
                        inventory.getBatch().getBatchCode(),
                        inventory.getQuantity(),
                        realQty)
                );
            }

            inventories.add(inventory);
            realQuantities.add(realQty);
        }

        // Save invoice first
        invoice = repository.save(invoice);

        // Process inventory and details
        List<InvoiceDetail> details = new ArrayList<>();
        for (int i = 0; i < req.getItems().size(); i++) {
            InvoiceItemRequest itemReq = req.getItems().get(i);
            Inventory inventory = inventories.get(i);
            Long realQty = realQuantities.get(i);

            // Update inventory quantity
            inventory.setQuantity(inventory.getQuantity() - realQty);
            inventoryRepository.save(inventory);

            // Create invoice detail
            InvoiceDetail detail = new InvoiceDetail();
            detail.setInvoice(invoice);
            detail.setInventory(inventory);
            detail.setQuantity((long) (itemReq.getQuantity()*itemReq.getSelectedMultiplier()));
            detail.setPrice(itemReq.getUnitPrice()/itemReq.getSelectedMultiplier());
            detail.setMultiplier(itemReq.getSelectedMultiplier());
            details.add(detail);
        }

        // Batch save details
        invoiceDetailRepository.saveAll(details);
        invoice.setDetails(details);

        return invoice;
    }

    @Override
    public synchronized String generateInvoiceCode() {
        String date = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        Long maxId = repository.findMaxInvoiceId();
        long next = (maxId != null ? maxId : 0) + 1;
        // Format: INV-20250201-000123
        return "INV-" + date + "-" + String.format("%06d", next);
    }
}
