package vn.edu.fpt.pharma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.fpt.pharma.entity.Shift;

import java.util.List;

public interface ShiftRepository extends JpaRepository<Shift, Long>, JpaSpecificationExecutor<Shift> {
    @Query("select s from Shift s where (:q is null or lower(s.name) like lower(concat('%', :q, '%'))) and (:branchId is null or s.branchId = :branchId)")
    List<Shift> search(@Param("q") String q, @Param("branchId") Long branchId);
}