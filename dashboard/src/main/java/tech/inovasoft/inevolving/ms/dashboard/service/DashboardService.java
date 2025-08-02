package tech.inovasoft.inevolving.ms.dashboard.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import tech.inovasoft.inevolving.ms.dashboard.domain.dto.response.ResponseCategoryDTO;
import tech.inovasoft.inevolving.ms.dashboard.domain.dto.response.ResponseDashbordDTO;
import tech.inovasoft.inevolving.ms.dashboard.domain.dto.response.ResponseObjectiveDTO;
import tech.inovasoft.inevolving.ms.dashboard.domain.exception.ExternalServiceErrorException;
import tech.inovasoft.inevolving.ms.dashboard.service.client.category.CategoryServiceClient;
import tech.inovasoft.inevolving.ms.dashboard.service.client.category.dto.CategoriesDTO;
import tech.inovasoft.inevolving.ms.dashboard.service.client.category.dto.CategoryDTO;
import tech.inovasoft.inevolving.ms.dashboard.service.client.category.dto.ObjectiveDTO;
import tech.inovasoft.inevolving.ms.dashboard.service.client.category.dto.ObjectivesByCategoryDTO;
import tech.inovasoft.inevolving.ms.dashboard.service.client.task.TaskServiceClient;
import tech.inovasoft.inevolving.ms.dashboard.service.client.task.dto.ObjectiveTaskAnalysisDTO;
import tech.inovasoft.inevolving.ms.dashboard.service.client.task.dto.StatusTaskDTO;
import tech.inovasoft.inevolving.ms.dashboard.service.client.task.dto.TaskDTO;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class DashboardService {

    @Autowired
    private TaskServiceClient taskServiceClient;

    @Autowired
    private CategoryServiceClient categoryServiceClient;

    /**
     * @desciprion - Analisa as tarefas de um objetivo. | Analyze the tasks of a goal.
     * @param idUser - ID do usuário. | User ID
     * @param idObjective - ID do objetivo. | Objective ID
     * @return - Objeto com informações sobre as tarefas. | Object with task information
     */
    public ObjectiveTaskAnalysisDTO analysisTheObjectiveTasks(
            UUID idUser,
            UUID idObjective
    ) throws ExternalServiceErrorException {
        ResponseEntity<List<TaskDTO>> response;
        try {
            response = taskServiceClient
                    .getTasksInDateRangeByObjectiveId(
                            idUser,
                            idObjective,
                            Date.valueOf(LocalDate.now().minusYears(1)),
                            Date.valueOf(LocalDate.now().plusYears(1))
                    );
        } catch (Exception e) {
//            throw new ExternalServiceErrorException("task-service");
             return new ObjectiveTaskAnalysisDTO(0,0,0,0,0,0,0,0,0);

        }

        var objectiveTaskAnalysisDTO = new ObjectiveTaskAnalysisDTO(0,0,0,0,0,0,0,0,0);
//        if (response.getStatusCode().isSameCodeAs(HttpStatus.NOT_FOUND)) {
//            return objectiveTaskAnalysisDTO;
//        }

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

            if (objectiveTaskAnalysisDTO.getTotNumberTasks() > 0) {
                double percentageTasksDone = ((double) objectiveTaskAnalysisDTO.getNumberTasksDone() / objectiveTaskAnalysisDTO.getTotNumberTasks()) * 100;
                objectiveTaskAnalysisDTO.setPercentageTasksDone((int) Math.round(percentageTasksDone));

                double percentageTasksOverdue = ((double) objectiveTaskAnalysisDTO.getNumberTasksOverdue() / objectiveTaskAnalysisDTO.getTotNumberTasks()) * 100;
                objectiveTaskAnalysisDTO.setPercentageTasksOverdue((int) Math.round(percentageTasksOverdue));

                double percentageTasksToDo = ((double) objectiveTaskAnalysisDTO.getNumberTasksToDo() / objectiveTaskAnalysisDTO.getTotNumberTasks()) * 100;
                objectiveTaskAnalysisDTO.setPercentageTasksToDo((int) Math.round(percentageTasksToDo));

                double percentageTasksInProgress = ((double) objectiveTaskAnalysisDTO.getNumberTasksInProgress() / objectiveTaskAnalysisDTO.getTotNumberTasks()) * 100;
                objectiveTaskAnalysisDTO.setPercentageTasksInProgress((int) Math.round(percentageTasksInProgress));
            }
        }

        return objectiveTaskAnalysisDTO;
    }

    /**
     * @desciprion - Busca as informações de um objetivo. | Search objective information.
     * @param idUser - ID do usuário. | User ID
     * @param objectiveDTO - Objeto com informações do objetivo. | Objective object
     * @return - Objeto com informações do objetivo. | Objective object
     */
    public ResponseObjectiveDTO getResponseObjectiveDTO(
            UUID idUser,
            ObjectiveDTO objectiveDTO
    ) throws ExternalServiceErrorException {
        var analysis = analysisTheObjectiveTasks(idUser, objectiveDTO.id());

        return new ResponseObjectiveDTO(
                objectiveDTO.id(),
                objectiveDTO.nameObjective(),
                objectiveDTO.descriptionObjective(),
                objectiveDTO.statusObjective(),
                objectiveDTO.completionDate(),
                objectiveDTO.idUser(),
                analysis.getTotNumberTasks(),
                analysis.getNumberTasksToDo(),
                analysis.getNumberTasksDone(),
                analysis.getNumberTasksInProgress(),
                analysis.getNumberTasksOverdue(),
                analysis.getPercentageTasksToDo(),
                analysis.getPercentageTasksDone(),
                analysis.getPercentageTasksInProgress(),
                analysis.getPercentageTasksOverdue()
        );
    }

    /**
     * @desciprion - Busca as informações de um objetivo. | Search objective information.
     * @param idUser - ID do usuário. | User ID
     * @param category - Objeto com informações da categoria. | Category object
     */
    public ResponseCategoryDTO getResponseCategoryDTO(
            UUID idUser,
            CategoryDTO category
    ) throws ExternalServiceErrorException {
        List<ResponseObjectiveDTO> objectives = new ArrayList<>();

        ResponseEntity<ObjectivesByCategoryDTO> objectivesByCategory = categoryServiceClient
                .getObjectivesByCategory(idUser, category.id());

        if (objectivesByCategory.getStatusCode().isSameCodeAs(HttpStatus.OK)) {
            for (ObjectiveDTO objective : objectivesByCategory.getBody().objectives()) {
                objectives.add(getResponseObjectiveDTO(idUser, objective));
            }
        }

        return new ResponseCategoryDTO(
                category.id(),
                category.categoryName(),
                category.categoryDescription(),
                objectives
        );
    }

    /**
     * @desciprion - Busca o dashboard do usuário. | Search user dashboard
     * @param idUser - ID do usuário. | User ID
     */
    public ResponseDashbordDTO getDashboard(
            UUID idUser
    ) throws ExternalServiceErrorException {

        ResponseEntity<CategoriesDTO> responseCategories = categoryServiceClient.getCategories(idUser);
        List<ResponseCategoryDTO> categoryDTOList = new ArrayList<>();

        if (responseCategories.getStatusCode().isSameCodeAs(HttpStatus.OK)) {
                for (CategoryDTO category : responseCategories.getBody().categories()) {
                    categoryDTOList.add(getResponseCategoryDTO(idUser, category));
                }
        }

        return new ResponseDashbordDTO(
                idUser,
                categoryDTOList
        );
    }

}
