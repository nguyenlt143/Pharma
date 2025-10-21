package vn.edu.fpt.pharma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import vn.edu.fpt.pharma.entity.ShiftWork;

public interface ShiftWorkRepository extends JpaRepository<ShiftWork, Long>, JpaSpecificationExecutor<ShiftWork> {
}