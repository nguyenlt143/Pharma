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
        final Map<Long, Long> countMap;
        final Map<Long, BigDecimal> totalMap;
        if (ids.isEmpty()) {
            countMap = Map.of();
            totalMap = Map.of();
        } else {
            List<Object[]> agg = requestDetailRepository.getAggregatedStatsByRequestIds(ids);
            countMap = agg.stream().collect(Collectors.toMap(r -> ((Number) r[0]).longValue(), r -> ((Number) r[1]).longValue()));
            totalMap = agg.stream().collect(Collectors.toMap(r -> ((Number) r[0]).longValue(), r -> {
                Object raw = r[3];
                return raw != null ? new BigDecimal(raw.toString()) : BigDecimal.ZERO;
            }));
        }
        return forms.stream().map(entity ->
                new RequestFormVM(
                        "#RQ" + String.format("%03d", entity.getId()),
                        entity.getRequestType() != null ? entity.getRequestType().name() : "N/A",
                        entity.getRequestStatus() != null ? entity.getRequestStatus().name() : "N/A",
                        entity.getNote() != null ? entity.getNote() : "",
                        entity.getCreatedAt() != null ? entity.getCreatedAt().format(fmt) : "",
                        countMap.getOrDefault(entity.getId(), 0L),
                        totalMap.getOrDefault(entity.getId(), BigDecimal.ZERO)
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
        System.out.println("DEBUG: Request IDs to aggregate: " + ids);

        final Map<Long, Long> countMap;
        final Map<Long, BigDecimal> totalMap;
        if (ids.isEmpty()) {
            countMap = Map.of();
            totalMap = Map.of();
        } else {
            List<Object[]> agg = requestDetailRepository.getAggregatedStatsByRequestIds(ids);
            System.out.println("DEBUG: Aggregate results count: " + agg.size());

            for (Object[] row : agg) {
                System.out.println("DEBUG: Row - request_id=" + row[0] + ", medicine_types=" + row[1] + ", total_units=" + row[2] + ", total_cost=" + row[3]);
            }

            countMap = agg.stream().collect(Collectors.toMap(row -> ((Number) row[0]).longValue(), row -> ((Number) row[1]).longValue()));
            totalMap = agg.stream().collect(Collectors.toMap(row -> ((Number) row[0]).longValue(), row -> {
                Object totalCostRaw = row[3];
                return totalCostRaw != null ? new BigDecimal(totalCostRaw.toString()) : BigDecimal.ZERO;
            }));

            System.out.println("DEBUG: countMap = " + countMap);
            System.out.println("DEBUG: totalMap = " + totalMap);
        }
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

    @Override
    public String createImportRequest(Long branchId, vn.edu.fpt.pharma.dto.inventory.ImportRequestDTO request) {
        // Create RequestForm
        RequestForm requestForm = RequestForm.builder()
                .branchId(branchId)
                .requestType(vn.edu.fpt.pharma.constant.RequestType.IMPORT)
                .requestStatus(vn.edu.fpt.pharma.constant.RequestStatus.REQUESTED)
                .note(request.getNote())
                .build();

        RequestForm savedForm = repository.save(requestForm);

        // Create RequestDetails
        for (vn.edu.fpt.pharma.dto.inventory.ImportRequestDTO.ImportItemDTO item : request.getItems()) {
            vn.edu.fpt.pharma.entity.RequestDetail detail = vn.edu.fpt.pharma.entity.RequestDetail.builder()
                    .requestForm(savedForm)
                    .variantId(item.getVariantId())
                    .quantity(item.getQuantity().longValue())
                    .build();
            requestDetailRepository.save(detail);
        }

        return "#RQ" + String.format("%03d", savedForm.getId());
    }

}
