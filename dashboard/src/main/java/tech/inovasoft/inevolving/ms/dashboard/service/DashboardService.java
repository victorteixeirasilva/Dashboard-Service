package tech.inovasoft.inevolving.ms.dashboard.service;

import org.springframework.stereotype.Service;
import tech.inovasoft.inevolving.ms.dashboard.domain.dto.response.ResponseCategoryDTO;
import tech.inovasoft.inevolving.ms.dashboard.domain.dto.response.ResponseDashbordDTO;
import tech.inovasoft.inevolving.ms.dashboard.domain.dto.response.ResponseObjectiveDTO;
import tech.inovasoft.inevolving.ms.dashboard.service.client.task.dto.ObjectiveTaskAnalysisDTO;

import java.util.UUID;

@Service
public class DashboardService {

    public ObjectiveTaskAnalysisDTO analysisTheObjectiveTasks(UUID idObjective) {
        // TODO: GREEN
        // TODO: BLUE
        return null;
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
