package io.microconfig.cli.commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import io.microconfig.cli.util.FileUtil;

import java.io.File;
import java.net.URI;

import static io.microconfig.cli.util.FileUtil.getOrCreateDir;
import static io.microconfig.cli.util.HttpUtil.httpGET;
import static io.microconfig.cli.util.HttpUtil.httpSend;
import static io.microconfig.cli.util.JsonUtil.parse;

public class ConfigsCommand extends Command {

    public ConfigsCommand(String[] args) {
        super(args);
    }

    @Override
    public int execute() {
        var component = component();

        var env = flags.env();
        var uri = uri(
            component,
            env.orElse("default")
        );

        var request = httpGET(uri, flags.timeout());
        addFlags(request);
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
            FileUtil.writeFile(file, content);
        }
    }

    private URI uri(String name, String env) {
        return URI.create(String.format("%s/api/configs/%s/%s", server(), name, env));
    }
}
