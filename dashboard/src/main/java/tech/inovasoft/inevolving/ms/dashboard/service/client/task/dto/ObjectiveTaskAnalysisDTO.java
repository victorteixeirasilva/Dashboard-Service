package tech.inovasoft.inevolving.ms.dashboard.service.client.task.dto;

import lombok.*;
import org.springframework.web.bind.annotation.GetMapping;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ObjectiveTaskAnalysisDTO{
    private int totNumberTasks;
    private int numberTasksToDo;
    private int numberTasksDone;
    private int numberTasksInProgress;
    private int numberTasksOverdue;
    private int percentageTasksToDo;
    private int percentageTasksDone;
    private int percentageTasksInProgress;
    private int percentageTasksOverdue;
}
