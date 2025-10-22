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

        return "pages/pharmacist/list_invoice";
    }
}
