package io.microconfig.server.rest;

import io.microconfig.server.configs.ConfigGenerator;
import io.microconfig.server.configs.ConfigOptions;
import io.microconfig.server.configs.ConfigResult;
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
    public List<ConfigResult> fetchConfigs(@PathVariable("component") String component,
                                           @PathVariable("env") String env,
                                           ConfigOptions options) {
        return configGenerator
            .generateConfigs(component, env, options)
            .stream()
            .filter(r -> !r.getContent().isEmpty())
            .collect(toList());
    }

    @GetMapping("/config/{type}/{component}/{env}")
    public String fetchDeploy(@PathVariable("type") String type,
                              @PathVariable("component") String component,
                              @PathVariable("env") String env,
                              ConfigOptions options) {
        return configGenerator
            .generateConfig(component, env, type, options)
            .getContent();
    }
}