package vn.edu.fpt.pharma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import vn.edu.fpt.pharma.entity.Store;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public interface StoreRepository extends JpaRepository<Store, Long>, JpaSpecificationExecutor<Store> {
    List<Store> findAllByStoreCodeIn(Collection<String> storeCodes);
}
