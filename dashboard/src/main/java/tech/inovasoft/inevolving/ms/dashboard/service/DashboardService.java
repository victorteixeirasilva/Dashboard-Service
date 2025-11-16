package tech.inovasoft.inevolving.ms.dashboard.service;

import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import tech.inovasoft.inevolving.ms.dashboard.domain.dto.response.*;
import tech.inovasoft.inevolving.ms.dashboard.domain.exception.ExternalServiceErrorException;
import tech.inovasoft.inevolving.ms.dashboard.service.client.Auth_For_MService.TokenCache;
import tech.inovasoft.inevolving.ms.dashboard.service.client.category.CategoryServiceClient;
import tech.inovasoft.inevolving.ms.dashboard.service.client.category.dto.CategoriesDTO;
import tech.inovasoft.inevolving.ms.dashboard.service.client.category.dto.CategoryDTO;
import tech.inovasoft.inevolving.ms.dashboard.service.client.category.dto.ObjectiveDTO;
import tech.inovasoft.inevolving.ms.dashboard.service.client.category.dto.ObjectivesByCategoryDTO;
import tech.inovasoft.inevolving.ms.dashboard.service.client.task.TaskServiceClient;
import tech.inovasoft.inevolving.ms.dashboard.service.client.task.dto.ObjectiveTaskAnalysisDTO;
import tech.inovasoft.inevolving.ms.dashboard.service.client.task.dto.StatusTaskDTO;
import tech.inovasoft.inevolving.ms.dashboard.service.client.task.dto.TaskDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static tech.inovasoft.inevolving.ms.dashboard.service.client.Auth_For_MService.MicroServices.CATEGORIES_SERVICE;
import static tech.inovasoft.inevolving.ms.dashboard.service.client.Auth_For_MService.MicroServices.TASKS_SERVICE;

@Service
public class DashboardService {

    @Autowired
    private TaskServiceClient taskServiceClient;

    @Autowired
    private CategoryServiceClient categoryServiceClient;

    @Autowired
    private TokenCache tokenCache;

    private String cachedTokenCategory;
    private String cachedTokenTask;

    private String getValidTokenCategory() {
        if (cachedTokenCategory == null) {
            cachedTokenCategory = tokenCache.getToken(CATEGORIES_SERVICE);
        }
        return cachedTokenCategory;
    }

    private String getValidTokenTask() {
        if (cachedTokenTask == null) {
            cachedTokenTask = tokenCache.getToken(TASKS_SERVICE);
        }
        return cachedTokenTask;
    }

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
                    .getTasksByObjectiveId(
                            idUser,
                            idObjective,
                            getValidTokenTask()
                    );
        } catch (FeignException.Unauthorized e) {
            cachedTokenTask = null;
            return analysisTheObjectiveTasks(idUser, idObjective);
        } catch (Exception e) {
            return new ObjectiveTaskAnalysisDTO(0,0,0,0,0,0,0,0,0,0,0);
        }

        var objectiveTaskAnalysisDTO = new ObjectiveTaskAnalysisDTO(0,0,0,0,0,0,0,0,0,0,0);

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
            objectiveTaskAnalysisDTO.setNumberTaskCancelled(
                    (int) tasks.stream()
                            .filter(task -> task.status().equals(StatusTaskDTO.CANCELLED))
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

                double percentageTasksCancelled =
                    ((double) objectiveTaskAnalysisDTO.getNumberTaskCancelled() /
                    objectiveTaskAnalysisDTO.getTotNumberTasks()) * 100;
                objectiveTaskAnalysisDTO.setPercentageTaskCancelled((int) Math.round(percentageTasksCancelled));

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
                analysis.getNumberTaskCancelled(),
                analysis.getPercentageTasksToDo(),
                analysis.getPercentageTasksDone(),
                analysis.getPercentageTasksInProgress(),
                analysis.getPercentageTasksOverdue(),
                analysis.getPercentageTaskCancelled()
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

        ResponseEntity<ObjectivesByCategoryDTO> objectivesByCategory;
        try {
            objectivesByCategory = categoryServiceClient
                    .getObjectivesByCategory(idUser, category.id(), getValidTokenCategory());
        } catch (FeignException.Unauthorized e) {
            cachedTokenCategory = null;
            return getResponseCategoryDTO(idUser, category);
        }

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
        ResponseEntity<CategoriesDTO> responseCategories;

        try {
            responseCategories = categoryServiceClient
                    .getCategories(idUser, getValidTokenCategory());
        } catch (FeignException.Unauthorized e) {
            cachedTokenCategory = null;
            return getDashboard(idUser);
        }

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

    public ResponseDashbordReasonCancellationDTO getDashReasonCancellationByIdObjective(UUID idUser, UUID idObjective) throws ExternalServiceErrorException {
        List<TaskDTO> tasks = getTasksCancelledByObjective(idUser, idObjective);
        List<ReasonDTO> reasonDTOList = new ArrayList<>();

        for (TaskDTO task : tasks) {
            String[] reasons = task.cancellationReason().split(";");

            for (String reason : reasons) {
                reason = reason.trim(); // Evita espaços indesejados
                boolean found = false;

                for (ReasonDTO reasonDTO : reasonDTOList) {
                    if (reason.equalsIgnoreCase(reasonDTO.getReason())) {
                        reasonDTO.setAmount(reasonDTO.getAmount() + 1);
                        found = true;
//                        break; // Já encontramos, não precisa continuar
                    }
                }

                if (!found) {
                    var newReason = new ReasonDTO();
                    newReason.setReason(reason);
                    newReason.setAmount(1);
                    reasonDTOList.add(newReason);
                }
            }
        }


        return new ResponseDashbordReasonCancellationDTO(tasks.size(), reasonDTOList);
    }

    public List<TaskDTO> getTasksCancelledByObjective(UUID idUser, UUID idObjective) throws ExternalServiceErrorException {
        ResponseEntity<List<TaskDTO>> response;
        try {
            response = taskServiceClient
                    .getTasksByObjectiveId(
                            idUser,
                            idObjective,
                            getValidTokenTask()
                    );
        } catch (FeignException.Unauthorized e) {
            cachedTokenTask = null;
            return getTasksCancelledByObjective(idUser, idObjective);
        } catch (Exception e) {
            throw new ExternalServiceErrorException("task-service");
        }

        List<TaskDTO> tasks = response.getBody();

        if (tasks != null && !tasks.isEmpty()) {
            List<TaskDTO> tasksCancel = new ArrayList<>();

            for (TaskDTO task : tasks) {
                if (task.status().equals(StatusTaskDTO.CANCELLED)){
                    tasksCancel.add(task);
                }
            }

            return tasksCancel;
        } else {
            return new ArrayList<>();
        }
    }
}
