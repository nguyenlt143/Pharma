package vn.edu.fpt.pharma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import vn.edu.fpt.pharma.entity.Price;

public interface PriceRepository extends JpaRepository<Price, Long>, JpaSpecificationExecutor<Price> {
    // Price không có foreign key từ bảng khác, chỉ có variantId là Long, không phải relationship
    // Nên không cần kiểm tra
}