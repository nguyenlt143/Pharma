package vn.edu.fpt.pharma.controller.pharmacist;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PharmacistController {

    @GetMapping("/pos")
    public String pos(Model model) {

        return "pages/pharmacist/pos";
    }

    @GetMapping("/pos/invoice")
    public String invoice(Model model) {

        return "pages/pharmacist/invoice";
    }

    @GetMapping("/pos/revenue")
    public String revenue(Model model) {

        return "pages/pharmacist/revenue";
    }

    @GetMapping("/pos/revenue/detail")
    public String revenueDetail(Model model) {

        return "pages/pharmacist/revenue_detail";
    }

    @GetMapping("/pos/shift")
    public String shift(Model model) {

        return "pages/pharmacist/shift";
    }

    @GetMapping("/pos/shiftDetail")
    public String shiftDetail(Model model) {

        return "pages/pharmacist/shift_detail";
    }

    @GetMapping("/pos/work")
    public String workSchedule(Model model) {

        return "pages/pharmacist/work_schedule";
    }

}
