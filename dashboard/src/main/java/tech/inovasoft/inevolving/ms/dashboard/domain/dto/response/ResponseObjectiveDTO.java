package tech.inovasoft.inevolving.ms.dashboard.domain.dto.response;

import java.sql.Date;
import java.util.List;
import java.util.UUID;

public record ResponseObjectiveDTO(
        UUID id,
        String nameObjective,
        String descriptionObjective,
        String statusObjective,
        Date completionDate,
        UUID idUser,
        int totNumberTasks,
        int numberTasksToDo,
        int numberTasksDone,
        int numberTasksInProgress,
        int numberTasksOverdue,
        int percentageTasksToDo,
        int percentageTasksDone,
        int percentageTasksInProgress,
        int percentageTasksOverdue
) {
}
