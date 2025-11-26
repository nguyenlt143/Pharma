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
import vn.edu.fpt.pharma.entity.Price;
import vn.edu.fpt.pharma.repository.PriceRepository;
import vn.edu.fpt.pharma.service.AuditService;
import vn.edu.fpt.pharma.service.PriceService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PriceServiceImpl extends BaseServiceImpl<Price, Long, PriceRepository> implements PriceService {

    public PriceServiceImpl(PriceRepository repository, AuditService auditService) {
        super(repository, auditService);
    }

    @Override
    public DataTableResponse<PriceResponse> getPrices(DataTableRequest request, Long variantId, Long branchId) {
        int page = request.start() / request.length();
        Sort sort = Sort.by(request.orderDir().equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, request.orderColumn());
        Pageable pageable = PageRequest.of(page, request.length(), sort);

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
        return PriceResponse.builder()
                .id(price.getId())
                .variantId(price.getVariantId())
                .branchId(price.getBranchId())
                .salePrice(price.getSalePrice())
                .startDate(price.getStartDate())
                .endDate(price.getEndDate())
                .build();
    }
}

