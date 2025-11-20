package vn.edu.fpt.pharma.service;

import vn.edu.fpt.pharma.base.BaseService;
import vn.edu.fpt.pharma.dto.requestform.RequestFormVM;
import vn.edu.fpt.pharma.dto.warehouse.RequestDetailVM;
import vn.edu.fpt.pharma.dto.warehouse.RequestList;
import vn.edu.fpt.pharma.entity.RequestForm;
import vn.edu.fpt.pharma.repository.RequestFormRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public interface RequestFormService extends BaseService<RequestForm, Long> {
    List<RequestFormVM> getRequestFormsByBranch(Long branchId);
    List<RequestFormVM> searchRequestForms(Long branchId, String code, LocalDate createdAt);
    List<RequestFormVM> searchImportForms(Long branchId, String code, LocalDate createdAt);
    List<RequestFormVM> searchExportForms(Long branchId, String code, LocalDate createdAt);

//    Request List
    List<RequestList> getAllRequestForms();              // all
    List<RequestList> getImportRequests();               // import
    List<RequestList> getReturnRequests();               // return
    RequestList getDetailById(Long id);                 // Detail
    List<RequestDetailVM> getDetailsOfRequest(Long requestId);
}
