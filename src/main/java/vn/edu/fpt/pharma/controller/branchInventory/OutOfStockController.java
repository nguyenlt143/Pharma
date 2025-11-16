package vn.edu.fpt.pharma.controller.branchInventory;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OutOfStockController {

    @GetMapping("/inventory/out-of-stock")
    public String showOutOfStockPage() {
        // Chỉ hiển thị giao diện tĩnh, không gọi service/backend
        return "inventory/out_of_stock";
    }
}
