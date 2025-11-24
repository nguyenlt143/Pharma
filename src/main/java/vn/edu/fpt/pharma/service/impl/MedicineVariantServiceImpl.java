package vn.edu.fpt.pharma.service.impl;

import org.springframework.stereotype.Service;
import vn.edu.fpt.pharma.base.BaseServiceImpl;
import vn.edu.fpt.pharma.dto.medicine.MedicineVariantRequest;
import vn.edu.fpt.pharma.dto.medicine.MedicineVariantResponse;
import vn.edu.fpt.pharma.dto.medicine.SearchMedicineVM;
import vn.edu.fpt.pharma.entity.Medicine;
import vn.edu.fpt.pharma.entity.MedicineVariant;
import vn.edu.fpt.pharma.entity.Unit;
import vn.edu.fpt.pharma.repository.InventoryRepository;
import vn.edu.fpt.pharma.repository.MedicineRepository;
import vn.edu.fpt.pharma.repository.MedicineVariantRepository;
import vn.edu.fpt.pharma.repository.UnitRepository;
import vn.edu.fpt.pharma.service.AuditService;
import vn.edu.fpt.pharma.service.MedicineVariantService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MedicineVariantServiceImpl extends BaseServiceImpl<MedicineVariant, Long, MedicineVariantRepository> 
        implements MedicineVariantService {

    private final MedicineRepository medicineRepository;
    private final UnitRepository unitRepository;
    private final MedicineVariantRepository medicineVariantRepository;
    private final InventoryRepository inventoryRepository;
    private final vn.edu.fpt.pharma.repository.PriceRepository priceRepository;

    public MedicineVariantServiceImpl(MedicineVariantRepository repository, AuditService auditService,
                                      MedicineRepository medicineRepository, UnitRepository unitRepository,
                                      MedicineVariantRepository medicineVariantRepository,
                                      InventoryRepository inventoryRepository,
                                      vn.edu.fpt.pharma.repository.PriceRepository priceRepository) {
        super(repository, auditService);
        this.medicineRepository = medicineRepository;
        this.unitRepository = unitRepository;
        this.medicineVariantRepository = medicineVariantRepository;
        this.inventoryRepository = inventoryRepository;
        this.priceRepository = priceRepository;
    }

    @Override
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
        
        variant.setMedicine(medicine);
        variant.setBaseUnitId(baseUnit);
        variant.setPackageUnitId(packageUnit);

        MedicineVariant updated = repository.save(variant);
        return MedicineVariantResponse.fromEntity(updated);
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
                            inventories
                    );
                })
                .collect(Collectors.toList());
    }

    private List<vn.edu.fpt.pharma.dto.medicine.InventoryDetailDTO> getInventoryByVariantId(Long variantId) {
        List<Object[]> inventoryRows = inventoryRepository.findInventoryByVariantId(variantId);
        // Assuming branchId is 1 for now, you need to get the current user's branch
        Long branchId = 1L;

        return inventoryRows.stream()
                .map(inv -> {
                    Double salePrice = priceRepository.findCurrentPriceForVariantAndBranch(variantId, branchId, java.time.LocalDateTime.now())
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

