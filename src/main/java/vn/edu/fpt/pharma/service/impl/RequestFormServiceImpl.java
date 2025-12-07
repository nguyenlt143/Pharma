package vn.edu.fpt.pharma.service.impl;

import org.springframework.stereotype.Service;
import vn.edu.fpt.pharma.base.BaseServiceImpl;
import vn.edu.fpt.pharma.constant.BranchType;
import vn.edu.fpt.pharma.constant.RequestType;
import vn.edu.fpt.pharma.dto.requestform.RequestFormVM;
import vn.edu.fpt.pharma.dto.warehouse.ExportCreateDTO;
import vn.edu.fpt.pharma.dto.warehouse.RequestDetailVM;
import vn.edu.fpt.pharma.dto.warehouse.RequestList;
import vn.edu.fpt.pharma.entity.*;
import vn.edu.fpt.pharma.repository.*;
import vn.edu.fpt.pharma.service.AuditService;
import vn.edu.fpt.pharma.service.RequestFormService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Comparator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RequestFormServiceImpl extends BaseServiceImpl<RequestForm, Long, RequestFormRepository>
        implements RequestFormService {

    private final RequestFormRepository repository;
    private final RequestDetailRepository requestDetailRepository;
    private final BranchRepository branchRepository;
    private final InventoryRepository inventoryRepository;
    private final MedicineVariantRepository medicineVariantRepository;
    private final PriceRepository priceRepository;

    public RequestFormServiceImpl(RequestFormRepository repository, AuditService auditService,
                                   RequestDetailRepository requestDetailRepository,
                                   BranchRepository branchRepository,
                                   InventoryRepository inventoryRepository,
                                   MedicineVariantRepository medicineVariantRepository,
                                   PriceRepository priceRepository) {
        super(repository, auditService);
        this.repository = repository;
        this.requestDetailRepository = requestDetailRepository;
        this.branchRepository = branchRepository;
        this.inventoryRepository = inventoryRepository;
        this.medicineVariantRepository = medicineVariantRepository;
        this.priceRepository = priceRepository;
    }

    @Override
    public List<RequestFormVM> getRequestFormsByBranch(Long branchId) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        List<RequestForm> forms = repository.findAllByBranchId(branchId);
        List<Long> ids = forms.stream().map(RequestForm::getId).toList();
        final Map<Long, Long> countMap;
        final Map<Long, BigDecimal> totalMap;
        if (ids.isEmpty()) {
            countMap = Map.of();
            totalMap = Map.of();
        } else {
            List<Object[]> agg = requestDetailRepository.getAggregatedStatsByRequestIds(ids);
            countMap = agg.stream().collect(Collectors.toMap(r -> ((Number) r[0]).longValue(), r -> ((Number) r[1]).longValue()));
            totalMap = agg.stream().collect(Collectors.toMap(r -> ((Number) r[0]).longValue(), r -> {
                Object raw = r[3];
                return raw != null ? new BigDecimal(raw.toString()) : BigDecimal.ZERO;
            }));
        }
        return forms.stream().map(entity ->
                new RequestFormVM(
                        "#RQ" + String.format("%03d", entity.getId()),
                        entity.getRequestType() != null ? entity.getRequestType().name() : "N/A",
                        entity.getRequestStatus() != null ? entity.getRequestStatus().name() : "N/A",
                        entity.getNote() != null ? entity.getNote() : "",
                        entity.getCreatedAt() != null ? entity.getCreatedAt().format(fmt) : "",
                        countMap.getOrDefault(entity.getId(), 0L),
                        totalMap.getOrDefault(entity.getId(), BigDecimal.ZERO)
                )
        ).toList();
    }

    @Override
    public List<RequestFormVM> searchRequestForms(Long branchId, String code, LocalDate createdAt) {
        Long id = null;
        if (code != null && code.startsWith("#RQ")) {
            try {
                id = Long.parseLong(code.substring(3));
            } catch (NumberFormatException ignored) {}
        }

        List<RequestForm> entities = repository.searchImportForms(branchId, id, createdAt);
        return mapToRequestFormVM(entities);
    }

    @Override
    public List<RequestFormVM> searchImportForms(Long branchId, String code, LocalDate createdAt) {
        Long id = null;
        if (code != null && code.startsWith("#RQ")) {
            try {
                id = Long.parseLong(code.substring(3));
            } catch (NumberFormatException ignored) {}
        }

        List<RequestForm> entities = repository.searchImportForms(branchId, id, createdAt);
        return mapToRequestFormVM(entities);
    }

    @Override
    public List<RequestFormVM> searchExportForms(Long branchId, String code, LocalDate createdAt) {
        Long id = null;
        if (code != null && code.startsWith("#RQ")) {
            try {
                id = Long.parseLong(code.substring(3));
            } catch (NumberFormatException ignored) {}
        }

        List<RequestForm> entities = repository.searchExportForms(branchId, id, createdAt);
        return mapToRequestFormVM(entities);
    }

    private List<RequestFormVM> mapToRequestFormVM(List<RequestForm> entities) {
        List<Long> ids = entities.stream().map(RequestForm::getId).toList();
        log.debug("Aggregate Request IDs: {}", ids);
        final Map<Long, Long> countMap;
        final Map<Long, BigDecimal> totalMap;
        if (ids.isEmpty()) {
            countMap = Map.of();
            totalMap = Map.of();
        } else {
            List<Object[]> agg = requestDetailRepository.getAggregatedStatsByRequestIds(ids);
            log.debug("Aggregate rows: {}", agg.size());
            for (Object[] row : agg) {
                log.debug("Row request_id={}, medicine_types={}, total_units={}, total_cost={}", row[0], row[1], row[2], row[3]);
            }
            countMap = agg.stream().collect(Collectors.toMap(row -> ((Number) row[0]).longValue(), row -> ((Number) row[1]).longValue()));
            totalMap = agg.stream().collect(Collectors.toMap(row -> ((Number) row[0]).longValue(), row -> {
                Object totalCostRaw = row[3];
                return totalCostRaw != null ? new BigDecimal(totalCostRaw.toString()) : BigDecimal.ZERO;
            }));
            log.debug("countMap={}, totalMap={}", countMap, totalMap);
        }
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return entities.stream()
                .map(entity -> new RequestFormVM(
                        "#RQ" + String.format("%03d", entity.getId()),
                        entity.getRequestType() != null ? entity.getRequestType().name() : "N/A",
                        entity.getRequestStatus() != null ? entity.getRequestStatus().name() : "N/A",
                        entity.getNote() != null ? entity.getNote() : "",
                        entity.getCreatedAt() != null ? entity.getCreatedAt().format(fmt) : "",
                        countMap.getOrDefault(entity.getId(), 0L),
                        totalMap.getOrDefault(entity.getId(), BigDecimal.ZERO)
                ))
                .toList();
    }

    @Override
    public List<RequestList> getAllRequestForms() {
        return repository.findAll()
                .stream()
                .sorted(Comparator.comparing(RequestForm::getCreatedAt,
                        Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .map(entity -> {
                    String branchName = getBranchNameById(entity.getBranchId());
                    return new RequestList(entity, branchName);
                })
                .toList();
    }

    @Override
    public List<RequestList> getImportRequests() {
        return repository.findByRequestType(RequestType.IMPORT)
                .stream()
                .sorted(Comparator.comparing(RequestForm::getCreatedAt,
                        Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .map(entity -> {
                    String branchName = getBranchNameById(entity.getBranchId());
                    return new RequestList(entity, branchName);
                })
                .toList();
    }

    @Override
    public List<RequestList> getReturnRequests() {
        return repository.findByRequestType(RequestType.RETURN)
                .stream()
                .sorted(Comparator.comparing(RequestForm::getCreatedAt,
                        Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .map(entity -> {
                    String branchName = getBranchNameById(entity.getBranchId());
                    return new RequestList(entity, branchName);
                })
                .toList();
    }

    @Override
    public RequestList getDetailById(Long id) {
        RequestForm entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("RequestForm not found"));
        String branchName = getBranchNameById(entity.getBranchId());
        return new RequestList(entity, branchName);
    }

    @Override
    public List<RequestDetailVM> getDetailsOfRequest(Long requestId) {
        // Determine warehouse branch id (HEAD_QUARTER)
        Long warehouseBranchId = branchRepository.findAll().stream()
                .filter(b -> b.getBranchType() == BranchType.HEAD_QUARTER)
                .findFirst()
                .map(Branch::getId)
                .orElse(1L);

        return requestDetailRepository.findByRequestFormId(requestId)
                .stream()
                .map(detail -> {
                    String medicineName = "N/A";
                    String activeIngredient = "-";
                    String strength = "N/A";
                    String dosageForm = "N/A";
                    String unit = "N/A";
                    String categoryName = "N/A";
                    Long batchCount = 0L;

                    if (detail.getVariantId() != null) {
                        Optional<MedicineVariant> variantOpt = medicineVariantRepository.findById(detail.getVariantId());
                        if (variantOpt.isPresent()) {
                            MedicineVariant variant = variantOpt.get();
                            Medicine medicine = variant.getMedicine();

                            if (medicine != null) {
                                medicineName = medicine.getName() != null ? medicine.getName() : "N/A";
                                if (medicine.getActiveIngredient() != null && !medicine.getActiveIngredient().isBlank()) {
                                    activeIngredient = medicine.getActiveIngredient();
                                }
                                if (medicine.getCategory() != null && medicine.getCategory().getName() != null) {
                                    categoryName = medicine.getCategory().getName();
                                }
                            }

                            strength = variant.getStrength() != null ? variant.getStrength() : "N/A";
                            dosageForm = variant.getDosage_form() != null ? variant.getDosage_form() : "N/A";

                            if (variant.getBaseUnitId() != null) {
                                unit = variant.getBaseUnitId().getName() != null ? variant.getBaseUnitId().getName() : "N/A";
                            }

                            // Count distinct batches available for this variant at warehouse
                            batchCount = inventoryRepository.findAll().stream()
                                    .filter(inv -> inv.getVariant() != null
                                            && inv.getVariant().getId().equals(variant.getId())
                                            && inv.getBranch() != null
                                            && inv.getBranch().getId().equals(warehouseBranchId))
                                    .map(inv -> inv.getBatch() != null ? inv.getBatch().getId() : null)
                                    .filter(java.util.Objects::nonNull)
                                    .distinct()
                                    .count();
                        }
                    }

                    return new RequestDetailVM(detail, medicineName, activeIngredient, strength, dosageForm, unit, categoryName, batchCount);
                })
                .toList();
    }

    @Override
    public String createImportRequest(Long branchId, vn.edu.fpt.pharma.dto.inventory.ImportRequestDTO request) {
        // Create RequestForm
        RequestForm requestForm = RequestForm.builder()
                .branchId(branchId)
                .requestType(vn.edu.fpt.pharma.constant.RequestType.IMPORT)
                .requestStatus(vn.edu.fpt.pharma.constant.RequestStatus.REQUESTED)
                .note(request.getNote())
                .build();

        RequestForm savedForm = repository.save(requestForm);

        // Create RequestDetails
        for (vn.edu.fpt.pharma.dto.inventory.ImportRequestDTO.ImportItemDTO item : request.getItems()) {
            if (item.getVariantId() == null || item.getQuantity() == null || item.getQuantity() <= 0) {
                log.warn("Skipping invalid item: variantId={}, quantity={}", item.getVariantId(), item.getQuantity());
                continue;
            }
            vn.edu.fpt.pharma.entity.RequestDetail detail = vn.edu.fpt.pharma.entity.RequestDetail.builder()
                    .requestForm(savedForm)
                    .variantId(item.getVariantId())
                    .quantity(item.getQuantity().longValue())
                    .build();
            requestDetailRepository.save(detail);
        }

        return "#RQ" + String.format("%03d", savedForm.getId());
    }


    @Override
    public ExportCreateDTO prepareExportCreation(Long requestId) {
        // Get request form
        RequestForm requestForm = repository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found with ID: " + requestId));

        // Get branch info
        Branch branch = branchRepository.findById(requestForm.getBranchId())
                .orElseThrow(() -> new RuntimeException("Branch not found with ID: " + requestForm.getBranchId()));

        // Get request details
        List<vn.edu.fpt.pharma.entity.RequestDetail> requestDetails =
                requestDetailRepository.findByRequestFormId(requestId);

        // Get warehouse branch (HEAD_QUARTER acts as warehouse)
        Long warehouseBranchId = branchRepository.findAll().stream()
                .filter(b -> b.getBranchType() == BranchType.HEAD_QUARTER)
                .findFirst()
                .map(Branch::getId)
                .orElse(1L);

        // Build medicine list with batches
        List<ExportCreateDTO.MedicineWithBatches> medicines = new ArrayList<>();

        for (vn.edu.fpt.pharma.entity.RequestDetail detail : requestDetails) {
            // Get variant info
            MedicineVariant variant = medicineVariantRepository.findById(detail.getVariantId())
                    .orElseThrow(() -> new RuntimeException("Variant not found: " + detail.getVariantId()));

            // Get available inventory in warehouse for this variant
            List<Inventory> inventoryList = inventoryRepository.findAll().stream()
                    .filter(inv -> inv.getVariant() != null &&
                                   inv.getVariant().getId().equals(detail.getVariantId()) &&
                                   inv.getBranch() != null &&
                                   inv.getBranch().getId().equals(warehouseBranchId) &&
                                   inv.getQuantity() > 0)
                    .sorted(Comparator.comparing(inv -> inv.getBatch().getExpiryDate()))
                    .toList();

            // Build batch info list
            List<ExportCreateDTO.BatchInfo> batches = new ArrayList<>();
            for (Inventory inventory : inventoryList) {
                Batch batch = inventory.getBatch();

                // Get branch price for this variant
                Double branchPrice = getBranchPriceForVariant(detail.getVariantId(), branch.getId());

                ExportCreateDTO.BatchInfo batchInfo = ExportCreateDTO.BatchInfo.builder()
                        .inventoryId(inventory.getId())
                        .batchId(batch.getId())
                        .batchCode(batch.getBatchCode())
                        .availableQuantity(inventory.getQuantity())
                        .branchPrice(branchPrice)
                        .quantityToSend(0L)
                        .build();

                batches.add(batchInfo);
            }

            // Build medicine with batches
            ExportCreateDTO.MedicineWithBatches medicine = ExportCreateDTO.MedicineWithBatches.builder()
                    .variantId(variant.getId())
                    .medicineName(variant.getMedicine() != null ? variant.getMedicine().getName() : "N/A")
                    .unit(variant.getPackageUnitId() != null ? variant.getPackageUnitId().getName() : "")
                    .concentration(variant.getStrength() != null ? variant.getStrength() : "")
                    .requestedQuantity(detail.getQuantity())
                    .batches(batches)
                    .build();

            medicines.add(medicine);
        }

        return ExportCreateDTO.builder()
                .requestId(requestId)
                .branchId(branch.getId())
                .branchName(branch.getName())
                .createdDate(LocalDate.now())
                .note(requestForm.getNote())
                .medicines(medicines)
                .build();
    }

    @Override
    public List<vn.edu.fpt.pharma.dto.inventory.ReturnRequestVM> getReturnRequestsForBranch(Long branchId) {
        List<RequestForm> forms = repository.findByBranchIdAndRequestType(branchId, RequestType.RETURN);
        List<Long> ids = forms.stream().map(RequestForm::getId).toList();

        final Map<Long, Long> countMap;
        if (ids.isEmpty()) {
            countMap = Map.of();
        } else {
            List<Object[]> countResults = requestDetailRepository.countDistinctMedicineByRequestIds(ids);
            countMap = countResults.stream()
                    .collect(Collectors.toMap(
                            row -> ((Number) row[0]).longValue(),
                            row -> ((Number) row[1]).longValue()
                    ));
        }

        return forms.stream()
                .sorted((f1, f2) -> {
                    if (f1.getCreatedAt() == null) return 1;
                    if (f2.getCreatedAt() == null) return -1;
                    return f2.getCreatedAt().compareTo(f1.getCreatedAt()); // mới nhất trước
                })
                .map(form -> vn.edu.fpt.pharma.dto.inventory.ReturnRequestVM.from(
                        form,
                        countMap.getOrDefault(form.getId(), 0L)
                ))
                .toList();
    }

    @Override
    public vn.edu.fpt.pharma.dto.inventory.ReturnRequestVM getReturnRequestDetail(Long id) {
        RequestForm form = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request form not found"));

        Long count = requestDetailRepository.countDistinctMedicineByRequestIds(List.of(id))
                .stream()
                .findFirst()
                .map(row -> ((Number) row[1]).longValue())
                .orElse(0L);

        return vn.edu.fpt.pharma.dto.inventory.ReturnRequestVM.from(form, count);
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public void confirmReturnRequest(Long requestId, Long branchId) {
        RequestForm requestForm = repository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request form not found"));

        if (!requestForm.getBranchId().equals(branchId)) {
            throw new IllegalArgumentException("Request does not belong to this branch");
        }

        if (!requestForm.getRequestType().equals(RequestType.RETURN)) {
            throw new IllegalArgumentException("Request is not a RETURN request");
        }

        if (!requestForm.getRequestStatus().equals(vn.edu.fpt.pharma.constant.RequestStatus.REQUESTED)) {
            throw new IllegalStateException("Request is not in REQUESTED status");
        }

        List<vn.edu.fpt.pharma.entity.RequestDetail> details = requestDetailRepository.findByRequestFormId(requestId);
        if (details.isEmpty()) {
            throw new IllegalArgumentException("Request has no details");
        }

        // Update request status to CONFIRMED
        requestForm.setRequestStatus(vn.edu.fpt.pharma.constant.RequestStatus.CONFIRMED);
        repository.save(requestForm);
    }

    @Override
    public String createReturnRequest(Long branchId, vn.edu.fpt.pharma.dto.inventory.ReturnRequestDTO request) {
        System.out.println("DEBUG createReturnRequest: note from DTO = " + request.getNote());
        
        // Create RequestForm for RETURN
        RequestForm requestForm = RequestForm.builder()
                .branchId(branchId)
                .requestType(vn.edu.fpt.pharma.constant.RequestType.RETURN)
                .requestStatus(vn.edu.fpt.pharma.constant.RequestStatus.REQUESTED)
                .note(request.getNote())
                .build();

        RequestForm savedForm = repository.save(requestForm);
        System.out.println("DEBUG createReturnRequest: saved form ID = " + savedForm.getId() + ", note = " + savedForm.getNote());

        // Create RequestDetails
        for (vn.edu.fpt.pharma.dto.inventory.ReturnRequestDTO.ReturnItemDTO item : request.getItems()) {
            vn.edu.fpt.pharma.entity.RequestDetail detail = vn.edu.fpt.pharma.entity.RequestDetail.builder()
                    .requestForm(savedForm)
                    .variantId(item.getVariantId())
                    .quantity(item.getQuantity().longValue())
                    .build();
            requestDetailRepository.save(detail);
        }

        // Set requestId back to DTO for movement linking
        request.setRequestId(savedForm.getId());

        return "#RQ" + String.format("%03d", savedForm.getId());
    }

    private String getBranchNameById(Long branchId) {
        if (branchId == null) {
            return "N/A";
        }
        return branchRepository.findById(branchId)
                .map(Branch::getName)
                .orElse("N/A");
    }

    private Double getBranchPriceForVariant(Long variantId, Long branchId) {
        // Try to find price for specific branch first
        Optional<Price> branchSpecificPrice = priceRepository.findAll().stream()
                .filter(p -> p.getVariantId().equals(variantId) &&
                           p.getBranchId() != null &&
                           p.getBranchId().equals(branchId) &&
                           (p.getStartDate() == null || p.getStartDate().isBefore(java.time.LocalDateTime.now())) &&
                           (p.getEndDate() == null || p.getEndDate().isAfter(java.time.LocalDateTime.now())))
                .findFirst();

        if (branchSpecificPrice.isPresent() && branchSpecificPrice.get().getBranchPrice() != null) {
            return branchSpecificPrice.get().getBranchPrice();
        }

        // Try global price
        Optional<Price> globalPrice = priceRepository.findAll().stream()
                .filter(p -> p.getVariantId().equals(variantId) &&
                           p.getBranchId() == null &&
                           (p.getStartDate() == null || p.getStartDate().isBefore(java.time.LocalDateTime.now())) &&
                           (p.getEndDate() == null || p.getEndDate().isAfter(java.time.LocalDateTime.now())))
                .findFirst();

        if (globalPrice.isPresent() && globalPrice.get().getBranchPrice() != null) {
            return globalPrice.get().getBranchPrice();
        }

        // Default to 0.0 if no price found
        return 0.0;
    }

    @Override
    public void confirmRequest(Long requestId) {
        RequestForm requestForm = repository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found with ID: " + requestId));

        if (requestForm.getRequestStatus() != vn.edu.fpt.pharma.constant.RequestStatus.REQUESTED) {
            throw new RuntimeException("Only requests with REQUESTED status can be confirmed");
        }

        requestForm.setRequestStatus(vn.edu.fpt.pharma.constant.RequestStatus.CONFIRMED);
        repository.save(requestForm);
        log.info("Request {} has been confirmed", requestId);
    }

    @Override
    public void cancelRequest(Long requestId) {
        RequestForm requestForm = repository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found with ID: " + requestId));

        if (requestForm.getRequestStatus() != vn.edu.fpt.pharma.constant.RequestStatus.REQUESTED) {
            throw new RuntimeException("Only requests with REQUESTED status can be cancelled");
        }

        requestForm.setRequestStatus(vn.edu.fpt.pharma.constant.RequestStatus.CANCELLED);
        repository.save(requestForm);
        log.info("Request {} has been cancelled", requestId);
    }
}
