package io.microconfig.server.rest;

import io.microconfig.server.configs.ConfigGenerator;
import io.microconfig.server.configs.GeneratedConfig;
import io.microconfig.server.vault.VaultClient;
import io.microconfig.server.vault.VaultCredentials;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MicroConfigServerController {
    private final VaultClient vaultClient;
    private final ConfigGenerator configGenerator;

    @GetMapping("/vault-secret")
    public String fetchSecret(VaultCredentials credentials, @RequestParam("secret") String secret) {
        log.debug("Fetching {}", secret);
        return vaultClient.fetchSecret(credentials, secret);
    }

    @GetMapping("/config/{component}")
    public List<GeneratedConfig> fetchConfig(@PathVariable("component") String component,
                                             @RequestParam("env") String env) {
        return configGenerator.generateConfig(component, env, null);
    }
}