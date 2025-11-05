package vn.edu.fpt.pharma.service.impl;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import vn.edu.fpt.pharma.base.BaseServiceImpl;
import vn.edu.fpt.pharma.config.CustomUserDetails;
import vn.edu.fpt.pharma.dto.DataTableRequest;
import vn.edu.fpt.pharma.dto.DataTableResponse;
import vn.edu.fpt.pharma.dto.manager.UserDto;
import vn.edu.fpt.pharma.dto.manager.UserRequest;
import vn.edu.fpt.pharma.dto.user.UserVM;
import vn.edu.fpt.pharma.entity.Role;
import vn.edu.fpt.pharma.entity.User;
import vn.edu.fpt.pharma.repository.RoleRepository;
import vn.edu.fpt.pharma.repository.UserRepository;
import vn.edu.fpt.pharma.service.AuditService;
import vn.edu.fpt.pharma.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl extends BaseServiceImpl<User, Long, UserRepository> implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserServiceImpl(UserRepository repository, AuditService auditService, UserRepository userRepository, RoleRepository roleRepository) {
        super(repository, auditService);
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = repository.findByUserNameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new CustomUserDetails(user);
    }

    @Override
    public DataTableResponse<User> getAllUsers(DataTableRequest reqDto) {
        DataTableResponse<User> users = findAllForDataTable(reqDto, List.of("email", "fullName", "storeCode"));
        return users.transform(auditService::addAuditInfo);
    }

    public List<UserVM> transformUsers(List<User> users) {
//        List<String> storeCodes = users.stream().map(User::getStoreCode).toList();
//        List<Store> stores = storeRepository.findAllByStoreCodeIn(storeCodes);
//        Map<String, String> storeNameMap = stores.stream()
//                .collect(Collectors.toMap(Store::getStoreCode, Store::getStoreName, (s1, s2) -> s1));
        return List.of();
    }

    @Override
    public List<UserDto> getStaffs(Long branchId) {
        return userRepository.findStaffInBranchId(branchId)
                .stream()
                .map(this::toDto)
                .toList();
    }


    @Override
    public UserDto getById(Long id) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Staff not found"));
        return toDto(u);
    }

    @Override
    public UserDto create(UserRequest req) {
        if (userRepository.existsByUserNameIgnoreCase(req.getUserName())) {
            throw new RuntimeException("Username already exists");
        }
        // Lấy role STAFF
        Role role = roleRepository.findByName("STAFF")
                .orElseThrow(() -> new RuntimeException("Role STAFF not found"));

        User user = new User();
        user.setFullName(req.getFullName());
        user.setUserName(req.getUserName());
        user.setPassword(req.getPassword()); //
        user.setEmail(req.getEmail());
        user.setPhoneNumber(req.getPhoneNumber());
        user.setBranchId(req.getBranchId());
        user.setRole(role);

        userRepository.save(user);
        return toDto(user);
    }

    @Override
    public UserDto update(Long id, UserRequest req) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        // ❗ Validate username nếu đổi
        if (!user.getUserName().equalsIgnoreCase(req.getUserName())
                && userRepository.existsByUserNameIgnoreCase(req.getUserName())) {
            throw new RuntimeException("Username already exists");
        }

        // ❗ Không cho sửa branchId
        req.setBranchId(user.getBranchId());

        user.setFullName(req.getFullName());
        user.setUserName(req.getUserName());
        user.setEmail(req.getEmail());
        user.setPhoneNumber(req.getPhoneNumber());

        userRepository.save(user);
        return toDto(user);
    }

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public User findByUserName(String username) {
        return repository.findByUserNameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }


    private UserDto toDto(User u) {
        return UserDto.builder()
                .id(u.getId())
                .fullName(u.getFullName())
                .userName(u.getUserName())
                .email(u.getEmail())
                .phoneNumber(u.getPhoneNumber())
                .roleName(u.getRole().getName())
                .branchId(u.getBranchId())
                .build();
    }
}
