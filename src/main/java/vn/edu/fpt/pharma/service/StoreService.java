package vn.edu.fpt.pharma.service;

import org.springframework.web.multipart.MultipartFile;
import vn.edu.fpt.pharma.base.BaseService;
import vn.edu.fpt.pharma.dto.CsvOutputResult;
import vn.edu.fpt.pharma.dto.DataTableRequest;
import vn.edu.fpt.pharma.dto.DataTableResponse;
import vn.edu.fpt.pharma.dto.store.StoreCsvInput;
import vn.edu.fpt.pharma.entity.Store;

public interface StoreService extends BaseService<Store, Long> {
    DataTableResponse<Store> findAllStores(DataTableRequest request);
    byte[] exportFileCsv();
    CsvOutputResult<StoreCsvInput> importFileCsv(MultipartFile file);
}
