package tech.inovasoft.inevolving.ms.dashboard.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.inovasoft.inevolving.ms.dashboard.domain.dto.response.ResponseObjectiveDTO;
import tech.inovasoft.inevolving.ms.dashboard.domain.exception.ExternalServiceErrorException;
import tech.inovasoft.inevolving.ms.dashboard.service.ClaudeService;
import tech.inovasoft.inevolving.ms.dashboard.service.client.Auth_For_MService.TokenService;
import tech.inovasoft.inevolving.ms.dashboard.service.client.Auth_For_MService.dto.TokenValidateResponse;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Tag(name = "Claude")
@RestController
@RequestMapping("/api/ia")
public class ClaudeController {

    @Autowired
    private TokenService tokenService;

    private final ClaudeService claudeService;

    public ClaudeController(ClaudeService claudeService) {
        this.claudeService = claudeService;
    }

    @Operation
    @PostMapping("/analisar/{token}")
    public ResponseEntity<String> analisar(
        @RequestBody ResponseObjectiveDTO dto,
        @PathVariable String token
    ) throws ExternalServiceErrorException {
        TokenValidateResponse tokenValidateResponse = null;

        try {
            tokenValidateResponse = tokenService.validateToken(token);
            if (tokenValidateResponse == null) {
                return ResponseEntity.status(
                        HttpStatus.UNAUTHORIZED
                ).build();
            }
        } catch (Exception e) {
            if (e.getMessage().equals("Invalid token")) {
                return ResponseEntity.status(
                        HttpStatus.UNAUTHORIZED
                ).build();
            }
        }

        String resposta = claudeService.enviarParaClaude(dto);
        return ResponseEntity.ok(resposta);
    }
}
