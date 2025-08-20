package tech.inovasoft.inevolving.ms.dashboard.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.inovasoft.inevolving.ms.dashboard.domain.dto.response.ResponseObjectiveDTO;
import tech.inovasoft.inevolving.ms.dashboard.domain.exception.ExternalServiceErrorException;
import tech.inovasoft.inevolving.ms.dashboard.service.ClaudeService;

import java.util.UUID;

@Tag(name = "Claude")
@RestController
@RequestMapping("/api/ia")
public class ClaudeController {

    private final ClaudeService claudeService;

    public ClaudeController(ClaudeService claudeService) {
        this.claudeService = claudeService;
    }

    @Operation
    @PostMapping("/analisar")
    public ResponseEntity<String> analisar(
        @RequestBody ResponseObjectiveDTO dto
    ) throws ExternalServiceErrorException {
        String resposta = claudeService.enviarParaClaude(dto);
        return ResponseEntity.ok(resposta);
    }
}
