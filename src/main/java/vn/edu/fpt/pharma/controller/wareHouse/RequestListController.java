package vn.edu.fpt.pharma.controller.wareHouse;

import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vn.edu.fpt.pharma.dto.common.PageResponse;
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
    public String listAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model
    ) {
        PageResponse<RequestList> pageResponse = requestFormService.getAllRequestFormsPaginated(page, size);
        model.addAttribute("requests", pageResponse.content());
        model.addAttribute("branches", branchService.findAll());
        model.addAttribute("pagination", pageResponse);
        return "pages/warehouse/request_list";
    }

    @GetMapping("/list/import")
    public String listImport(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model
    ) {
        PageResponse<RequestList> pageResponse = requestFormService.getImportRequestsPaginated(page, size);
        model.addAttribute("requests", pageResponse.content());
        model.addAttribute("branches", branchService.findAll());
        model.addAttribute("pagination", pageResponse);
        return "pages/warehouse/request_list";
    }

    @GetMapping("/list/return")
    public String listReturn(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model
    ) {
        PageResponse<RequestList> pageResponse = requestFormService.getReturnRequestsPaginated(page, size);
        model.addAttribute("requests", pageResponse.content());
        model.addAttribute("branches", branchService.findAll());
        model.addAttribute("pagination", pageResponse);
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
    public PageResponse<RequestList> filterRequests(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Long branchId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return requestFormService.getRequestListPaginated(type, branchId, status, page, size);
    }
}
