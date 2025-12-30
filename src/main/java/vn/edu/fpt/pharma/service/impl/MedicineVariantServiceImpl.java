package vn.edu.fpt.pharma.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.pharma.base.BaseServiceImpl;
import vn.edu.fpt.pharma.dto.medicine.MedicineVariantRequest;
import vn.edu.fpt.pharma.dto.medicine.MedicineVariantResponse;
import vn.edu.fpt.pharma.dto.medicine.SearchMedicineVM;
import vn.edu.fpt.pharma.dto.medicine.UnitConversionVM;
import vn.edu.fpt.pharma.entity.DosageForm;
import vn.edu.fpt.pharma.entity.Medicine;
import vn.edu.fpt.pharma.entity.MedicineVariant;
import vn.edu.fpt.pharma.repository.DosageFormRepository;
import vn.edu.fpt.pharma.entity.Unit;
import vn.edu.fpt.pharma.entity.UnitConversion;
import vn.edu.fpt.pharma.repository.*;
import vn.edu.fpt.pharma.service.AuditService;
import vn.edu.fpt.pharma.service.MedicineVariantService;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MedicineVariantServiceImpl extends BaseServiceImpl<MedicineVariant, Long, MedicineVariantRepository> 
        implements MedicineVariantService {

    private final MedicineRepository medicineRepository;
    private final UnitRepository unitRepository;
    private final MedicineVariantRepository medicineVariantRepository;
    private final InventoryRepository inventoryRepository;
    private final vn.edu.fpt.pharma.repository.PriceRepository priceRepository;
    private final UnitConversionRepository unitConversionRepository;
    private final DosageFormRepository dosageFormRepository;

    public MedicineVariantServiceImpl(MedicineVariantRepository repository, AuditService auditService,
                                      MedicineRepository medicineRepository, UnitRepository unitRepository,
                                      MedicineVariantRepository medicineVariantRepository,
                                      InventoryRepository inventoryRepository,
                                      vn.edu.fpt.pharma.repository.PriceRepository priceRepository,
                                      UnitConversionRepository unitConversionRepository,
                                      DosageFormRepository dosageFormRepository) {
        super(repository, auditService);
        this.medicineRepository = medicineRepository;
        this.unitRepository = unitRepository;
        this.medicineVariantRepository = medicineVariantRepository;
        this.inventoryRepository = inventoryRepository;
        this.priceRepository = priceRepository;
        this.unitConversionRepository = unitConversionRepository;
        this.dosageFormRepository = dosageFormRepository;
    }

    @Override
    @Transactional
    public MedicineVariantResponse createVariant(MedicineVariantRequest request) {
        Medicine medicine = medicineRepository.findById(request.getMedicineId())
                .orElseThrow(() -> new RuntimeException("Medicine not found"));

        // Load DosageForm from repository by ID
        DosageForm dosageForm = dosageFormRepository
                .findById(request.getDosageFormId())
                .orElseThrow(() -> new RuntimeException("Dạng bào chế không tồn tại"));

        // Prevent duplicate variant with same basic data
        long duplicateCount = medicineVariantRepository.countDuplicateVariant(
                request.getMedicineId(),
                dosageForm.getId(),
                request.getDosage(),
                request.getStrength()
        );
        if (duplicateCount > 0) {
            throw new IllegalArgumentException("Biến thể thuốc với thông tin này đã tồn tại");
        }

        MedicineVariant variant = MedicineVariant.builder()
                .medicine(medicine)
                .dosageForm(dosageForm)
                .dosage(request.getDosage())
                .strength(request.getStrength())
                .packaging(request.getPackaging())
                .barcode(request.getBarcode())
                .registrationNumber(request.getRegistrationNumber())
                .storageConditions(request.getStorageConditions())
                .instructions(request.getInstructions())
                .prescription_require(request.getPrescription_require() != null ? request.getPrescription_require() : false)
                .note(request.getNote())
                .build();

        MedicineVariant saved = repository.save(variant);

        // Save unit conversions if provided
        if (request.getUnitConversions() != null && !request.getUnitConversions().isEmpty()) {
            saveUnitConversions(saved, request.getUnitConversions());
        }

        return MedicineVariantResponse.fromEntity(saved);
    }

    @Override
    public List<MedicineVariantResponse> getVariantsByMedicineId(Long medicineId) {
        List<MedicineVariant> variants = repository.findByMedicineId(medicineId);
        return variants.stream()
                .map(MedicineVariantResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public MedicineVariantResponse updateVariant(Long id, MedicineVariantRequest request) {
        MedicineVariant variant = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medicine variant not found"));

        Medicine medicine = request.getMedicineId() != null ?
                medicineRepository.findById(request.getMedicineId())
                        .orElseThrow(() -> new RuntimeException("Medicine not found")) : variant.getMedicine();

        if (request.getDosageFormId() != null) {
            DosageForm dosageForm = dosageFormRepository
                    .findById(request.getDosageFormId())
                    .orElseThrow(() -> new RuntimeException("Dạng bào chế không tồn tại"));
            variant.setDosageForm(dosageForm);
        }
        if (request.getDosage() != null) variant.setDosage(request.getDosage());
        if (request.getStrength() != null) variant.setStrength(request.getStrength());
        if (request.getPackaging() != null) variant.setPackaging(request.getPackaging());
        if (request.getBarcode() != null) variant.setBarcode(request.getBarcode());
        if (request.getRegistrationNumber() != null) variant.setRegistrationNumber(request.getRegistrationNumber());
        if (request.getStorageConditions() != null) variant.setStorageConditions(request.getStorageConditions());
        if (request.getInstructions() != null) variant.setInstructions(request.getInstructions());
        if (request.getPrescription_require() != null) variant.setPrescription_require(request.getPrescription_require());
        if (request.getNote() != null) variant.setNote(request.getNote());

        variant.setMedicine(medicine);

        MedicineVariant updated = repository.save(variant);

        // Update unit conversions if provided
        if (request.getUnitConversions() != null) {
            // Delete existing conversions
            List<UnitConversion> existingConversions = unitConversionRepository.findByVariantIdId(id);
            for (UnitConversion uc : existingConversions) {
                unitConversionRepository.delete(uc);
            }
            // Save new conversions
            saveUnitConversions(updated, request.getUnitConversions());
        }

        return MedicineVariantResponse.fromEntity(updated);
    }

    private void saveUnitConversions(MedicineVariant variant, List<MedicineVariantRequest.UnitConversionDTO> conversions) {
        for (MedicineVariantRequest.UnitConversionDTO dto : conversions) {
            Unit unit = unitRepository.findById(dto.getUnitId())
                    .orElseThrow(() -> new RuntimeException("Unit not found: " + dto.getUnitId()));

            UnitConversion conversion = UnitConversion.builder()
                    .variantId(variant)
                    .unitId(unit)
                    .multiplier(dto.getMultiplier())
                    .isSale(dto.getIsSale() != null ? dto.getIsSale() : false)
                    .build();

            unitConversionRepository.save(conversion);
        }
    }

    @Override
    public List<Map<String, Object>> getUnitConversions(Long variantId) {
        List<UnitConversion> conversions = unitConversionRepository.findByVariantIdId(variantId);

        return conversions.stream()
                .map(conv -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", conv.getId());
                    map.put("unitId", conv.getUnitId());
                    map.put("multiplier", conv.getMultiplier());
                    map.put("isSale", conv.getIsSale());
                    return map;
                })
                .collect(Collectors.toList());
    }

    @Override
    public MedicineVariantResponse getVariantById(Long id) {
        MedicineVariant variant = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medicine variant not found"));
        return MedicineVariantResponse.fromEntity(variant);
    }

    @Override
    public List<SearchMedicineVM> findByKeyword(String keyword) {
        List<Object[]> rows = medicineVariantRepository.findByNameActiveIngredient(keyword);

        return rows.stream()
                .map(r -> new SearchMedicineVM(
                        ((Number) r[0]).longValue(),  // id
                        (String) r[1],  // name
                        (String) r[2],  // active_ingredient
                        (String) r[3],  // manufacturer
                        (String) r[4],  // strength
                        (String) r[5],  // country
                        (String) r[6],  // packaging (changed from quantity_per_package)
                        (String) r[7],  // uses (from medicine)
                        (String) r[8],  // contraindications (from medicine)
                        (String) r[9]   // side_effects (from medicine)
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<vn.edu.fpt.pharma.dto.medicine.VariantInventoryDTO> getVariantsWithInventoryByMedicineId(Long medicineId) {
        List<Object[]> variantRows = medicineVariantRepository.findVariantsByMedicineIdWithDetails(medicineId);

        return variantRows.stream()
                .map(r -> {
                    Long variantId = ((Number) r[0]).longValue();

                    // Get inventory for this variant
                    List<vn.edu.fpt.pharma.dto.medicine.InventoryDetailDTO> inventories =
                        getInventoryByVariantId(variantId);
                    List<UnitConversionVM> units = unitConversionRepository.findByVariantIdId(variantId)
                            .stream()
                            .map(u -> new UnitConversionVM(
                                    u.getUnitId().getId(),
                                    u.getUnitId().getName(),
                                    u.getMultiplier(),
                                    u.getIsSale()
                            ))
                            .collect(Collectors.toList());

                    return new vn.edu.fpt.pharma.dto.medicine.VariantInventoryDTO(
                            variantId,
                            (String) r[1],  // dosageForm
                            (String) r[2],  // dosage
                            (String) r[3],  // strength
                            (String) r[4],  // barcode
                            (String) r[5],  // registrationNumber
                            (String) r[6],  // storageConditions
                            (String) r[10], // instructions
                            r[11] != null && (r[11] instanceof Boolean ? (Boolean) r[11] : ((Number) r[11]).intValue() == 1), // prescriptionRequire
                            (String) r[7],  // indications (from medicine)
                            (String) r[8],  // contraindications (from medicine)
                            (String) r[9],  // sideEffects (from medicine)
                            (String) r[12], // uses (from medicine)
                            (String) r[13], // country
                            (String) r[14], // manufacturer
                            (String) r[15], // note (new field)
                            (String) r[16], // packaging (new field)
                            inventories,
                            units
                    );
                })
                .collect(Collectors.toList());
    }

    private List<vn.edu.fpt.pharma.dto.medicine.InventoryDetailDTO> getInventoryByVariantId(Long variantId) {
        List<Object[]> inventoryRows = inventoryRepository.findInventoryByVariantId(variantId);
        return inventoryRows.stream()
                .map(inv -> {
                    Double salePrice = priceRepository.findCurrentPriceForVariantAndBranch(variantId, java.time.LocalDateTime.now())
                            .map(vn.edu.fpt.pharma.entity.Price::getSalePrice)
                            .orElse(null);

                    return new vn.edu.fpt.pharma.dto.medicine.InventoryDetailDTO(
                            ((Number) inv[0]).longValue(),  // id
                            (String) inv[1],  // batchNumber
                            inv[2] != null ? ((java.sql.Date) inv[2]).toLocalDate() : null,  // expiryDate
                            inv[3] != null ? ((Number) inv[3]).longValue() : 0L,  // quantity
                            inv[4] != null ? ((Number) inv[4]).doubleValue() : 0.0,  // costPrice
                            (String) inv[5],  // supplierName
                            salePrice
                    );
                })
                .collect(Collectors.toList());
    }
}

