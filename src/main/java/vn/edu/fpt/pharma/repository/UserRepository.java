package vn.edu.fpt.pharma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import vn.edu.fpt.pharma.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    User findByEmailIgnoreCase(String email);
    Optional<User> findByUserNameIgnoreCase(String username);
    @Query("SELECT u FROM User u WHERE (u.role.id = 4 OR u.role.id = 6) AND u.branchId = :branchId")
    List<User> findStaffInBranchId( Long branchId);
    List<User> findByBranchId(Long branchId);
}
