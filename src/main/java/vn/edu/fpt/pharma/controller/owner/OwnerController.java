package vn.edu.fpt.pharma.controller.owner;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/owner")
public class OwnerController {

    // --- Quản lý thuốc ---
    @GetMapping("/medicine/list")
    public String medicineList(Model model) {
        return "pages/owner/medicine_list";
    }

    @GetMapping("/price/list")
    public String priceList(Model model) {
        return "pages/owner/price_list";
    }

    // --- Quản lý nhóm hàng hóa ---
    @GetMapping("/category/list")
    public String categoryList(Model model) {
        return "pages/owner/category_list";
    }

    @GetMapping("/category/create")
    public String categoryCreate(Model model) {
        return "pages/owner/category_create";
    }



        @GetMapping("/owner/dashboard-report")
        public String dashboardReport() {
            return "owner/dashboard_report.jte";
        }




}
