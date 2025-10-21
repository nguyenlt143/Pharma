package vn.edu.fpt.pharma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import vn.edu.fpt.pharma.entity.Role;


public interface RoleRepository extends JpaRepository<Role, Long>, JpaSpecificationExecutor<Role> {
}