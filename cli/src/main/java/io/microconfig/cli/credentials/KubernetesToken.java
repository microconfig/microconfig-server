package io.microconfig.cli.credentials;

import java.net.http.HttpRequest;

class KubernetesToken implements Credentials {
    @Override
    public HttpRequest.Builder addCredentials(HttpRequest.Builder request, String token) {
        return request
            .setHeader("X-AUTH-TYPE", "KUBERNETES")
            .setHeader("X-KUBERNETES-TOKEN", token);
    }
}
