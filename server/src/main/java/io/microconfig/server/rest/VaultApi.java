package io.microconfig.server.rest;

import io.microconfig.server.vault.VaultClient;
import io.microconfig.server.vault.credentials.VaultCredentials;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static java.util.Objects.requireNonNull;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class VaultApi {
    private final VaultClient vaultClient;

    @GetMapping("/vault-kv")
    public String fetchSecret(VaultCredentials credentials, @RequestParam("secretName") String secretName) {
        requireNonNull(credentials, "No credentials provided");
        return vaultClient.fetchSecret(credentials, secretName);
    }
}
