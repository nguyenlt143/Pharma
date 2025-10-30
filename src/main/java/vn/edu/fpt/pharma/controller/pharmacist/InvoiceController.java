package vn.edu.fpt.pharma.controller.pharmacist;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.fpt.pharma.dto.DataTableRequest;
import vn.edu.fpt.pharma.dto.DataTableResponse;
import vn.edu.fpt.pharma.dto.invoice.InvoiceVM;
import vn.edu.fpt.pharma.service.InvoiceService;
import vn.edu.fpt.pharma.util.StringUtils;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/pharmacist/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @GetMapping
    public String index(){
        return "pages/pharmacist/index";
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex, RedirectAttributes redirectAttributes){
        log.error("Exception: ", ex);
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/pharmacist/invoice";
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handleValidation(MethodArgumentNotValidException ex, RedirectAttributes redirectAttributes){
        log.error("MethodArgumentNotValidException: ", ex);
        String errorMsg = StringUtils.convertValidationExceptionToString(ex.getBindingResult().getAllErrors());
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/pharmacist/invoice";
    }

    @GetMapping("all")
    public ResponseEntity<DataTableResponse<InvoiceVM>> getAllInvoices(HttpServletRequest request){
        DataTableRequest reqDto = DataTableRequest.fromParams(request.getParameterMap());
        return ResponseEntity.ok(invoiceService.findAllInvoices(reqDto).map(InvoiceVM::new));
    }

}
