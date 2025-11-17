package vn.edu.fpt.pharma.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.fpt.pharma.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long>, JpaSpecificationExecutor<Category> {
    @Query("SELECT COUNT(m) FROM Medicine m WHERE m.category.id = :categoryId")
    long countMedicinesByCategoryId(@Param("categoryId") Long categoryId);
}