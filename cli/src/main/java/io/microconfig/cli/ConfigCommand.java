package io.microconfig.cli;

import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;

import static java.net.http.HttpClient.newHttpClient;
import static java.util.Collections.emptyMap;
import static java.util.Optional.empty;
import static java.util.stream.Collectors.joining;

@RequiredArgsConstructor
public class ConfigCommand implements Command {
    private final String[] args;

    @Override
    public int execute() {
        if (args.length < 2) throw new CliException("Components not provided", 3);
        var name = args[args.length - 1];
        var type = type();
        var env = env();
        var branch = branch();
        var uri = uri(
            type.orElse("app"),
            name,
            env.orElse("default"),
            getQuery(branch.map(b -> Map.of("branch", b)).orElse(emptyMap()))
        );

        var request = httpRequest(uri);
        try {
            var response = newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                System.out.println(response.body());
                return 0;
            } else {
                throw new CliException("Bad response code: " + response.statusCode(), 200);
            }
        } catch (IOException e) {
            throw new CliException("Failed during network call: " + e.getMessage(), 100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return 666;
        }
    }

    private HttpRequest httpRequest(URI uri) {
        return HttpRequest.newBuilder(uri)
            .GET()
            .timeout(Duration.ofSeconds(2))
            .build();
    }

    private URI uri(String type, String name, String env, String query) {
        return URI.create(String.format("%s/api/config/%s/%s/%s%s", server(), type, name, env, query));
    }

    private String getQuery(Map<String, String> query) {
        var q = query.entrySet().stream()
            .map(e -> e.getKey() + "=" + e.getValue())
            .collect(joining("&"));

        return q.isEmpty()
            ? q
            : "?" + q;
    }

    private String server() {
        return "http://localhost:8080";
    }

    private Optional<String> type() {
        var pos = findFlag("-t", "Config type", 4);
        return pos > 0 ? Optional.of(args[pos + 1]) : empty();
    }

    private Optional<String> env() {
        var pos = findFlag("-e", "Environment ", 5);
        return pos > 0 ? Optional.of(args[pos + 1]) : empty();
    }

    private Optional<String> branch() {
        var pos = findFlag("-b", "Config branch ", 5);
        return pos > 0 ? Optional.of(args[pos + 1]) : empty();
    }

    private int findFlag(String flag, String flagName, int code) {
        for (int i = 1; i < args.length; i++) {
            if (args[i].equals(flag)) {
                if (i + 1 >= args.length) throw new CliException(flagName + " and component not provided", code);
                if (i + 1 == args.length - 1) throw new CliException(flagName + " or component not provided", code);
                return i;
            }
        }
        return -1;
    }
}
