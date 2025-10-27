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

    @GetMapping("/shiftDetail")
    public String shiftDetail(Model model) {

        return "pages/pharmacist/shift_detail";
    }


    @GetMapping("/pos/hieu")
    public String Hieu(Model model) {

        return "pages/warehouse/receipt_create";
    }



}
