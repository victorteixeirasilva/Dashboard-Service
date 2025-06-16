package tech.inovasoft.inevolving.ms.dashboard.unit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
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
        public void getResponseObjectiveDTO() {
                // Given
                UUID idCategory = UUID.randomUUID();
                CategoryDTO categoryDTO = new CategoryDTO(
                        idCategory,
                        "Category 1",
                        "Category 1 description"
                );
                List<ObjectiveDTO> objectiveDTOList = new ArrayList<>();
                objectiveDTOList.add(
                        new ObjectiveDTO(
                                UUID.randomUUID(),
                                "Objective 1",
                                "Objective 1 description",
                                StatusObjectiveDTO.TODO,
                                null,
                                idUser
                        )
                );
                objectiveDTOList.add(
                        new ObjectiveDTO(
                                UUID.randomUUID(),
                                "Objective 2",
                                "Objective 2 description",
                                StatusObjectiveDTO.TODO,
                                null,
                                idUser
                        )
                );
                ObjectivesByCategoryDTO objectivesByCategoryDTO = new ObjectivesByCategoryDTO(
                        categoryDTO,
                        objectiveDTOList
                );


                //TODO: Seguir com os testes
                // When
                // Then
        }

        @Test
        public void analysisTheObjectiveTasks() {
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
                        Date.valueOf(LocalDate.now().minusDays(1)),
                        Date.valueOf(LocalDate.now().plusDays(30))
                )).thenReturn(ResponseEntity.ok(taskDTOList));
                var result = dashboardService.analysisTheObjectiveTasks(idObjective);

                // Then
                assertNotNull(result);
                assertEquals(10, result.totNumberTasks());
                assertEquals(5, result.numberTasksToDo());
                assertEquals(1, result.percentageTasksOverdue());
                assertEquals(2, result.numberTasksDone());
                assertEquals(2, result.numberTasksInProgress());
                assertEquals(50, result.percentageTasksToDo());
                assertEquals(10, result.percentageTasksOverdue());
                assertEquals(20, result.numberTasksInProgress());
                assertEquals(20, result.percentageTasksDone());

                verify(taskServiceClient).getTasksInDateRangeByObjectiveId(
                        idUser,
                        idObjective,
                        Date.valueOf(LocalDate.now().minusDays(1)),
                        Date.valueOf(LocalDate.now().plusDays(30))
                );

        }


}
