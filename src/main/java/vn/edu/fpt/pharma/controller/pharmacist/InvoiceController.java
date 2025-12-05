package vn.edu.fpt.pharma.controller.pharmacist;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.fpt.pharma.config.CustomUserDetails;
import vn.edu.fpt.pharma.dto.DataTableRequest;
import vn.edu.fpt.pharma.dto.DataTableResponse;
import vn.edu.fpt.pharma.dto.invoice.InvoiceDetailVM;
import vn.edu.fpt.pharma.dto.invoice.InvoiceVM;
import vn.edu.fpt.pharma.service.InvoiceDetailService;
import vn.edu.fpt.pharma.service.InvoiceService;
import vn.edu.fpt.pharma.util.StringUtils;

import java.util.List;

@Slf4j
@Controller // RE-ENABLED for invoice viewing
@RequiredArgsConstructor
@RequestMapping("/pharmacist/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;


    @GetMapping
    public String invoices(Model model){
        // Thêm success và error messages (sẽ null nếu không có)
        model.addAttribute("success", null);
        model.addAttribute("error", null);
        return "pages/pharmacist/invoices";
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex, RedirectAttributes redirectAttributes){
        log.error("Exception: ", ex);
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/pharmacist/invoices";
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handleValidation(MethodArgumentNotValidException ex, RedirectAttributes redirectAttributes){
        log.error("MethodArgumentNotValidException: ", ex);
        String errorMsg = StringUtils.convertValidationExceptionToString(ex.getBindingResult().getAllErrors());
        redirectAttributes.addFlashAttribute("error", errorMsg);
        return "redirect:/pharmacist/invoices";
    }

    @GetMapping("all")
    public ResponseEntity<DataTableResponse<InvoiceVM>> getAllInvoices(HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Long userId = userDetails.getId();
        DataTableRequest reqDto = DataTableRequest.fromParams(request.getParameterMap());
        return ResponseEntity.ok(invoiceService.findAllInvoices(reqDto, userId).map(InvoiceVM::new));
    }

    @GetMapping("detail")
    public String viewDetails(@RequestParam("invoiceId") Long invoiceId, Model model, RedirectAttributes redirectAttributes){
        // Validation cho invoiceId
        if (invoiceId == null || invoiceId <= 0) {
            redirectAttributes.addFlashAttribute("error", "ID hóa đơn không hợp lệ");
            return "redirect:/pharmacist/invoices";
        }

        try {
            // Verify user access to this invoice
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();

            InvoiceDetailVM invoiceDetailVM = invoiceService.getInvoiceDetail(invoiceId);

            // Check if invoice was found
            if (invoiceDetailVM == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy hóa đơn với ID: " + invoiceId);
                return "redirect:/pharmacist/invoices";
            }

            // Check if customer info is properly handled
            log.info("Invoice detail retrieved - Customer: {}, Phone: {}",
                    invoiceDetailVM.customerName(), invoiceDetailVM.customerPhone());

            model.addAttribute("user", userDetails);
            model.addAttribute("invoice", invoiceDetailVM);
            model.addAttribute("medicines", invoiceDetailVM.medicines());
            return "pages/pharmacist/invoice_detail";

        } catch (Exception e) {
            log.error("Error retrieving invoice detail for ID: {}", invoiceId, e);
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xem chi tiết hóa đơn: " + e.getMessage());
            return "redirect:/pharmacist/invoices";
        }
    }
}
