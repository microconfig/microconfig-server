package io.microconfig.server.vault.credentials;

import io.microconfig.server.vault.exceptions.VaultAuthException;
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
        validate();
        var request = request();
        var response = httpSend(request);
        var node = validateResponse(response);
        var policy = node.path("auth").path("token_policies").toString();

        log.debug("Fetched token with k8s JWT. Assigned policies: {}", policy);
        token = node.path("auth").path("client_token").asText();
        return token;
    }

    private void validate() {
        if (address == null) throw new VaultAuthException("Missing microconfig.vault.address");
        if (path == null) throw new VaultAuthException("Missing microconfig.vault.kubernetes.path");
        if (role == null) throw new VaultAuthException("Missing microconfig.vault.kubernetes.role");
        if (jwt == null) throw new VaultAuthException("Missing microconfig.vault.kubernetes.jwt");
    }

    private HttpRequest request() {
        var body = objectNode()
                .put("role", role)
                .put("jwt", jwt)
                .toString();

        var path = String.format("%s/v1/auth/%s/login", address, this.path);
        log.debug("Calling {} to auth with kubernetes jwt for '{}' role", path, role);
        return newBuilder(URI.create(path))
                .POST(ofString(body))
                .timeout(ofSeconds(10))
                .build();
    }
}
