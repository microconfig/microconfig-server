package io.microconfig.server.vault.credentials;

import io.microconfig.server.vault.exceptions.VaultAuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.http.HttpRequest;
import java.time.Duration;

import static io.microconfig.server.util.HttpUtil.httpSend;
import static io.microconfig.server.util.JsonUtil.objectNode;
import static io.microconfig.server.util.JsonUtil.parseJson;

@RequiredArgsConstructor
@Slf4j
public class VaultAppRoleCredentials implements VaultCredentials {
    private final String address;
    private final String path;
    private final String role;
    private final String secret;

    private String token;

    @Override
    public String getToken() {
        if (token != null) return token;

        var request = request();
        var response = httpSend(request);

        if (response.statusCode() != 200) {
            var node = parseJson(response.body());
            if (node.get("errors") != null) {
                throw new VaultAuthException(node.get("errors").asText());
            } else {
                throw new VaultAuthException("Auth failed with cod: " + response.statusCode());
            }
        }

        log.debug("Fetched token with AppRole");
        token = parseJson(response.body()).path("auth").path("client_token").asText();
        return token;
    }

    private HttpRequest request() {
        var body = objectNode().put("role_id", role).put("secret_id", secret).toString();
        return HttpRequest.newBuilder(URI.create(String.format("%s/v1/auth/%s/login", address, path)))
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .timeout(Duration.ofSeconds(2))
            .build();
    }
}
