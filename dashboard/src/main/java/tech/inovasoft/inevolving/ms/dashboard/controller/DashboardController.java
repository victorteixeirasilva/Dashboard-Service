package tech.inovasoft.inevolving.ms.dashboard.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.inovasoft.inevolving.ms.dashboard.domain.dto.response.ResponseDashbordDTO;
import tech.inovasoft.inevolving.ms.dashboard.service.DashboardService;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Tag(name = "Dashboard", description = "Dashboard Endpoint Manager | Gerenciador dos endpoints de Analise de Dados")
@RestController
@RequestMapping("/ms/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @Operation(
            summary =   "Get data analysis regarding user categories, goals and tasks. | " +
                        "Pegar analise de dados referente as categorias, objetivos e tarefas do usuário.",
            description =   "Returns a dashboard with data regarding user categories, goals and tasks. | " +
                            "Retorna um dashboard com dados referente as categorias, objetivos e tarefas do usuário."
    )
    @Async("asyncExecutor")
    @GetMapping("/{idUser}")
    public CompletableFuture<ResponseEntity<ResponseDashbordDTO>> getDashboard(UUID idUser) {
        return CompletableFuture.completedFuture(
                ResponseEntity.ok(
                        dashboardService.getDashboard(idUser)
                )
        );
    }

}
