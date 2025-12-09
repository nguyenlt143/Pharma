package vn.edu.fpt.pharma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.pharma.entity.Branch;

import java.util.List;

public interface BranchRepository extends JpaRepository<Branch, Long>, JpaSpecificationExecutor<Branch> {

    @Query(value = "SELECT * FROM branchs", nativeQuery = true)
    List<Branch> findAllIncludingDeleted();

    @Modifying
    @Transactional
    @Query(value = "UPDATE branchs SET deleted = false WHERE id = :id", nativeQuery = true)
    void restoreById(@Param("id") Long id);

}