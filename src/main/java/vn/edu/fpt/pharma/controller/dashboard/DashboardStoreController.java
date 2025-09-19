package vn.edu.fpt.pharma.controller.dashboard;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.fpt.pharma.dto.DataTableRequest;
import vn.edu.fpt.pharma.dto.DataTableResponse;
import vn.edu.fpt.pharma.service.StoreService;
import vn.edu.fpt.pharma.dto.store.StoreVM;
import vn.edu.fpt.pharma.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/dashboard/stores")
@RequiredArgsConstructor
public class DashboardStoreController {

    private final StoreService storeService;

    @GetMapping
    public String index() {
        return "pages/dashboard/store/index";
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex, RedirectAttributes redirectAttributes) {
        log.error("Exception: ", ex);
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/dashboard/stores";
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handleValidationException(MethodArgumentNotValidException ex, RedirectAttributes redirectAttributes) {
        log.error("MethodArgumentNotValidException: ", ex);
        String errorMessage = StringUtils.convertValidationExceptionToString(ex.getBindingResult().getAllErrors());
        redirectAttributes.addFlashAttribute("error", errorMessage);
        return "redirect:/dashboard/stores";
    }

    @GetMapping("/all")
    public ResponseEntity<DataTableResponse<StoreVM>> getAllStores(HttpServletRequest request) {
        DataTableRequest reqDto = DataTableRequest.fromParams(request.getParameterMap());
        return ResponseEntity.ok(storeService.findAllStores(reqDto).map(StoreVM::new));
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportStore() {
        byte[] csvData = storeService.exportFileCsv();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=stores.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvData);
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteStore(@RequestBody List<Long> ids) {
        storeService.deleteByIds(ids);
        return ResponseEntity.noContent().build();
    }
}
