package vn.edu.fpt.pharma.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.fpt.pharma.base.BaseServiceImpl;
import vn.edu.fpt.pharma.dto.CsvOutputResult;
import vn.edu.fpt.pharma.dto.DataTableRequest;
import vn.edu.fpt.pharma.dto.DataTableResponse;
import vn.edu.fpt.pharma.dto.store.StoreCsvInput;
import vn.edu.fpt.pharma.dto.store.StoreCsvOutput;
import vn.edu.fpt.pharma.entity.Store;
import vn.edu.fpt.pharma.repository.StoreRepository;
import vn.edu.fpt.pharma.service.AuditService;
import vn.edu.fpt.pharma.service.CsvService;
import vn.edu.fpt.pharma.service.StoreService;
import vn.edu.fpt.pharma.util.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class StoreServiceImpl extends BaseServiceImpl<Store, Long, StoreRepository> implements StoreService {

    private final CsvService csvService;

    public StoreServiceImpl(StoreRepository repository, AuditService auditService, CsvService csvService) {
        super(repository, auditService);
        this.csvService = csvService;
    }

    @Override
    public DataTableResponse<Store> findAllStores(DataTableRequest request) {
        DataTableResponse<Store> stores = findAllForDataTable(request, List.of("storeCode", "storeName"));
        return stores.transform(auditService::addAuditInfo);
    }

    @Override
    public CsvOutputResult<StoreCsvInput> importFileCsv(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return new CsvOutputResult<>(List.of(),
                    List.of(new CsvOutputResult.CsvOutputError(0, new String[]{}, "Uploaded CSV file is empty")));
        }

        CsvOutputResult<StoreCsvInput> result =
                csvService.read(StoreCsvInput.class, file, true, importCsvRowProcess);

        List<StoreCsvInput> stores = result.data();
        List<CsvOutputResult.CsvOutputError> errors = new ArrayList<>(result.errors());

        validateNoBlankFields(stores, errors);
        validateNoDuplicateCodesInFile(stores, errors);
        validateNotExistInDatabase(stores, errors);

        if (errors.isEmpty()) {
            // only save when no errors
            repository.saveAll(stores.stream()
                    .map(StoreCsvInput::toEntity)
                    .toList());
        }

        return new CsvOutputResult<>(stores, errors);
    }

    private void validateNoBlankFields(List<StoreCsvInput> stores,
                                       List<CsvOutputResult.CsvOutputError> errors) {
        for (int i = 0; i < stores.size(); i++) {
            StoreCsvInput store = stores.get(i);

            if (StringUtils.isBlank(store.storeCode()) ||
                    StringUtils.isBlank(store.storeName())) {
                errors.add(new CsvOutputResult.CsvOutputError(
                        i + 1,
                        new String[]{store.storeCode(), store.storeName()},
                        "Store code or store name cannot be blank"
                ));
            }
        }
    }

    private void validateNoDuplicateCodesInFile(List<StoreCsvInput> stores,
                                                List<CsvOutputResult.CsvOutputError> errors) {
        Map<String, List<Integer>> codeMap = new HashMap<>();

        for (int i = 0; i < stores.size(); i++) {
            String code = stores.get(i).storeCode();
            codeMap.computeIfAbsent(code, k -> new ArrayList<>()).add(i + 1);
        }

        codeMap.entrySet().stream()
                .filter(e -> e.getValue().size() > 1)
                .forEach(e -> errors.add(new CsvOutputResult.CsvOutputError(
                        e.getValue().getFirst(),
                        new String[]{e.getKey()},
                        "Duplicate store code found in file: " + e.getKey()
                )));
    }

    private void validateNotExistInDatabase(List<StoreCsvInput> stores,
                                            List<CsvOutputResult.CsvOutputError> errors) {
        Set<String> storeCodes = stores.stream()
                .map(StoreCsvInput::storeCode)
                .collect(Collectors.toSet());

        List<String> existingStoreCodes = repository.findAllByStoreCodeIn(storeCodes)
                .stream()
                .map(Store::getStoreCode)
                .toList();

        for (String existing : existingStoreCodes) {
            errors.add(new CsvOutputResult.CsvOutputError(
                    0, new String[]{existing},
                    "Store code already exists in database: " + existing
            ));
        }
    }


    private static final Function<String[], StoreCsvInput> importCsvRowProcess = (line) -> new StoreCsvInput(
            line[0], // storeCode
            line[1], // storeName
            line[2], // address
            line[3], // addressCode
            line[4], // phoneNumber
            line[5].isEmpty() ? null : Double.parseDouble(line[5]), // latitude
            line[6].isEmpty() ? null : Double.parseDouble(line[6])  // longitude
    );

    @Override
    public byte[] exportFileCsv() {
        List<StoreCsvOutput> stores = repository.findAll().stream().map(StoreCsvOutput::new).toList();
        String[] headers = {
                "Id",
                "Store Code",
                "Store Name",
                "Address",
                "Address Code",
                "Phone Number",
                "Latitude",
                "Longitude"
        };
        return csvService.write(stores, headers, exportCsvRowProcess);
    }

    private static final Function<StoreCsvOutput, String[]> exportCsvRowProcess = (store) -> new String[]{
            String.valueOf(store.id()),
            store.storeCode(),
            store.storeName(),
            store.address() != null ? store.address() : "",
            store.addressCode() != null ? store.addressCode() : "",
            store.phoneNumber() != null ? store.phoneNumber() : "",
            store.latitude() != null ? String.valueOf(store.latitude()) : "",
            store.longitude() != null ? String.valueOf(store.longitude()) : ""
    };
}
