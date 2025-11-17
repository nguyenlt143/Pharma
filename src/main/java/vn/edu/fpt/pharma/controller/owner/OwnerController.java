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

    // --- Quản lý nhà cung cấp ---
    @GetMapping("/supplier/list")
    public String supplierList(Model model) {
        return "pages/owner/supplier_list";
    }

    // --- Dashboard ---
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        return "pages/owner/dashboard";
    }

    // --- Báo cáo ---
    @GetMapping("/report/revenue")
    public String reportRevenue(Model model) {
        return "pages/owner/report_revenue";
    }

    @GetMapping("/report/profit")
    public String reportProfit(Model model) {
        return "pages/owner/report_profit";
    }
}
