package vn.edu.fpt.pharma.controller.wareHouse;

import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vn.edu.fpt.pharma.dto.warehouse.RequestList;
import vn.edu.fpt.pharma.entity.Branch;
import vn.edu.fpt.pharma.service.RequestFormService;
import vn.edu.fpt.pharma.service.BranchService;

@Controller
@RequestMapping("/warehouse/request")
public class RequestListController {
    private final RequestFormService requestFormService;
    private final BranchService branchService;

    public RequestListController(RequestFormService requestFormService, BranchService branchService) {
        this.requestFormService = requestFormService;
        this.branchService = branchService;
    }

    @GetMapping("/list")
    public String listAll(Model model) {
        model.addAttribute("requests", requestFormService.getAllRequestForms());
        model.addAttribute("branches", branchService.findAll());
        return "pages/warehouse/request_list";
    }

    @GetMapping("/list/import")
    public String listImport(Model model) {
        model.addAttribute("requests", requestFormService.getImportRequests());
        model.addAttribute("branches", branchService.findAll());
        return "pages/warehouse/request_list";
    }

    @GetMapping("/list/return")
    public String listReturn(Model model) {
        model.addAttribute("requests", requestFormService.getReturnRequests());
        model.addAttribute("branches", branchService.findAll());
        return "pages/warehouse/request_list";
    }

    @GetMapping("/detail")
    public String detail(@RequestParam Long id, Model model) {
        model.addAttribute("request", requestFormService.getDetailById(id));
        model.addAttribute("details", requestFormService.getDetailsOfRequest(id));

        return "pages/warehouse/request_detail";
    }

    @PostMapping("/confirm/{id}")
    public ResponseEntity<Void> confirmRequest(@PathVariable Long id) {
        requestFormService.confirmRequest(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/cancel/{id}")
    public ResponseEntity<Void> cancelRequest(@PathVariable Long id) {
        requestFormService.cancelRequest(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/list/filter")
    @org.springframework.web.bind.annotation.ResponseBody
    public java.util.List<RequestList> filterRequests(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Long branchId,
            @RequestParam(required = false) String status
    ) {
        return requestFormService.getRequestList(type, branchId, status);
    }
}
