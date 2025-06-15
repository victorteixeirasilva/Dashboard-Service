package tech.inovasoft.inevolving.ms.dashboard.service.client.category.dto;

import java.util.List;

public record ObjectivesByCategoryDTO(
        CategoryDTO category,
        List<ObjectiveDTO> objectives
) {
}
