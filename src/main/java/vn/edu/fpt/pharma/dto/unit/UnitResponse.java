package vn.edu.fpt.pharma.dto.unit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.fpt.pharma.entity.Unit;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnitResponse {
    private Long id;
    private String name;
    private String description;
    private Boolean isBase;
    private String listUnitAvailable;
    private List<Long> availableUnitIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static UnitResponse fromEntity(Unit unit) {
        if (unit == null) return null;

        List<Long> availableIds = Collections.emptyList();
        if (unit.getListUnitAvailable() != null && !unit.getListUnitAvailable().isEmpty()) {
            try {
                availableIds = Arrays.stream(unit.getListUnitAvailable().split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .map(Long::parseLong)
                        .collect(Collectors.toList());
            } catch (NumberFormatException e) {
                // Log error if needed
            }
        }

        return UnitResponse.builder()
                .id(unit.getId())
                .name(unit.getName())
                .description(unit.getDescription())
                .isBase(unit.getIsBase())
                .listUnitAvailable(unit.getListUnitAvailable())
                .availableUnitIds(availableIds)
                .createdAt(unit.getCreatedAt())
                .updatedAt(unit.getUpdatedAt())
                .build();
    }
}

