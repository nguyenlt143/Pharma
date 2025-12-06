package vn.edu.fpt.pharma.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.pharma.base.BaseServiceImpl;
import vn.edu.fpt.pharma.dto.medicine.MedicineVariantRequest;
import vn.edu.fpt.pharma.dto.medicine.MedicineVariantResponse;
import vn.edu.fpt.pharma.dto.medicine.SearchMedicineVM;
import vn.edu.fpt.pharma.dto.medicine.UnitConversionVM;
import vn.edu.fpt.pharma.entity.Medicine;
import vn.edu.fpt.pharma.entity.MedicineVariant;
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

    public MedicineVariantServiceImpl(MedicineVariantRepository repository, AuditService auditService,
                                      MedicineRepository medicineRepository, UnitRepository unitRepository,
                                      MedicineVariantRepository medicineVariantRepository,
                                      InventoryRepository inventoryRepository,
                                      vn.edu.fpt.pharma.repository.PriceRepository priceRepository,
                                      UnitConversionRepository unitConversionRepository) {
        super(repository, auditService);
        this.medicineRepository = medicineRepository;
        this.unitRepository = unitRepository;
        this.medicineVariantRepository = medicineVariantRepository;
        this.inventoryRepository = inventoryRepository;
        this.priceRepository = priceRepository;
        this.unitConversionRepository = unitConversionRepository;
    }

    @Override
    @Transactional
    public MedicineVariantResponse createVariant(MedicineVariantRequest request) {
        Medicine medicine = medicineRepository.findById(request.getMedicineId())
                .orElseThrow(() -> new RuntimeException("Medicine not found"));

        Unit baseUnit = request.getBaseUnitId() != null ?
                unitRepository.findById(request.getBaseUnitId())
                        .orElseThrow(() -> new RuntimeException("Base unit not found")) : null;

        Unit packageUnit = request.getPackageUnitId() != null ?
                unitRepository.findById(request.getPackageUnitId())
                        .orElseThrow(() -> new RuntimeException("Package unit not found")) : null;

        MedicineVariant variant = MedicineVariant.builder()
                .medicine(medicine)
                .dosage_form(request.getDosageForm())
                .dosage(request.getDosage())
                .strength(request.getStrength())
                .packageUnitId(packageUnit)
                .baseUnitId(baseUnit)
                .quantityPerPackage(request.getQuantityPerPackage())
                .Barcode(request.getBarcode())
                .registrationNumber(request.getRegistrationNumber())
                .storageConditions(request.getStorageConditions())
                .indications(request.getIndications())
                .contraindications(request.getContraindications())
                .sideEffects(request.getSideEffects())
                .instructions(request.getInstructions())
                .prescription_require(request.getPrescription_require() != null ? request.getPrescription_require() : false)
                .uses(request.getUses())
                .build();

        MedicineVariant saved = repository.save(variant);

        // Automatically create unit conversions from baseUnit and packageUnit
        createUnitConversionsFromVariant(saved);

        // Save additional unit conversions if provided
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

        Unit baseUnit = request.getBaseUnitId() != null ?
                unitRepository.findById(request.getBaseUnitId())
                        .orElseThrow(() -> new RuntimeException("Base unit not found")) : variant.getBaseUnitId();

        Unit packageUnit = request.getPackageUnitId() != null ?
                unitRepository.findById(request.getPackageUnitId())
                        .orElseThrow(() -> new RuntimeException("Package unit not found")) : variant.getPackageUnitId();

        if (request.getDosageForm() != null) variant.setDosage_form(request.getDosageForm());
        if (request.getDosage() != null) variant.setDosage(request.getDosage());
        if (request.getStrength() != null) variant.setStrength(request.getStrength());
        if (request.getBarcode() != null) variant.setBarcode(request.getBarcode());
        if (request.getQuantityPerPackage() != null) variant.setQuantityPerPackage(request.getQuantityPerPackage());
        if (request.getRegistrationNumber() != null) variant.setRegistrationNumber(request.getRegistrationNumber());
        if (request.getStorageConditions() != null) variant.setStorageConditions(request.getStorageConditions());
        if (request.getIndications() != null) variant.setIndications(request.getIndications());
        if (request.getContraindications() != null) variant.setContraindications(request.getContraindications());
        if (request.getSideEffects() != null) variant.setSideEffects(request.getSideEffects());
        if (request.getInstructions() != null) variant.setInstructions(request.getInstructions());
        if (request.getUses() != null) variant.setUses(request.getUses());
        if (request.getPrescription_require() != null) variant.setPrescription_require(request.getPrescription_require());
        
        // Check if base unit or package unit changed
        boolean unitsChanged = false;
        if (request.getBaseUnitId() != null && !variant.getBaseUnitId().equals(baseUnit)) {
            unitsChanged = true;
        }
        if (request.getPackageUnitId() != null && !variant.getPackageUnitId().equals(packageUnit)) {
            unitsChanged = true;
        }
        if (request.getQuantityPerPackage() != null &&
            !request.getQuantityPerPackage().equals(variant.getQuantityPerPackage())) {
            unitsChanged = true;
        }

        variant.setMedicine(medicine);
        variant.setBaseUnitId(baseUnit);
        variant.setPackageUnitId(packageUnit);

        MedicineVariant updated = repository.save(variant);

        // If units changed, recreate unit conversions
        if (unitsChanged) {
            // Delete old conversions for base and package units
            List<UnitConversion> existingConversions = unitConversionRepository.findByVariantIdId(id);
            for (UnitConversion uc : existingConversions) {
                if (uc.getUnitId().equals(variant.getBaseUnitId()) ||
                    uc.getUnitId().equals(variant.getPackageUnitId())) {
                    unitConversionRepository.delete(uc);
                }
            }

            // Create new unit conversions from updated units
            createUnitConversionsFromVariant(updated);
        }

        // Update additional unit conversions if provided
        if (request.getUnitConversions() != null) {
            // Delete existing additional conversions (not base/package)
            List<UnitConversion> existingConversions = unitConversionRepository.findByVariantIdId(id);
            for (UnitConversion uc : existingConversions) {
                if (!uc.getUnitId().equals(variant.getBaseUnitId()) &&
                    !uc.getUnitId().equals(variant.getPackageUnitId())) {
                    unitConversionRepository.delete(uc);
                }
            }
            // Save new additional conversions
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
                        ((Number) r[0]).longValue(),
                        (String) r[1],
                        (String) r[2],
                        (String) r[3],
                        (String) r[4],
                        (String) r[5],
                        (String) r[6],
                        (long) ((Number) r[7]).doubleValue(),
                        (String) r[8],
                        (String) r[9],
                        (String) r[10],
                        (String) r[11]
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
                                    u.getMultiplier()
                            ))
                            .collect(Collectors.toList());

                    return new vn.edu.fpt.pharma.dto.medicine.VariantInventoryDTO(
                            variantId,
                            (String) r[1],  // dosageForm
                            (String) r[2],  // dosage
                            (String) r[3],  // strength
                            (String) r[4],  // packageUnitName
                            (String) r[5],  // baseUnitName
                            r[6] != null ? ((Number) r[6]).doubleValue() : null,  // quantityPerPackage
                            (String) r[7],  // barcode
                            (String) r[8],  // registrationNumber
                            (String) r[9],  // storageConditions
                            (String) r[10], // indications
                            (String) r[11], // contraindications
                            (String) r[12], // sideEffects
                            (String) r[13], // instructions
                            r[14] != null && (r[14] instanceof Boolean ? (Boolean) r[14] : ((Number) r[14]).intValue() == 1), // prescriptionRequire
                            (String) r[15], // uses
                            (String) r[16], // country
                            (String) r[17], // manufacturer
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

    @Override
    @Transactional
    public void createUnitConversionsFromVariant(MedicineVariant variant) {
        if (variant == null) {
            throw new IllegalArgumentException("MedicineVariant không được để trống");
        }

        // Get existing conversions for this variant
        List<UnitConversion> existingConversions = unitConversionRepository.findByVariantIdId(variant.getId());

        // Check if baseUnitId and packageUnitId are the same
        boolean isSameUnit = variant.getBaseUnitId() != null && variant.getPackageUnitId() != null &&
                             variant.getBaseUnitId().getId().equals(variant.getPackageUnitId().getId());

        // Create conversion for base unit (multiplier = 1)
        if (variant.getBaseUnitId() != null) {
            // Check if conversion already exists for this variant and base unit
            boolean baseUnitExists = existingConversions.stream()
                    .anyMatch(uc -> uc.getUnitId().equals(variant.getBaseUnitId()));

            if (!baseUnitExists) {
                UnitConversion baseConversion = UnitConversion.builder()
                        .variantId(variant)
                        .unitId(variant.getBaseUnitId())
                        .multiplier(1.0)
                        .build();
                unitConversionRepository.save(baseConversion);
            }
        }

        // Create conversion for package unit only if it's different from base unit
        if (variant.getPackageUnitId() != null && variant.getQuantityPerPackage() != null && !isSameUnit) {
            // Check if conversion already exists for this variant and package unit
            boolean packageUnitExists = existingConversions.stream()
                    .anyMatch(uc -> uc.getUnitId().equals(variant.getPackageUnitId()));

            if (!packageUnitExists) {
                UnitConversion packageConversion = UnitConversion.builder()
                        .variantId(variant)
                        .unitId(variant.getPackageUnitId())
                        .multiplier(variant.getQuantityPerPackage())
                        .build();
                unitConversionRepository.save(packageConversion);
            }
        }
    }

    @Override
    @Transactional
    public void migrateAllVariantsToUnitConversions() {
        // Get all medicine variants
        List<MedicineVariant> allVariants = repository.findAll();

        int totalVariants = allVariants.size();
        int processedCount = 0;
        int createdCount = 0;

        System.out.println("========================================");
        System.out.println("Bắt đầu migrate " + totalVariants + " MedicineVariant sang UnitConversion...");
        System.out.println("========================================");

        for (MedicineVariant variant : allVariants) {
            try {
                // Get existing conversions count before
                int beforeCount = unitConversionRepository.findByVariantIdId(variant.getId()).size();

                // Create unit conversions
                createUnitConversionsFromVariant(variant);

                // Get existing conversions count after
                int afterCount = unitConversionRepository.findByVariantIdId(variant.getId()).size();
                int newConversions = afterCount - beforeCount;

                processedCount++;
                createdCount += newConversions;

                if (newConversions > 0) {
                    System.out.println("✓ Variant ID " + variant.getId() +
                                     " [" + variant.getMedicine().getName() + "]: " +
                                     newConversions + " unit conversion(s) được tạo");
                }
            } catch (Exception e) {
                System.err.println("✗ Lỗi xử lý variant ID " + variant.getId() + ": " + e.getMessage());
            }
        }

        System.out.println("========================================");
        System.out.println("Hoàn thành! Đã xử lý: " + processedCount + "/" + totalVariants + " variants");
        System.out.println("Tổng số unit conversions được tạo: " + createdCount);
        System.out.println("========================================");
    }
}

