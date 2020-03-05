package io.microconfig.cli.commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import io.microconfig.cli.CliException;
import io.microconfig.cli.FileUtil;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.net.URI;
import java.util.Map;
import java.util.Optional;

import static io.microconfig.cli.FileUtil.getOrCreateDir;
import static io.microconfig.cli.HttpUtil.getQuery;
import static io.microconfig.cli.HttpUtil.httpGET;
import static io.microconfig.cli.HttpUtil.httpSend;
import static io.microconfig.cli.JsonUtil.parse;
import static java.util.Collections.emptyMap;
import static java.util.Optional.empty;

@RequiredArgsConstructor
public class ConfigsCommand implements Command {
    private final String[] args;

    @Override
    public int execute() {
        if (args.length < 2) throw new CliException("Components not provided", 3);
        var name = args[args.length - 1];
        var env = env();
        var branch = branch();
        var uri = uri(
            name,
            env.orElse("default"),
            getQuery(branch.map(b -> Map.of("branch", b)).orElse(emptyMap()))
        );

        var request = httpGET(uri);
        var body = httpSend(request);
        var json = parse(body);
        var outDir = getOrCreateDir(dir().orElse("."));
        saveFiles(outDir, (ArrayNode) json);
        return 0;
    }

    private void saveFiles(File outDir, ArrayNode nodes) {
        for (JsonNode node : nodes) {
            var filename = node.get("fileName").asText();
            var content = node.get("content").asText();
            var file = new File(outDir, filename);
            FileUtil.write(file, content);
        }
    }

    private URI uri(String name, String env, String query) {
        return URI.create(String.format("%s/api/configs/%s/%s%s", server(), name, env, query));
    }

    private String server() {
        return "http://localhost:8080";
    }

    private Optional<String> env() {
        var pos = findFlag("-e", "Environment ", 6);
        return pos > 0 ? Optional.of(args[pos + 1]) : empty();
    }

    private Optional<String> branch() {
        var pos = findFlag("-b", "Config branch ", 6);
        return pos > 0 ? Optional.of(args[pos + 1]) : empty();
    }

    private Optional<String> dir() {
        var pos = findFlag("-d", "Output directory ", 6);
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
