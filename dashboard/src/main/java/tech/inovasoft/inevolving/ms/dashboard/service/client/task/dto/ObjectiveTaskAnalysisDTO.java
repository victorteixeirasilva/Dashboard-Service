package tech.inovasoft.inevolving.ms.dashboard.service.client.task.dto;

public record ObjectiveTaskAnalysisDTO(
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
