package io.microconfig.server.rest;

import io.microconfig.core.properties.serializers.ConfigResult;
import io.microconfig.core.templates.Template;
import io.microconfig.server.configs.ConfigGenerator;
import io.microconfig.server.configs.ConfigOptions;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ConfigApi {
    private final ConfigGenerator configGenerator;

    @GetMapping("/configs/{component}/{env}")
    public List<ConfigsResponse> fetchConfigs(@PathVariable("component") String component,
                                              @PathVariable("env") String env,
                                              ConfigOptions options) {
        return configGenerator
                .generateConfigs(component, env, options)
                .stream()
                .filter(r -> !r.getContent().isEmpty())
                .map(ConfigsResponse::map)
                .flatMap(List::stream)
                .collect(toList());
    }

    @GetMapping("/config/{type}/{component}/{env}")
    public String fetchType(@PathVariable("type") String type,
                            @PathVariable("component") String component,
                            @PathVariable("env") String env,
                            ConfigOptions options) {
        return configGenerator
                .generateConfig(component, env, type, options)
                .getContent();
    }

    @RequiredArgsConstructor
    @Getter
    public static class ConfigsResponse {
        private final String service;
        private final String type;
        private final String fileName;
        private final String content;

        private static List<ConfigsResponse> map(ConfigResult result) {
            var resp = new ConfigsResponse(result.getComponent(), result.getConfigType(), result.getFileName(), result.getContent());
            var files = templates(result.getComponent(), result.getTemplates());
            files.add(resp);
            return files;
        }

        private static List<ConfigsResponse> templates(String component, List<Template> templates) {
            return templates.stream()
                    .map(t -> new ConfigsResponse(component, "template", t.getFileName(), t.getContent()))
                    .collect(toList());
        }

    }
}