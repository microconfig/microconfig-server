package io.microconfig.server.rest;

import io.microconfig.server.configs.ConfigGenerator;
import io.microconfig.server.vault.VaultClient;
import io.microconfig.server.vault.VaultCredentials;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MicroConfigServerApi {
    private final VaultClient vaultClient;
    private final ConfigGenerator configGenerator;

    @GetMapping("/vault-secret/{secretName}")
    public String fetchSecret(VaultCredentials credentials, @RequestParam("secretName") String secretName) {
        log.debug("Fetching {}", secretName);
        return vaultClient.fetchSecret(credentials, secretName);
    }

    @GetMapping("/config/{component}/{env}")
    public Map<String, String> fetchConfigs(@PathVariable("component") String component,
                                            @PathVariable("env") String env) {
        return configGenerator.generateConfigs(env, component, null);
    }
}