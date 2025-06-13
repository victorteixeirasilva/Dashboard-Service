package tech.inovasoft.inevolving.ms.dashboard.service.client.task;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import tech.inovasoft.inevolving.ms.dashboard.service.client.task.dto.Task;

import java.sql.Date;
import java.util.List;
import java.util.UUID;

@FeignClient(
        name = "task-service",
        url = "http://localhost:8085/ms/tasks"
)
public interface TaskServiceClient {

    @GetMapping("/{idUser}/{idObjective}/{startDate}/{endDate}")
    ResponseEntity<List<Task>> getTasksInDateRangeByObjectiveId(
            @PathVariable UUID idUser,
            @PathVariable UUID idObjective,
            @PathVariable Date startDate,
            @PathVariable Date endDate
    );
}
