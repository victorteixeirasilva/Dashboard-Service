package tech.inovasoft.inevolving.ms.dashboard.unit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import tech.inovasoft.inevolving.ms.dashboard.domain.dto.response.ResponseObjectiveDTO;
import tech.inovasoft.inevolving.ms.dashboard.domain.exception.ExternalServiceErrorException;
import tech.inovasoft.inevolving.ms.dashboard.service.DashboardService;
import tech.inovasoft.inevolving.ms.dashboard.service.client.category.CategoryServiceClient;
import tech.inovasoft.inevolving.ms.dashboard.service.client.category.dto.CategoryDTO;
import tech.inovasoft.inevolving.ms.dashboard.service.client.category.dto.ObjectiveDTO;
import tech.inovasoft.inevolving.ms.dashboard.service.client.category.dto.ObjectivesByCategoryDTO;
import tech.inovasoft.inevolving.ms.dashboard.service.client.category.dto.StatusObjectiveDTO;
import tech.inovasoft.inevolving.ms.dashboard.service.client.task.TaskServiceClient;
import tech.inovasoft.inevolving.ms.dashboard.service.client.task.dto.StatusTaskDTO;
import tech.inovasoft.inevolving.ms.dashboard.service.client.task.dto.TaskDTO;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class DashboardServiceTest {

        @Mock
        private TaskServiceClient taskServiceClient;

        @Mock
        private CategoryServiceClient categoryServiceClient;

        @InjectMocks
        private DashboardService dashboardService;

        UUID idUser = UUID.randomUUID();

        @Test
        public void analysisTheObjectiveTasks() throws ExternalServiceErrorException {
                // Given
                UUID idObjective = UUID.randomUUID();
                List<TaskDTO> taskDTOList = new ArrayList<>();
                for (int i = 1; i <= 5; i++) {
                        taskDTOList.add(new TaskDTO(
                                UUID.randomUUID(),
                                "Task " + i,
                                "Task " + i + " description",
                                StatusTaskDTO.TODO,
                                Date.valueOf(LocalDate.now()),
                                idObjective,
                                idUser,
                                null,
                                null,
                                false,
                                false,
                                false,
                                null
                                )
                        );
                }

                for (int i = 1; i <= 2; i++) {
                        taskDTOList.add(new TaskDTO(
                                        UUID.randomUUID(),
                                        "Task " + i,
                                        "Task " + i + " description",
                                        StatusTaskDTO.DONE,
                                        Date.valueOf(LocalDate.now()),
                                        idObjective,
                                        idUser,
                                        null,
                                        null,
                                        false,
                                        false,
                                        false,
                                        null
                                )
                        );
                }

                for (int i = 1; i <= 2; i++) {
                        taskDTOList.add(new TaskDTO(
                                        UUID.randomUUID(),
                                        "Task " + i,
                                        "Task " + i + " description",
                                        StatusTaskDTO.IN_PROGRESS,
                                        Date.valueOf(LocalDate.now()),
                                        idObjective,
                                        idUser,
                                        null,
                                        null,
                                        false,
                                        false,
                                        false,
                                        null
                                )
                        );
                }

                taskDTOList.add(new TaskDTO(
                                UUID.randomUUID(),
                                "Task ",
                                "Task description",
                                StatusTaskDTO.LATE,
                                Date.valueOf(LocalDate.now()),
                                idObjective,
                                idUser,
                                null,
                                null,
                                false,
                                false,
                                false,
                                null
                        )
                );


                // When
                when(taskServiceClient.getTasksInDateRangeByObjectiveId(
                        idUser,
                        idObjective,
                        Date.valueOf(LocalDate.now().minusYears(1)),
                        Date.valueOf(LocalDate.now().plusYears(1))
                )).thenReturn(ResponseEntity.ok(taskDTOList));
                var result = dashboardService.analysisTheObjectiveTasks(idUser,idObjective);

                // Then
                assertNotNull(result);
                assertEquals(10, result.getTotNumberTasks());
                assertEquals(5, result.getNumberTasksToDo());
                assertEquals(1, result.getNumberTasksOverdue());
                assertEquals(2, result.getNumberTasksDone());
                assertEquals(2, result.getNumberTasksInProgress());
                assertEquals(50, result.getPercentageTasksToDo());
                assertEquals(10, result.getPercentageTasksOverdue());
                assertEquals(20, result.getPercentageTasksInProgress());
                assertEquals(20, result.getPercentageTasksDone());

                verify(taskServiceClient).getTasksInDateRangeByObjectiveId(
                        idUser,
                        idObjective,
                        Date.valueOf(LocalDate.now().minusYears(1)),
                        Date.valueOf(LocalDate.now().plusYears(1))
                );

        }

        @Test
        public void getResponseObjectiveDTO() throws ExternalServiceErrorException {
                // Given
                var idUser = UUID.randomUUID();
                UUID idObjective = UUID.randomUUID();
                var objectiveDTO = new ObjectiveDTO(
                        idObjective,
                        "Objective 1",
                        "Objective 1 description",
                        StatusObjectiveDTO.TODO,
                        null,
                        idUser
                );

                List<TaskDTO> taskDTOList = new ArrayList<>();
                for (int i = 1; i <= 5; i++) {
                        taskDTOList.add(new TaskDTO(
                                        UUID.randomUUID(),
                                        "Task " + i,
                                        "Task " + i + " description",
                                        StatusTaskDTO.TODO,
                                        Date.valueOf(LocalDate.now()),
                                        idObjective,
                                        idUser,
                                        null,
                                        null,
                                        false,
                                        false,
                                        false,
                                        null
                                )
                        );
                }

                for (int i = 1; i <= 2; i++) {
                        taskDTOList.add(new TaskDTO(
                                        UUID.randomUUID(),
                                        "Task " + i,
                                        "Task " + i + " description",
                                        StatusTaskDTO.DONE,
                                        Date.valueOf(LocalDate.now()),
                                        idObjective,
                                        idUser,
                                        null,
                                        null,
                                        false,
                                        false,
                                        false,
                                        null
                                )
                        );
                }

                for (int i = 1; i <= 2; i++) {
                        taskDTOList.add(new TaskDTO(
                                        UUID.randomUUID(),
                                        "Task " + i,
                                        "Task " + i + " description",
                                        StatusTaskDTO.IN_PROGRESS,
                                        Date.valueOf(LocalDate.now()),
                                        idObjective,
                                        idUser,
                                        null,
                                        null,
                                        false,
                                        false,
                                        false,
                                        null
                                )
                        );
                }

                taskDTOList.add(new TaskDTO(
                                UUID.randomUUID(),
                                "Task ",
                                "Task description",
                                StatusTaskDTO.LATE,
                                Date.valueOf(LocalDate.now()),
                                idObjective,
                                idUser,
                                null,
                                null,
                                false,
                                false,
                                false,
                                null
                        )
                );

                ResponseObjectiveDTO expectedResponseObjectiveDTO = new ResponseObjectiveDTO(
                        objectiveDTO.id(),
                        objectiveDTO.nameObjective(),
                        objectiveDTO.descriptionObjective(),
                        objectiveDTO.statusObjective(),
                        objectiveDTO.completionDate(),
                        objectiveDTO.idUser(),
                        10,
                        5,
                        2,
                        2,
                        1,
                        50,
                        20,
                        20,
                        10
                );


                // When
                when(taskServiceClient.getTasksInDateRangeByObjectiveId(
                        idUser,
                        idObjective,
                        Date.valueOf(LocalDate.now().minusYears(1)),
                        Date.valueOf(LocalDate.now().plusYears(1))
                )).thenReturn(ResponseEntity.ok(taskDTOList));
                var result = dashboardService.getResponseObjectiveDTO(idUser, objectiveDTO);

                // Then
                assertNotNull(result);
                assertEquals(expectedResponseObjectiveDTO.id(), result.id());
                assertEquals(expectedResponseObjectiveDTO.nameObjective(), result.nameObjective());
                assertEquals(expectedResponseObjectiveDTO.descriptionObjective(), result.descriptionObjective());
                assertEquals(expectedResponseObjectiveDTO.statusObjective(), result.statusObjective());
                assertEquals(expectedResponseObjectiveDTO.completionDate(), result.completionDate());
                assertEquals(10, result.totNumberTasks());
                assertEquals(5, result.numberTasksToDo());
                assertEquals(1, result.numberTasksOverdue());
                assertEquals(2, result.numberTasksDone());
                assertEquals(2, result.numberTasksInProgress());
                assertEquals(50, result.percentageTasksToDo());
                assertEquals(10, result.percentageTasksOverdue());
                assertEquals(20, result.percentageTasksInProgress());
                assertEquals(20, result.percentageTasksDone());
        }


}
