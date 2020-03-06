package io.microconfig.server.vault;

import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static java.net.http.HttpClient.newHttpClient;

@RequiredArgsConstructor
@Slf4j
public class VaultAppRoleCredentials implements VaultCredentials {
    private static final ObjectMapper mapper = new ObjectMapper();
    private final String path = "team-approle";
    private final String roleId = "526cae12-6958-b99e-bfaa-3dde5d0ee5b1";
    private final String secretId;

    @Override
    public Vault toVault(VaultConfig config) {
        var token = token(config);
        return new Vault(config.token(token));
    }

    private String token(VaultConfig config) {
        var body = mapper.createObjectNode().put("role_id", roleId).put("secret_id", secretId).toString();
        var request = HttpRequest.newBuilder(URI.create(String.format("%s/v1/auth/%s/login", config.getAddress(), path)))
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .timeout(Duration.ofSeconds(2))
            .build();
        try {
            var response = newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            var json = mapper.readTree(response.body());
            return json.path("auth").path("client_token").asText();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
