package vn.edu.fpt.pharma.controller.pharmacist;


import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.fpt.pharma.config.CustomUserDetails;
import vn.edu.fpt.pharma.dto.user.ProfileVM;
import vn.edu.fpt.pharma.entity.User;
import vn.edu.fpt.pharma.service.UserService;

@Controller
@RequestMapping("/pharmacist")
@RequiredArgsConstructor
public class PharmacistController {

    private final UserService userService;

    @GetMapping("/pos")
    public String pos(Model model) {

        return "pages/pharmacist/pos";
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
