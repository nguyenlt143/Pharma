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

    @GetMapping("/all/revenue/detail/view")
    public String revenueDetailPage(
            @RequestParam("period") String period,
            Model model) {

        model.addAttribute("period", period);
        return "pages/pharmacist/revenue_details";
    }

    @GetMapping("/all/revenue/detail")
    public Object getDetailRevenue(
            @RequestParam("period") String period,
            HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Long userId = userDetails.getId();
        String[] parts;
        if (period.contains("/")) {
            parts = period.split("/");
        } else if (period.contains("-")) {
            parts = period.split("-");
        } else {
            return ResponseEntity.badRequest().body("Invalid period format");
        }
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);

        if (year < 100) {
            int tmp = year;
            year = month;
            month = tmp;
        }
        DataTableRequest reqDto = DataTableRequest.fromParams(request.getParameterMap());
        DataTableResponse<RevenueDetailVM> response = revenueService.ViewRevenuesDetail(reqDto, userId, year, month);
        return ResponseEntity.ok(response);
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

    @GetMapping("/all/shift/detail/view")
    public String shiftDetailPage(
            @RequestParam("shiftName") String shiftName,
            Model model) {

        model.addAttribute("shiftName", shiftName);
        return "pages/pharmacist/shift_details";
    }

    @GetMapping("/all/shift/detail")
    public Object getDetailShift(
            @RequestParam("shiftName") String shiftName,
            HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Long userId = userDetails.getId();

        DataTableRequest reqDto = DataTableRequest.fromParams(request.getParameterMap());
        DataTableResponse<RevenueDetailVM> response = revenueService.ViewShiftDetail(reqDto, userId, shiftName);
        return ResponseEntity.ok(response);
    }
}
