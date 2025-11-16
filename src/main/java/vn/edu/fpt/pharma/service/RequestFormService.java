package vn.edu.fpt.pharma.service;

import vn.edu.fpt.pharma.base.BaseService;
import vn.edu.fpt.pharma.dto.requestform.RequestFormVM;
import vn.edu.fpt.pharma.dto.warehouse.RequestList;
import vn.edu.fpt.pharma.entity.RequestForm;

import java.util.List;

public interface RequestFormService extends BaseService<RequestForm, Long> {
    List<RequestFormVM> getRequestFormsByBranch(Long branchId);

}
