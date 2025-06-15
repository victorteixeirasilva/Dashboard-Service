package tech.inovasoft.inevolving.ms.dashboard.service.client.category.dto;

import java.util.List;
import java.util.UUID;

public record CategoriesDTO(
        UUID idUser,
        List<CategoryDTO> categories
) {
}
