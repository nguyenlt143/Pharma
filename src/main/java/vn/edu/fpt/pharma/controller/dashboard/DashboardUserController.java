package vn.edu.fpt.pharma.controller.dashboard;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import vn.edu.fpt.pharma.dto.DataTableRequest;
import vn.edu.fpt.pharma.dto.DataTableResponse;
import vn.edu.fpt.pharma.dto.user.UserVM;
import vn.edu.fpt.pharma.entity.User;
import vn.edu.fpt.pharma.service.UserService;

@Slf4j
@Controller
@RequestMapping("/dashboard/users")
@RequiredArgsConstructor
public class DashboardUserController {

    private final UserService userService;

    @GetMapping
    public String index() {
        return "pages/dashboard/user/index";
    }

    @GetMapping("/all")
    public ResponseEntity<DataTableResponse<UserVM>> getAllUsers(HttpServletRequest request) {
        return null;
    }
}
