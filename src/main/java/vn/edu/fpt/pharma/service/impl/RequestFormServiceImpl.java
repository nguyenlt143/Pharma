package vn.edu.fpt.pharma.service.impl;

import org.springframework.stereotype.Service;
import vn.edu.fpt.pharma.base.BaseServiceImpl;
import vn.edu.fpt.pharma.constant.RequestType;
import vn.edu.fpt.pharma.dto.requestform.RequestFormVM;
import vn.edu.fpt.pharma.dto.warehouse.RequestDetailVM;
import vn.edu.fpt.pharma.dto.warehouse.RequestList;
import vn.edu.fpt.pharma.entity.RequestForm;
import vn.edu.fpt.pharma.repository.RequestDetailRepository;
import vn.edu.fpt.pharma.repository.RequestFormRepository;
import vn.edu.fpt.pharma.service.AuditService;
import vn.edu.fpt.pharma.service.RequestFormService;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RequestFormServiceImpl extends BaseServiceImpl<RequestForm, Long, RequestFormRepository>
        implements RequestFormService {

    private final RequestFormRepository repository;
    private final AuditService auditService;
    private final RequestDetailRepository requestDetailRepository;
    private final vn.edu.fpt.pharma.repository.InventoryMovementRepository movementRepository;
    private final vn.edu.fpt.pharma.repository.InventoryMovementDetailRepository movementDetailRepository;
    private final vn.edu.fpt.pharma.repository.InventoryRepository inventoryRepository;
    private final vn.edu.fpt.pharma.repository.PriceRepository priceRepository;
    private final vn.edu.fpt.pharma.repository.BranchRepository branchRepository;


    // Constructor gọi explicit BaseServiceImpl
    public RequestFormServiceImpl(
            RequestFormRepository repository,
            AuditService auditService,
            RequestDetailRepository requestDetailRepository,
            vn.edu.fpt.pharma.repository.InventoryMovementRepository movementRepository,
            vn.edu.fpt.pharma.repository.InventoryMovementDetailRepository movementDetailRepository,
            vn.edu.fpt.pharma.repository.InventoryRepository inventoryRepository,
            vn.edu.fpt.pharma.repository.PriceRepository priceRepository,
            vn.edu.fpt.pharma.repository.BranchRepository branchRepository
    ) {
        super(repository, auditService);
        this.repository = repository;
        this.auditService = auditService;
        this.requestDetailRepository = requestDetailRepository;
        this.movementRepository = movementRepository;
        this.movementDetailRepository = movementDetailRepository;
        this.inventoryRepository = inventoryRepository;
        this.priceRepository = priceRepository;
        this.branchRepository = branchRepository;
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
    public List<RequestFormVM> searchRequestForms(Long branchId, String code, java.time.LocalDate createdAt) {
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
    public List<RequestFormVM> searchImportForms(Long branchId, String code, java.time.LocalDate createdAt) {
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
    public List<RequestFormVM> searchExportForms(Long branchId, String code, java.time.LocalDate createdAt) {
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
        System.out.println("DEBUG: Request IDs to aggregate: " + ids);

        final Map<Long, Long> countMap;
        final Map<Long, BigDecimal> totalMap;
        if (ids.isEmpty()) {
            countMap = Map.of();
            totalMap = Map.of();
        } else {
            List<Object[]> agg = requestDetailRepository.getAggregatedStatsByRequestIds(ids);
            System.out.println("DEBUG: Aggregate results count: " + agg.size());

            for (Object[] row : agg) {
                System.out.println("DEBUG: Row - request_id=" + row[0] + ", medicine_types=" + row[1] + ", total_units=" + row[2] + ", total_cost=" + row[3]);
            }

            countMap = agg.stream().collect(Collectors.toMap(row -> ((Number) row[0]).longValue(), row -> ((Number) row[1]).longValue()));
            totalMap = agg.stream().collect(Collectors.toMap(row -> ((Number) row[0]).longValue(), row -> {
                Object totalCostRaw = row[3];
                return totalCostRaw != null ? new BigDecimal(totalCostRaw.toString()) : BigDecimal.ZERO;
            }));

            System.out.println("DEBUG: countMap = " + countMap);
            System.out.println("DEBUG: totalMap = " + totalMap);
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

    // Request List
    @Override
    public List<RequestList> getAllRequestForms() {
        return repository.findAll()
                .stream()
                .map(RequestList::new)
                .toList();
    }

    @Override
    public List<RequestList> getImportRequests() {
        return repository.findByRequestType(RequestType.IMPORT)
                .stream()
                .map(RequestList::new)
                .toList();
    }

    @Override
    public List<RequestList> getReturnRequests() {
        return repository.findByRequestType(RequestType.RETURN)
                .stream()
                .map(RequestList::new)
                .toList();
    }

    @Override
    public RequestList getDetailById(Long id) {
        RequestForm entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("RequestForm not found"));
        return new RequestList(entity);
    }

    @Override
    public List<RequestDetailVM> getDetailsOfRequest(Long requestId) {
        return requestDetailRepository.findByRequestFormId(requestId)
                .stream()
                .map(RequestDetailVM::new)
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
    public List<vn.edu.fpt.pharma.dto.inventory.ReturnRequestVM> getReturnRequestsForBranch(Long branchId) {
        List<RequestForm> forms = repository.findByBranchIdAndRequestType(branchId, vn.edu.fpt.pharma.constant.RequestType.RETURN);
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

        if (!requestForm.getRequestType().equals(vn.edu.fpt.pharma.constant.RequestType.RETURN)) {
            throw new IllegalArgumentException("Request is not a RETURN request");
        }

        if (!requestForm.getRequestStatus().equals(vn.edu.fpt.pharma.constant.RequestStatus.REQUESTED)) {
            throw new IllegalStateException("Request is not in REQUESTED status");
        }

        List<vn.edu.fpt.pharma.entity.RequestDetail> details = requestDetailRepository.findByRequestFormId(requestId);
        if (details.isEmpty()) {
            throw new IllegalArgumentException("Request has no details");
        }

        vn.edu.fpt.pharma.entity.InventoryMovement movement = vn.edu.fpt.pharma.entity.InventoryMovement.builder()
                .movementType(vn.edu.fpt.pharma.constant.MovementType.BR_TO_WARE)
                .sourceBranchId(branchId)
                .destinationBranchId(1L)
                .requestForm(requestForm)
                .movementStatus(vn.edu.fpt.pharma.constant.MovementStatus.RECEIVED)
                .totalMoney(0.0)
                .build();

        movement = movementRepository.save(movement);

        double totalMoney = 0.0;

        for (vn.edu.fpt.pharma.entity.RequestDetail detail : details) {
            List<vn.edu.fpt.pharma.entity.Inventory> branchInventories =
                    inventoryRepository.findByBranchIdAndVariantId(branchId, detail.getVariantId());

            if (branchInventories.isEmpty()) {
                throw new IllegalStateException("No inventory found for variant " + detail.getVariantId());
            }

            Long remainingQty = detail.getQuantity();

            for (vn.edu.fpt.pharma.entity.Inventory branchInv : branchInventories) {
                if (remainingQty <= 0) break;

                Long qtyToReturn = Math.min(remainingQty, branchInv.getQuantity());
                Double price = priceRepository.findBranchPriceByVariantId(detail.getVariantId()).orElse(0.0);

                vn.edu.fpt.pharma.entity.InventoryMovementDetail movementDetail =
                        vn.edu.fpt.pharma.entity.InventoryMovementDetail.builder()
                        .movement(movement)
                        .variant(branchInv.getVariant())
                        .batch(branchInv.getBatch())
                        .quantity(qtyToReturn)
                        .price(price)
                        .snapCost(branchInv.getCostPrice())
                        .build();

                movementDetailRepository.save(movementDetail);
                totalMoney += qtyToReturn * price;

                branchInv.setQuantity(branchInv.getQuantity() - qtyToReturn);
                inventoryRepository.save(branchInv);

                var warehouseInv = inventoryRepository.findByBranchIdAndVariantIdAndBatchId(
                        1L, detail.getVariantId(), branchInv.getBatch().getId()
                );

                if (warehouseInv.isPresent()) {
                    var inv = warehouseInv.get();
                    inv.setQuantity(inv.getQuantity() + qtyToReturn);
                    inventoryRepository.save(inv);
                } else {
                    vn.edu.fpt.pharma.entity.Branch warehouseBranch = branchRepository.findById(1L)
                            .orElseThrow(() -> new IllegalStateException("Warehouse not found"));

                    vn.edu.fpt.pharma.entity.Inventory newInv = vn.edu.fpt.pharma.entity.Inventory.builder()
                            .branch(warehouseBranch)
                            .variant(branchInv.getVariant())
                            .batch(branchInv.getBatch())
                            .quantity(qtyToReturn)
                            .costPrice(branchInv.getCostPrice())
                            .minStock(0L)
                            .build();
                    inventoryRepository.save(newInv);
                }

                remainingQty -= qtyToReturn;
            }

            if (remainingQty > 0) {
                throw new IllegalStateException("Not enough inventory for variant " + detail.getVariantId());
            }
        }

        movement.setTotalMoney(totalMoney);
        movementRepository.save(movement);

        requestForm.setRequestStatus(vn.edu.fpt.pharma.constant.RequestStatus.CONFIRMED);
        repository.save(requestForm);
    }

}
