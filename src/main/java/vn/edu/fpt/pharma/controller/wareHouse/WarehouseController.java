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

    @GetMapping("/receipt/list")
    public String receiptList(Model model) {

        return "pages/warehouse/receipt_list";
    }

    @GetMapping("/request/list")
    public String requestList(Model model) {

        return "pages/warehouse/request_list";
    }

    @GetMapping("/request/detail")
    public String requestDetail(Model model) {

        return "pages/warehouse/request_detail";
    }

    @GetMapping("/inventory")
    public String warehouseInventory(Model model) {

        return "pages/warehouse/warehouse_manage";
    }

    @GetMapping("/export")
    public String exportcreate(Model model) {

        return "pages/warehouse/export_create";
    }

}











