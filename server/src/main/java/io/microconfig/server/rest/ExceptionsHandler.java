package io.microconfig.server.rest;

import io.microconfig.server.vault.exceptions.VaultSecretNotFound;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN;

@ControllerAdvice
@RestController
@Slf4j
public class ExceptionsHandler {
    private static final HttpHeaders HEADERS = headers();
    private static final ResponseEntity<String> BAD_REQUEST = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    private static final ResponseEntity<String> NOT_FOUND = new ResponseEntity<>(HttpStatus.NOT_FOUND);
    private static final ResponseEntity<String> UNAUTHORIZED = new ResponseEntity<>(HEADERS, HttpStatus.UNAUTHORIZED);
    private static final ResponseEntity<String> FORBIDDEN = new ResponseEntity<>(HEADERS, HttpStatus.FORBIDDEN);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handle(Exception ex) {
        log.trace("Exception on rest call", ex);
        return response(ex);
    }

    private ResponseEntity<String> response(Exception ex) {
        if (ex instanceof VaultSecretNotFound) {
            return NOT_FOUND;
        }

        return BAD_REQUEST;
    }

    private static HttpHeaders headers() {
        var headers = new HttpHeaders();
        headers.add(ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        return headers;
    }

}
