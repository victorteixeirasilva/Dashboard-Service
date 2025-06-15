package tech.inovasoft.inevolving.ms.dashboard.service.client.category.dto;

import java.sql.Date;
import java.util.UUID;

public record ObjectiveDTO(
        UUID id,
        String nameObjective,
        String descriptionObjective,
        String statusObjective,
        Date completionDate,
        UUID idUser
) {
}
