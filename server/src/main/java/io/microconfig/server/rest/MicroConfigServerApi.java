package io.microconfig.server.rest;

import io.microconfig.core.properties.resolver.placeholder.PlaceholderResolveStrategy;
import io.microconfig.server.configs.ConfigGenerator;
import io.microconfig.server.configs.ConfigResult;
import io.microconfig.server.configs.VaultPlaceholderResolveStrategy;
import io.microconfig.server.vault.VaultClient;
import io.microconfig.server.vault.VaultCredentials;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static io.microconfig.factory.configtypes.StandardConfigTypes.DEPLOY;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MicroConfigServerApi {
    private final VaultClient vaultClient;
    private final ConfigGenerator configGenerator;

    @GetMapping("/vault-secret/")
    public String fetchSecret(VaultCredentials credentials, @RequestParam("secretName") String secretName) {
        log.debug("Fetching {}", secretName);
        requireNonNull(credentials, "No credentials provided");
        return vaultClient.fetchSecret(credentials, secretName);
    }

    @GetMapping("/config/{component}/{env}")
    public List<ConfigResult> fetchConfigs(@PathVariable("component") String component,
                                           @PathVariable("env") String env,
                                           @RequestParam(value = "branch", required = false) String branch,
                                           VaultCredentials credentials) {
        var resolvers = resolvers(credentials);
        return configGenerator
            .generateConfigs(component, env, branch, resolvers)
            .stream()
            .filter(r -> !r.getContent().isEmpty())
            .collect(toList());
    }

    @GetMapping("/config/deploy/{component}/{env}")
    public String fetchDeploy(@PathVariable("component") String component,
                              @PathVariable("env") String env,
                              @RequestParam(value = "branch", required = false) String branch,
                              VaultCredentials credentials) {
        var resolvers = resolvers(credentials);
        return configGenerator
            .generateConfig(component, env, branch, DEPLOY.getType(), resolvers)
            .getContent();
    }

    private PlaceholderResolveStrategy[] resolvers(VaultCredentials vaultCredentials) {
        return vaultCredentials != null
            ? new PlaceholderResolveStrategy[]{new VaultPlaceholderResolveStrategy(vaultClient, vaultCredentials)}
            : new PlaceholderResolveStrategy[0];
    }
}