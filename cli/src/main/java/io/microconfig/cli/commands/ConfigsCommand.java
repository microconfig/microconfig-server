package io.microconfig.cli.commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import io.microconfig.cli.CliException;
import io.microconfig.cli.CliFlags;
import io.microconfig.cli.credentials.CredentialsProvider;
import io.microconfig.cli.util.FileUtil;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.net.URI;

import static io.microconfig.cli.util.FileUtil.getOrCreateDir;
import static io.microconfig.cli.util.HttpUtil.httpGET;
import static io.microconfig.cli.util.HttpUtil.httpSend;
import static io.microconfig.cli.util.JsonUtil.parse;

@RequiredArgsConstructor
public class ConfigsCommand implements Command {
    private static final CredentialsProvider credentials = new CredentialsProvider();
    private final CliFlags flags;
    private final String[] args;

    public ConfigsCommand(String[] args) {
        this.args = args;
        this.flags = new CliFlags(args);
    }

    @Override
    public int execute() {
        if (args.length < 2) throw new CliException("Components not provided", 3);
        var name = args[args.length - 1];
        var env = flags.env();
        var uri = uri(
            name,
            env.orElse("default")
        );

        var request = httpGET(uri);
        flags.auth().ifPresent(t -> credentials.addCredentials(t, request, args));
        flags.branch().ifPresent(b -> request.setHeader("X-BRANCH", b));
        flags.tag().ifPresent(t -> request.setHeader("X-TAG", t));
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
        return URI.create(String.format("%s/api/configs/%s/%s", flags.server(), name, env));
    }
}
