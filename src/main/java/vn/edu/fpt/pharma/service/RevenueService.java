package vn.edu.fpt.pharma.service;

import vn.edu.fpt.pharma.base.BaseService;
import vn.edu.fpt.pharma.dto.DataTableRequest;
import vn.edu.fpt.pharma.dto.DataTableResponse;
import vn.edu.fpt.pharma.dto.reveuce.RevenueDetailVM;
import vn.edu.fpt.pharma.dto.reveuce.RevenueShiftVM;
import vn.edu.fpt.pharma.dto.reveuce.RevenueVM;
import vn.edu.fpt.pharma.entity.Invoice;

public interface RevenueService extends BaseService<Invoice, Long> {
    DataTableResponse<RevenueVM> findAllRevenues(DataTableRequest reqDto, Long userId);

    DataTableResponse<RevenueDetailVM> ViewRevenuesDetail(DataTableRequest reqDto, Long userId, Integer year, Integer month);

    DataTableResponse<RevenueShiftVM> getRevenueShiftSummary(DataTableRequest reqDto, Long userId);

    DataTableResponse<RevenueDetailVM> ViewShiftDetail(DataTableRequest reqDto, Long userId, String shiftName);
}
