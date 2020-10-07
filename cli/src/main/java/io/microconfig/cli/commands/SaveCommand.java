package io.microconfig.cli.commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.File;
import java.net.URI;

import static io.microconfig.cli.util.FileUtil.getOrCreateDir;
import static io.microconfig.cli.util.FileUtil.writeFile;
import static io.microconfig.cli.util.HttpUtil.httpGET;
import static io.microconfig.cli.util.HttpUtil.httpSend;
import static io.microconfig.cli.util.JsonUtil.parse;

public class SaveCommand extends Command {

    public SaveCommand(String[] args) {
        super(args);
    }

    @Override
    public int execute() {
        var component = component(helpMessage());

        var env = flags.env();
        var uri = uri(
                component,
                env.orElse("default")
        );

        var request = httpGET(uri, flags.timeout());
        addHeaders(request);
        var body = httpSend(request.build());
        var json = parse(body);
        var outDir = getOrCreateDir(flags.dir().orElse("."));
        saveFiles(outDir, (ArrayNode) json);
        return 0;
    }

    private void saveFiles(File outDir, ArrayNode nodes) {
        for (JsonNode node : nodes) {
            var filename = node.get("fileName").asText();
            var content = node.get("content").asText();
            var file = new File(outDir, filename);
            writeFile(file, content);
        }
    }

    private URI uri(String name, String env) {
        return URI.create(String.format("%s/api/configs/%s/%s", server(), name, env));
    }

    private String helpMessage() {
        return "Usage microctl save [component] [flags]\n"
                + "Generates configuration for component and saves it to disk\n"
                + "Flags: \n"
                + "  -e, --env:   config environment\n"
                + "  -t, --type:  config type, all types by default\n"
                + "  -d, --dir:   output directory, current dir by default\n"
                + "  -s, --set:   override values for placeholders [foo.bar=baz]\n"
                ;
    }

}
