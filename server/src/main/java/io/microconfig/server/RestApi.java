package io.microconfig.server;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api")
@Slf4j
public class RestApi {
    private final VaultClient vaultClient;

    @GetMapping("/vault-secret")
    public String fetchSecret(VaultCredentials credentials, @RequestParam("secret") String secret) {
        log.debug("Fetching {}", secret);
        return vaultClient.fetch(credentials, secret);
    }
}
