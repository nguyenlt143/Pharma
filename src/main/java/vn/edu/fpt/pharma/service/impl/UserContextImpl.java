package vn.edu.fpt.pharma.service.impl;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import vn.edu.fpt.pharma.config.CustomUserDetails;
import vn.edu.fpt.pharma.service.ShiftWorkService;
import vn.edu.fpt.pharma.service.UserContext;

@Service
public class UserContextImpl implements UserContext {

    private final ShiftWorkService shiftWorkService;

    public UserContextImpl(ShiftWorkService shiftWorkService) {
        this.shiftWorkService = shiftWorkService;
    }

    @Override
    public Long getUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null)
            throw new RuntimeException("Không tìm thấy thông tin user trong Security Context");

        if (auth.getPrincipal() instanceof Long) {
            return (Long) auth.getPrincipal();
        }

        if (auth.getPrincipal() instanceof CustomUserDetails user) {
            return user.getId();
        }

        throw new RuntimeException("Không thể lấy userId");
    }

    @Override
    public Long getBranchId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || auth.getPrincipal() == null) {
            throw new RuntimeException("Không tìm thấy thông tin user trong Security Context");
        }

        Object principal = auth.getPrincipal();

        // Trường hợp bạn set Principal = userId (Long)
        if (principal instanceof Long userId) {
            throw new RuntimeException("Principal không chứa branchId, cần CustomUserDetails");
        }

        // Trường hợp dùng CustomUserDetails
        if (principal instanceof CustomUserDetails userDetails) {
            Long branchId = userDetails.getUser().getBranchId();
            if (branchId == null) {
                throw new RuntimeException("User không có branchId");
            }
            return branchId;
        }

        throw new RuntimeException("Không thể lấy branchId từ Security Context");
    }

    @Override
    public Long getShiftWorkId() {
        try {
            Long userId = getUserId();
            Long branchId = getBranchId();
            return shiftWorkService.getCurrentShiftWorkId(userId, branchId);
        } catch (Exception e) {
            // Return null if user is not in active shift instead of throwing exception
            return null;
        }
    }

}
