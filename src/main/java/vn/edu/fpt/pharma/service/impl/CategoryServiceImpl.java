package vn.edu.fpt.pharma.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl extends BaseServiceImpl<Category, Long, CategoryRepository> implements CategoryService {

    public CategoryServiceImpl(CategoryRepository repository, AuditService auditService) {
        super(repository, auditService);
    }

    @Override
    public DataTableResponse<CategoryResponse> getCategories(DataTableRequest request) {
        Pageable pageable = createPageable(request);
        Specification<Category> spec = buildSearchSpecification(request, List.of("name", "description"));
        
        Page<Category> pageResult = spec != null ? 
                repository.findAll(spec, pageable) : 
                repository.findAll(pageable);

        List<CategoryResponse> responses = pageResult.getContent().stream()
                .map(CategoryResponse::fromEntity)
                .collect(Collectors.toList());

        Page<CategoryResponse> responsePage = new org.springframework.data.domain.PageImpl<>(
                responses, pageable, pageResult.getTotalElements());

        return createDataTableResponse(request, responsePage, repository.count());
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
