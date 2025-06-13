package tech.inovasoft.inevolving.ms.dashboard.service.client.objective;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import tech.inovasoft.inevolving.ms.dashboard.service.client.objective.dto.Objective;

import java.util.List;
import java.util.UUID;

@FeignClient(
        name = "objectives-service",
        url = "http://localhost:8081/ms/objectives"
)
public interface ObjectiveServiceClient { //TODO: Desenvolver Teste de Integração.

    @GetMapping("/{idObjective}/{idUser}")
    ResponseEntity<Objective> getObjectiveById(
            @PathVariable UUID idObjective,
            @PathVariable UUID idUser
    );

    @GetMapping("/user/{idUser}")
    ResponseEntity<List<Objective>>  getObjectivesByIdUser(
            @PathVariable UUID idUser
    );

    @GetMapping("/status/todo/user/{idUser}")
    ResponseEntity<List<Objective>> getObjectivesByIdUserToDo(
            @PathVariable UUID idUser
    );

    @GetMapping("/status/done/user/{idUser}")
    ResponseEntity<List<Objective>> getObjectivesByIdUserDone(
            @PathVariable UUID idUser
    );

}
