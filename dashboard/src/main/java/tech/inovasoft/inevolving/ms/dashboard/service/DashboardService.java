package tech.inovasoft.inevolving.ms.dashboard.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import tech.inovasoft.inevolving.ms.dashboard.domain.dto.response.ResponseCategoryDTO;
import tech.inovasoft.inevolving.ms.dashboard.domain.dto.response.ResponseDashbordDTO;
import tech.inovasoft.inevolving.ms.dashboard.domain.dto.response.ResponseObjectiveDTO;
import tech.inovasoft.inevolving.ms.dashboard.domain.exception.ExternalServiceErrorException;
import tech.inovasoft.inevolving.ms.dashboard.service.client.task.TaskServiceClient;
import tech.inovasoft.inevolving.ms.dashboard.service.client.task.dto.ObjectiveTaskAnalysisDTO;
import tech.inovasoft.inevolving.ms.dashboard.service.client.task.dto.StatusTaskDTO;
import tech.inovasoft.inevolving.ms.dashboard.service.client.task.dto.TaskDTO;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class DashboardService {

    private TaskServiceClient taskServiceClient;

    public ObjectiveTaskAnalysisDTO analysisTheObjectiveTasks(UUID idUser,UUID idObjective) throws ExternalServiceErrorException {
        ResponseEntity<List<TaskDTO>> response;
        try {
            response = taskServiceClient
                    .getTasksInDateRangeByObjectiveId(
                            idUser,
                            idObjective,
                            // TODO: Corrigir end-point para não pedir datas.
                            Date.valueOf(LocalDate.now().minusYears(1)),
                            Date.valueOf(LocalDate.now().plusYears(1))
                    );
        } catch (Exception e) {
            // TODO: Desenvolver teste da falha.
            throw new ExternalServiceErrorException("task-service");
        }

        if (response.getStatusCode().isSameCodeAs(HttpStatus.NOT_FOUND)) {
            // TODO: Nenhuma tarefa encontrada.
        }

        var objectiveTaskAnalysisDTO = new ObjectiveTaskAnalysisDTO(0,0,0,0,0,0,0,0,0);
        List<TaskDTO> tasks = response.getBody();

        if (tasks != null) {
            objectiveTaskAnalysisDTO.setTotNumberTasks(tasks.size());
            objectiveTaskAnalysisDTO.setNumberTasksDone(
                    (int) tasks.stream()
                            .filter(task -> task.status().equals(StatusTaskDTO.DONE))
                            .count()
            );
            objectiveTaskAnalysisDTO.setNumberTasksOverdue(
                    (int) tasks.stream()
                            .filter(task -> task.status().equals(StatusTaskDTO.LATE))
                            .count()
            );
            objectiveTaskAnalysisDTO.setNumberTasksToDo(
                    (int) tasks.stream()
                            .filter(task -> task.status().equals(StatusTaskDTO.TODO))
                            .count()
            );
            objectiveTaskAnalysisDTO.setNumberTasksInProgress(
                    (int) tasks.stream()
                            .filter(task -> task.status().equals(StatusTaskDTO.IN_PROGRESS))
                            .count()
            );

            // TODO: Falta tasks canceladas.
            if (objectiveTaskAnalysisDTO.getTotNumberTasks() > 0) {
                double percentageTasksDone = ((double) objectiveTaskAnalysisDTO.getNumberTasksDone() / objectiveTaskAnalysisDTO.getTotNumberTasks()) * 100;
                objectiveTaskAnalysisDTO.setPercentageTasksDone((int) Math.round(percentageTasksDone));

                double percentageTasksOverdue = ((double) objectiveTaskAnalysisDTO.getNumberTasksOverdue() / objectiveTaskAnalysisDTO.getTotNumberTasks()) * 100;
                objectiveTaskAnalysisDTO.setPercentageTasksOverdue((int) Math.round(percentageTasksOverdue));

                double percentageTasksToDo = ((double) objectiveTaskAnalysisDTO.getNumberTasksToDo() / objectiveTaskAnalysisDTO.getTotNumberTasks()) * 100;
                objectiveTaskAnalysisDTO.setPercentageTasksToDo((int) Math.round(percentageTasksToDo));

                double percentageTasksInProgress = ((double) objectiveTaskAnalysisDTO.getNumberTasksInProgress() / objectiveTaskAnalysisDTO.getTotNumberTasks()) * 100;
                objectiveTaskAnalysisDTO.setPercentageTasksInProgress((int) Math.round(percentageTasksInProgress));
                // TODO: Falta tasks canceladas.
            }
        }

        return objectiveTaskAnalysisDTO;
        // TODO: BLUE
    }

    public ResponseObjectiveDTO getResponseObjectiveDTO(UUID idUser, UUID idCategory) {
        // TODO: RED
        // TODO: GREEN
        // TODO: BLUE
        return null;
    }

    public ResponseCategoryDTO getResponseCategoryDTO() { // TODO: Definir parâmetros para a busca
        // TODO: RED
        // TODO: GREEN
        // TODO: BLUE
        return null;
    }

    public ResponseDashbordDTO getDashboard(UUID idUser) {
        // TODO: RED
        // TODO: GREEN
        // TODO: BLUE
        return null;
    }

}
