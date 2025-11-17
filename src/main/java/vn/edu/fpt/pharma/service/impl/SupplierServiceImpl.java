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
import vn.edu.fpt.pharma.dto.supplier.SupplierRequest;
import vn.edu.fpt.pharma.dto.supplier.SupplierResponse;
import vn.edu.fpt.pharma.entity.Supplier;
import vn.edu.fpt.pharma.exception.EntityInUseException;
import vn.edu.fpt.pharma.repository.SupplierRepository;
import vn.edu.fpt.pharma.service.AuditService;
import vn.edu.fpt.pharma.service.SupplierService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SupplierServiceImpl extends BaseServiceImpl<Supplier, Long, SupplierRepository> implements SupplierService {

    public SupplierServiceImpl(SupplierRepository repository, AuditService auditService) {
        super(repository, auditService);
    }

    @Override
    public List<Supplier> getAllSuppliers() {
        return repository.findAll();
    }

    @Override
    public DataTableResponse<SupplierResponse> getSuppliers(DataTableRequest request) {
        int page = request.start() / request.length();
        
        Sort sort = Sort.unsorted();
        if (request.orderColumn() != null && !request.orderColumn().isEmpty()) {
            String[] props = request.orderColumn().split("\\.");
            sort = "desc".equalsIgnoreCase(request.orderDir()) ?
                    Sort.by(props).descending() :
                    Sort.by(props).ascending();
        }
        Pageable pageable = PageRequest.of(page, request.length(), sort);

        Specification<Supplier> spec = buildSpecification(request);
        Page<Supplier> pageResult = spec != null ? 
                repository.findAll(spec, pageable) : 
                repository.findAll(pageable);

        List<SupplierResponse> responses = pageResult.getContent().stream()
                .map(SupplierResponse::fromEntity)
                .collect(Collectors.toList());

        return new DataTableResponse<>(
                request.draw(),
                repository.count(),
                pageResult.getTotalElements(),
                responses
        );
    }

    private Specification<Supplier> buildSpecification(DataTableRequest request) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.searchValue() != null && !request.searchValue().isEmpty()) {
                String keyword = "%" + request.searchValue().toLowerCase() + "%";
                Predicate namePredicate = cb.like(cb.lower(root.get("name")), keyword);
                Predicate phonePredicate = cb.like(cb.lower(root.get("phone")), keyword);
                Predicate addressPredicate = cb.like(cb.lower(root.get("address")), keyword);
                predicates.add(cb.or(namePredicate, phonePredicate, addressPredicate));
            }

            return predicates.isEmpty() ? null : cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    @Override
    public SupplierResponse createSupplier(SupplierRequest request) {
        Supplier supplier = Supplier.builder()
                .name(request.getSupplierName())
                .phone(request.getPhone())
                .address(request.getAddress())
                .build();

        Supplier saved = repository.save(supplier);
        return SupplierResponse.fromEntity(saved);
    }

    @Override
    public SupplierResponse updateSupplier(Long id, SupplierRequest request) {
        Supplier existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));
        
        if (request.getSupplierName() != null) existing.setName(request.getSupplierName());
        if (request.getPhone() != null) existing.setPhone(request.getPhone());
        if (request.getAddress() != null) existing.setAddress(request.getAddress());
        
        Supplier updated = repository.save(existing);
        return SupplierResponse.fromEntity(updated);
    }

    @Override
    public SupplierResponse getSupplierById(Long id) {
        Supplier supplier = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));
        return SupplierResponse.fromEntity(supplier);
    }

    @Override
    public void deleteById(Long id) {
        long batchCount = repository.countBatchesBySupplierId(id);
        long movementCount = repository.countInventoryMovementsBySupplierId(id);
        
        if (batchCount > 0 || movementCount > 0) {
            StringBuilder usage = new StringBuilder();
            if (batchCount > 0) {
                usage.append("danh sách lô hàng (").append(batchCount).append(" lô)");
            }
            if (movementCount > 0) {
                if (usage.length() > 0) usage.append(", ");
                usage.append("phiếu nhập xuất (").append(movementCount).append(" phiếu)");
            }
            throw new EntityInUseException("Nhà cung cấp", usage.toString());
        }
        super.deleteById(id);
    }
}
