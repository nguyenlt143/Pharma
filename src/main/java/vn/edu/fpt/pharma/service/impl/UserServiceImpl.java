package vn.edu.fpt.pharma.service.impl;

import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.pharma.base.BaseServiceImpl;
import vn.edu.fpt.pharma.config.CustomUserDetails;
import vn.edu.fpt.pharma.dto.manager.UserDto;
import vn.edu.fpt.pharma.dto.manager.UserRequest;
import vn.edu.fpt.pharma.dto.user.ProfileVM;
import vn.edu.fpt.pharma.dto.user.UserVM;
import vn.edu.fpt.pharma.entity.Role;
import vn.edu.fpt.pharma.entity.ShiftAssignment;
import vn.edu.fpt.pharma.entity.User;
import vn.edu.fpt.pharma.exception.EntityInUseException;
import vn.edu.fpt.pharma.repository.RoleRepository;
import vn.edu.fpt.pharma.repository.ShiftAssignmentRepository;
import vn.edu.fpt.pharma.repository.UserRepository;
import vn.edu.fpt.pharma.service.AuditService;
import vn.edu.fpt.pharma.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl extends BaseServiceImpl<User, Long, UserRepository> implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ShiftAssignmentRepository shiftAssignmentRepository;
    private final vn.edu.fpt.pharma.repository.BranchRepository branchRepository;

    public UserServiceImpl(UserRepository repository, AuditService auditService, UserRepository userRepository, RoleRepository roleRepository, @Lazy PasswordEncoder passwordEncoder, ShiftAssignmentRepository shiftAssignmentRepository, vn.edu.fpt.pharma.repository.BranchRepository branchRepository) {
        super(repository, auditService);
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.shiftAssignmentRepository = shiftAssignmentRepository;
        this.branchRepository = branchRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = repository.findByUserNameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        if (user.getRole() == null) {
            throw new UsernameNotFoundException("User '" + username + "' does not have a role assigned");
        }

        // If user belongs to a branch, ensure the branch is active (not soft-deleted).
        if (user.getBranchId() != null) {
            boolean branchExists = branchRepository.findById(user.getBranchId()).isPresent();
            if (!branchExists) {
                // Throw here to stop authentication and provide a clear message
                throw new UsernameNotFoundException("Chi nhánh đã ngừng hoạt động");
            }
        }

        return new CustomUserDetails(user);
    }

    public List<UserVM> transformUsers(List<User> users) {
        return List.of();
    }

    @Override
    public List<UserDto> getStaffs(Long branchId) {
        return userRepository.findStaffInBranchIdIncludingDeleted(branchId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public List<UserDto> getStaffsActive(Long branchId) {
        return userRepository.findStaffInBranchId(branchId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public List<UserDto> getPharmacists(Long branchId) {
        return userRepository.findPharmacistsInBranchId(branchId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public List<UserDto> getAccountsByRoles(List<Long> roleIds, boolean showDeleted) {
        return userRepository.findAccountsByRoles(roleIds, showDeleted)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public UserDto getById(Long id) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nhân viên không tồn tại"));
        return toDto(u);
    }

    @Override
    public UserDto create(UserRequest req) {
        if (userRepository.existsByUserNameIgnoreCase(req.getUserName())) {
            throw new RuntimeException("Tên đăng nhập đã tồn tại");
        }
        if (req.getEmail() != null && !req.getEmail().trim().isEmpty()
                && userRepository.existsByEmailIgnoreCase(req.getEmail())) {
            throw new RuntimeException("Email đã tồn tại");
        }
        if (req.getPhoneNumber() != null && !req.getPhoneNumber().trim().isEmpty()
                && userRepository.existsByPhoneNumber(req.getPhoneNumber())) {
            throw new RuntimeException("Số điện thoại đã tồn tại");
        }
        Role role = roleRepository.findById(req.getRoleId())
                .orElseThrow(() -> new RuntimeException("Vai trò không tồn tại"));

        // Validate: Mỗi chi nhánh chỉ có tối đa 1 MANAGER (roleId = 3)
        if (req.getRoleId() == 3L && req.getBranchId() != null) {
            boolean hasManager = userRepository.existsByRoleIdAndBranchIdAndDeletedFalse(3L, req.getBranchId());
            if (hasManager) {
                throw new RuntimeException("Chi nhánh này đã có Manager. Mỗi chi nhánh chỉ có thể có 1 Manager.");
            }
        }

        User user = new User();
        user.setFullName(req.getFullName());
        user.setUserName(req.getUserName());
        user.setPassword(req.getPassword() == null ? null : passwordEncoder.encode(req.getPassword()));
        user.setEmail(req.getEmail());
        user.setPhoneNumber(req.getPhoneNumber());
        user.setBranchId(req.getBranchId());
        user.setRole(role);

        userRepository.save(user);

        // Tự động gán Manager cho Branch nếu role = MANAGER
        if (req.getRoleId() == 3L && req.getBranchId() != null) {
            branchRepository.findById(req.getBranchId()).ifPresent(branch -> {
                branch.setUserId(user.getId());
                branchRepository.save(branch);
            });
        }

        return toDto(user);
    }

    @Override
    public UserDto update(Long id, UserRequest req) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nhân viên không tồn tại"));

        if (!user.getUserName().equalsIgnoreCase(req.getUserName())
                && userRepository.existsByUserNameIgnoreCase(req.getUserName())) {
            throw new RuntimeException("Tên đăng nhập đã tồn tại");
        }
        if (req.getEmail() != null && !req.getEmail().trim().isEmpty()
                && !equalsIgnoreCase(user.getEmail(), req.getEmail())
                && userRepository.existsByEmailIgnoreCaseAndIdNot(req.getEmail(), id)) {
            throw new RuntimeException("Email đã tồn tại");
        }
        if (req.getPhoneNumber() != null && !req.getPhoneNumber().trim().isEmpty()
                && !equals(user.getPhoneNumber(), req.getPhoneNumber())
                && userRepository.existsByPhoneNumberAndIdNot(req.getPhoneNumber(), id)) {
            throw new RuntimeException("Số điện thoại đã tồn tại");
        }

        // Validate: Nếu đổi role thành MANAGER hoặc đổi branch của MANAGER
        // thì phải kiểm tra chi nhánh đích chưa có Manager
        Long currentRoleId = user.getRole().getId();
        Long newBranchId = req.getBranchId();

        // Trường hợp 1: Đổi role thành MANAGER (roleId = 3)
        if (req.getRoleId() == 3L && currentRoleId != 3L && newBranchId != null) {
            boolean hasManager = userRepository.existsByRoleIdAndBranchIdAndDeletedFalse(3L, newBranchId);
            if (hasManager) {
                throw new RuntimeException("Chi nhánh này đã có Manager. Mỗi chi nhánh chỉ có thể có 1 Manager.");
            }
        }

        // Trường hợp 2: User đã là MANAGER và đổi chi nhánh
        if (currentRoleId == 3L && newBranchId != null && !newBranchId.equals(user.getBranchId())) {
            boolean hasManager = userRepository.existsByRoleIdAndBranchIdAndDeletedFalse(3L, newBranchId);
            if (hasManager) {
                throw new RuntimeException("Chi nhánh đích đã có Manager. Mỗi chi nhánh chỉ có thể có 1 Manager.");
            }
        }

        req.setBranchId(user.getBranchId());
        user.setFullName(req.getFullName());
        user.setUserName(req.getUserName());
        user.setEmail(req.getEmail());
        user.setPhoneNumber(req.getPhoneNumber());
        if (req.getPassword() != null && !req.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(req.getPassword()));
        }

        userRepository.save(user);
        return toDto(user);
    }

    @Override
    public void delete(Long id) {
        List<ShiftAssignment> assignments = shiftAssignmentRepository.findByUserId(id);
        if (!assignments.isEmpty()) {
            String shiftDetails = assignments.stream()
                    .map(assignment -> assignment.getShift().getName())
                    .collect(Collectors.joining(", "));
            throw new EntityInUseException("Nhân viên", "ca làm việc: " + shiftDetails);
        }

        // Nếu xóa Manager, xóa userId khỏi Branch
        User user = userRepository.findById(id).orElse(null);
        if (user != null && user.getRole() != null && user.getRole().getId() == 3L && user.getBranchId() != null) {
            branchRepository.findById(user.getBranchId()).ifPresent(branch -> {
                if (branch.getUserId() != null && branch.getUserId().equals(id)) {
                    branch.setUserId(null);
                    branchRepository.save(branch);
                }
            });
        }

        userRepository.deleteById(id);
    }

    @Override
    public void restore(Long id) {
        userRepository.restoreById(id);
    }

    @Override
    public User findByUserName(String username) {
        return repository.findByUserNameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    @Transactional
    public void updateProfile(Long id, vn.edu.fpt.pharma.dto.user.ProfileUpdateRequest profileUpdateRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Only verify current password if user wants to change password
        if (profileUpdateRequest.getPassword() != null && !profileUpdateRequest.getPassword().isBlank()) {
            // Validate password length
            if (profileUpdateRequest.getPassword().length() < 6) {
                throw new RuntimeException("Mật khẩu mới phải có ít nhất 6 ký tự");
            }
            if (profileUpdateRequest.getPassword().length() > 100) {
                throw new RuntimeException("Mật khẩu mới không được vượt quá 100 ký tự");
            }

            // Verify current password when changing password
            if (profileUpdateRequest.getCurrentPassword() == null || profileUpdateRequest.getCurrentPassword().isBlank()) {
                throw new RuntimeException("Vui lòng nhập mật khẩu hiện tại để đổi mật khẩu mới");
            }
            if (!passwordEncoder.matches(profileUpdateRequest.getCurrentPassword(), user.getPassword())) {
                throw new RuntimeException("Mật khẩu hiện tại không đúng");
            }
        }

        // Validate email uniqueness
        if (userRepository.existsByEmailIgnoreCaseAndIdNot(profileUpdateRequest.getEmail(), id)) {
            throw new RuntimeException("Email đã được sử dụng bởi người dùng khác");
        }

        // Validate phone uniqueness (if provided)
        if (profileUpdateRequest.getPhone() != null && !profileUpdateRequest.getPhone().trim().isEmpty()) {
            if (userRepository.existsByPhoneNumberAndIdNot(profileUpdateRequest.getPhone(), id)) {
                throw new RuntimeException("Số điện thoại đã được sử dụng bởi người dùng khác");
            }
        }

        user.setFullName(profileUpdateRequest.getFullName());
        user.setPhoneNumber(profileUpdateRequest.getPhone());
        user.setEmail(profileUpdateRequest.getEmail());

        // Update avatar if provided
        if (profileUpdateRequest.getAvatarData() != null && !profileUpdateRequest.getAvatarData().isEmpty()) {
            user.setImageUrl(profileUpdateRequest.getAvatarData());
        }

        // Chỉ cập nhật mật khẩu nếu được cung cấp
        if (profileUpdateRequest.getPassword() != null && !profileUpdateRequest.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(profileUpdateRequest.getPassword()));
        }

        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateProfile(Long id, ProfileVM profileVM) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setFullName(profileVM.fullName());
        user.setPhoneNumber(profileVM.phone());
        if (profileVM.password() != null && !profileVM.password().isBlank()) {
            if (!profileVM.password().equals(profileVM.confirmPassword())) {
                throw new IllegalArgumentException("Mật khẩu xác nhận không khớp");
            }
            user.setPassword(passwordEncoder.encode(profileVM.password()));
        }
        userRepository.save(user);
    }

    private boolean equalsIgnoreCase(String a, String b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.equalsIgnoreCase(b);
    }

    private boolean equals(String a, String b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.equals(b);
    }

    private UserDto toDto(User u) {
        String branchName = null;
        if (u.getBranchId() != null) {
            branchName = branchRepository.findById(u.getBranchId())
                    .map(vn.edu.fpt.pharma.entity.Branch::getName)
                    .orElse(null);
        }
        return UserDto.builder()
                .id(u.getId())
                .fullName(u.getFullName())
                .userName(u.getUserName())
                .email(u.getEmail())
                .phoneNumber(u.getPhoneNumber())
                .password(u.getPassword())
                .roleId(u.getRole().getId())
                .roleName(u.getRole().getName())
                .branchId(u.getBranchId())
                .branchName(branchName)
                .deleted(u.isDeleted())
                .build();
    }
}
