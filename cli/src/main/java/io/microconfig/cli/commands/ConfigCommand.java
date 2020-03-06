package io.microconfig.cli.commands;

import io.microconfig.cli.CliException;
import io.microconfig.cli.CliFlags;
import io.microconfig.cli.credentials.CredentialsProvider;

import java.net.URI;
import java.util.Map;

import static io.microconfig.cli.util.HttpUtil.getQuery;
import static io.microconfig.cli.util.HttpUtil.httpGET;
import static io.microconfig.cli.util.HttpUtil.httpSend;
import static java.util.Collections.emptyMap;

public class ConfigCommand implements Command {
    private static final CredentialsProvider credentials = new CredentialsProvider();
    private final CliFlags flags;
    private final String[] args;

    public ConfigCommand(String[] args) {
        this.args = args;
        this.flags = new CliFlags(args);
    }

    @Override
    public int execute() {
        if (args.length < 2) throw new CliException("Components not provided", 3);
        var component = args[args.length - 1];
        var type = flags.type();
        var env = flags.env();
        var branch = flags.branch();
        var uri = uri(
            type.orElse("app"),
            component,
            env.orElse("default"),
            getQuery(branch.map(b -> Map.of("branch", b)).orElse(emptyMap()))
        );

        var request = httpGET(uri);
        flags.auth().ifPresent(t -> credentials.addCredentials(t, request, args));
        var body = httpSend(request.build());
        System.out.println(body);
        return 0;
    }

    private URI uri(String type, String name, String env, String query) {
        return URI.create(String.format("%s/api/config/%s/%s/%s%s", flags.server(), type, name, env, query));
    }
}
