package tech.inovasoft.inevolving.ms.dashboard.support;

import tech.inovasoft.inevolving.ms.dashboard.service.client.task.dto.StatusTaskDTO;
import tech.inovasoft.inevolving.ms.dashboard.service.client.task.dto.TaskDTO;

import java.sql.Date;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public final class TaskTestDataFactory {

    public static final OffsetDateTime SAMPLE_CREATED_AT = OffsetDateTime.parse("2026-05-29T10:15:00-03:00");
    public static final OffsetDateTime SAMPLE_CANCELLED_AT = OffsetDateTime.parse("2026-05-29T14:30:00-03:00");

    private TaskTestDataFactory() {
    }

    public static TaskDTO buildTask(String status, UUID idObjective, UUID idUser) {
        return buildTask(status, idObjective, idUser, null, null, null, null, null);
    }

    public static TaskDTO buildTask(
            String status,
            UUID idObjective,
            UUID idUser,
            String cancellationReason,
            OffsetDateTime createdAt,
            OffsetDateTime inProgressAt,
            OffsetDateTime completedAt,
            OffsetDateTime cancelledAt
    ) {
        return new TaskDTO(
                UUID.randomUUID(),
                "Task name",
                "Task description",
                status,
                Date.valueOf(LocalDate.now()),
                idObjective,
                idUser,
                null,
                null,
                false,
                false,
                false,
                cancellationReason,
                UUID.randomUUID(),
                createdAt,
                inProgressAt,
                completedAt,
                cancelledAt
        );
    }

    public static TaskDTO buildCancelledTask(UUID idObjective, UUID idUser, String cancellationReason) {
        return buildTask(
                StatusTaskDTO.CANCELLED,
                idObjective,
                idUser,
                cancellationReason,
                SAMPLE_CREATED_AT,
                null,
                null,
                SAMPLE_CANCELLED_AT
        );
    }
}
