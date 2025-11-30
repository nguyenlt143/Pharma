package vn.edu.fpt.pharma.controller.pharmacist;


import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.fpt.pharma.config.CustomUserDetails;
import vn.edu.fpt.pharma.dto.invoice.InvoiceCreateRequest;
import vn.edu.fpt.pharma.dto.medicine.MedicineSearchDTO;
import vn.edu.fpt.pharma.dto.medicine.VariantInventoryDTO;
import vn.edu.fpt.pharma.dto.shifts.ShiftSummaryVM;
import vn.edu.fpt.pharma.dto.user.ProfileVM;
import vn.edu.fpt.pharma.entity.Branch;
import vn.edu.fpt.pharma.entity.Invoice;
import vn.edu.fpt.pharma.entity.User;
import vn.edu.fpt.pharma.repository.BranchRepository;
import vn.edu.fpt.pharma.service.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/pharmacist")
@RequiredArgsConstructor
public class PharmacistController {

    private final UserService userService;
    private final MedicineVariantService medicineVariantService;
    private final MedicineService medicineService;
    private final BranchRepository branchRepository;
    private final ShiftWorkService shiftWorkService;
    private final ShiftService shiftService;
    private final InvoiceService invoiceService;

    @GetMapping("/pos")
    public String pos(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Long userId = userDetails.getId();
        Long branchId = userDetails.getUser().getBranchId();
        boolean inShift = shiftService
                .getCurrentShift(userId, branchId)
                .isPresent();

        model.addAttribute("inShift", true);
        model.addAttribute("keyword", "");
        return "pages/pharmacist/pos";
    }

    @GetMapping("/pos/api/search")
    @ResponseBody
    public List<MedicineSearchDTO> search(@RequestParam(required = false, defaultValue = "") String keyword) {
        return medicineService.searchMedicinesByKeyword(keyword);
    }

    @GetMapping("/pos/api/medicine/{medicineId}/variants")
    @ResponseBody
    public List<VariantInventoryDTO> getVariantsWithInventory(@PathVariable Long medicineId) {
        return medicineVariantService.getVariantsWithInventoryByMedicineId(medicineId);
    }

    @PostMapping("/pos/api/invoices")
    public ResponseEntity<?> createInvoice(@RequestBody InvoiceCreateRequest req) {

        Invoice invoice = invoiceService.createInvoice(req);

        return ResponseEntity.ok(Map.of(
                "id", invoice.getId(),
                "invoiceCode", invoice.getInvoiceCode(),
                "message", "Thanh toán thành công"
        ));
    }

    @GetMapping("/work")
    public String work(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            Model model
    ) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Long userId = userDetails.getId();
        User user = userService.findById(userId);

        // Nếu chưa truyền ngày => tự động lấy tuần hiện tại
        if (start == null || end == null) {
            LocalDate today = LocalDate.now();
            start = today.with(DayOfWeek.MONDAY);
            end = start.plusDays(6);
        }

        // Gọi service để lấy dữ liệu tổng kết ca
        List<ShiftSummaryVM> summaries = shiftWorkService.getSummary(user.getBranchId(), userId, start, end);

        // Đẩy dữ liệu sang view
        model.addAttribute("summaries", summaries);
        model.addAttribute("start", start);
        model.addAttribute("end", end);

        return "pages/pharmacist/work_schedule";
    }


    @GetMapping("/profile")
    public String profile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Long userId = userDetails.getId();
        User user = userService.findById(userId);
        ProfileVM profileVM = new ProfileVM(user);
        model.addAttribute("profile", profileVM);
        return "pages/profile/profile";
    }

    @PostMapping("/profile/update")
    public String update(@ModelAttribute ProfileVM profileVM,  RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Long userId = userDetails.getId();
        userService.updateProfile(userId, profileVM);
        redirectAttributes.addFlashAttribute("success", "Cập nhật thành công!");
        return "redirect:/pharmacist/profile";
    }
}
