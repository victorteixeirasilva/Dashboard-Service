package tech.inovasoft.inevolving.ms.dashboard.service.client.category.dto.request;

import java.util.UUID;

public record RequestCreateObjectiveDTO(
        String nameObjective,
        String descriptionObjective,
        UUID idUser
) {
}
