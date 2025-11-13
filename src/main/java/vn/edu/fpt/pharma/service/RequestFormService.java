package vn.edu.fpt.pharma.service;

import vn.edu.fpt.pharma.base.BaseService;
import vn.edu.fpt.pharma.dto.requestform.RequestFormVM;
import vn.edu.fpt.pharma.entity.RequestForm;
import vn.edu.fpt.pharma.repository.RequestFormRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public interface RequestFormService extends BaseService<RequestForm, Long> {
    List<RequestFormVM> getRequestFormsByBranch(Long branchId);
    List<RequestFormVM> searchRequestForms(Long branchId, String code, LocalDate createdAt);

}
