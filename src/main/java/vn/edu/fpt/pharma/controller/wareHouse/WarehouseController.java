package vn.edu.fpt.pharma.controller.wareHouse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/warehouse")
public class WarehouseController {
    @GetMapping("/receipt/create")
    public String receiptCreate(Model model) {

        return "pages/warehouse/receipt_create";
    }
}











