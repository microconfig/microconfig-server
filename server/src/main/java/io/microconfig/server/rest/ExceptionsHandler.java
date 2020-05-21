package io.microconfig.server.rest;

import io.microconfig.server.rest.exceptions.BadRequestException;
import io.microconfig.server.rest.exceptions.ForbiddenException;
import io.microconfig.server.rest.exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import static io.microconfig.server.util.JsonUtil.objectNode;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ControllerAdvice
@RestController
@Slf4j
public class ExceptionsHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handle(Exception ex) {
        log.error("{}: {} at {}", ex.getClass(), ex.getMessage(), ex.getStackTrace()[0]);
        return response(ex);
    }

    private ResponseEntity<String> response(Exception ex) {
        if (ex instanceof NotFoundException) {
            return response(ex.getMessage(), NOT_FOUND);
        }

        if (ex instanceof ForbiddenException) {
            return response(ex.getMessage(), FORBIDDEN);
        }

        if (ex instanceof BadRequestException) {
            return response(ex.getMessage(), BAD_REQUEST);
        }

        return response("Something went wrong", BAD_REQUEST);
    }

    private ResponseEntity<String> response(String error, HttpStatus status) {
        var body = objectNode().put("error", error).toString();
        return new ResponseEntity<>(body, status);
    }
}
