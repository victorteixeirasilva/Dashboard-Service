package tech.inovasoft.inevolving.ms.dashboard.unit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tech.inovasoft.inevolving.ms.dashboard.service.client.task.dto.StatusTaskDTO;
import tech.inovasoft.inevolving.ms.dashboard.support.TaskTestDataFactory;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static tech.inovasoft.inevolving.ms.dashboard.support.TaskTestDataFactory.SAMPLE_CANCELLED_AT;
import static tech.inovasoft.inevolving.ms.dashboard.support.TaskTestDataFactory.SAMPLE_CREATED_AT;

class TaskDTOTest {

    @Test
    @DisplayName("toString deve incluir timestamps de ciclo de vida da tarefa")
    void toString_includesTimestamps() {
        // Given
        UUID idObjective = UUID.randomUUID();
        UUID idUser = UUID.randomUUID();
        var task = TaskTestDataFactory.buildCancelledTask(idObjective, idUser, "Imprevisto");

        // When
        String result = task.toString();

        // Then
        assertThat(result).contains("createdAt=" + SAMPLE_CREATED_AT);
        assertThat(result).contains("cancelledAt=" + SAMPLE_CANCELLED_AT);
        assertThat(result).contains("status='" + StatusTaskDTO.CANCELLED + "'");
        assertThat(result).contains("cancellationReason='Imprevisto'");
    }
}
