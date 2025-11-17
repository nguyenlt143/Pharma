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
import vn.edu.fpt.pharma.dto.price.PriceRequest;
import vn.edu.fpt.pharma.dto.price.PriceResponse;
import vn.edu.fpt.pharma.entity.MedicineVariant;
import vn.edu.fpt.pharma.entity.Price;
import vn.edu.fpt.pharma.repository.MedicineVariantRepository;
import vn.edu.fpt.pharma.repository.PriceRepository;
import vn.edu.fpt.pharma.service.AuditService;
import vn.edu.fpt.pharma.service.PriceService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PriceServiceImpl extends BaseServiceImpl<Price, Long, PriceRepository> implements PriceService {

    private final MedicineVariantRepository medicineVariantRepository;

    public PriceServiceImpl(PriceRepository repository, AuditService auditService, 
                            MedicineVariantRepository medicineVariantRepository) {
        super(repository, auditService);
        this.medicineVariantRepository = medicineVariantRepository;
    }

    @Override
    public DataTableResponse<PriceResponse> getPrices(DataTableRequest request, Long variantId, Long branchId) {
        int page = request.start() / request.length();
        
        Sort sort = Sort.unsorted();
        if (request.orderColumn() != null && !request.orderColumn().isEmpty()) {
            String[] props = request.orderColumn().split("\\.");
            sort = "desc".equalsIgnoreCase(request.orderDir()) ?
                    Sort.by(props).descending() :
                    Sort.by(props).ascending();
        }
        Pageable pageable = PageRequest.of(page, request.length(), sort);

        Specification<Price> spec = buildSpecification(request, variantId, branchId);
        Page<Price> pageResult = spec != null ? 
                repository.findAll(spec, pageable) : 
                repository.findAll(pageable);

        // Load all variants for mapping
        List<Long> variantIds = pageResult.getContent().stream()
                .map(Price::getVariantId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());
        
        Map<Long, MedicineVariant> variantMap = medicineVariantRepository.findAllById(variantIds).stream()
                .collect(Collectors.toMap(MedicineVariant::getId, v -> v));

        List<PriceResponse> responses = pageResult.getContent().stream()
                .map(price -> PriceResponse.fromEntity(price, variantMap.get(price.getVariantId())))
                .collect(Collectors.toList());

        return new DataTableResponse<>(
                request.draw(),
                repository.count(),
                pageResult.getTotalElements(),
                responses
        );
    }

    private Specification<Price> buildSpecification(DataTableRequest request, Long variantId, Long branchId) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (variantId != null) {
                predicates.add(cb.equal(root.get("variantId"), variantId));
            }

            if (branchId != null) {
                predicates.add(cb.equal(root.get("branchId"), branchId));
            }

            // Search filter (if needed)
            if (request.searchValue() != null && !request.searchValue().isEmpty()) {
                // Add search logic if needed
            }

            return predicates.isEmpty() ? null : cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    @Override
    public PriceResponse createOrUpdatePrice(PriceRequest request) {
        // Check if price exists for variant and branch combination
        Price existingPrice = repository.findAll().stream()
                .filter(p -> {
                    boolean variantMatch = p.getVariantId() != null && p.getVariantId().equals(request.getVariantId());
                    boolean branchMatch = (request.getBranchId() == null && p.getBranchId() == null) ||
                                          (request.getBranchId() != null && p.getBranchId() != null &&
                                           p.getBranchId().equals(request.getBranchId()));
                    return variantMatch && branchMatch;
                })
                .findFirst()
                .orElse(null);

        Price price;
        if (existingPrice != null) {
            // Update existing price
            price = existingPrice;
            if (request.getSalePrice() != null) price.setSalePrice(request.getSalePrice());
            if (request.getBranchPrice() != null) price.setBranchPrice(request.getBranchPrice());
            if (request.getStartDate() != null) price.setStartDate(request.getStartDate());
            if (request.getEndDate() != null) price.setEndDate(request.getEndDate());
            price.setBranchId(request.getBranchId());
        } else {
            // Create new price
            price = Price.builder()
                    .variantId(request.getVariantId())
                    .branchId(request.getBranchId())
                    .salePrice(request.getSalePrice())
                    .branchPrice(request.getBranchPrice())
                    .startDate(request.getStartDate() != null ? request.getStartDate() : LocalDateTime.now())
                    .endDate(request.getEndDate())
                    .build();
        }

        Price saved = repository.save(price);
        MedicineVariant variant = saved.getVariantId() != null ?
                medicineVariantRepository.findById(saved.getVariantId()).orElse(null) : null;
        return PriceResponse.fromEntity(saved, variant);
    }

    @Override
    public PriceResponse getPriceById(Long id) {
        Price price = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Price not found"));
        MedicineVariant variant = price.getVariantId() != null ?
                medicineVariantRepository.findById(price.getVariantId()).orElse(null) : null;
        return PriceResponse.fromEntity(price, variant);
    }
}

