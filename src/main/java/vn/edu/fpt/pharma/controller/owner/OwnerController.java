package vn.edu.fpt.pharma.controller.owner;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vn.edu.fpt.pharma.repository.BranchRepository;
import vn.edu.fpt.pharma.repository.CategoryRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/owner")
@RequiredArgsConstructor
public class OwnerController {

    private final BranchRepository branchRepository;
    private final CategoryRepository categoryRepository;

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

    // --- Báo cáo kho ---
    @GetMapping("/report/inventory")
    public String inventoryReport(
            @RequestParam(required = false) Long branchId,
            @RequestParam(required = false, defaultValue = "week") String range,
            @RequestParam(required = false) Long category,
            Model model) {
        
        // Load branches for filter
        List<Map<String, Object>> branches = new ArrayList<>();
        branchRepository.findAll().forEach(branch -> {
            Map<String, Object> branchMap = new HashMap<>();
            branchMap.put("id", branch.getId());
            branchMap.put("name", branch.getName());
            branchMap.put("selected", branchId != null && branch.getId().equals(branchId));
            branches.add(branchMap);
        });
        
        // Load categories for filter
        List<Map<String, Object>> categories = new ArrayList<>();
        categoryRepository.findAll().forEach(cat -> {
            Map<String, Object> catMap = new HashMap<>();
            catMap.put("id", cat.getId());
            catMap.put("name", cat.getName());
            catMap.put("selected", category != null && cat.getId().equals(category));
            categories.add(catMap);
        });
        
        // Initial values (will be loaded via API)
        model.addAttribute("userCanExport", true);
        model.addAttribute("totalValue", "");
        model.addAttribute("valueDeltaLabel", "");
        model.addAttribute("lowStockCount", 0);
        model.addAttribute("pendingInbound", 0);
        model.addAttribute("pendingOutbound", 0);
        model.addAttribute("branches", branches);
        model.addAttribute("range", range);
        model.addAttribute("categories", categories);
        model.addAttribute("recentActivities", new ArrayList<>());
        
        return "pages/owner/inventory";
    }
}
