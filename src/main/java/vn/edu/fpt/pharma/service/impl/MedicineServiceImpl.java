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
import vn.edu.fpt.pharma.dto.medicine.MedicineRequest;
import vn.edu.fpt.pharma.dto.medicine.MedicineResponse;
import vn.edu.fpt.pharma.dto.medicine.MedicineSearchDTO;
import vn.edu.fpt.pharma.entity.Category;
import vn.edu.fpt.pharma.entity.Medicine;
import vn.edu.fpt.pharma.exception.EntityInUseException;
import vn.edu.fpt.pharma.repository.CategoryRepository;
import vn.edu.fpt.pharma.repository.MedicineRepository;
import vn.edu.fpt.pharma.service.AuditService;
import vn.edu.fpt.pharma.service.MedicineService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MedicineServiceImpl extends BaseServiceImpl<Medicine, Long, MedicineRepository> implements MedicineService {

    private final CategoryRepository categoryRepository;

    public MedicineServiceImpl(MedicineRepository repository, AuditService auditService, CategoryRepository categoryRepository) {
        super(repository, auditService);
        this.categoryRepository = categoryRepository;
    }

    @Override
    public DataTableResponse<MedicineResponse> getMedicines(DataTableRequest request, Integer status) {
        int page = request.start() / request.length();
        
        Sort sort = Sort.unsorted();
        if (request.orderColumn() != null && !request.orderColumn().isEmpty()) {
            String[] props = request.orderColumn().split("\\.");
            sort = "desc".equalsIgnoreCase(request.orderDir()) ?
                    Sort.by(props).descending() :
                    Sort.by(props).ascending();
        }
        Pageable pageable = PageRequest.of(page, request.length(), sort);

        Specification<Medicine> spec = buildSpecification(request, status);
        Page<Medicine> pageResult = spec != null ? 
                repository.findAll(spec, pageable) : 
                repository.findAll(pageable);

        List<MedicineResponse> responses = pageResult.getContent().stream()
                .map(MedicineResponse::fromEntity)
                .collect(Collectors.toList());

        return new DataTableResponse<>(
                request.draw(),
                repository.count(),
                pageResult.getTotalElements(),
                responses
        );
    }

    private Specification<Medicine> buildSpecification(DataTableRequest request, Integer status) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Status filter
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            // Search filter
            if (request.searchValue() != null && !request.searchValue().isEmpty()) {
                String keyword = "%" + request.searchValue().toLowerCase() + "%";
                Predicate namePredicate = cb.like(cb.lower(root.get("name")), keyword);
                Predicate brandPredicate = cb.like(cb.lower(root.get("brandName")), keyword);
                Predicate ingredientPredicate = cb.like(cb.lower(root.get("activeIngredient")), keyword);
                predicates.add(cb.or(namePredicate, brandPredicate, ingredientPredicate));
            }

            return predicates.isEmpty() ? null : cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    @Override
    public MedicineResponse createMedicine(MedicineRequest request) {
        Category category = request.getCategoryId() != null ?
                categoryRepository.findById(request.getCategoryId())
                        .orElseThrow(() -> new RuntimeException("Category not found")) : null;

        Medicine medicine = Medicine.builder()
                .name(request.getMedicineName())
                .category(category)
                .activeIngredient(request.getActiveIngredient())
                .brandName(request.getBrandName())
                .manufacturer(request.getManufacturer())
                .country(request.getCountryOfOrigin())
                .build();

        Medicine saved = repository.save(medicine);
        return MedicineResponse.fromEntity(saved);
    }

    @Override
    public MedicineResponse updateMedicine(Long id, MedicineRequest request) {
        Medicine medicine = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medicine not found"));

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            medicine.setCategory(category);
        }

        if (request.getMedicineName() != null) medicine.setName(request.getMedicineName());
        if (request.getActiveIngredient() != null) medicine.setActiveIngredient(request.getActiveIngredient());
        if (request.getBrandName() != null) medicine.setBrandName(request.getBrandName());
        if (request.getManufacturer() != null) medicine.setManufacturer(request.getManufacturer());
        if (request.getCountryOfOrigin() != null) medicine.setCountry(request.getCountryOfOrigin());

        Medicine updated = repository.save(medicine);
        return MedicineResponse.fromEntity(updated);
    }

    @Override
    public MedicineResponse getMedicineById(Long id) {
        Medicine medicine = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medicine not found"));
        return MedicineResponse.fromEntity(medicine);
    }

    @Override
    public void deleteById(Long id) {
        long variantCount = repository.countVariantsByMedicineId(id);
        if (variantCount > 0) {
            throw new EntityInUseException("Thuốc", "danh sách biến thể thuốc (" + variantCount + " biến thể)");
        }
        super.deleteById(id);
    }

    @Override
    public List<MedicineSearchDTO> searchMedicinesByKeyword(String keyword) {
        List<Object[]> rows = repository.searchMedicinesByKeyword(keyword);
        return rows.stream()
                .map(r -> new MedicineSearchDTO(
                        ((Number) r[0]).longValue(),
                        (String) r[1],
                        (String) r[2],
                        (String) r[3],
                        (String) r[4],
                        (String) r[5]
                ))
                .collect(Collectors.toList());
    }
}
