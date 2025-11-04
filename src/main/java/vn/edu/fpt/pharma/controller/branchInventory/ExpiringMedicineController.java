package vn.edu.fpt.pharma.controller.branchInventory;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ExpiringMedicineController {

    @GetMapping("/inventory/expiring-medicine")
    public String showExpiringMedicinePage() {
        // Chỉ hiển thị giao diện tĩnh, không gọi service/backend
        return "inventory/expiring_medicine";
    }
}
