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

    // --- Quản lý nhóm hàng hóa ---
    @GetMapping("/category/list")
    public String categoryList(Model model) {
        return "pages/owner/category_list";
    }

    @GetMapping("/category/create")
    public String categoryCreate(Model model) {
        return "pages/owner/category_create";
    }

    // --- Quản lý nhà cung cấp ---
    @GetMapping("/supplier/list")
    public String supplierList(Model model) {
        return "pages/owner/supplier_list";
    }

    @GetMapping("/supplier/create")
    public String supplierCreate(Model model) {
        return "pages/owner/supplier_create";
    }

    // --- Quản lý khách hàng ---
    @GetMapping("/customer/list")
    public String customerList(Model model) {
        return "pages/owner/customer_list";
    }

    @GetMapping("/customer/create")
    public String customerCreate(Model model) {
        return "pages/owner/customer_create";
    }
}
