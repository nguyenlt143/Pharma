package vn.edu.fpt.pharma.service;

import vn.edu.fpt.pharma.base.BaseService;
import vn.edu.fpt.pharma.dto.DataTableRequest;
import vn.edu.fpt.pharma.dto.DataTableResponse;
import vn.edu.fpt.pharma.dto.user.UserVM;
import vn.edu.fpt.pharma.entity.User;

import java.util.List;

public interface UserService extends BaseService<User, Long> {
    DataTableResponse<User> getAllUsers(DataTableRequest reqDto);
    List<UserVM> transformUsers(List<User> users);
}
