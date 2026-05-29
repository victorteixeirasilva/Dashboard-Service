package tech.inovasoft.inevolving.ms.dashboard.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import tech.inovasoft.inevolving.ms.dashboard.domain.dto.response.ResponseDashbordReasonCancellationDTO;
import tech.inovasoft.inevolving.ms.dashboard.domain.dto.response.ResponseObjectiveDTO;
import tech.inovasoft.inevolving.ms.dashboard.domain.exception.ExternalServiceErrorException;
import tech.inovasoft.inevolving.ms.dashboard.service.ClaudeService;
import tech.inovasoft.inevolving.ms.dashboard.service.DashboardService;
import tech.inovasoft.inevolving.ms.dashboard.service.client.task.dto.TaskDTO;
import tech.inovasoft.inevolving.ms.dashboard.support.TaskTestDataFactory;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClaudeServiceTest {

    @Mock
    private DashboardService dashboardService;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ClaudeService claudeService;

    private ResponseObjectiveDTO objectiveDto;
    private UUID idUser;
    private UUID idObjective;

    @BeforeEach
    void setUp() {
        idUser = UUID.randomUUID();
        idObjective = UUID.randomUUID();
        objectiveDto = new ResponseObjectiveDTO(
                idObjective,
                "Objetivo",
                "Descrição",
                "TODO",
                null,
                idUser,
                10, 5, 2, 2, 1, 1,
                50, 20, 20, 10, 10
        );

        ReflectionTestUtils.setField(claudeService, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(claudeService, "openRouterKey", "test-openrouter-key");
    }

    @Test
    @DisplayName("enviarParaClaude deve repassar timezone às consultas de cancelamento")
    void enviarParaClaude_propagatesTimezone() throws ExternalServiceErrorException {
        // Given
        String timezone = "America/Sao_Paulo";
        List<TaskDTO> cancelledTasks = List.of(
                TaskTestDataFactory.buildCancelledTask(idObjective, idUser, "Imprevisto")
        );
        when(dashboardService.getDashReasonCancellationByIdObjective(idUser, idObjective, timezone))
                .thenReturn(new ResponseDashbordReasonCancellationDTO(1, Collections.emptyList()));
        when(dashboardService.getTasksCancelledByObjective(idUser, idObjective, timezone))
                .thenReturn(cancelledTasks);
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                .thenReturn(ResponseEntity.ok("{\"choices\":[{\"message\":{\"content\":\"Análise\"}}]}"));

        // When
        String result = claudeService.enviarParaClaude(objectiveDto, timezone);

        // Then
        assertThat(result).contains("choices");
        verify(dashboardService).getDashReasonCancellationByIdObjective(idUser, idObjective, timezone);
        verify(dashboardService).getTasksCancelledByObjective(idUser, idObjective, timezone);
    }

    @Test
    @DisplayName("enviarParaClaude deve repassar null quando timezone ausente")
    void enviarParaClaude_nullTimezone() throws ExternalServiceErrorException {
        // Given
        when(dashboardService.getDashReasonCancellationByIdObjective(idUser, idObjective, null))
                .thenReturn(new ResponseDashbordReasonCancellationDTO(0, Collections.emptyList()));
        when(dashboardService.getTasksCancelledByObjective(idUser, idObjective, null))
                .thenReturn(Collections.emptyList());
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                .thenReturn(ResponseEntity.ok("resposta-ia"));

        // When
        String result = claudeService.enviarParaClaude(objectiveDto, null);

        // Then
        assertThat(result).isEqualTo("resposta-ia");
        verify(dashboardService).getDashReasonCancellationByIdObjective(idUser, idObjective, null);
        verify(dashboardService).getTasksCancelledByObjective(idUser, idObjective, null);
    }
}
