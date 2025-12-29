package vn.edu.fpt.pharma.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.edu.fpt.pharma.base.BaseServiceImpl;
import vn.edu.fpt.pharma.dto.DataTableRequest;
import vn.edu.fpt.pharma.dto.DataTableResponse;
import vn.edu.fpt.pharma.dto.unit.UnitRequest;
import vn.edu.fpt.pharma.dto.unit.UnitResponse;
import vn.edu.fpt.pharma.entity.DosageForm;
import vn.edu.fpt.pharma.entity.Unit;
import vn.edu.fpt.pharma.exception.EntityInUseException;
import vn.edu.fpt.pharma.repository.DosageFormRepository;
import vn.edu.fpt.pharma.repository.UnitRepository;
import vn.edu.fpt.pharma.service.AuditService;
import vn.edu.fpt.pharma.service.UnitService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UnitServiceImpl extends BaseServiceImpl<Unit, Long, UnitRepository> implements UnitService {

    private final DosageFormRepository dosageFormRepository;

    public UnitServiceImpl(UnitRepository repository, AuditService auditService,
                          DosageFormRepository dosageFormRepository) {
        super(repository, auditService);
        this.dosageFormRepository = dosageFormRepository;
    }

    @Override
    public DataTableResponse<UnitResponse> getUnits(DataTableRequest request) {
        Pageable pageable = createPageable(request);
        Specification<Unit> spec = buildSearchSpecification(request, List.of("name", "description"));

        Page<Unit> pageResult = spec != null ?
                repository.findAll(spec, pageable) :
                repository.findAll(pageable);

        List<UnitResponse> responses = pageResult.getContent().stream()
                .map(UnitResponse::fromEntity)
                .collect(Collectors.toList());

        Page<UnitResponse> responsePage = new org.springframework.data.domain.PageImpl<>(
                responses, pageable, pageResult.getTotalElements());

        return createDataTableResponse(request, responsePage, repository.count());
    }

    @Override
    public UnitResponse createUnit(UnitRequest request) {
        Unit unit = Unit.builder()
                .name(request.getName())
                .description(request.getDescription())
                .isBase(request.getIsBase() != null ? request.getIsBase() : false)
                .listUnitAvailable(request.getListUnitAvailable())
                .build();

        Unit saved = repository.save(unit);
        return UnitResponse.fromEntity(saved);
    }

    @Override
    public UnitResponse updateUnit(Long id, UnitRequest request) {
        Unit unit = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Unit not found"));

        if (request.getName() != null) unit.setName(request.getName());
        if (request.getDescription() != null) unit.setDescription(request.getDescription());
        if (request.getIsBase() != null) unit.setIsBase(request.getIsBase());
        if (request.getListUnitAvailable() != null) unit.setListUnitAvailable(request.getListUnitAvailable());

        Unit updated = repository.save(unit);
        return UnitResponse.fromEntity(updated);
    }

    @Override
    public UnitResponse getUnitById(Long id) {
        Unit unit = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Unit not found"));
        return UnitResponse.fromEntity(unit);
    }

    @Override
    public List<UnitResponse> getAllBaseUnits() {
        return repository.findAllBaseUnits().stream()
                .map(UnitResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<UnitResponse> getAvailableUnitsForDosageForm(Long dosageFormId) {
        if (dosageFormId == null) {
            return List.of();
        }

        // Load dosage form entity
        DosageForm dosageForm = dosageFormRepository.findById(dosageFormId)
                .orElse(null);
        if (dosageForm == null) {
            return List.of();
        }

        // Get the base unit for this dosage form
        Unit baseUnit = dosageForm.getBaseUnit();
        if (baseUnit == null || baseUnit.getListUnitAvailable() == null || baseUnit.getListUnitAvailable().isEmpty()) {
            return List.of();
        }

        // Parse the comma-separated list of unit IDs
        try {
            List<Long> unitIds = java.util.Arrays.stream(baseUnit.getListUnitAvailable().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Long::parseLong)
                    .collect(Collectors.toList());

            return repository.findAllByIds(unitIds).stream()
                    .map(UnitResponse::fromEntity)
                    .collect(Collectors.toList());
        } catch (NumberFormatException e) {
            return List.of();
        }
    }

    @Override
    public Unit getBaseUnitForDosageForm(Long dosageFormId) {
        if (dosageFormId == null) {
            return null;
        }

        return dosageFormRepository.findById(dosageFormId)
                .map(DosageForm::getBaseUnit)
                .orElse(null);
    }

    @Override
    public boolean isValidBaseUnit(Long dosageFormId, Long unitId) {
        if (dosageFormId == null || unitId == null) {
            return false;
        }

        Unit unit = repository.findById(unitId).orElse(null);
        if (unit == null || !unit.getIsBase()) {
            return false;
        }

        return dosageFormRepository.findById(dosageFormId)
                .map(df -> df.getBaseUnit().getId().equals(unitId))
                .orElse(false);
    }

    @Override
    public void deleteById(Long id) {
        long variantCount = repository.countVariantsByUnitId(id);
        if (variantCount > 0) {
            throw new EntityInUseException("Đơn vị", "danh sách chuyển đổi đơn vị (" + variantCount + " conversion)");
        }
        super.deleteById(id);
    }
}

