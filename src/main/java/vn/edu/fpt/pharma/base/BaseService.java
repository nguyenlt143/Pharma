package vn.edu.fpt.pharma.base;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BaseService <T extends BaseEntity<ID>, ID> {
    T create(T entity);
    T update(T entity);
    void deleteById(ID id);
    void deleteByIds(List<ID> ids);
    void delete(T entity);
    T findById(ID id);
    List<T> findAll();
    List<T> findAllWithAuditUser();
}
