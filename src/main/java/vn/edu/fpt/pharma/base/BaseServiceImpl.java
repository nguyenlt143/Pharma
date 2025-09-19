package vn.edu.fpt.pharma.base;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import vn.edu.fpt.pharma.dto.DataTableRequest;
import vn.edu.fpt.pharma.dto.DataTableResponse;
import vn.edu.fpt.pharma.service.AuditService;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public abstract class BaseServiceImpl<T extends BaseEntity<ID>, ID, RE extends JpaRepository<T, ID> & JpaSpecificationExecutor<T>>
        implements BaseService<T, ID> {
    protected final RE repository;
    protected final AuditService auditService;

    @Override
    public T create(T entity) {
        if (entity.getId() != null) {
            throw new IllegalArgumentException("New entity cannot have an ID");
        }
        return repository.save(entity);
    }

    @Override
    public T update(T entity) {
        if (entity.getId() == null) {
            throw new IllegalArgumentException("Entity ID cannot be null for update");
        }
        return repository.save(entity);
    }

    @Override
    public void deleteById(ID id) {
        repository.deleteById(id);
    }

    public void deleteByIds(List<ID> ids) {
        repository.deleteAllById(ids);
    }

    @Override
    public void delete(T entity) {
        repository.delete(entity);
    }

    @Override
    public T findById(ID id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public List<T> findAll() {
        return repository.findAll();
    }

    @Override
    public List<T> findAllWithAuditUser() {
        List<T> entities = repository.findAll();
        return auditService.addAuditInfo(entities);
    }

    protected DataTableResponse<T> findAllForDataTable(
            DataTableRequest request,
            List<String> searchableColumns
    ) {
        int page = request.start() / request.length();

        Sort sort = Sort.unsorted();
        if (request.orderColumn() != null && !request.orderColumn().isEmpty()) {
            sort = "desc".equalsIgnoreCase(request.orderDir()) ?
                    Sort.by(request.orderColumn()).descending() :
                    Sort.by(request.orderColumn()).ascending();
        }
        Pageable pageable = PageRequest.of(page, request.length(), sort);

        Specification<T> spec = buildSearchSpec(request, searchableColumns);

        Page<T> pageResult = (spec != null) ?
                repository.findAll(spec, pageable) :
                repository.findAll(pageable);

        return new DataTableResponse<>(
                request.draw(),
                repository.count(),
                pageResult.getTotalElements(),
                pageResult.getContent()
        );
    }

    private Specification<T> buildSearchSpec(DataTableRequest request, List<String> searchableColumns) {
        if (request.searchValue() == null || request.searchValue().isEmpty() || searchableColumns.isEmpty()) {
            return null;
        }

        String keyword = "%" + request.searchValue() + "%";
        return (root, _, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            for (String col : searchableColumns) {
                predicates.add(cb.like(root.get(col), keyword));
            }
            return cb.or(predicates.toArray(new Predicate[0]));
        };
    }
}
