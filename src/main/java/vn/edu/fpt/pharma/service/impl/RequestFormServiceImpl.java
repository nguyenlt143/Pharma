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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return repository.findAllByBranchId(branchId).stream().map(entity ->
                new RequestFormVM(
                        "#RQ" + String.format("%03d", entity.getId()),
                        entity.getRequestType() != null ? entity.getRequestType().name() : "N/A",
                        entity.getRequestStatus() != null ? entity.getRequestStatus().name() : "N/A",
                        entity.getNote() != null ? entity.getNote() : "",
                        entity.getCreatedAt() != null ? entity.getCreatedAt().format(fmt) : ""
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

        // Gọi repository với id
        List<RequestForm> entities = repository.searchRequestForms(branchId, id, createdAt);

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return entities.stream()
                .map(entity -> new RequestFormVM(
                        "#RQ" + String.format("%03d", entity.getId()),
                        entity.getRequestType() != null ? entity.getRequestType().name() : "N/A",
                        entity.getRequestStatus() != null ? entity.getRequestStatus().name() : "N/A",
                        entity.getNote() != null ? entity.getNote() : "",
                        entity.getCreatedAt() != null ? entity.getCreatedAt().format(fmt) : ""
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
