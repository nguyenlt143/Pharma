package vn.edu.fpt.pharma.service;

import vn.edu.fpt.pharma.base.BaseService;
import vn.edu.fpt.pharma.dto.reveuce.RevenueVM;
import vn.edu.fpt.pharma.entity.Invoice;
import java.util.List;

public interface RevenueService extends BaseService<Invoice, Long> {

    List<RevenueVM> getRevenueSummary();

    List<RevenueVM> getRevenueShiftSummary();
}
