package tech.inovasoft.inevolving.ms.dashboard.service.client.category.dto;

import tech.inovasoft.inevolving.ms.dashboard.domain.dto.response.ResponseObjectiveDTO;

import java.util.List;

public record ObjectivesByCategoryDTO(
        CategoryDTO category,
        List<ResponseObjectiveDTO> objectives
) {
}
