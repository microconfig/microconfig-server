package io.microconfig.cli.commands;

import java.net.URI;
import java.util.Optional;

import static io.microconfig.cli.util.HttpUtil.httpGET;
import static io.microconfig.cli.util.HttpUtil.httpSend;

public class ShowCommand extends Command {

    public ShowCommand(String[] args) {
        super(args);
    }

    @Override
    public int execute() {
        var component = component(helpMessage());

        var type = flags.type();
        var env = flags.env();
        var uri = uri(
                type.orElse("app"),
                component,
                env.orElse("default")
        );

        var request = httpGET(uri, flags.timeout());
        addFlags(request);
        var body = httpSend(request.build());
        System.out.println(body);
        return 0;
    }

    private URI uri(String type, String name, String env) {
        return URI.create(String.format("%s/api/config/%s/%s/%s", server(), type, name, env));
    }

    private String helpMessage() {
        return "Usage microctl show [component] [flags]\n"
                + "Generates configuration for component of specified type and outputs it to console\n"
                + "Flags: \n"
                + "  -e, --env:   config environment\n"
                + "  -t, --type:  config type, 'app' by default\n";
    }

}
