package vn.edu.fpt.pharma.service;

import vn.edu.fpt.pharma.base.BaseService;
import vn.edu.fpt.pharma.dto.DataTableRequest;
import vn.edu.fpt.pharma.dto.DataTableResponse;
import vn.edu.fpt.pharma.dto.price.PriceRequest;
import vn.edu.fpt.pharma.dto.price.PriceResponse;
import vn.edu.fpt.pharma.entity.Price;

public interface PriceService extends BaseService<Price, Long> {
    DataTableResponse<PriceResponse> getPrices(DataTableRequest request, Long variantId, Long branchId);
    PriceResponse createOrUpdatePrice(PriceRequest request);
    PriceResponse getPriceById(Long id);
}
