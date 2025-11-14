package vn.edu.fpt.pharma.controller.pharmacist;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.fpt.pharma.dto.DataTableRequest;
import vn.edu.fpt.pharma.dto.DataTableResponse;
import vn.edu.fpt.pharma.dto.reveuce.RevenueVM;
import vn.edu.fpt.pharma.service.RevenueService;
import vn.edu.fpt.pharma.util.StringUtils;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/pharmacist")
public class RevenueController {

    private final RevenueService revenueService;

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
        DataTableRequest reqDto = DataTableRequest.fromParams(request.getParameterMap());
        List<RevenueVM> data = revenueService.getRevenueSummary();
        DataTableResponse<RevenueVM> response = new DataTableResponse<>(
                reqDto.draw(),
                data.size(),
                data.size(),
                data
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all/shift")
    public ResponseEntity<DataTableResponse<RevenueVM>> getAllRevenuesShift(HttpServletRequest request) {
        DataTableRequest reqDto = DataTableRequest.fromParams(request.getParameterMap());
        List<RevenueVM> data = revenueService.getRevenueSummary();
        DataTableResponse<RevenueVM> response = new DataTableResponse<>(
                reqDto.draw(),
                data.size(),
                data.size(),
                data
        );
        return ResponseEntity.ok(response);
    }
}
