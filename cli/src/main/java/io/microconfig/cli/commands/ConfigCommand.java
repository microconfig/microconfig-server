package io.microconfig.cli.commands;

import java.net.URI;

import static io.microconfig.cli.util.HttpUtil.httpGET;
import static io.microconfig.cli.util.HttpUtil.httpSend;

public class ConfigCommand extends Command {

    public ConfigCommand(String[] args) {
        super(args);
    }

    @Override
    public int execute() {
        var component = component();

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
        return URI.create(String.format("%s/api/config/%s/%s/%s", server(), type, name, env));
    }
}
