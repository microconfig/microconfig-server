package io.microconfig.cli;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

import static java.util.Optional.empty;

@RequiredArgsConstructor
public class CliFlags {
    private final String[] args;

    public String server() {
        var server = System.getenv("MCS_ADDRESS");
        return server != null ? server : "http://localhost:8080";
    }

    public Optional<String> type() {
        return findFlag("-t", "--type");
    }

    public Optional<String> env() {
        return findFlag("-e", "--env");
    }

    public Optional<String> branch() {
        return findFlag("--branch");
    }

    public Optional<String> tag() {
        return findFlag("--tag");
    }

    public Optional<String> dir() {
        return findFlag("-d", "--dir");
    }

    public Optional<String> auth() {
        return findFlag("-a", "--auth");
    }

    public Optional<String> vaultToken() {
        return findFlag("--vault-token");
    }

    public Optional<String> vaultSecretId() {
        return findFlag("--vault-secret-id");
    }

    public Optional<String> findFlag(String... flag) {
        var flags = Arrays.asList(flag);
        for (int i = 1; i < args.length; i++) {
            if (flags.contains(args[i])) {
                // "-flag flagValue component"
                if (i + 2 >= args.length) throw new CliException(args[i] + " value or component not provided", 4);
                return Optional.of(args[i + 1]);
            }
        }
        return empty();
    }
}
