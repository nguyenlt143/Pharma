package vn.edu.fpt.pharma.service.impl;

import org.springframework.stereotype.Service;
import vn.edu.fpt.pharma.base.BaseServiceImpl;
import vn.edu.fpt.pharma.dto.medicine.MedicineVariantRequest;
import vn.edu.fpt.pharma.dto.medicine.MedicineVariantResponse;
import vn.edu.fpt.pharma.dto.medicine.SearchMedicineVM;
import vn.edu.fpt.pharma.entity.Medicine;
import vn.edu.fpt.pharma.entity.MedicineVariant;
import vn.edu.fpt.pharma.entity.Unit;
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

    public MedicineVariantServiceImpl(MedicineVariantRepository repository, AuditService auditService,
                                      MedicineRepository medicineRepository, UnitRepository unitRepository, MedicineVariantRepository medicineVariantRepository) {
        super(repository, auditService);
        this.medicineRepository = medicineRepository;
        this.unitRepository = unitRepository;
        this.medicineVariantRepository = medicineVariantRepository;
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
}

