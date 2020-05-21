package io.microconfig.cli.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.microconfig.cli.CliException;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.Collections.emptyMap;
import static java.util.Spliterator.ORDERED;
import static java.util.Spliterators.spliteratorUnknownSize;
import static java.util.stream.StreamSupport.stream;

public class JsonUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static JsonNode parse(String string) {
        try {
            return MAPPER.readTree(string);
        } catch (IOException e) {
            throw new CliException("Failed to parse response json: " + string, 100);
        }
    }

    public static Map<String, String> readMap(String json) {
        try {
            if (json.isEmpty()) return emptyMap();
            return MAPPER.readValue(json, new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new CliException("Failed to parse response json", 100);
        }
    }

    public static ObjectNode objectNode() {
        return MAPPER.createObjectNode();
    }

    public static Stream<JsonNode> nodes(ArrayNode node) {
        return stream(spliteratorUnknownSize(node.elements(), ORDERED), false);
    }
}
