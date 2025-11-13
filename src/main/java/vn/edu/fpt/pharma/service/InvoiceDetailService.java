package vn.edu.fpt.pharma.service;

import vn.edu.fpt.pharma.base.BaseService;
import vn.edu.fpt.pharma.dto.invoice.MedicineItemVM;
import vn.edu.fpt.pharma.entity.InvoiceDetail;

import java.util.List;

public interface InvoiceDetailService extends BaseService<InvoiceDetail, Long> {
    List<MedicineItemVM> getListMedicine(Long invoiceId);
}
