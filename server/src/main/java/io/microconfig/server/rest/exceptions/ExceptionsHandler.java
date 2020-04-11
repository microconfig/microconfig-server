package io.microconfig.server.rest.exceptions;

import io.microconfig.server.configs.ComponentNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@ControllerAdvice
@RestController
@Slf4j
public class ExceptionsHandler {
    private static final ResponseEntity<String> BAD_REQUEST = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    private static final ResponseEntity<String> NOT_FOUND = new ResponseEntity<>(HttpStatus.NOT_FOUND);
    private static final ResponseEntity<String> FORBIDDEN = new ResponseEntity<>(HttpStatus.FORBIDDEN);
    private static final ResponseEntity<String> TEAPOT = new ResponseEntity<>(HttpStatus.I_AM_A_TEAPOT);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handle(Exception ex) {
        log.trace("Exception on rest call", ex);
        return response(ex);
    }

    private ResponseEntity<String> response(Exception ex) {
        if (ex instanceof NotFoundException) {
            return NOT_FOUND;
        }

        if (ex instanceof ForbiddenException) {
            return FORBIDDEN;
        }

        if (ex instanceof ComponentNotFoundException) {
            return TEAPOT;
        }

        return BAD_REQUEST;
    }
}
