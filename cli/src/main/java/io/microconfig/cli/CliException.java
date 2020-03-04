package io.microconfig.cli;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class CliException extends RuntimeException {
    private final String message;
    private final int exitCode;
}
