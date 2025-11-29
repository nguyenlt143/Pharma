package vn.edu.fpt.pharma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.pharma.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByUserNameIgnoreCase(String username);
    // Generalized method to find staff by role IDs and branch
    @Query(value = "SELECT * FROM users u WHERE u.role_id IN (:roleIds) AND u.branch_id = :branchId AND (:includeDeleted = true OR u.deleted = false)", nativeQuery = true)
    List<User> findStaffByRolesAndBranch(@Param("roleIds") List<Long> roleIds, @Param("branchId") Long branchId, @Param("includeDeleted") boolean includeDeleted);

    // Convenience methods using the generalized query
    default List<User> findStaffInBranchId(Long branchId) {
        return findStaffByRolesAndBranch(List.of(4L, 6L), branchId, false);
    }

    default List<User> findStaffInBranchIdIncludingDeleted(Long branchId) {
        return findStaffByRolesAndBranch(List.of(4L, 6L), branchId, true);
    }

    default List<User> findPharmacistsInBranchId(Long branchId) {
        return findStaffByRolesAndBranch(List.of(6L), branchId, false);
    }

    boolean existsByUserNameIgnoreCase(String userName);

    Optional<User> findById(long id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE users SET deleted = false WHERE id = :id", nativeQuery = true)
    void restoreById(@Param("id") Long id);
}
