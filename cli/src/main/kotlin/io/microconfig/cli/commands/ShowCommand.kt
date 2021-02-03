package io.microconfig.cli.commands;

import com.fasterxml.jackson.databind.node.ArrayNode;
import io.microconfig.cli.CliException;

import java.net.URI;

import static io.microconfig.cli.util.HttpUtil.httpGET;
import static io.microconfig.cli.util.HttpUtil.httpSend;
import static io.microconfig.cli.util.JsonUtil.parse;
import static java.util.stream.StreamSupport.stream;

public class ShowCommand extends Command {

    public ShowCommand(String[] args) {
        super(args);
    }

    @Override
    public int execute() {
        var component = component(helpMessage());
        var type = flags.type().orElse("app");
        var env = flags.env();
        var uri = uri(
                component,
                env.orElse("default")
        );

        var request = httpGET(uri, flags.timeout());
        addHeaders(request);
        var body = httpSend(request.build(), sslContext());
        var json = (ArrayNode) parse(body);
        var content = stream(json.spliterator(), false)
                .filter(j -> j.path("type").asText().equals(type))
                .findFirst()
                .map(j -> j.get("content").asText())
                .orElseThrow(() -> new CliException("No component with requested type", 404));

        System.out.println(content);
        return 0;
    }

    private URI uri(String name, String env) {
        return URI.create(String.format("%s/api/configs/%s/%s", server(), name, env));
    }

    private String helpMessage() {
        return "Usage microctl show [component] [flags]\n"
                + "Generates configuration for component of specified type and outputs it to console\n"
                + "Flags: \n"
                + "  -e, --env  [name]: config environment\n"
                + "  -t, --type [app]: config type, 'app' by default\n"
                + "  -s, --set  [foo=bar]: override values for placeholders\n"
                ;
    }

}
