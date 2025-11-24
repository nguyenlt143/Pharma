package vn.edu.fpt.pharma.controller.pharmacist;


import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.fpt.pharma.config.CustomUserDetails;
import vn.edu.fpt.pharma.dto.medicine.MedicineSearchDTO;
import vn.edu.fpt.pharma.dto.medicine.VariantInventoryDTO;
import vn.edu.fpt.pharma.dto.user.ProfileVM;
import vn.edu.fpt.pharma.entity.User;
import vn.edu.fpt.pharma.service.MedicineService;
import vn.edu.fpt.pharma.service.MedicineVariantService;
import vn.edu.fpt.pharma.service.UserService;

import java.util.List;

@Controller
@RequestMapping("/pharmacist")
@RequiredArgsConstructor
public class PharmacistController {

    private final UserService userService;
    private final MedicineVariantService medicineVariantService;
    private final MedicineService medicineService;



    @GetMapping("/pos")
    public String pos(Model model) {
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


    @GetMapping("/work")
    public String work(Model model) {

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
