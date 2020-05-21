package io.microconfig.server.vault.credentials;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.http.HttpRequest;

import static io.microconfig.server.util.HttpUtil.httpSend;
import static io.microconfig.server.util.JsonUtil.objectNode;
import static io.microconfig.server.vault.VaultUtil.validateResponse;
import static java.net.http.HttpRequest.BodyPublishers.ofString;
import static java.net.http.HttpRequest.newBuilder;
import static java.time.Duration.ofSeconds;

@RequiredArgsConstructor
@Slf4j
public class KubernetesTokenCredentials implements VaultCredentials {
    private final String address;
    private final String path;
    private final String role;
    private final String jwt;
    private String token;

    @Override
    public String getToken() {
        if (token != null) return token;
        var request = request();
        var response = httpSend(request);
        var node = validateResponse(response);
        log.debug("Fetched token with k8s JWT");
        token = node.path("auth").path("client_token").asText();
        return token;
    }

    private HttpRequest request() {
        var body = objectNode()
            .put("role", role)
            .put("jwt", jwt)
            .toString();

        return newBuilder(URI.create(String.format("%s/v1/auth/%s/login", address, path)))
            .POST(ofString(body))
            .timeout(ofSeconds(10))
            .build();
    }
}
