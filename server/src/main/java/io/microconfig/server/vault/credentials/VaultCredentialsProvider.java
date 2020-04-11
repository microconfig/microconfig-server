package io.microconfig.server.vault.credentials;

import io.microconfig.server.configs.DynamicVarsResolverStrategy;
import io.microconfig.server.vault.VaultConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VaultCredentialsProvider {
    private final VaultConfig config;
    private VaultCredentials credentials;

    public VaultCredentials credentials(DynamicVarsResolverStrategy dynamicVarsResolverStrategy) {
        if (credentials != null) return credentials;
        switch (config.getCredentialsType()) {
            case "kubernetes":
                return new KubernetesTokenCredentials(config);
            case "token":
                return new VaultTokenCredentials();
            case "approle":
                return new VaultAppRoleCredentials(config);
            default:
                throw new IllegalStateException("Unsupported auth type");
        }
    }
}
