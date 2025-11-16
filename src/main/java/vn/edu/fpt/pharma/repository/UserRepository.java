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
    User findByEmailIgnoreCase(String email);
    Optional<User> findByUserNameIgnoreCase(String username);
    @Query("""
    SELECT u 
    FROM User u 
    WHERE u.role.id IN (4, 6) 
      AND u.branchId = :branchId
""")
    List<User> findStaffInBranchId(@Param("branchId") Long branchId);

    // Include soft-deleted users as well (bypass @SQLRestriction via native query)
    @Query(value = "SELECT * FROM users u WHERE u.role_id IN (4,6) AND u.branch_id = :branchId", nativeQuery = true)
    List<User> findStaffInBranchIdIncludingDeleted(@Param("branchId") Long branchId);

    @Query("""
    SELECT u
    FROM User u
    WHERE u.role.id = 6
      AND u.branchId = :branchId
""")
    List<User> findPharmacistsInBranchId(@Param("branchId") Long branchId);

    List<User> findByBranchId(Long branchId);
    boolean existsByUserNameIgnoreCase(String userName);

    Optional<User> findById(long id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE users SET deleted = false WHERE id = :id", nativeQuery = true)
    void restoreById(@Param("id") Long id);
}
