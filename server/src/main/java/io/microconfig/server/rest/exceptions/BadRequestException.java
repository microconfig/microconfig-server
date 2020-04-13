package io.microconfig.server.rest.exceptions;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BadRequestException extends RuntimeException {
    private final String message;
}
