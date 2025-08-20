package tech.inovasoft.inevolving.ms.dashboard.domain.dto.response;

import java.util.List;

public record ResponseDashbordReasonCancellationDTO(
        int totNumberTasks,
        List<ReasonDTO> reasonList
) {
    @Override
    public String toString() {
        return "ResponseDashbordReasonCancellationDTO{" +
                "totNumberTasks=" + totNumberTasks +
                ", reasonList=" + reasonList +
                '}';
    }
}
