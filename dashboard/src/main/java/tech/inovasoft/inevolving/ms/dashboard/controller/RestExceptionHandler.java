package tech.inovasoft.inevolving.ms.dashboard.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import tech.inovasoft.inevolving.ms.dashboard.domain.dto.response.ExceptionResponse;
import tech.inovasoft.inevolving.ms.dashboard.domain.exception.ExternalServiceErrorException;

@Slf4j
@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(ExternalServiceErrorException.class)
    public ResponseEntity<ExceptionResponse> handleDataBaseException(ExternalServiceErrorException exception) {
        log.error("ERROR: {} - {}", exception.getClass().getSimpleName(), exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ExceptionResponse(
                        exception.getClass().getSimpleName(),
                        exception.getMessage()
                ));
    }


}
