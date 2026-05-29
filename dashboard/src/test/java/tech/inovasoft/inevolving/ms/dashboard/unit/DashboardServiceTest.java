package tech.inovasoft.inevolving.ms.dashboard.unit;

import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import tech.inovasoft.inevolving.ms.dashboard.domain.dto.response.ResponseCategoryDTO;
import tech.inovasoft.inevolving.ms.dashboard.domain.dto.response.ResponseDashbordDTO;
import tech.inovasoft.inevolving.ms.dashboard.domain.dto.response.ResponseDashbordReasonCancellationDTO;
import tech.inovasoft.inevolving.ms.dashboard.domain.dto.response.ResponseObjectiveDTO;
import tech.inovasoft.inevolving.ms.dashboard.domain.exception.ExternalServiceErrorException;
import tech.inovasoft.inevolving.ms.dashboard.service.DashboardService;
import tech.inovasoft.inevolving.ms.dashboard.service.client.Auth_For_MService.TokenCache;
import tech.inovasoft.inevolving.ms.dashboard.service.client.category.CategoryServiceClient;
import tech.inovasoft.inevolving.ms.dashboard.service.client.category.dto.*;
import tech.inovasoft.inevolving.ms.dashboard.service.client.task.TaskServiceClient;
import tech.inovasoft.inevolving.ms.dashboard.service.client.task.dto.StatusTaskDTO;
import tech.inovasoft.inevolving.ms.dashboard.service.client.task.dto.TaskDTO;
import tech.inovasoft.inevolving.ms.dashboard.support.TaskTestDataFactory;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static tech.inovasoft.inevolving.ms.dashboard.service.client.Auth_For_MService.MicroServices.TASKS_SERVICE;
import static tech.inovasoft.inevolving.ms.dashboard.support.TaskTestDataFactory.SAMPLE_CANCELLED_AT;
import static tech.inovasoft.inevolving.ms.dashboard.support.TaskTestDataFactory.SAMPLE_CREATED_AT;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    private static final String MS_TOKEN = "ms-token";

    @Mock
    private TaskServiceClient taskServiceClient;

    @Mock
    private CategoryServiceClient categoryServiceClient;

    @Mock
    private TokenCache tokenCache;

    @InjectMocks
    private DashboardService dashboardService;

    private UUID idUser;
    private UUID idObjective;

    @BeforeEach
    void setUp() {
        idUser = UUID.randomUUID();
        idObjective = UUID.randomUUID();
        lenient().when(tokenCache.getToken(TASKS_SERVICE)).thenReturn(MS_TOKEN);
    }

    @Test
    @DisplayName("analysisTheObjectiveTasks deve calcular contagens e percentuais incluindo CANCELLED")
    void analysisTheObjectiveTasks_mixedStatuses() throws ExternalServiceErrorException {
        // Given
        List<TaskDTO> tasks = List.of(
                TaskTestDataFactory.buildTask(StatusTaskDTO.TODO, idObjective, idUser),
                TaskTestDataFactory.buildTask(StatusTaskDTO.TODO, idObjective, idUser),
                TaskTestDataFactory.buildTask(StatusTaskDTO.DONE, idObjective, idUser),
                TaskTestDataFactory.buildTask(StatusTaskDTO.IN_PROGRESS, idObjective, idUser),
                TaskTestDataFactory.buildTask(StatusTaskDTO.LATE, idObjective, idUser),
                TaskTestDataFactory.buildTask(StatusTaskDTO.CANCELLED, idObjective, idUser)
        );
        when(taskServiceClient.getTasksByObjectiveId(idUser, idObjective, MS_TOKEN, "America/Sao_Paulo"))
                .thenReturn(ResponseEntity.ok(tasks));

        // When
        var result = dashboardService.analysisTheObjectiveTasks(idUser, idObjective, "America/Sao_Paulo");

        // Then
        assertThat(result.getTotNumberTasks()).isEqualTo(6);
        assertThat(result.getNumberTasksToDo()).isEqualTo(2);
        assertThat(result.getNumberTasksDone()).isEqualTo(1);
        assertThat(result.getNumberTasksInProgress()).isEqualTo(1);
        assertThat(result.getNumberTasksOverdue()).isEqualTo(1);
        assertThat(result.getNumberTaskCancelled()).isEqualTo(1);
        assertThat(result.getPercentageTasksToDo()).isEqualTo(33);
        assertThat(result.getPercentageTasksDone()).isEqualTo(17);
        assertThat(result.getPercentageTasksInProgress()).isEqualTo(17);
        assertThat(result.getPercentageTasksOverdue()).isEqualTo(17);
        assertThat(result.getPercentageTaskCancelled()).isEqualTo(17);
    }

    @Test
    @DisplayName("analysisTheObjectiveTasks deve retornar DTO zerado quando body é null")
    void analysisTheObjectiveTasks_nullBody() throws ExternalServiceErrorException {
        // Given
        when(taskServiceClient.getTasksByObjectiveId(idUser, idObjective, MS_TOKEN, null))
                .thenReturn(ResponseEntity.ok(null));

        // When
        var result = dashboardService.analysisTheObjectiveTasks(idUser, idObjective, null);

        // Then
        assertThat(result.getTotNumberTasks()).isZero();
        assertThat(result.getNumberTasksToDo()).isZero();
    }

    @Test
    @DisplayName("analysisTheObjectiveTasks deve retornar DTO zerado quando Feign lança exceção genérica")
    void analysisTheObjectiveTasks_genericException() throws ExternalServiceErrorException {
        // Given
        when(taskServiceClient.getTasksByObjectiveId(idUser, idObjective, MS_TOKEN, null))
                .thenThrow(new RuntimeException("feign error"));

        // When
        var result = dashboardService.analysisTheObjectiveTasks(idUser, idObjective, null);

        // Then
        assertThat(result.getTotNumberTasks()).isZero();
    }

    @Test
    @DisplayName("analysisTheObjectiveTasks deve refazer chamada após Unauthorized com mesmo timezone")
    void analysisTheObjectiveTasks_unauthorizedRetry() throws ExternalServiceErrorException {
        // Given
        FeignException.Unauthorized unauthorized = unauthorizedException();
        when(taskServiceClient.getTasksByObjectiveId(idUser, idObjective, MS_TOKEN, "Europe/Lisbon"))
                .thenThrow(unauthorized)
                .thenReturn(ResponseEntity.ok(List.of(
                        TaskTestDataFactory.buildTask(StatusTaskDTO.TODO, idObjective, idUser)
                )));

        // When
        var result = dashboardService.analysisTheObjectiveTasks(idUser, idObjective, "Europe/Lisbon");

        // Then
        assertThat(result.getTotNumberTasks()).isEqualTo(1);
        verify(taskServiceClient, times(2))
                .getTasksByObjectiveId(idUser, idObjective, MS_TOKEN, "Europe/Lisbon");
        verify(tokenCache, times(2)).getToken(TASKS_SERVICE);
    }

    @Test
    @DisplayName("analysisTheObjectiveTasks deve repassar timezone válido ao Feign")
    void analysisTheObjectiveTasks_passesValidTimezone() throws ExternalServiceErrorException {
        // Given
        when(taskServiceClient.getTasksByObjectiveId(idUser, idObjective, MS_TOKEN, "America/Sao_Paulo"))
                .thenReturn(ResponseEntity.ok(List.of()));

        // When
        dashboardService.analysisTheObjectiveTasks(idUser, idObjective, "America/Sao_Paulo");

        // Then
        verify(taskServiceClient).getTasksByObjectiveId(idUser, idObjective, MS_TOKEN, "America/Sao_Paulo");
    }

    @Test
    @DisplayName("analysisTheObjectiveTasks deve enviar null ao Feign quando timezone é blank")
    void analysisTheObjectiveTasks_blankTimezoneBecomesNull() throws ExternalServiceErrorException {
        // Given
        when(taskServiceClient.getTasksByObjectiveId(idUser, idObjective, MS_TOKEN, null))
                .thenReturn(ResponseEntity.ok(List.of()));

        // When
        dashboardService.analysisTheObjectiveTasks(idUser, idObjective, "   ");

        // Then
        ArgumentCaptor<String> timezoneCaptor = ArgumentCaptor.forClass(String.class);
        verify(taskServiceClient).getTasksByObjectiveId(
                eq(idUser), eq(idObjective), eq(MS_TOKEN), timezoneCaptor.capture()
        );
        assertThat(timezoneCaptor.getValue()).isNull();
    }

    @Test
    @DisplayName("getTasksCancelledByObjective deve retornar apenas tarefas CANCELLED com timestamps")
    void getTasksCancelledByObjective_filtersCancelled() throws ExternalServiceErrorException {
        // Given
        TaskDTO cancelled = TaskTestDataFactory.buildCancelledTask(idObjective, idUser, "Imprevisto");
        List<TaskDTO> tasks = List.of(
                TaskTestDataFactory.buildTask(StatusTaskDTO.TODO, idObjective, idUser),
                cancelled
        );
        when(taskServiceClient.getTasksByObjectiveId(idUser, idObjective, MS_TOKEN, "America/Sao_Paulo"))
                .thenReturn(ResponseEntity.ok(tasks));

        // When
        List<TaskDTO> result = dashboardService.getTasksCancelledByObjective(
                idUser, idObjective, "America/Sao_Paulo"
        );

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().status()).isEqualTo(StatusTaskDTO.CANCELLED);
        assertThat(result.getFirst().createdAt()).isEqualTo(SAMPLE_CREATED_AT);
        assertThat(result.getFirst().cancelledAt()).isEqualTo(SAMPLE_CANCELLED_AT);
    }

    @Test
    @DisplayName("getTasksCancelledByObjective deve retornar lista vazia quando body é null ou vazio")
    void getTasksCancelledByObjective_emptyOrNull() throws ExternalServiceErrorException {
        // Given
        when(taskServiceClient.getTasksByObjectiveId(idUser, idObjective, MS_TOKEN, null))
                .thenReturn(ResponseEntity.ok(null));

        // When
        List<TaskDTO> result = dashboardService.getTasksCancelledByObjective(idUser, idObjective, null);

        // Then
        assertThat(result).isEmpty();

        when(taskServiceClient.getTasksByObjectiveId(idUser, idObjective, MS_TOKEN, null))
                .thenReturn(ResponseEntity.ok(Collections.emptyList()));
        assertThat(dashboardService.getTasksCancelledByObjective(idUser, idObjective, null)).isEmpty();
    }

    @Test
    @DisplayName("getTasksCancelledByObjective deve lançar ExternalServiceErrorException em erro genérico")
    void getTasksCancelledByObjective_genericException() {
        // Given
        when(taskServiceClient.getTasksByObjectiveId(idUser, idObjective, MS_TOKEN, null))
                .thenThrow(new RuntimeException("connection failed"));

        // When / Then
        assertThatThrownBy(() -> dashboardService.getTasksCancelledByObjective(idUser, idObjective, null))
                .isInstanceOf(ExternalServiceErrorException.class)
                .hasMessageContaining("task-service");
    }

    @Test
    @DisplayName("getTasksCancelledByObjective deve refazer chamada após Unauthorized")
    void getTasksCancelledByObjective_unauthorizedRetry() throws ExternalServiceErrorException {
        // Given
        FeignException.Unauthorized unauthorized = unauthorizedException();
        when(taskServiceClient.getTasksByObjectiveId(idUser, idObjective, MS_TOKEN, null))
                .thenThrow(unauthorized)
                .thenReturn(ResponseEntity.ok(List.of(
                        TaskTestDataFactory.buildCancelledTask(idObjective, idUser, "Acabou")
                )));

        // When
        List<TaskDTO> result = dashboardService.getTasksCancelledByObjective(idUser, idObjective, null);

        // Then
        assertThat(result).hasSize(1);
        verify(taskServiceClient, times(2))
                .getTasksByObjectiveId(idUser, idObjective, MS_TOKEN, null);
    }

    @Test
    @DisplayName("getDashReasonCancellationByIdObjective deve agregar motivos separados por ponto e vírgula")
    void getDashReasonCancellationByIdObjective_aggregatesReasons() throws ExternalServiceErrorException {
        // Given
        List<TaskDTO> tasks = List.of(
                TaskTestDataFactory.buildCancelledTask(idObjective, idUser, "Imprevisto; Acabou"),
                TaskTestDataFactory.buildCancelledTask(idObjective, idUser, "Imprevisto"),
                TaskTestDataFactory.buildCancelledTask(idObjective, idUser, "acabou")
        );
        when(taskServiceClient.getTasksByObjectiveId(idUser, idObjective, MS_TOKEN, "UTC"))
                .thenReturn(ResponseEntity.ok(tasks));

        // When
        ResponseDashbordReasonCancellationDTO result =
                dashboardService.getDashReasonCancellationByIdObjective(idUser, idObjective, "UTC");

        // Then
        assertThat(result.totNumberTasks()).isEqualTo(3);
        assertThat(result.reasonList()).hasSize(2);
        assertThat(result.reasonList().stream().filter(r -> r.getReason().equalsIgnoreCase("Imprevisto")).findFirst())
                .isPresent()
                .get()
                .satisfies(r -> assertThat(r.getAmount()).isEqualTo(2));
        assertThat(result.reasonList().stream().filter(r -> r.getReason().equalsIgnoreCase("Acabou")).findFirst())
                .isPresent()
                .get()
                .satisfies(r -> assertThat(r.getAmount()).isEqualTo(2));
    }

    @Test
    @DisplayName("getResponseObjectiveDTO deve repassar timezone ao Feign")
    void getResponseObjectiveDTO_propagatesTimezone() throws ExternalServiceErrorException {
        // Given
        ObjectiveDTO objective = new ObjectiveDTO(
                idObjective, "Obj", "Desc", StatusObjectiveDTO.TODO, null, idUser
        );
        when(taskServiceClient.getTasksByObjectiveId(idUser, idObjective, MS_TOKEN, "Europe/Lisbon"))
                .thenReturn(ResponseEntity.ok(List.of(
                        TaskTestDataFactory.buildTask(StatusTaskDTO.TODO, idObjective, idUser)
                )));

        // When
        ResponseObjectiveDTO result = dashboardService.getResponseObjectiveDTO(idUser, objective, "Europe/Lisbon");

        // Then
        assertThat(result.totNumberTasks()).isEqualTo(1);
        verify(taskServiceClient).getTasksByObjectiveId(idUser, idObjective, MS_TOKEN, "Europe/Lisbon");
    }

    @Test
    @DisplayName("getResponseCategoryDTO deve repassar timezone ao Feign")
    void getResponseCategoryDTO_propagatesTimezone() throws ExternalServiceErrorException {
        // Given
        CategoryDTO category = new CategoryDTO(UUID.randomUUID(), "Cat", "Desc");
        ObjectiveDTO objective = new ObjectiveDTO(
                idObjective, "Obj", "Desc", StatusObjectiveDTO.TODO, null, idUser
        );
        when(categoryServiceClient.getObjectivesByCategory(eq(idUser), eq(category.id()), anyString()))
                .thenReturn(ResponseEntity.ok(new ObjectivesByCategoryDTO(category, List.of(objective))));
        when(tokenCache.getToken(any())).thenReturn(MS_TOKEN);
        when(taskServiceClient.getTasksByObjectiveId(idUser, idObjective, MS_TOKEN, "America/Sao_Paulo"))
                .thenReturn(ResponseEntity.ok(List.of(
                        TaskTestDataFactory.buildTask(StatusTaskDTO.DONE, idObjective, idUser)
                )));

        // When
        ResponseCategoryDTO result = dashboardService.getResponseCategoryDTO(
                idUser, category, "America/Sao_Paulo"
        );

        // Then
        assertThat(result.objectives()).hasSize(1);
        assertThat(result.objectives().getFirst().numberTasksDone()).isEqualTo(1);
        verify(taskServiceClient).getTasksByObjectiveId(idUser, idObjective, MS_TOKEN, "America/Sao_Paulo");
    }

    @Test
    @DisplayName("getDashboard deve repassar timezone ao Feign para cada objetivo")
    void getDashboard_propagatesTimezone() throws ExternalServiceErrorException {
        // Given
        UUID categoryId = UUID.randomUUID();
        CategoryDTO category = new CategoryDTO(categoryId, "Cat", "Desc");
        ObjectiveDTO objective = new ObjectiveDTO(
                idObjective, "Obj", "Desc", StatusObjectiveDTO.TODO, null, idUser
        );
        when(categoryServiceClient.getCategories(eq(idUser), anyString()))
                .thenReturn(ResponseEntity.ok(new CategoriesDTO(idUser, List.of(category))));
        when(categoryServiceClient.getObjectivesByCategory(eq(idUser), eq(categoryId), anyString()))
                .thenReturn(ResponseEntity.ok(new ObjectivesByCategoryDTO(category, List.of(objective))));
        when(tokenCache.getToken(any())).thenReturn(MS_TOKEN);
        when(taskServiceClient.getTasksByObjectiveId(idUser, idObjective, MS_TOKEN, "America/Sao_Paulo"))
                .thenReturn(ResponseEntity.ok(new ArrayList<>()));

        // When
        ResponseDashbordDTO result = dashboardService.getDashboard(idUser, "America/Sao_Paulo");

        // Then
        assertThat(result.categoryDTOList()).hasSize(1);
        verify(taskServiceClient).getTasksByObjectiveId(idUser, idObjective, MS_TOKEN, "America/Sao_Paulo");
    }

    private FeignException.Unauthorized unauthorizedException() {
        Request request = Request.create(
                Request.HttpMethod.GET,
                "/ms/tasks/objective",
                Collections.emptyMap(),
                null,
                StandardCharsets.UTF_8,
                new RequestTemplate()
        );
        return new FeignException.Unauthorized("Unauthorized", request, null, null);
    }
}
