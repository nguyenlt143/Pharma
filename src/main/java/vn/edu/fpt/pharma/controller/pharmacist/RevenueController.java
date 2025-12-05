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

import java.util.Map;

@Slf4j
@Controller // RE-ENABLED for revenue functionality
@RequiredArgsConstructor // RE-ENABLED with dependencies
@RequestMapping("/pharmacist") // RE-ENABLED
public class RevenueController {

    private final RevenueService revenueService; // RE-ENABLED
    private final InvoiceDetailService invoiceDetailService; // RE-ENABLED

    @GetMapping("/revenues")
    public String revenues(Model model){
        model.addAttribute("success", null);
        model.addAttribute("error", null);
        return "pages/pharmacist/revenues";
    }

    @GetMapping("/shifts")
    public String shifts(Model model){
        model.addAttribute("success", null);
        model.addAttribute("error", null);
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
        redirectAttributes.addFlashAttribute("error", errorMsg);
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
            Model model,
            RedirectAttributes redirectAttributes) {
        // Validation cho period format
        if (period == null || period.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Kỳ báo cáo không được để trống");
            return "redirect:/pharmacist/revenues";
        }

        // Accept both MM/YYYY and YYYY/MM formats
        if (!period.matches("^\\d{1,4}[/-]\\d{1,4}$")) {
            redirectAttributes.addFlashAttribute("error", "Định dạng kỳ báo cáo không hợp lệ (VD: 12/2024 hoặc 2024-12)");
            return "redirect:/pharmacist/revenues";
        }

        // Validate period parts exist
        String[] parts;
        if (period.contains("/")) {
            parts = period.split("/");
        } else if (period.contains("-")) {
            parts = period.split("-");
        } else {
            redirectAttributes.addFlashAttribute("error", "Định dạng kỳ báo cáo không hợp lệ");
            return "redirect:/pharmacist/revenues";
        }

        if (parts.length != 2) {
            redirectAttributes.addFlashAttribute("error", "Định dạng kỳ báo cáo không hợp lệ");
            return "redirect:/pharmacist/revenues";
        }

        try {
            int part1 = Integer.parseInt(parts[0]);
            int part2 = Integer.parseInt(parts[1]);

            // Determine which is month and which is year
            int month, year;
            if (part1 > 12 || (part2 <= 12 && part2 > 0 && part1 > 1900)) {
                // part1 is year, part2 is month
                year = part1;
                month = part2;
            } else {
                // part1 is month, part2 is year
                month = part1;
                year = part2;
            }

            // Validation
            if (year < 2000 || year > 2100) {
                redirectAttributes.addFlashAttribute("error", "Năm không hợp lệ (2000-2100)");
                return "redirect:/pharmacist/revenues";
            }
            if (month < 1 || month > 12) {
                redirectAttributes.addFlashAttribute("error", "Tháng không hợp lệ (1-12)");
                return "redirect:/pharmacist/revenues";
            }

        } catch (NumberFormatException e) {
            redirectAttributes.addFlashAttribute("error", "Định dạng kỳ báo cáo không hợp lệ");
            return "redirect:/pharmacist/revenues";
        }

        model.addAttribute("period", period);
        model.addAttribute("success", null);
        model.addAttribute("error", null);
        return "pages/pharmacist/revenue_details";
    }

    @GetMapping("/all/revenue/detail")
    public ResponseEntity<?> getDetailRevenue(
            @RequestParam("period") String period,
            HttpServletRequest request) {
        try {
            // Validation cho period
            if (period == null || period.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Kỳ báo cáo không được để trống"));
            }

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
            Long userId = userDetails.getId();

            String[] parts;
            if (period.contains("/")) {
                parts = period.split("/");
            } else if (period.contains("-")) {
                parts = period.split("-");
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "Định dạng kỳ báo cáo không hợp lệ"));
            }

            if (parts.length != 2) {
                return ResponseEntity.badRequest().body(Map.of("error", "Định dạng kỳ báo cáo không hợp lệ"));
            }

            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);

            if (year < 100) {
                int tmp = year;
                year = month;
                month = tmp;
            }

            // Validation cho year và month
            if (year < 2000 || year > 2100) {
                return ResponseEntity.badRequest().body(Map.of("error", "Năm không hợp lệ"));
            }
            if (month < 1 || month > 12) {
                return ResponseEntity.badRequest().body(Map.of("error", "Tháng không hợp lệ"));
            }

            DataTableRequest reqDto = DataTableRequest.fromParams(request.getParameterMap());
            DataTableResponse<RevenueDetailVM> response = revenueService.ViewRevenuesDetail(reqDto, userId, year, month);
            return ResponseEntity.ok(response);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Định dạng kỳ báo cáo không hợp lệ"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Lỗi xử lý: " + e.getMessage()));
        }
    }

    @GetMapping("/all/shift")
    public ResponseEntity<DataTableResponse<RevenueShiftVM>> getAllRevenuesShift(HttpServletRequest request) {
        DataTableRequest reqDto = null;
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
            Long userId = userDetails.getId();

            log.info("Getting shift revenue data for user: {}", userId);

            reqDto = DataTableRequest.fromParams(request.getParameterMap());
            log.info("DataTable request: {}", reqDto);

            DataTableResponse<RevenueShiftVM> response = revenueService.getRevenueShiftSummary(reqDto, userId);
            log.info("Shift revenue response - Total records: {}, Filtered: {}",
                    response.recordsTotal(), response.recordsFiltered());
            log.info("Shift data size: {}", response.data().size());

            if (response.data().isEmpty()) {
                log.warn("No shift data found for user: {}", userId);
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting shift revenue data", e);
            if (reqDto == null) {
                reqDto = new DataTableRequest(1, 0, 10, "", "shiftName", "asc");
            }
            DataTableResponse<RevenueShiftVM> errorResponse = new DataTableResponse<>(
                    reqDto.draw(),
                    0,
                    0,
                    java.util.Collections.emptyList()
            );
            return ResponseEntity.ok(errorResponse);
        }
    }

    @GetMapping("/all/shift/detail/view")
    public String shiftDetailPage(
            @RequestParam("shiftName") String shiftName,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Validation cho shiftName
        if (shiftName == null || shiftName.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Tên ca làm việc không được để trống");
            return "redirect:/pharmacist/shifts";
        }

        model.addAttribute("shiftName", shiftName);
        model.addAttribute("success", null);
        model.addAttribute("error", null);
        return "pages/pharmacist/shift_details";
    }

    @GetMapping("/all/shift/detail")
    public ResponseEntity<?> getDetailShift(
            @RequestParam("shiftName") String shiftName,
            HttpServletRequest request) {
        try {
            // Validation cho shiftName
            if (shiftName == null || shiftName.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Tên ca làm việc không được để trống"));
            }

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
            Long userId = userDetails.getId();

            DataTableRequest reqDto = DataTableRequest.fromParams(request.getParameterMap());
            DataTableResponse<RevenueDetailVM> response = revenueService.ViewShiftDetail(reqDto, userId, shiftName);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Lỗi xử lý: " + e.getMessage()));
        }
    }
}
