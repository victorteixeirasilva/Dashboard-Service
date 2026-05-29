package tech.inovasoft.inevolving.ms.dashboard.service.client.task;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import tech.inovasoft.inevolving.ms.dashboard.service.client.task.dto.TaskDTO;

import java.sql.Date;
import java.util.List;
import java.util.UUID;

@FeignClient(
        name = "task-service",
        url = "http://tasks-service:8085/ms/tasks"
//        url = "${inevolving.uri.ms.task}"
)
public interface TaskServiceClient {

    @GetMapping("/{idUser}/{idObjective}/{startDate}/{endDate}/{token}")
    ResponseEntity<List<TaskDTO>> getTasksInDateRangeByObjectiveId(
            @PathVariable UUID idUser,
            @PathVariable UUID idObjective,
            @PathVariable Date startDate,
            @PathVariable Date endDate,
            @PathVariable String token,
            @RequestHeader(value = "X-User-Timezone", required = false) String userTimezone
    );

    @GetMapping("/objective/{idUser}/{idObjective}/{token}")
    ResponseEntity<List<TaskDTO>> getTasksByObjectiveId(
            @PathVariable UUID idUser,
            @PathVariable UUID idObjective,
            @PathVariable String token,
            @RequestHeader(value = "X-User-Timezone", required = false) String userTimezone
    );
}
