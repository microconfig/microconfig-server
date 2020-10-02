package io.microconfig.server.vault;

import com.fasterxml.jackson.databind.JsonNode;
import io.microconfig.server.vault.exceptions.VaultSecretNotFound;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.http.HttpRequest;
import java.time.Duration;

import static io.microconfig.server.util.HttpUtil.httpSend;
import static io.microconfig.server.vault.VaultUtil.validateResponse;
import static java.net.http.HttpRequest.newBuilder;

@Slf4j
@RequiredArgsConstructor
public class VaultClientImpl implements VaultClient {
    private final VaultConfig config;

    @Override
    public String fetchKV(String property) {
        int dotIndex = property.lastIndexOf('.');
        String path = property.substring(0, dotIndex);
        String key = property.substring(dotIndex + 1);
        log.debug("Fetching {} {}", path, key);

        var node = readPath(path, config.getCredentials().getToken());
        var value = node.path("data").path("data").get(key);
        if (value == null) throw new VaultSecretNotFound(property);
        return value.asText();
    }

    private JsonNode readPath(String path, String token) {
        var splitPath = splitPath(path);
        var url = URI.create(config.getAddress() + "/v1/" + splitPath[0] + "/data" + splitPath[1]);
        log.debug("Calling Vault via {}", url);
        var request = newBuilder(url)
            .setHeader("X-Vault-Token", token)
            .timeout(Duration.ofSeconds(2))
            .build();
        var response = httpSend(request);
        return validateResponse(response);
    }

    private String[] splitPath(String path) {
        int slash = path.indexOf('/');
        return new String[]{path.substring(0, slash), path.substring(slash)};
    }
}
