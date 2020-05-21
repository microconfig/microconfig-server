package io.microconfig.server.rest.exceptions;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ServerErrorException extends RuntimeException {
    public ServerErrorException(String message) {
        super(message);
    }
}
