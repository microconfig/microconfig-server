package io.microconfig.cli.commands;

import io.microconfig.cli.CliException;
import lombok.RequiredArgsConstructor;

import java.net.URI;
import java.util.Map;
import java.util.Optional;

import static io.microconfig.cli.HttpUtil.getQuery;
import static io.microconfig.cli.HttpUtil.httpGET;
import static io.microconfig.cli.HttpUtil.httpSend;
import static java.util.Collections.emptyMap;
import static java.util.Optional.empty;

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

        var request = httpGET(uri);
        var body = httpSend(request);
        System.out.println(body);
        return 0;
    }

    private URI uri(String type, String name, String env, String query) {
        return URI.create(String.format("%s/api/config/%s/%s/%s%s", server(), type, name, env, query));
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
