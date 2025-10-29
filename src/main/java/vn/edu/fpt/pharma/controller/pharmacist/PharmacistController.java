package vn.edu.fpt.pharma.controller.pharmacist;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/pharmacist")
public class PharmacistController {

    @GetMapping("/pos")
    public String pos(Model model) {

        return "pages/pharmacist/pos";
    }

    @GetMapping("/invoice")
    public String invoice(Model model) {

        return "pages/pharmacist/invoice";
    }

    @GetMapping("/revenue")
    public String revenue(Model model) {

        return "pages/pharmacist/revenue";
    }

    @GetMapping("/revenue/detail")
    public String revenueDetail(Model model) {

        return "pages/pharmacist/revenue_detail";
    }

    @GetMapping("/shift")
    public String shift(Model model) {

        return "pages/pharmacist/shift";
    }

    @GetMapping("/shift/detail")
    public String shiftDetail(Model model) {

        return "pages/pharmacist/shift_detail";
    }

    @GetMapping("/work")
    public String work(Model model) {

        return "pages/pharmacist/work_schedule";
    }

    @GetMapping("/profile")
    public String profile(Model model) {

        return "pages/profile/profile";
    }

    @GetMapping("/abc")
    public String index(Model model) {

        return "pages/home/abc";
    }
}
