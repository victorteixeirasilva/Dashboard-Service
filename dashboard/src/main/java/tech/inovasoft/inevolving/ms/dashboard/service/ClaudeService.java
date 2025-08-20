package tech.inovasoft.inevolving.ms.dashboard.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tech.inovasoft.inevolving.ms.dashboard.domain.dto.response.ReasonDTO;
import tech.inovasoft.inevolving.ms.dashboard.domain.dto.response.ResponseDashbordReasonCancellationDTO;
import tech.inovasoft.inevolving.ms.dashboard.domain.dto.response.ResponseObjectiveDTO;
import tech.inovasoft.inevolving.ms.dashboard.domain.exception.ExternalServiceErrorException;
import tech.inovasoft.inevolving.ms.dashboard.service.client.task.dto.TaskDTO;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ClaudeService {

    @Autowired
    private DashboardService dashboardService;

    private final RestTemplate restTemplate;

    public ClaudeService() {
        this.restTemplate = new RestTemplate();
    }

    public String enviarParaClaude(ResponseObjectiveDTO dto) throws ExternalServiceErrorException {
        String url = "https://openrouter.ai/api/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth("");

        String prompt =
                "Você receberá três objetos JSON:\n" +
                "\n" +
                "\n" +
                "Sua tarefa é analisar esses objetos JSON e fornecer conselhos acionáveis para ajudar o usuário a melhorar seu desempenho e concluir mais tarefas relacionadas ao seu objetivo.\n" +
                "\n" +
                "Siga esses passos:\n" +
                "\n" +
                "1. **Analise a Análise Objetiva:**\n" +
                " * Identifique o status do objetivo (TODO, EM ANDAMENTO, CONCLUÍDO).\n" +
                " * Avalie a taxa de conclusão (percentageTasksDone).\n" +
                " * Anote o número de tarefas atrasadas (numberTasksOverdue).\n" +
                " * Considerar os progressos globais na consecução do objectivo.\n" +
                "\n" +
                "2. **Analise os motivos do cancelamento:**\n" +
                " * Identifique os motivos mais frequentes para cancelamentos de tarefas.\n" +
                " * Procure padrões ou problemas recorrentes que levam a cancelamentos.\n" +
                "\n" +
                "3. **Analise os detalhes da tarefa:**\n" +
                " * Examine os tipos de tarefas que são frequentemente canceladas.\n" +
                " * Identifique todas as tarefas que estão consistentemente atrasadas.\n" +
                " * Procure dependências entre tarefas que possam estar causando atrasos.\n" +
                "\n" +
                "4. **Gere conselhos acionáveis:**\n" +
                " * Forneça recomendações específicas sobre o que o usuário pode fazer para melhorar seu desempenho.\n" +
                " * **O que fazer:** Sugira ações específicas que o usuário pode tomar para resolver os problemas identificados (por exemplo, dividir tarefas grandes em tarefas menores, priorizar tarefas atrasadas, delegar tarefas).\n" +
                " * **O que comprar:** Recomende ferramentas, recursos ou serviços que possam ajudar o usuário a gerenciar suas tarefas com mais eficiência (por exemplo, software de gerenciamento de tarefas, aplicativos de produtividade, livros sobre gerenciamento de tempo).\n" +
                " * **O que estudar:** Sugira tópicos ou habilidades que o usuário possa aprender para melhorar suas habilidades de gerenciamento de tarefas (por exemplo, técnicas de gerenciamento de tempo, metodologias de gerenciamento de projetos, estratégias de priorização).\n" +
                "\n" +
                "5. **Formate sua resposta:**\n" +
                " * Comece com um breve resumo de sua análise.\n" +
                " * Organize seus conselhos em marcadores claros e concisos.\n" +
                " * Use um tom motivacional e de apoio.\n" +
                " * Concentre-se em fornecer etapas práticas e acionáveis que o usuário possa tomar.\n" +
                "\n" +
                "Exemplo de resposta:\n" +
                "\n" +
                "\"Com base na minha análise dos dados de suas tarefas, parece que um número significativo de tarefas está sendo cancelado devido a 'Acabou' e 'Imprevisto'. Para melhorar seu desempenho, recomendo o seguinte:\n" +
                "\n" +
                "* **O que fazer:**\n" +
                " Divida grandes tarefas em etapas menores e mais gerenciáveis para reduzir a sensação de estar sobrecarregado.\n" +
                " * Priorize as tarefas com base em sua importância e urgência para garantir que as tarefas críticas sejam concluídas no prazo.\n" +
                " * Agende check-ins regulares para revisar seu progresso e fazer os ajustes necessários.\n" +
                "* **O que comprar:**\n" +
                " * Considere usar um aplicativo de gerenciamento de tarefas como Todoist ou Asana para ajudá-lo a organizar e acompanhar suas tarefas.\n" +
                " * Invista em um planejador ou caderno para acompanhar sua agenda e lista de tarefas.\n" +
                "* **O que estudar:**\n" +
                " * Aprenda sobre técnicas de gerenciamento de tempo como a Técnica Pomodoro ou a Matriz de Eisenhower para melhorar sua produtividade.\n" +
                " * Leia livros ou artigos sobre gerenciamento de projetos para obter uma melhor compreensão de como planejar e executar tarefas complexas. ";

        ResponseDashbordReasonCancellationDTO cancellationDTO =
                dashboardService.getDashReasonCancellationByIdObjective(dto.idUser(), dto.id());

        String json =
                "{" +
                "nameObjective: " + dto.nameObjective() + "," +
                "descriptionObjective: " + dto.descriptionObjective() + "," +
                "statusObjective: " + dto.statusObjective() + "," +
                "totNumberTasks: " + dto.totNumberTasks() + "," +
                "numberTasksToDo: " + dto.numberTasksToDo() + "," +
                "numberTasksDone: " + dto.numberTasksDone() + "," +
                "numberTasksInProgress: " + dto.numberTasksInProgress() + "," +
                "numberTasksOverdue: " + dto.numberTasksOverdue() + "," +
                "numberTasksCancelled: " + dto.numberTasksCancelled() + "," +
                "percentageTasksToDo: " + dto.percentageTasksToDo() + "," +
                "percentageTasksDone: " + dto.percentageTasksDone() + "," +
                "percentageTasksInProgress: " + dto.percentageTasksInProgress() + "," +
                "percentageTasksOverdue: " + dto.percentageTasksOverdue() + "," +
                "percentageTasksCancelled: " + dto.percentageTasksCancelled() +
                "}" +
                "{" +
                "número total de tarefas cancelada: " + cancellationDTO.totNumberTasks() + "," +
                "Lista de motivos, pelo qual as tarefas não foram realizadas: [";

        for (ReasonDTO reasonDTO: cancellationDTO.reasonList()) {
            json += reasonDTO.toString();
        }

        json += "]}";

        json += "{" +
                "Tarefas Canceladas E Seus respectivos motivos: [";

        List<TaskDTO> taskDTOList = dashboardService.getTasksCancelledByObjective(dto.idUser(), dto.id());

        for (TaskDTO taskDTO : taskDTOList) {
            json += taskDTO.toString();
        }

        json += "]}";

        Map<String, Object> body = Map.of(
                "model", "anthropic/claude-opus-4.1",
                "messages", List.of(
                        Map.of("role", "user", "content", prompt + "\n\n" + json)
                )
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        return response.getBody();
    }
}
