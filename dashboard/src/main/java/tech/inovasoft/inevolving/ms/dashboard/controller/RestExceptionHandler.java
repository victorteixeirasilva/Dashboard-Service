package tech.inovasoft.inevolving.ms.dashboard.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import tech.inovasoft.inevolving.ms.dashboard.domain.dto.response.ExceptionResponse;
import tech.inovasoft.inevolving.ms.dashboard.domain.exception.ExternalServiceErrorException;


@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(ExternalServiceErrorException.class)
    public ResponseEntity handleDataBaseException(ExternalServiceErrorException exception) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ExceptionResponse(
                        exception.getClass().getSimpleName(),
                        exception.getMessage()
                ));
    }


}
