package tech.inovasoft.inevolving.ms.dashboard.service.client.objective.dto;

import java.sql.Date;
import java.util.UUID;

public record Objective(
        UUID id,
        String nameObjective,
        String descriptionObjective,
        String statusObjective,
        Date completionDate,
        UUID idUser
) {
}
