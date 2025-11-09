package vn.edu.fpt.pharma.service.impl;

import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.edu.fpt.pharma.base.BaseServiceImpl;
import vn.edu.fpt.pharma.config.CustomUserDetails;
import vn.edu.fpt.pharma.dto.manager.UserDto;
import vn.edu.fpt.pharma.dto.manager.UserRequest;
import vn.edu.fpt.pharma.dto.user.ProfileVM;
import vn.edu.fpt.pharma.dto.user.UserVM;
import vn.edu.fpt.pharma.entity.Role;
import vn.edu.fpt.pharma.entity.User;
import vn.edu.fpt.pharma.repository.RoleRepository;
import vn.edu.fpt.pharma.repository.UserRepository;
import vn.edu.fpt.pharma.service.AuditService;
import vn.edu.fpt.pharma.service.UserService;

import java.util.List;

@Service
public class UserServiceImpl extends BaseServiceImpl<User, Long, UserRepository> implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    public UserServiceImpl(UserRepository repository, AuditService auditService, UserRepository userRepository, RoleRepository roleRepository, @Lazy PasswordEncoder passwordEncoder) {
        super(repository, auditService);
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = repository.findByUserNameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new CustomUserDetails(user);
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
        Role role = roleRepository.findById(req.getRoleId())
                .orElseThrow(() -> new RuntimeException("Role  not found"));

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

    @Override
    public void updateProfile(Long id, ProfileVM profileVM) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setFullName(profileVM.fullName());
        user.setEmail(profileVM.email());
        user.setPhoneNumber(profileVM.phone());
        if (profileVM.password() != null && !profileVM.password().isBlank()) {
            if (!profileVM.password().equals(profileVM.confirmPassword())) {
                throw new IllegalArgumentException("Mật khẩu xác nhận không khớp");
            }
            user.setPassword(passwordEncoder.encode(profileVM.password()));
        }
        userRepository.save(user);
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
