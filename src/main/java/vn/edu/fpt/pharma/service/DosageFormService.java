package vn.edu.fpt.pharma.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.pharma.dto.dosageform.DosageFormRequest;
import vn.edu.fpt.pharma.dto.dosageform.DosageFormResponse;
import vn.edu.fpt.pharma.entity.DosageForm;
import vn.edu.fpt.pharma.entity.Unit;
import vn.edu.fpt.pharma.repository.DosageFormRepository;
import vn.edu.fpt.pharma.repository.UnitRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DosageFormService {

    private final DosageFormRepository dosageFormRepository;
    private final UnitRepository unitRepository;

    @Transactional(readOnly = true)
    public List<DosageFormResponse> getAllDosageForms() {
        return dosageFormRepository.findAllActiveOrderByDisplayOrder()
                .stream()
                .map(DosageFormResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DosageFormResponse getDosageFormById(Long id) {
        DosageForm dosageForm = dosageFormRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy dạng bào chế với ID: " + id));
        return DosageFormResponse.fromEntity(dosageForm);
    }

    @Transactional
    public DosageFormResponse createDosageForm(DosageFormRequest request) {
        // Get base unit first
        Unit baseUnit = unitRepository.findById(request.getBaseUnitId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn vị cơ bản với ID: " + request.getBaseUnitId()));

        // Verify that the unit is actually a base unit
        if (!baseUnit.getIsBase()) {
            throw new RuntimeException("Đơn vị '" + baseUnit.getName() + "' không phải là đơn vị cơ bản");
        }

        // Check if combination of displayName and baseUnit already exists
        if (dosageFormRepository.existsByDisplayNameAndBaseUnit(request.getDisplayName(), baseUnit)) {
            throw new RuntimeException("Dạng bào chế '" + request.getDisplayName() +
                                     "' với đơn vị cơ bản '" + baseUnit.getName() + "' đã tồn tại");
        }

        DosageForm dosageForm = DosageForm.builder()
                .displayName(request.getDisplayName())
                .baseUnit(baseUnit)
                .description(request.getDescription())
                .active(request.getActive() != null ? request.getActive() : true)
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
                .build();

        DosageForm saved = dosageFormRepository.save(dosageForm);
        return DosageFormResponse.fromEntity(saved);
    }

    @Transactional
    public DosageFormResponse updateDosageForm(Long id, DosageFormRequest request) {
        DosageForm dosageForm = dosageFormRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy dạng bào chế với ID: " + id));

        // Get the new base unit
        Unit newBaseUnit = unitRepository.findById(request.getBaseUnitId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn vị cơ bản với ID: " + request.getBaseUnitId()));

        if (!newBaseUnit.getIsBase()) {
            throw new RuntimeException("Đơn vị '" + newBaseUnit.getName() + "' không phải là đơn vị cơ bản");
        }

        // Check if the combination of displayName and baseUnit would create a duplicate
        // (excluding the current record being updated)
        boolean isNameOrUnitChanged = !dosageForm.getDisplayName().equals(request.getDisplayName())
                                   || !dosageForm.getBaseUnit().getId().equals(request.getBaseUnitId());

        if (isNameOrUnitChanged) {
            if (dosageFormRepository.existsByDisplayNameAndBaseUnitExcludingId(
                    request.getDisplayName(), newBaseUnit, id)) {
                throw new RuntimeException("Dạng bào chế '" + request.getDisplayName() +
                                         "' với đơn vị cơ bản '" + newBaseUnit.getName() + "' đã tồn tại");
            }
        }

        // Update fields
        dosageForm.setDisplayName(request.getDisplayName());
        dosageForm.setBaseUnit(newBaseUnit);
        dosageForm.setDescription(request.getDescription());
        if (request.getActive() != null) {
            dosageForm.setActive(request.getActive());
        }
        if (request.getDisplayOrder() != null) {
            dosageForm.setDisplayOrder(request.getDisplayOrder());
        }

        DosageForm updated = dosageFormRepository.save(dosageForm);
        return DosageFormResponse.fromEntity(updated);
    }

    @Transactional
    public void deleteDosageForm(Long id) {
        DosageForm dosageForm = dosageFormRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy dạng bào chế với ID: " + id));

        // Soft delete
        dosageFormRepository.delete(dosageForm);
    }
}

