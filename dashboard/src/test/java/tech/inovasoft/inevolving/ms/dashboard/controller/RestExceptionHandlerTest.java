package tech.inovasoft.inevolving.ms.dashboard.controller;

import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import tech.inovasoft.inevolving.ms.dashboard.domain.dto.response.ExceptionResponse;
import tech.inovasoft.inevolving.ms.dashboard.domain.exception.ExternalServiceErrorException;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class RestExceptionHandlerTest {

    private RestExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new RestExceptionHandler();
    }

    @Test
    @DisplayName("FeignException com status 400 deve retornar HTTP 400")
    void handleFeignException_status400() {
        // Given
        FeignException exception = feignExceptionWithStatus(400, "Invalid timezone");

        // When
        ResponseEntity<ExceptionResponse> response = handler.handleFeignException(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).contains("Invalid timezone");
    }

    @Test
    @DisplayName("FeignException com status 404 deve retornar HTTP 404")
    void handleFeignException_status404() {
        // Given
        FeignException exception = feignExceptionWithStatus(404, "Not found");

        // When
        ResponseEntity<ExceptionResponse> response = handler.handleFeignException(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("FeignException com status desconhecido deve retornar HTTP 500")
    void handleFeignException_unknownStatus() {
        // Given
        FeignException exception = feignExceptionWithStatus(0, "Unknown");

        // When
        ResponseEntity<ExceptionResponse> response = handler.handleFeignException(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("ExternalServiceErrorException deve retornar HTTP 500")
    void handleExternalServiceErrorException() {
        // Given
        var exception = new ExternalServiceErrorException("task-service");

        // When
        ResponseEntity<ExceptionResponse> response = handler.handleDataBaseException(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().simpleName()).isEqualTo("ExternalServiceErrorException");
        assertThat(response.getBody().message()).isEqualTo("Error in the task-service");
    }

    private FeignException feignExceptionWithStatus(int status, String message) {
        Request request = Request.create(
                Request.HttpMethod.GET,
                "/ms/tasks/objective",
                Collections.emptyMap(),
                null,
                StandardCharsets.UTF_8,
                new RequestTemplate()
        );
        return FeignException.errorStatus(
                "TaskServiceClient#getTasksByObjectiveId",
                feign.Response.builder()
                        .status(status)
                        .reason(message)
                        .request(request)
                        .build()
        );
    }
}
