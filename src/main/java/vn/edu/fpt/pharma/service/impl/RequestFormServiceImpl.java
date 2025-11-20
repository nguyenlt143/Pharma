package vn.edu.fpt.pharma.service.impl;

import org.springframework.stereotype.Service;
import vn.edu.fpt.pharma.base.BaseServiceImpl;
import vn.edu.fpt.pharma.constant.RequestType;
import vn.edu.fpt.pharma.dto.requestform.RequestFormVM;
import vn.edu.fpt.pharma.dto.warehouse.RequestDetailVM;
import vn.edu.fpt.pharma.dto.warehouse.RequestList;
import vn.edu.fpt.pharma.entity.RequestForm;
import vn.edu.fpt.pharma.repository.RequestDetailRepository;
import vn.edu.fpt.pharma.repository.RequestFormRepository;
import vn.edu.fpt.pharma.service.AuditService;
import vn.edu.fpt.pharma.service.RequestFormService;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RequestFormServiceImpl extends BaseServiceImpl<RequestForm, Long, RequestFormRepository>
        implements RequestFormService {

    private final RequestFormRepository repository;
    private final AuditService auditService;
    private final RequestDetailRepository requestDetailRepository;


    // Constructor gọi explicit BaseServiceImpl
    public RequestFormServiceImpl(RequestFormRepository repository, AuditService auditService, RequestDetailRepository requestDetailRepository) {
        super(repository, auditService);
        this.repository = repository;
        this.auditService = auditService;
        this.requestDetailRepository = requestDetailRepository;
    }

    @Override
    public List<RequestFormVM> getRequestFormsByBranch(Long branchId) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        List<RequestForm> forms = repository.findAllByBranchId(branchId);
        List<Long> ids = forms.stream().map(RequestForm::getId).toList();
        Map<Long, Long> countMap = ids.isEmpty() ? Map.of() : requestDetailRepository.countDistinctMedicineByRequestIds(ids)
                .stream()
                .collect(Collectors.toMap(row -> ((Number) row[0]).longValue(), row -> ((Number) row[1]).longValue()));

        return forms.stream().map(entity ->
                new RequestFormVM(
                        "#RQ" + String.format("%03d", entity.getId()),
                        entity.getRequestType() != null ? entity.getRequestType().name() : "N/A",
                        entity.getRequestStatus() != null ? entity.getRequestStatus().name() : "N/A",
                        entity.getNote() != null ? entity.getNote() : "",
                        entity.getCreatedAt() != null ? entity.getCreatedAt().format(fmt) : "",
                        countMap.getOrDefault(entity.getId(), 0L),
                        BigDecimal.ZERO
                )
        ).toList();
    }

    @Override
    public List<RequestFormVM> searchRequestForms(Long branchId, String code, java.time.LocalDate createdAt) {
        Long id = null;
        if (code != null && code.startsWith("#RQ")) {
            try {
                id = Long.parseLong(code.substring(3));
            } catch (NumberFormatException ignored) {}
        }

        List<RequestForm> entities = repository.searchImportForms(branchId, id, createdAt);
        return mapToRequestFormVM(entities);
    }

    @Override
    public List<RequestFormVM> searchImportForms(Long branchId, String code, java.time.LocalDate createdAt) {
        Long id = null;
        if (code != null && code.startsWith("#RQ")) {
            try {
                id = Long.parseLong(code.substring(3));
            } catch (NumberFormatException ignored) {}
        }

        List<RequestForm> entities = repository.searchImportForms(branchId, id, createdAt);
        return mapToRequestFormVM(entities);
    }

    @Override
    public List<RequestFormVM> searchExportForms(Long branchId, String code, java.time.LocalDate createdAt) {
        Long id = null;
        if (code != null && code.startsWith("#RQ")) {
            try {
                id = Long.parseLong(code.substring(3));
            } catch (NumberFormatException ignored) {}
        }

        List<RequestForm> entities = repository.searchExportForms(branchId, id, createdAt);
        return mapToRequestFormVM(entities);
    }

    private List<RequestFormVM> mapToRequestFormVM(List<RequestForm> entities) {
        List<Long> ids = entities.stream().map(RequestForm::getId).toList();
        Map<Long, Long> countMap = ids.isEmpty() ? Map.of() : requestDetailRepository.countDistinctMedicineByRequestIds(ids)
                .stream()
                .collect(Collectors.toMap(row -> ((Number) row[0]).longValue(), row -> ((Number) row[1]).longValue()));

        Map<Long, BigDecimal> totalMap = ids.isEmpty() ? Map.of() : requestDetailRepository.getTotalPriceByRequestIds(ids)
                .stream()
                .collect(Collectors.toMap(
                        row -> ((Number) row[0]).longValue(),
                        row -> row[1] != null ? (BigDecimal) row[1] : BigDecimal.ZERO
                ));

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return entities.stream()
                .map(entity -> new RequestFormVM(
                        "#RQ" + String.format("%03d", entity.getId()),
                        entity.getRequestType() != null ? entity.getRequestType().name() : "N/A",
                        entity.getRequestStatus() != null ? entity.getRequestStatus().name() : "N/A",
                        entity.getNote() != null ? entity.getNote() : "",
                        entity.getCreatedAt() != null ? entity.getCreatedAt().format(fmt) : "",
                        countMap.getOrDefault(entity.getId(), 0L),
                        totalMap.getOrDefault(entity.getId(), BigDecimal.ZERO)
                ))
                .toList();
    }

    // Request List
    @Override
    public List<RequestList> getAllRequestForms() {
        return repository.findAll()
                .stream()
                .map(RequestList::new)
                .toList();
    }

    @Override
    public List<RequestList> getImportRequests() {
        return repository.findByRequestType(RequestType.IMPORT)
                .stream()
                .map(RequestList::new)
                .toList();
    }

    @Override
    public List<RequestList> getReturnRequests() {
        return repository.findByRequestType(RequestType.RETURN)
                .stream()
                .map(RequestList::new)
                .toList();
    }

    @Override
    public RequestList getDetailById(Long id) {
        RequestForm entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("RequestForm not found"));
        return new RequestList(entity);
    }

    @Override
    public List<RequestDetailVM> getDetailsOfRequest(Long requestId) {
        return requestDetailRepository.findByRequestFormId(requestId)
                .stream()
                .map(RequestDetailVM::new)
                .toList();
    }

}
