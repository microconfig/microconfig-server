package io.microconfig.server.vault.credentials;

import io.microconfig.server.vault.VaultConfig;
import io.microconfig.server.vault.exceptions.VaultAuthException;
import io.microconfig.server.vault.exceptions.VaultException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static io.microconfig.server.util.JsonUtil.objectNode;
import static io.microconfig.server.util.JsonUtil.parseJson;
import static java.net.http.HttpClient.newHttpClient;

@RequiredArgsConstructor
@Slf4j
public class VaultAppRoleCredentials implements VaultCredentials {
    private final String secretId;
    private String token;

    @Override
    public String getToken(VaultConfig config) {
        if (token != null) return token;

        var request = request(config);
        var response = fetch(request);
        if (response.statusCode() != 200) throw new VaultAuthException();

        log.debug("Fetched token with AppRole");
        token = parseJson(response.body()).path("auth").path("client_token").asText();
        return token;
    }

    private HttpResponse<String> fetch(HttpRequest request) {
        try {
            return newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new VaultException();
        }
    }

    private HttpRequest request(VaultConfig config) {
        var body = objectNode().put("role_id", config.getRoleId()).put("secret_id", secretId).toString();
        return HttpRequest.newBuilder(URI.create(String.format("%s/v1/auth/%s/login", config.getAddress(), config.getPath())))
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .timeout(Duration.ofSeconds(2))
            .build();
    }
}
