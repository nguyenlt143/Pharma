package vn.edu.fpt.pharma.service.impl;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.edu.fpt.pharma.base.BaseServiceImpl;
import vn.edu.fpt.pharma.dto.DataTableRequest;
import vn.edu.fpt.pharma.dto.DataTableResponse;
import vn.edu.fpt.pharma.dto.category.CategoryRequest;
import vn.edu.fpt.pharma.dto.category.CategoryResponse;
import vn.edu.fpt.pharma.entity.Category;
import vn.edu.fpt.pharma.exception.EntityInUseException;
import vn.edu.fpt.pharma.repository.CategoryRepository;
import vn.edu.fpt.pharma.service.AuditService;
import vn.edu.fpt.pharma.service.CategoryService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl extends BaseServiceImpl<Category, Long, CategoryRepository> implements CategoryService {

    public CategoryServiceImpl(CategoryRepository repository, AuditService auditService) {
        super(repository, auditService);
    }

    @Override
    public DataTableResponse<CategoryResponse> getCategories(DataTableRequest request) {
        int page = request.start() / request.length();
        
        Sort sort = Sort.unsorted();
        if (request.orderColumn() != null && !request.orderColumn().isEmpty()) {
            String[] props = request.orderColumn().split("\\.");
            sort = "desc".equalsIgnoreCase(request.orderDir()) ?
                    Sort.by(props).descending() :
                    Sort.by(props).ascending();
        }
        Pageable pageable = PageRequest.of(page, request.length(), sort);

        Specification<Category> spec = buildSpecification(request);
        Page<Category> pageResult = spec != null ? 
                repository.findAll(spec, pageable) : 
                repository.findAll(pageable);

        List<CategoryResponse> responses = pageResult.getContent().stream()
                .map(CategoryResponse::fromEntity)
                .collect(Collectors.toList());

        return new DataTableResponse<>(
                request.draw(),
                repository.count(),
                pageResult.getTotalElements(),
                responses
        );
    }

    private Specification<Category> buildSpecification(DataTableRequest request) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.searchValue() != null && !request.searchValue().isEmpty()) {
                String keyword = "%" + request.searchValue().toLowerCase() + "%";
                Predicate namePredicate = cb.like(cb.lower(root.get("name")), keyword);
                Predicate descPredicate = cb.like(cb.lower(root.get("description")), keyword);
                predicates.add(cb.or(namePredicate, descPredicate));
            }

            return predicates.isEmpty() ? null : cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    @Override
    public CategoryResponse createCategory(CategoryRequest request) {
        Category category = Category.builder()
                .name(request.getCategoryName())
                .description(request.getDescription())
                .build();

        Category saved = repository.save(category);
        return CategoryResponse.fromEntity(saved);
    }

    @Override
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if (request.getCategoryName() != null) category.setName(request.getCategoryName());
        if (request.getDescription() != null) category.setDescription(request.getDescription());

        Category updated = repository.save(category);
        return CategoryResponse.fromEntity(updated);
    }

    @Override
    public CategoryResponse getCategoryById(Long id) {
        Category category = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        return CategoryResponse.fromEntity(category);
    }

    @Override
    public void deleteById(Long id) {
        long medicineCount = repository.countMedicinesByCategoryId(id);
        if (medicineCount > 0) {
            throw new EntityInUseException("Danh mục", "danh sách thuốc (" + medicineCount + " thuốc)");
        }
        super.deleteById(id);
    }
}
