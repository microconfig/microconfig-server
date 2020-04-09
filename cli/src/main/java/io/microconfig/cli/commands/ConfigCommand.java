package io.microconfig.cli.commands;

import io.microconfig.cli.CliException;

import java.net.URI;

import static io.microconfig.cli.util.HttpUtil.httpGET;
import static io.microconfig.cli.util.HttpUtil.httpSend;

public class ConfigCommand extends Command {

    public ConfigCommand(String[] args) {
        super(args);
    }

    @Override
    public int execute() {
        if (args.length < 2) throw new CliException("Components not provided", 3);
        var component = args[args.length - 1];
        var type = flags.type();
        var env = flags.env();
        var uri = uri(
            type.orElse("app"),
            component,
            env.orElse("default")
        );

        var request = httpGET(uri);
        addFlags(request);
        var body = httpSend(request.build());
        System.out.println(body);
        return 0;
    }

    private URI uri(String type, String name, String env) {
        return URI.create(String.format("%s/api/config/%s/%s/%s", flags.server(), type, name, env));
    }
}
