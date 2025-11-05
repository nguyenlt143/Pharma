package vn.edu.fpt.pharma.controller.branchInventory;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BranchInventoryController {

    // ✅ Màn hình thuốc hết hàng
    @GetMapping("/inventory/out-of-stock?")
    public String outOfStockPage() {
        return "inventory/out_of_stock"; // tương ứng file src/main/jte/inventory/out_of_stock.jte
    }

    // ✅ Màn hình thuốc sắp hết hạn
    @GetMapping("/inventory/expiring-medicine?")
    public String expiringMedicinePage() {
        return "inventory/expiring_medicine"; // tương ứng file src/main/jte/inventory/expiring_medicine.jte
    }

    // ✅ Màn hình  phiếu xuất trả
    @GetMapping("/inventory/export_list")
    public String exportListPage() {
        return "inventory/export_list";
    }

    @GetMapping("/inventory/export_detail")
    public String exportDetailPage() {
        return "inventory/export_detail";
    }

    @GetMapping("/inventory/export_success")
    public String exportSuccessPage() {
        return "inventory/export_success";
    }

    // ✅ Màn hình  phiếu kiểm kho
    @GetMapping("/inventory/inventory_check_list")
    public String inventoryCheckListPage() {
        return "inventory/inventory_check_list";
    }

    @GetMapping("/inventory/inventory_check_detail")
    public String inventoryCheckDetailPage() {
        return "inventory/inventory_check_detail";
    }

    @GetMapping("/inventory/inventory_check_success")
    public String inventoryCheckSuccessPage() {
        return "inventory/inventory_check_success";
    }

}
