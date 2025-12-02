package vn.edu.fpt.pharma.service.impl;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PriceServiceImpl extends BaseServiceImpl<Price, Long, PriceRepository> implements PriceService {

    private final MedicineVariantRepository medicineVariantRepository;

    public PriceServiceImpl(PriceRepository repository, AuditService auditService, MedicineVariantRepository medicineVariantRepository) {
        super(repository, auditService);
        this.medicineVariantRepository = medicineVariantRepository;
    }

    @Override
    public DataTableResponse<PriceResponse> getPrices(DataTableRequest request, Long variantId, Long branchId) {
        Pageable pageable = createPageable(request);

        Specification<Price> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (variantId != null) {
                predicates.add(cb.equal(root.get("variantId"), variantId));
            }
            if (branchId != null) {
                predicates.add(cb.equal(root.get("branchId"), branchId));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Price> pricePage = repository.findAll(spec, pageable);
        List<PriceResponse> priceResponses = pricePage.getContent().stream()
                .map(this::mapToPriceResponse)
                .collect(Collectors.toList());

        return new DataTableResponse<>(request.draw(), pricePage.getTotalElements(), pricePage.getTotalElements(), priceResponses);
    }

    @Override
    public PriceResponse createOrUpdatePrice(PriceRequest request) {
        Price price = request.getId() != null ? repository.findById(request.getId()).orElse(new Price()) : new Price();
        price.setVariantId(request.getVariantId());
        price.setBranchId(request.getBranchId());
        price.setSalePrice(request.getSalePrice());
        price.setBranchPrice(request.getBranchPrice());
        price.setStartDate(request.getStartDate());
        price.setEndDate(request.getEndDate());
        Price savedPrice = repository.save(price);
        return mapToPriceResponse(savedPrice);
    }

    @Override
    public PriceResponse getPriceById(Long id) {
        return repository.findById(id).map(this::mapToPriceResponse).orElse(null);
    }

    private PriceResponse mapToPriceResponse(Price price) {
        MedicineVariant variant = null;
        if (price.getVariantId() != null) {
            variant = medicineVariantRepository.findById(price.getVariantId()).orElse(null);
        }
        return PriceResponse.fromEntity(price, variant);
    }
}

