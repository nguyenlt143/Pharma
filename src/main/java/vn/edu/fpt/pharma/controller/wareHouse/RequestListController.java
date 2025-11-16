package vn.edu.fpt.pharma.controller.wareHouse;

import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import vn.edu.fpt.pharma.service.RequestFormService;

@Controller
@RequestMapping("/warehouse/request")
public class RequestListController {
    private final RequestFormService requestFormService;

    public RequestListController(RequestFormService requestFormService) {
        this.requestFormService = requestFormService;
    }

    @GetMapping("/list")
    public String listAll(Model model) {
        model.addAttribute("requests", requestFormService.getAllRequestForms());
        return "pages/warehouse/request_list";
    }

    @GetMapping("/list/import")
    public String listImport(Model model) {
        model.addAttribute("requests", requestFormService.getImportRequests());
        return "pages/warehouse/request_list";
    }

    @GetMapping("/list/return")
    public String listReturn(Model model) {
        model.addAttribute("requests", requestFormService.getReturnRequests());
        return "pages/warehouse/request_list";
    }

    @GetMapping("/detail")
    public String detail(Long id, Model model) {
        // bạn có thể load detail ở đây
        // model.addAttribute("detail", ...);
        return "pages/warehouse/request_list";
    }
}
