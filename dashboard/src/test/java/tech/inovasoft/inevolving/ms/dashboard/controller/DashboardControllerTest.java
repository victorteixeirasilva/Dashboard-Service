package tech.inovasoft.inevolving.ms.dashboard.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tech.inovasoft.inevolving.ms.dashboard.domain.dto.response.ResponseCategoryDTO;
import tech.inovasoft.inevolving.ms.dashboard.domain.dto.response.ResponseDashbordDTO;
import tech.inovasoft.inevolving.ms.dashboard.domain.dto.response.ResponseDashbordReasonCancellationDTO;
import tech.inovasoft.inevolving.ms.dashboard.service.DashboardService;
import tech.inovasoft.inevolving.ms.dashboard.service.client.Auth_For_MService.TokenService;
import tech.inovasoft.inevolving.ms.dashboard.service.client.Auth_For_MService.dto.TokenValidateResponse;
import tech.inovasoft.inevolving.ms.dashboard.service.client.task.dto.TaskDTO;
import tech.inovasoft.inevolving.ms.dashboard.support.TaskTestDataFactory;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class DashboardControllerTest {

    private static final String VALID_TOKEN = "valid-token";
    private static final String INVALID_TOKEN = "invalid-token";
    private static final String BAD_CLAIM_TOKEN = "bad-claim-token";

    @Mock
    private DashboardService dashboardService;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private DashboardController dashboardController;

    private MockMvc mockMvc;

    private UUID idUser;
    private UUID idObjective;
    private UUID idCategory;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(dashboardController, "dashboardService", dashboardService);
        ReflectionTestUtils.setField(dashboardController, "tokenService", tokenService);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        mockMvc = MockMvcBuilders.standaloneSetup(dashboardController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();

        idUser = UUID.randomUUID();
        idObjective = UUID.randomUUID();
        idCategory = UUID.randomUUID();

        lenient().when(tokenService.validateToken(VALID_TOKEN))
                .thenReturn(new TokenValidateResponse("subject", "dashboard-service"));
        lenient().when(tokenService.validateToken(INVALID_TOKEN)).thenReturn(null);
        lenient().when(tokenService.validateToken(BAD_CLAIM_TOKEN))
                .thenThrow(new RuntimeException("Invalid token"));
    }

    private ResultActions performAsync(MockHttpServletRequestBuilder requestBuilder) throws Exception {
        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(request().asyncStarted())
                .andReturn();
        return mockMvc.perform(asyncDispatch(mvcResult));
    }

    @Nested
    @DisplayName("GET /ms/dashboard/{idUser}/{token}")
    class GetDashboard {

        @Test
        @DisplayName("deve retornar 200 e repassar timezone quando token válido")
        void successWithTimezone() throws Exception {
            // Given
            when(dashboardService.getDashboard(idUser, "America/Sao_Paulo"))
                    .thenReturn(new ResponseDashbordDTO(idUser, Collections.emptyList()));

            // When / Then
            performAsync(get("/ms/dashboard/{idUser}/{token}", idUser, VALID_TOKEN)
                            .header("X-User-Timezone", "America/Sao_Paulo"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.idUser").value(idUser.toString()));

            verify(dashboardService).getDashboard(idUser, "America/Sao_Paulo");
        }

        @Test
        @DisplayName("deve retornar 200 sem header de timezone")
        void successWithoutTimezone() throws Exception {
            // Given
            when(dashboardService.getDashboard(idUser, null))
                    .thenReturn(new ResponseDashbordDTO(idUser, Collections.emptyList()));

            // When / Then
            performAsync(get("/ms/dashboard/{idUser}/{token}", idUser, VALID_TOKEN))
                    .andExpect(status().isOk());

            verify(dashboardService).getDashboard(idUser, null);
        }

        @Test
        @DisplayName("deve retornar 401 quando token inválido")
        void unauthorizedNullToken() throws Exception {
            performAsync(get("/ms/dashboard/{idUser}/{token}", idUser, INVALID_TOKEN))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("deve retornar 401 quando claim inválida")
        void unauthorizedBadClaim() throws Exception {
            performAsync(get("/ms/dashboard/{idUser}/{token}", idUser, BAD_CLAIM_TOKEN))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("GET /ms/dashboard/category/objectives/{idUser}/{idCategory}/{token}")
    class GetObjectivesOfCategory {

        @Test
        @DisplayName("deve retornar 200 e repassar timezone")
        void successWithTimezone() throws Exception {
            // Given
            when(dashboardService.getObjectivesOfCategory(idUser, idCategory, "Europe/Lisbon"))
                    .thenReturn(new ResponseCategoryDTO(idCategory, "Cat", "Desc", Collections.emptyList()));

            // When / Then
            performAsync(get(
                            "/ms/dashboard/category/objectives/{idUser}/{idCategory}/{token}",
                            idUser, idCategory, VALID_TOKEN
                    ).header("X-User-Timezone", "Europe/Lisbon"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(idCategory.toString()));

            verify(dashboardService).getObjectivesOfCategory(idUser, idCategory, "Europe/Lisbon");
        }

        @Test
        @DisplayName("deve retornar 401 quando token inválido")
        void unauthorized() throws Exception {
            performAsync(get(
                            "/ms/dashboard/category/objectives/{idUser}/{idCategory}/{token}",
                            idUser, idCategory, INVALID_TOKEN
                    ))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("GET /ms/dashboard/cancellation-reason/{idUser}/{idObjective}/{token}")
    class GetCancellationReason {

        @Test
        @DisplayName("deve retornar 200 e repassar timezone")
        void successWithTimezone() throws Exception {
            // Given
            when(dashboardService.getDashReasonCancellationByIdObjective(idUser, idObjective, "UTC"))
                    .thenReturn(new ResponseDashbordReasonCancellationDTO(0, Collections.emptyList()));

            // When / Then
            performAsync(get(
                            "/ms/dashboard/cancellation-reason/{idUser}/{idObjective}/{token}",
                            idUser, idObjective, VALID_TOKEN
                    ).header("X-User-Timezone", "UTC"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totNumberTasks").value(0));

            verify(dashboardService).getDashReasonCancellationByIdObjective(idUser, idObjective, "UTC");
        }

        @Test
        @DisplayName("deve retornar 401 quando token inválido")
        void unauthorized() throws Exception {
            performAsync(get(
                            "/ms/dashboard/cancellation-reason/{idUser}/{idObjective}/{token}",
                            idUser, idObjective, INVALID_TOKEN
                    ))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("GET /ms/dashboard/tasks/cancellation-reason/{idUser}/{idObjective}/{token}")
    class GetTasksCancellationReason {

        @Test
        @DisplayName("deve retornar 200 com JSON contendo timestamps e idResponsibleUser")
        void successWithTimestampFieldsInJson() throws Exception {
            // Given
            TaskDTO cancelledTask = TaskTestDataFactory.buildCancelledTask(idObjective, idUser, "Imprevisto");
            when(dashboardService.getTasksCancelledByObjective(idUser, idObjective, "Europe/Lisbon"))
                    .thenReturn(List.of(cancelledTask));

            // When / Then
            performAsync(get(
                            "/ms/dashboard/tasks/cancellation-reason/{idUser}/{idObjective}/{token}",
                            idUser, idObjective, VALID_TOKEN
                    ).header("X-User-Timezone", "Europe/Lisbon"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].createdAt").value("2026-05-29T10:15:00-03:00"))
                    .andExpect(jsonPath("$[0].cancelledAt").value("2026-05-29T14:30:00-03:00"))
                    .andExpect(jsonPath("$[0].idResponsibleUser").isNotEmpty())
                    .andExpect(jsonPath("$[0].cancellationReason").value("Imprevisto"));

            verify(dashboardService).getTasksCancelledByObjective(idUser, idObjective, "Europe/Lisbon");
        }

        @Test
        @DisplayName("deve retornar 200 sem header de timezone")
        void successWithoutTimezone() throws Exception {
            // Given
            when(dashboardService.getTasksCancelledByObjective(idUser, idObjective, null))
                    .thenReturn(Collections.emptyList());

            // When / Then
            performAsync(get(
                            "/ms/dashboard/tasks/cancellation-reason/{idUser}/{idObjective}/{token}",
                            idUser, idObjective, VALID_TOKEN
                    ))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray());

            verify(dashboardService).getTasksCancelledByObjective(idUser, idObjective, null);
        }

        @Test
        @DisplayName("deve retornar 401 quando token inválido")
        void unauthorized() throws Exception {
            performAsync(get(
                            "/ms/dashboard/tasks/cancellation-reason/{idUser}/{idObjective}/{token}",
                            idUser, idObjective, INVALID_TOKEN
                    ))
                    .andExpect(status().isUnauthorized());
        }
    }
}
