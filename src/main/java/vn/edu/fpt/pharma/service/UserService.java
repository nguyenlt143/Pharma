package vn.edu.fpt.pharma.service;

import vn.edu.fpt.pharma.base.BaseService;
import vn.edu.fpt.pharma.dto.manager.UserDto;
import vn.edu.fpt.pharma.dto.manager.UserRequest;
import vn.edu.fpt.pharma.dto.user.ProfileVM;
import vn.edu.fpt.pharma.dto.user.UserVM;
import vn.edu.fpt.pharma.entity.User;

import java.util.List;

public interface UserService extends BaseService<User, Long> {
    List<UserVM> transformUsers(List<User> users);
    List<UserDto> getStaffs(Long branchId);
    List<UserDto> getStaffsActive(Long branchId);
    List<UserDto> getPharmacists(Long branchId);
    List<UserDto> getAccountsByRoles(List<Long> roleIds, boolean showDeleted);
    UserDto getById(Long id);
    UserDto create(UserRequest req);
    UserDto update(Long id, UserRequest req);
    void delete(Long id);
    void restore(Long id);
    User findByUserName(String username);
    void updateProfile(Long id, ProfileVM profileVM);
}
