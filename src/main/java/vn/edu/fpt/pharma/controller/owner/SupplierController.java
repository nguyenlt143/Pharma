package vn.edu.fpt.pharma.controller.owner;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.pharma.entity.Supplier;
import vn.edu.fpt.pharma.service.SupplierService;

import java.util.List;

@Controller
@RequestMapping("/owner/supplier")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    // Render trang quản lý
    @GetMapping("/list")
    public String list(Model model) {
        List<Supplier> suppliers = supplierService.getAllSuppliers();
        model.addAttribute("suppliers", suppliers);
        return "pages/owner/supplier_list";
    }

    // Thêm mới supplier (AJAX)
    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<Supplier> add(@RequestBody Supplier supplier) {
        Supplier saved = supplierService.saveSupplier(supplier);
        return ResponseEntity.ok(saved);
    }

    // Cập nhật supplier (AJAX)
    @PostMapping("/update/{id}")
    @ResponseBody
    public ResponseEntity<Supplier> update(@PathVariable Long id, @RequestBody Supplier supplier) {
        Supplier updated = supplierService.updateSupplier(id, supplier);
        return ResponseEntity.ok(updated);
    }

    // Lấy chi tiết supplier (AJAX)
    @GetMapping("/detail/{id}")
    @ResponseBody
    public ResponseEntity<Supplier> detail(@PathVariable Long id) {
        Supplier supplier = supplierService.getSupplierById(id);
        return ResponseEntity.ok(supplier);
    }
}
