package tech.inovasoft.inevolving.ms.dashboard.service.client.task.dto;

import java.sql.Date;
import java.time.OffsetDateTime;
import java.util.UUID;

public record TaskDTO(
        UUID id,
        String nameTask,
        String descriptionTask,
        String status,
        Date dateTask,
        UUID idObjective,
        UUID idUser,
        UUID idParentTask,
        UUID idOriginalTask,
        Boolean hasSubtasks,
        Boolean blockedByObjective,
        Boolean isCopy,
        String cancellationReason,
        UUID idResponsibleUser,
        OffsetDateTime createdAt,
        OffsetDateTime inProgressAt,
        OffsetDateTime completedAt,
        OffsetDateTime cancelledAt
) {
    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", nameTask='" + nameTask + '\'' +
                ", descriptionTask='" + descriptionTask + '\'' +
                ", status='" + status + '\'' +
                ", dateTask=" + dateTask +
                ", idObjective=" + idObjective +
                ", idUser=" + idUser +
                ", cancellationReason='" + cancellationReason + '\'' +
                ", createdAt=" + createdAt +
                ", inProgressAt=" + inProgressAt +
                ", completedAt=" + completedAt +
                ", cancelledAt=" + cancelledAt +
                "},";
    }
}
