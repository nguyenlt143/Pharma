package vn.edu.fpt.pharma.controller.pharmacist;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.fpt.pharma.config.CustomUserDetails;
import vn.edu.fpt.pharma.dto.invoice.InvoiceCreateRequest;
import java.util.Map;
import vn.edu.fpt.pharma.dto.medicine.MedicineSearchDTO;
import vn.edu.fpt.pharma.dto.medicine.VariantInventoryDTO;
import vn.edu.fpt.pharma.dto.shifts.ShiftSummaryVM;
import vn.edu.fpt.pharma.dto.user.ProfileUpdateRequest;
import vn.edu.fpt.pharma.dto.user.ProfileVM;
import vn.edu.fpt.pharma.entity.Invoice;
import vn.edu.fpt.pharma.entity.User;
import vn.edu.fpt.pharma.service.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/pharmacist")
@RequiredArgsConstructor
public class PharmacistController {

    private final UserService userService;
    private final MedicineVariantService medicineVariantService;
    private final MedicineService medicineService;
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

        model.addAttribute("inShift", inShift);
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
    public ResponseEntity<?> createInvoice(@Valid @RequestBody InvoiceCreateRequest req) {
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

        // Tạo form object để binding
        ProfileUpdateRequest profileUpdateRequest = new ProfileUpdateRequest();
        profileUpdateRequest.setFullName(user.getFullName());
        profileUpdateRequest.setPhone(user.getPhoneNumber());
        profileUpdateRequest.setEmail(user.getEmail());

        model.addAttribute("profile", profileVM);
        model.addAttribute("profileUpdateRequest", profileUpdateRequest);
        model.addAttribute("success", null);
        model.addAttribute("error", null);

        // Pre-populate display values để tránh complex expressions trong JTE
        model.addAttribute("displayFullName", user.getFullName());
        model.addAttribute("displayEmail", user.getEmail());
        model.addAttribute("displayPhone", user.getPhoneNumber());

        return "pages/profile/profile";
    }

    @PostMapping("/profile/update")
    public String update(@Valid @ModelAttribute("profileUpdateRequest") ProfileUpdateRequest profileUpdateRequest,
                        BindingResult bindingResult,
                        Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Long userId = userDetails.getId();
        User user = userService.findById(userId);
        ProfileVM profileVM = new ProfileVM(user);

        model.addAttribute("profile", profileVM);
        model.addAttribute("profileUpdateRequest", profileUpdateRequest);

        // Pre-populate display values
        model.addAttribute("displayFullName", profileUpdateRequest.getFullName() != null ? profileUpdateRequest.getFullName() : user.getFullName());
        model.addAttribute("displayEmail", profileUpdateRequest.getEmail() != null ? profileUpdateRequest.getEmail() : user.getEmail());
        model.addAttribute("displayPhone", profileUpdateRequest.getPhone() != null ? profileUpdateRequest.getPhone() : user.getPhoneNumber());

        if (bindingResult.hasErrors()) {
            // Nếu có lỗi validation, quay lại trang profile với thông báo lỗi
            model.addAttribute("error", "Vui lòng kiểm tra lại thông tin đã nhập");
            model.addAttribute("success", null);
            return "pages/profile/profile";
        }

        try {
            userService.updateProfile(userId, profileUpdateRequest);
            model.addAttribute("success", "Cập nhật thành công!");
            model.addAttribute("error", null);

            // Cập nhật lại ProfileVM và display values với dữ liệu mới
            User updatedUser = userService.findById(userId);
            ProfileVM updatedProfileVM = new ProfileVM(updatedUser);
            model.addAttribute("profile", updatedProfileVM);

            // Update display values với dữ liệu mới
            model.addAttribute("displayFullName", updatedUser.getFullName());
            model.addAttribute("displayEmail", updatedUser.getEmail());
            model.addAttribute("displayPhone", updatedUser.getPhoneNumber());

        } catch (Exception e) {
            model.addAttribute("error", "Cập nhật thất bại: " + e.getMessage());
            model.addAttribute("success", null);
        }

        return "pages/profile/profile";
    }
}
