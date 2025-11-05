package vn.edu.fpt.pharma.service;

import org.springframework.data.jpa.repository.Query;
import vn.edu.fpt.pharma.base.BaseService;
import vn.edu.fpt.pharma.dto.DataTableRequest;
import vn.edu.fpt.pharma.dto.DataTableResponse;
import vn.edu.fpt.pharma.dto.manager.UserDto;
import vn.edu.fpt.pharma.dto.manager.UserRequest;
import vn.edu.fpt.pharma.dto.user.UserVM;
import vn.edu.fpt.pharma.entity.User;

import java.util.List;

public interface UserService extends BaseService<User, Long> {
    DataTableResponse<User> getAllUsers(DataTableRequest reqDto);
    List<UserVM> transformUsers(List<User> users);
    List<UserDto> getStaffs(Long branchId);
    UserDto getById(Long id);
    UserDto create(UserRequest req);
    UserDto update(Long id, UserRequest req);
    void delete(Long id);
    User findByUserName(String username);
}
