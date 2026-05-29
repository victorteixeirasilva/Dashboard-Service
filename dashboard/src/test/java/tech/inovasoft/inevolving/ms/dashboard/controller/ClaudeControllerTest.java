package tech.inovasoft.inevolving.ms.dashboard.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tech.inovasoft.inevolving.ms.dashboard.domain.dto.response.ResponseObjectiveDTO;
import tech.inovasoft.inevolving.ms.dashboard.domain.exception.ExternalServiceErrorException;
import tech.inovasoft.inevolving.ms.dashboard.service.ClaudeService;
import tech.inovasoft.inevolving.ms.dashboard.service.client.Auth_For_MService.TokenService;
import tech.inovasoft.inevolving.ms.dashboard.service.client.Auth_For_MService.dto.TokenValidateResponse;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClaudeController.class)
@TestPropertySource(properties = {
        "spring.cloud.config.enabled=false",
        "spring.config.import="
})
class ClaudeControllerTest {

    private static final String VALID_TOKEN = "valid-token";
    private static final String INVALID_TOKEN = "invalid-token";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ClaudeService claudeService;

    @MockitoBean
    private TokenService tokenService;

    private ResponseObjectiveDTO objectiveDto;

    @BeforeEach
    void setUp() {
        UUID idUser = UUID.randomUUID();
        objectiveDto = new ResponseObjectiveDTO(
                UUID.randomUUID(),
                "Objetivo",
                "Descrição",
                "TODO",
                null,
                idUser,
                10, 5, 2, 2, 1, 1,
                50, 20, 20, 10, 10
        );

        when(tokenService.validateToken(VALID_TOKEN))
                .thenReturn(new TokenValidateResponse("subject", "dashboard-service"));
        when(tokenService.validateToken(INVALID_TOKEN)).thenReturn(null);
    }

    @Test
    @DisplayName("POST /api/ia/analisar deve repassar timezone ao ClaudeService")
    void analisar_withTimezone() throws Exception {
        // Given
        when(claudeService.enviarParaClaude(objectiveDto, "America/Sao_Paulo"))
                .thenReturn("{\"analysis\":\"ok\"}");

        // When / Then
        mockMvc.perform(post("/api/ia/analisar/{token}", VALID_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-Timezone", "America/Sao_Paulo")
                        .content(objectMapper.writeValueAsString(objectiveDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"analysis\":\"ok\"}"));

        verify(claudeService).enviarParaClaude(objectiveDto, "America/Sao_Paulo");
    }

    @Test
    @DisplayName("POST /api/ia/analisar sem timezone deve repassar null")
    void analisar_withoutTimezone() throws Exception {
        // Given
        when(claudeService.enviarParaClaude(objectiveDto, null))
                .thenReturn("resposta");

        // When / Then
        mockMvc.perform(post("/api/ia/analisar/{token}", VALID_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(objectiveDto)))
                .andExpect(status().isOk());

        verify(claudeService).enviarParaClaude(objectiveDto, null);
    }

    @Test
    @DisplayName("POST /api/ia/analisar deve retornar 401 com token inválido")
    void analisar_unauthorized() throws Exception {
        mockMvc.perform(post("/api/ia/analisar/{token}", INVALID_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(objectiveDto)))
                .andExpect(status().isUnauthorized());
    }
}
