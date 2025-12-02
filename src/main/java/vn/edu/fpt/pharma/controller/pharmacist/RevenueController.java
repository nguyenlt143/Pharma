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
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.fpt.pharma.config.CustomUserDetails;
import vn.edu.fpt.pharma.dto.DataTableRequest;
import vn.edu.fpt.pharma.dto.DataTableResponse;
import vn.edu.fpt.pharma.dto.reveuce.RevenueDetailVM;
import vn.edu.fpt.pharma.dto.reveuce.RevenueShiftVM;
import vn.edu.fpt.pharma.dto.reveuce.RevenueVM;
import vn.edu.fpt.pharma.service.InvoiceDetailService;
import vn.edu.fpt.pharma.service.RevenueService;
import vn.edu.fpt.pharma.util.StringUtils;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/pharmacist")
public class RevenueController {

    private final RevenueService revenueService;
    private final InvoiceDetailService invoiceDetailService;

    @GetMapping("/revenues")
    public String revenues(){
        return "pages/pharmacist/revenues";
    }

    @GetMapping("/shifts")
    public String shifts(){
        return "pages/pharmacist/shifts";
    }


    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex, RedirectAttributes redirectAttributes){
        log.error("Exception: ", ex);
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/pharmacist/revenues";
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handleValidation(MethodArgumentNotValidException ex, RedirectAttributes redirectAttributes){
        log.error("MethodArgumentNotValidException: ", ex);
        String errorMsg = StringUtils.convertValidationExceptionToString(ex.getBindingResult().getAllErrors());
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/pharmacist/revenues";
    }

    @GetMapping("/all/revenue")
    public ResponseEntity<DataTableResponse<RevenueVM>> getAllRevenues(HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Long userId = userDetails.getId();
        DataTableRequest reqDto = DataTableRequest.fromParams(request.getParameterMap());
        DataTableResponse<RevenueVM> response = revenueService.findAllRevenues(reqDto, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all/revenue/detail")
    public String detail(@RequestParam("period") String period, Model model) {

        // Period dạng: "2025/11"
        String[] parts = period.split("/");
        int year = Integer.parseInt(parts[1]);
        int month = Integer.parseInt(parts[0]);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();

        List<RevenueDetailVM> revenueDetailVMS =
                invoiceDetailService.getRevenueDetail(userDetails.getId(), year, month);

        model.addAttribute("period", period);
        model.addAttribute("revenueDetailVMS", revenueDetailVMS);

        return "pages/pharmacist/revenue_detail";
    }


    @GetMapping("/all/shift")
    public ResponseEntity<DataTableResponse<RevenueShiftVM>> getAllRevenuesShift(HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Long userId = userDetails.getId();
        DataTableRequest reqDto = DataTableRequest.fromParams(request.getParameterMap());
        DataTableResponse<RevenueShiftVM> response = revenueService.getRevenueShiftSummary(reqDto, userId);
        return ResponseEntity.ok(response);
    }
}
