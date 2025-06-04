package tech.inovasoft.inevolving.ms.dashboard.domain.dto.response;

import java.util.List;
import java.util.UUID;

public record ResponseCategoryDTO(
        UUID id,
        String categoryName,
        String categoryDescription,
        List<ResponseObjectiveDTO> objectives
) {
}
