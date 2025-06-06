package tech.inovasoft.inevolving.ms.dashboard.service.client.category.dto;

import java.util.UUID;

public record CategoryDTO(
        UUID id,
        String categoryName,
        String categoryDescription
) {
}
