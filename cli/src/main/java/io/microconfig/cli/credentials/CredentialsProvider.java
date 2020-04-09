package io.microconfig.cli.credentials;

import java.net.http.HttpRequest;

public class CredentialsProvider {
    private static final VaultToken vaultToken = new VaultToken();
    private static final KubernetesToken kubernetesToken = new KubernetesToken();

    public HttpRequest.Builder addKubernetesToken(HttpRequest.Builder request, String token) {
        return kubernetesToken.addCredentials(request, token);
    }

    public HttpRequest.Builder addVaultToken(HttpRequest.Builder request, String token) {
        return vaultToken.addCredentials(request, token);
    }
}
