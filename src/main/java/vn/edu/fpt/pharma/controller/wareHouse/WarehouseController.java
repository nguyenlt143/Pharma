package vn.edu.fpt.pharma.controller.wareHouse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import vn.edu.fpt.pharma.dto.warehouse.InventoryMovementDetailVM;
import vn.edu.fpt.pharma.dto.warehouse.InventoryMovementVM;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/warehouse")
public class WarehouseController {
    @GetMapping("/receipt/create")
    public String receiptCreate(Model model) {
        // Tạo ViewModel rỗng cho form mới
        InventoryMovementVM inventoryMovementVM = new InventoryMovementVM(
                null, null, null, null, null, null, null, null, 0.0, null
        );
        List<InventoryMovementDetailVM> inventoryMovementDetails = new ArrayList<>();

        model.addAttribute("inventoryMovementVM", inventoryMovementVM);
        model.addAttribute("inventoryMovementDetails", inventoryMovementDetails);

        return "pages/warehouse/receipt_create";
    }

    @GetMapping("/receipt/list")
    public String receiptList(Model model) {

        return "pages/warehouse/receipt_list";
    }

//    @GetMapping("/request/detail")
//    public String requestDetail(Model model) {
//
//        return "pages/warehouse/request_detail";
//    }

    @GetMapping("/inventory")
    public String warehouseInventory(Model model) {

        return "pages/warehouse/warehouse_manage";
    }

    @GetMapping("/export")
    public String exportcreate(Model model) {

        return "pages/warehouse/export_create";
    }

}











